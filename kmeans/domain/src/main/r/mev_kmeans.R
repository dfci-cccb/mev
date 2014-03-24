#K means clustering

#a helper method for finding the 2-norm (simple euclidean length) of a vector:
norm_vec <- function(x) sqrt(sum(x^2))

#load the data:
data_mtx<-read.table(INFILE, sep='\t', header=T)
rownames(data_mtx)<-data_mtx[,1]  #name the rows by the genes, which occupy the first column
data_mtx<-na.omit(data_mtx[,-1]) #remove the first column containing the gene names, skipping any incomplete rows
data_mtx<-data.matrix(data_mtx)

#specify which samples to include in the analysis:
sample_mtx<-read.table(SAMPLE_FILE, header=F, sep="\t")
sample_mtx<-sample_mtx[(sample_mtx[,2]!=-1),]  #parse out the samples we do NOT want to include (marked with -1)
data_mtx<-data_mtx[,as.character(sample_mtx[,1])]  #retain only the samples we care about

#run the k-means algorithm:
results<-kmeans(data_mtx, CLUSTER_COUNT)

#extract the relevant info:
cluster_assignments<-results$cluster  #array with the assignments for each of the genes/rows in data_mtx
centers<-results$centers   #the centroids of the k-clusters 
group_counts<-results$size  #how many members are assigned to each cluster

#get some data about the clustering:
bg_ss<-results$betweenss
total_ss<-results$totss
cluster_measure<-bg_ss/total_ss

#perform some operations for a better two-dimensional visualization of the high-dimensional clusters
#c.f. http://www.cs.utexas.edu/~inderjit/public_papers/csda.pdf describing the idea
N<-nrow(data_mtx)
mq<-(1/N)*colSums(group_counts*centers)
V<-centers-mq
D<-diag(group_counts);
S<-N*t(V)%*%D%*%V
ev<-eigen(S,TRUE)
e_vectors<-ev$vectors  #eigenvectors are the columns

#project the centroid locations into the 2-dimensional eigenspace
centroid_projections<-centers%*%e_vectors

#project the original datapoints/vectors:
datapoint_projections<-data_mtx%*%e_vectors

#write the output to a file:
sink(OUTFILE)

cat(paste(CLUSTER_COUNT,"\n",sep=''))

rn=rownames(data_mtx)

rownames(datapoint_projections)<-rn
names(cluster_assignments)<-rn
#write centroid info:
for(i in 1:CLUSTER_COUNT)
{
	cat(paste("Cluster ",i,"\t",i,"\t",centroid_projections[i,1],"\t",centroid_projections[i,2],"\n", sep=''))
}
cat(paste(N,"\n",sep=''))

#write projected gene info:
for (name in rn)
{
	cat(paste(name,"\t",cluster_assignments[name],"\t",datapoint_projections[name,1],"\t",datapoint_projections[name,2],"\n",sep=''))

}

sink()





