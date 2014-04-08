suppressMessages(library(multtest))


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

groupings<-sample_mtx[,2]  

exp_data<-exp_data[,as.character(sample_mtx[,1])]  #retain only the samples we care about

#calculate the group means:
#for each row/gene, find the mean of each of the defined groups
group_means<-data.frame(row.names=rownames(exp_data))
for (i in levels(factor(groupings)))
{
	this_group<-as.character(sample_mtx[(sample_mtx[,2]==i),1])
	group_means<-cbind(group_means,rowMeans(exp_data[,this_group]))
}
group_means<-data.matrix(group_means)

#allocate a matrix of the appropriate size (n*(n-1)/2 columns for all pairwise ratios):
group_count<-nlevels(factor(groupings))
num_cols=as.integer((group_count*(group_count-1))/2)
lfc_matrix<-matrix(,nrow=dim(exp_data)[1],ncol=num_cols)
previous<-0
column_headers<-c()
for (i in 1:(group_count-1))
{
	lfc_matrix[,(previous+1):(previous+group_count-i)]<-group_means[,i]/group_means[,(i+1):group_count]
	previous<-previous+group_count-i
	
	for(j in (i+1):group_count)
	{
		column_headers<-c(column_headers,paste(levels(groupings)[i],DELIMITER,levels(groupings)[j]))
	}
}
#make it a log-fold change:
lfc_matrix<-log(lfc_matrix)

colnames(lfc_matrix)<-column_headers

#use the mt.teststat() to find the F-statistics.
f_stats<-mt.teststat(exp_data,groupings,test="f")
df1<-group_count-1  #between group dof
df2<-ncol(exp_data)-group_count  #intra-group dof's

#get the p_values for each row/gene:
p_vals<-1-pf(f_stats,df1,df2)

#adjust the p-vals for multiple testing, if desired:
if(CORRECT_FOR_MULTIPLE_TESTING)
{
	p_vals<-p.adjust(p_vals, method="fdr")
}

results=data.frame(rownames(exp_data),p_vals, lfc_matrix)

colnames(results)<-c('gene', 'p_val', column_headers) 

write.table(results, OUTFILE, sep='\t', row.names=F, col.names=T, quote=F)

