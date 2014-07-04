library(limma)

## NOTE
## Performs limma on a data matrix 
## Input two files: 1) data matrix; 2) Sample definition matrix
## Sample definition matrix contains sample number and status (tab delim)
## Status 1 = Experiment
## Status 0 = Control
## Status -1= Not included in analysis
##
## Data Matrix Assumption
## 
## 1) No "\'" in the input data file
## 2) All NaN is converted in NA
##
## DATE: 9.30.2013
## Author: Yaoyu
##
##
## NOTE
## Add in variable BETA as the threshold for Log Fold Change
##
## DATE: 01.13.2013
## Author: Yaoyu
##
## NOTE
## Remove SIGGENE_OUT file definition.  All filtering is to be done at the user level
## DATE 01.14.2014
## Author: Yaoyu
##
## Note
## Adjust input matrix to handle negative values in matrix
## Add in readjustment for Average Expression column 


## definte limma function
run.limma<-function(in.mtx, Experiment=NA, Control=NA){
    
  CurrMtx=in.mtx
  CurrMtx=in.mtx[,Experiment]
  
  CurrMtx=cbind(CurrMtx, in.mtx[,Control])
  
  Tissue=array(NA, dim=c(1,length(colnames(CurrMtx))))
  Tissue[1:length(Experiment)]="Experiment"
  Tissue[(length(Experiment)+1):length(colnames(CurrMtx))]="Control"
  
  Tissue=factor(Tissue,levels=c("Experiment","Control"))
  design=model.matrix(~0+Tissue)
  colnames(design)=c("Experiment", "Control")
  
  fit<-lmFit(CurrMtx, design)
  efit<-eBayes(fit)
  
  contrast.matrix=makeContrasts(ExpvsContr=Experiment-Control, levels=design)
  fit<-eBayes(contrasts.fit(fit, contrast.matrix[,"ExpvsContr"]))
  result_full=topTable(fit, adjust="fdr", number=dim(CurrMtx)[1])
  return(result_full)
}   

##

##
## read files
in.mtx=read.table(INFILE, header=TRUE, sep="\t", nrow=-1)

##
## Assume the file starts with \t and the first column is gene id
rownames(in.mtx)=in.mtx[,1]
in.mtx=na.omit(in.mtx[,-1])
in.mtx=data.matrix(in.mtx)

## Check to determine if matrix contains negative values
## off set the matrix to operate in positive values for limma
## starting with 0
min.val=min(in.mtx)
in.mtx=if(min.val<0){in.mtx+min.val*-1}else{in.mtx}


## Assign group
sample.mtx=read.table(SAMPLE_FILE, header=FALSE, sep="\t")
EXP=sample.mtx[(sample.mtx[,2]==1),1]
CON=sample.mtx[(sample.mtx[2]==0),1]

##
##  Run limma
result=run.limma(in.mtx,
  Experiment=EXP,
  Control=CON)

##
## reassign colnames
colnames(result)=c("ID", "Log Fold Change", "Average Expression", "t", "P-value", "q-value", "B")

## If the matrix contains negative values,  
## adjust the offset back to its original input values
##
result[,"Average Expression"]=if(min.val<0){result[,"Average Expression"]-(min.val*-1)}else{result[,"Average Expression"]}

##
## write rnk file
gsea_rnk <-result[,c("ID", "t")]
write.table(gsea_rnk, file=RNK_OUT, quote=FALSE, sep="\t", row.names=FALSE, col.names=FALSE)

#################################
## topGO implementation
## Lan Hu, 07/01/2014
#################################
tryCatch ({
## input variables from MeV:
# SPECIES
# GO_TYPE
# THRES
# TEST_TYPE

## output file
# TOPGO_OUT

library(topGO)
library(org.Hs.eg.db)
library(org.Mm.eg.db)
library(org.Rn.eg.db)

## assign the proper annotation db
if (SPECIES=='human')
    anno.db = 'org.Hs.eg.db'
if (SPECIES=='mouse')
    anno.db = 'org.Mm.eg.db'
if (SPECIES=='rat')
    anno.db = 'org.Hs.eg.db'

## create a topGOdata object
myGenes = rep(0,nrow(result)) # from limma
myGenes[result[,'P-value']<THRES] = 1 # mark 'significant ones'
names(myGenes) = as.vector(result[,'ID']) # ID must be gene symbols
myGenes = factor(myGenes)

myGOdata = new('topGOdata', ontology=GO_TYPE, description='topGO analysis', nodeSize=10,
  annot=annFUN.org, mapping=anno.db, ID='symbol', allGenes=myGenes)

## run the enrichment test
if (TEST_TYPE=='Fisher test')
    topGO.stat <- new('classicCount', testStatistic=GOFisherTest, name=TEST_TYPE)
if (TEST_TYPE=='KS test')
    topGO.stat <- new('classicScore', testStatistic=GOKSTest, name=TEST_TYPE)
topGO.result <- getSigGroups(myGOdata, topGO.stat)

## summarize the top 100 GO terms and write to the file
topGO.table = GenTable(myGOdata, topGO.result, topNodes=40)
colnames(topGO.table) = c('GO ID','GO Term','Annotated Genes','Significant Genes','Expected','P-value')

write.table(file=TOPGO_OUT, topGO.table, sep='\t', quote=FALSE, row.names=FALSE, col.names=FALSE)
}, error=function(cond) {}, warning=function (cond) {}, finally={})
#################################
## End topGO
#################################

##
## write full result file
result=result[result[,"P-value"]<0.05,]
colnames(result)[1]=""
write.table(result[,-c(4,7)], 
            file=RESULT_OUT, quote=FALSE, sep="\t", row.names=FALSE, col.names=FALSE)

##
## Define Significant gene expression
## NOTE:  filtering is to be done at the user level rather than R analysis level
## q.thresh=ALPHA
## logfc.thresh=BETA

##
## write Significant gene set
## result.sig=result[result[,"q-value"]<q.thresh && abs(result[,"Log Fold Change"])>logfc.thresh,]
## write.table(result[,-c(4,7)], 
##            file=SIGGENE_OUT, quote=FALSE, sep="\t", row.names=FALSE, col.names=FALSE)