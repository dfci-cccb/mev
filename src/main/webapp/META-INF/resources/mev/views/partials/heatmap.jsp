<div class="container">
	<div class="row">
		
		<div class="span7">
		    <vis-Heatmap 
				inputdata="transformeddata"
				inputcolor="red"
				showlabels="true"
				width="700"
				height="1000"
				marginleft="80"
				marginright="80"
				margintop="200"
				marginbottom="120"
				celllink="selectedcells"
				pushtomarked="markRow(inputindecies, inputdimension)">
			</vis-Heatmap>
		</div>
		
		<!--End Visualization Column -->
		<div class="span3 offset1">
			
		    <div class="row">
		    	<p>
					<button class="btn btn-mini" type="button" ng-click="pageLeft()"><i class="icon-chevron-left"></i></button>
					<button class="btn btn-mini" type="button" ng-click="pageRight()"><i class="icon-chevron-right"></i></button>
				</p>
		    </div>
		    <div class="row">
		    	<p>
					<button class="btn btn-mini" type="button" ng-click="pageUp()"><i class="icon-chevron-up"></i></button>
					<button class="btn btn-mini" type="button" ng-click="pageDown()"><i class="icon-chevron-down"></i></button>
				</p>
		    </div>
		    <div class="row" >
		 	   <vis-Ranged-Bar 
					inputdata="selectedcells"
				</vis-Ranged-Bar>
		    </div>
			
			<div class="accordion" id="accordion2">
			
				<div class="accordion-group">
					<div class="accordion-heading">
						<a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseOne">
							Annotations
						</a>
					</div>
					<div id="collapseOne" class="accordion-body collapse">
						<div class="accordion-inner">
						
							<input id="file" type="file" multiple name="upload" />

							<div class="progress progress-striped active" id="progbox" style="visibility: hidden;">
								<div class="bar" id="progbar"></div>
								
							</div>
						
							<button class="btn btn-block btn-primary" ng-click="sendRowFile()">Submit Row Annotations</button>
							<button class="btn btn-block btn-primary" ng-click="sendColFile()">Submit Column Annotations</button>
							
							<div id="rowoutput"></div>
							<div id="coloutput"></div>
						</div>
					</div>
				</div>
				
				<div class="accordion-group">
					<div class="accordion-heading">
						<a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseTwo">
							Selections
						</a>
					</div>
					<div id="collapseTwo" class="accordion-body collapse">
						<div class="accordion-inner">
						
							<p>Your Row Selections:</p>
							
							<p ng-repeat="selection in selections['row']">{{selection}}</p>
							
							
							<p>Your Column Selections:</p>
							
							<p ng-repeat="selection in selections['column']">{{selection}}</p>
							
							<button class="btn btn-block btn-primary" data-target="#myRowModal" data-toggle="modal">Add Row Selection </button>
							<button class="btn btn-block btn-primary" data-target="#myColumnModal" data-toggle="modal">Add Column Selection </button>
						</div>
					</div>
				</div>
				
				<div class="accordion-group">
					<div class="accordion-heading">
						<a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseThree">
							Analysis
						</a>
					</div>
					<div id="collapseThree" class="accordion-body collapse">
						<div class="accordion-inner">
							
							<button class="btn btn-block btn-primary" data-target="#EuclideanClusteringModal" data-toggle="modal">Euclidean Cluster</button>
							<button class="btn btn-block btn-primary" data-target="#LIMMAModal" data-toggle="modal">Limma Analyze</button>
														
						</div>
					</div>
				</div>
				
			</div>
						
		</div>
		<!--End Column 2 -->
	</div>

</div>

<div id="myRowModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">

  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true"><i class="icon-remove"></i></button>
    <h3 id="myModalLabel">Add New Filter</h3>
  </div>
  
  <div class="modal-body">
  
		<p>Name:</p><input ng-model='selectionname' />

  </div>
  
  <div class="modal-footer">
    <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
    <button class="btn btn-primary"  data-dismiss="modal" aria-hidden="true" ng-click="pushSelections('row') ">Add Row Selection</button>
  </div>
  
</div>

<div id="myColumnModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">

  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true"><i class="icon-remove"></i></button>
    <h3 id="myModalLabel">Add New Filter</h3>
  </div>
  
  <div class="modal-body">
  
		<p>Name:</p><input ng-model='selectionname' />

  </div>
  
  <div class="modal-footer">
    <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
    <button class="btn btn-primary"  data-dismiss="modal" aria-hidden="true" ng-click="pushSelections('column')">Add Column Selection</button>
  </div>
</div>

<div id="EuclideanClusteringModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">

  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true"><i class="icon-remove"></i></button>
    <h3 id="myModalLabel">Clustering</h3>
  </div>
  
  <div class="modal-body">

		<select ng-model="ClusterType">
			<option ng-repeat="clustertype in ['Euclidian', 'Distance']" value="{{clustertype}}"> {{clustertype}} </option> 
		</select> <p> by </p>
		<select ng-model="ClusterDimension"> 
			<option ng-repeat="selection in ['column']" value="{{selection}}"> {{selection}} </option>
		</select>

  </div>
  
  <div class="modal-footer">
    <button class="btn btn-warning" data-dismiss="modal" aria-hidden="true">Cancel</button>
    <button class="btn btn-primary"  data-dismiss="modal" aria-hidden="true" ng-click="analyzeClustering()">Analyze</button>
  </div>
  
</div>

<div id="LIMMAModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">

  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true"><i class="icon-remove"></i></button>
    <h3 id="myModalLabel">Limma Analysis</h3>
  </div>
  
  <div class="modal-body">
  
		<select ng-model="LimmaSelection1"> 
							
			<option ng-repeat="selection in selections.row.concat(selections.column)" value="{{selection}}"> {{selection}} </option>
							
		</select>
		
		<select ng-model="LimmaSelection2"> 
							
			<option ng-repeat="selection in selections.row.concat(selections.column)" value="{{selection}}"> {{selection}} </option>
							
		</select>
		
		<select ng-model="LimmaOutputOption"> 
							
			<option ng-repeat="option in ['significant', 'full', 'rnk']" value="{{option}}"> {{option}} </option>
							
		</select>

  </div>
  
  <div class="modal-footer">
    <button class="btn btn-warning" data-dismiss="modal" aria-hidden="true">Cancel</button>
    <button class="btn btn-primary"  data-dismiss="modal" aria-hidden="true" ng-click="analyzeLimmaRequester()">Analyze</button>
  </div>
  
</div>
