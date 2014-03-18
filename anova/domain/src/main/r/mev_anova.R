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

#use the mt.teststat() to find the F-statistics.
f_stats<-mt.teststat(exp_data,groupings,test="f")
group_count<-nlevels(factor(groupings))
df1<-group_count-1  #between group dof
df2<-ncol(exp_data)-group_count  #intra-group dof's
p_vals<-1-pf(f_stats,df1,df2)

#adjust the p-vals for multiple testing, if desired:
if(CORRECT_FOR_MULTIPLE_TESTING)
{
	p_vals<-p.adjust(p_vals, method="fdr")
}

results=data.frame(rownames(exp_data),p_vals)

write.table(results, OUTFILE, sep='\t', row.names=F, col.names=F, quote=F)
