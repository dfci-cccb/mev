<form role="form">

  <div class="row">
  
        <div class='col-md-12'>
			  <div class="form-group">
			    <label for="limmaAnalysisName" class="control-label">Name</label>
			        <input id="limmaAnalysisName" ng-model="params.name" placeholder="Ex: My_Analysis_1">
			  </div>
			
			  <!--
			  <div class="form-group">
			    <label for="limmaAnalysisDimension" class="control-label">Dimension</label>
			
			      <select id="limmaAnalysisDimension" ng-model="params.dimension" ng-options="dimension.name for dimension in dimensions">
			      </select>
			
			  </div>
			  -->
			
			  <div class="form-group">
			    <label for="limmaAnalysisControl" class="control-label">Control</label>
			
			      <select id="limmaAnalysisControl" ng-model="params.control" ng-options="selection.name for selection in dataset.column.selections">
			      </select>
			
			  </div>
			  
			  <div class="form-group">
			    <label for="limmaAnalysisExperiment" class="control-label">Experiment</label>
			      <select id="limmaAnalysisExperiment" ng-model="params.experiment" ng-options="selection.name for selection in dataset.column.selections">
			      </select>
			      
			  </div>
			  
			  <button class="btn btn-success btn-block" ng-click="testInit(false)" data-dismiss="modal" aria-hidden="true">Analyze</button>
	  
	  <hr>
	  
	  
			  <div class="form-group">
			    <label for="limmaAnalysisSpecies" class="control-label">Species</label>
			      <select id="limmaAnalysisSpecies" ng-model="params.species" ng-options="selection.name for selection in available.species">
			      </select>
			  </div>
			  
			  <div class="form-group">
			    <label for="limmaAnalysisGoType" class="control-label">Go Type</label>
			      <select id="limmaAnalysisGoType" ng-model="params.goType" ng-options="selection.name for selection in available.goType">
			      </select>
			  </div>
			  
			  <div class="form-group">
			    <label for="limmaAnalysisTestType" class="control-label">Test Type</label>
			      <select id="limmaAnalysisGoType" ng-model="params.testType" ng-options="selection.name for selection in available.testType">
			      </select>
			  </div>
			  
			  <button class="btn btn-success btn-block" ng-click="testInit(true)" data-dismiss="modal" aria-hidden="true">Top Go Analyze</button>
	  </div>
	  
	  
  </div>
  
</form>


    