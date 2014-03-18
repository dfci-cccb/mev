
######### read in expression data matrix ##############
exp_data<-read.table(INFILE, header=T, sep="\t")

rownames(exp_data)<-exp_data[,1]  #name the rows by the genes, which occupy the first column
exp_data<-na.omit(exp_data[,-1]) #remove the first column containing the gene names, skipping any incomplete rows
exp_data<-data.matrix(exp_data)  #cast as a numeric matrix

## Check to determine if matrix contains negative values
## off set the matrix to operate in positive values
## starting with 0
exp_data<-if(min(exp_data)<0){exp_data+min(exp_data)*-1}else{exp_data}

######### read groupings file #####################
sample_mtx<-read.table(SAMPLE_FILE, header=F, sep="\t")
sample_mtx<-sample_mtx[(sample_mtx[,2]!=-1),]  #parse out the samples we do NOT want to include (marked with -1)
exp_data<-exp_data[,as.character(sample_mtx[,1])]  #retain only the samples we care about

if(TEST_TYPE==ONE_SAMPLE)
{
	#one-sample t-test.  User supplies a mean to test against.  Allows
	#for two-tailed and one-tailed alternate hypotheses.
	
	p_vals<-apply(exp_data,1,
      function(x)
      {
        #t.test(x, mu=USER_MU, alternative=HYPOTHESIS_STYLE) #if different alternative hypotheses desired.
        t.test(x, mu=USER_MU)
      }
     )
	


} else {
	#only import the library if doing a two-sample or paired test
	suppressMessages(library(multtest))
	suppressMessages(library(genefilter))
	
	if (TEST_TYPE==TWO_SAMPLE)
	{
		#two-sample t-test.  Only perform two-tailed test here.
	
		#group the samples and get the sizes:
		group_a<-as.character(sample_mtx[(sample_mtx[,2]==0),1])
		group_b<-as.character(sample_mtx[(sample_mtx[,2]==1),1])
		size_a<-length(group_a)
		size_b<-length(group_b)

		#arrange the matrix so one group is on 'the left' and the other is on 'the right' of the matrix
		#(just makes reading this cleaner and less likely to errors introduced elsewhere)
		exp_data<-cbind(exp_data[, group_a],exp_data[, group_b])
	
		#a vector labeling the groups
		labels<-c(rep(0,size_a),rep(1,size_b))
		
		#using mt.teststat gets the t-statistics very quickly (much quicker than apply(...))
		if(EQUAL_VARIANCE)
		{
			t_stats<-mt.teststat(exp_data, labels, test="t.equalvar")
			
			#now calculate the p-vals (not yet adjusted for multiple testing)
			p_vals<-2*(1-pt(t_stats,size_a+size_b-2)) 

		} else  #Welch's t-test (unequal variance)
		{
			t_stats<-abs(mt.teststat(exp_data, labels, test="t")) #take absolute value for calculating p-value later
			#since this is Welch's t-test, have to estimate the degrees of freedom by the Welch–Satterthwaite equation:
			sa_sqd<-rowVars(exp_data[,1:size_a])  #calculate the sample variance for each row/gene in group A
			sb_sqd<-rowVars(exp_data[,(size_a+1):(size_a+size_b)]) #calculate the sample variance for each row/gene in group B
			nu_1<-size_a-1
			nu_2<-size_b-1
			dof<-((sa_sqd/size_a+sb_sqd/size_b)^2)/(((sa_sqd^2)/(size_a^2*nu_1))+((sb_sqd^2)/(size_b^2*nu_2))) #Welch–Satterthwaite equation
			p_vals<-2*(1-pt(t_stats,dof)) #two-tailed p vals

		}
		
		#test:
# 		p_vals_test<-apply(exp_data,1,
# 		  function(x)
#		  {
# 			t.test(x[1:size_a],x[(size_a+1):(size_a+size_b)])$p.value
# 		  }
#		 )
# 		 print(p_vals_test)
# 		 print(p_vals)
	
	} else if (TEST_TYPE==PAIRED)
	{
	
		sample_mtx<-sample_mtx[(sample_mtx[,2]==0),]  #the file is potentially "symmetric" so only need to keep the lines that have a group identifier of zero.
		sample_mtx<-na.omit(sample_mtx)	#ensure the samples are properly matched to another sample- remove any samples "paired" with a NA
		num_pairs<-dim(sample_mtx)[1]

		#create a vector of the sample pairings in order (the eventual column-order of the expression matrix)
		sample_orderings<-c()
		for (i in seq(1:num_pairs))
		{
			sample_orderings<-c(sample_orderings,as.character(sample_mtx[i,1]), as.character(sample_mtx[i,3]))
		}

		#rearrange the expression matrix so the columns of paired samples are adjacent to each other.  The t-test method we call requires ordering of this nature
		exp_data<-exp_data[,sample_orderings]

		labels<-rep(0:1,num_pairs)
		t_stats<-abs(mt.teststat(exp_data, labels, test="pairt"))
		p_vals<-2*(1-pt(t_stats,num_pairs-1))
		
		#test
#		evens=seq(2,2*num_pairs,2)
#		odds=seq(1,2*num_pairs,2)
#		p_vals_test<-apply(exp_data,1,function(x){
#		t.test(x[evens],x[odds],paired=T)$p.value
#		})
#		print(p_vals_test)
	} else
	{
		#error!  did not specify the test type
	}
}

#adjust the p-vals for multiple testing, if desired:
if(CORRECT_FOR_MULTIPLE_TESTING)
{
	p_vals<-p.adjust(p_vals, method="fdr")
}

results=data.frame(rownames(exp_data),p_vals)

write.table(results, OUTFILE, sep='\t', row.names=F, col.names=F, quote=F)