<div class="page-header">
	    <h2>
          Analyze Data 
	      <small>
			View existing data, check on the status of analysis, or upload new data.
	      </small>
	    </h2>
</div>

<div class="container">
	<div class="row">
		<div class="span6">
			<div id="fileup" ng-controller="UploadCtrl">
				<input id="file" type="file" name="upload" />
				<button ng-click="sendFile(0)">Submit Files</button>
				<div id="progressbar">
					<div id="bar"></div>
					<div id="percent"></div>
				</div>
				<div id="output">{{output}}</div>
			</div> 
		</div>
		<!--End First Column -->
		<div class="span6">
			<div class="row">
				<p class="lead">Visualizations</p>
				<table class="table table-striped table-bordered">
					<thead>
						<th>Name</th>
						<th>Type</th>
						<th>Status</th>
						<th>Date</th>
						<th>Options</th>
					</thead>
					<tbody>
						<tr ng-repeat="visualization in visualizationdata">
						  <td>{{visualization.name}}</td>
						  <td>{{visualization.type}}</td>
						  <td><i class={{visualization.status_icon}}></i> {{visualization.status}}</td>
						  <td>{{visualization.date}}</td>
						  <td><a href={{visualization.options_link}}><i class={{visualization.options_icon}}></i></a></td>
						</tr>
					</tbody>
				</table>
			</div>
			<!-- End Visualization Row -->
			<div class="row">
				<p class="lead">Data</p>
				<table class="table table-striped table-bordered">
					<thead>
						<th>Name</th>
						<th>Type</th>
						<th>Size</th>
						<th>Status</th>
						<th>Date</th>
						<th>Options</th>
					</thead>
					<tbody>
						<tr ng-repeat="upload in uploaddata">
						  <td>{{upload.name}}</td>
						  <td>{{upload.type}}</td>
						  <td>{{upload.size}}</td>
						  <td><i class={{upload.status_icon}}></i> {{upload.status}}</td>
						  <td>{{upload.date}}</td>
						  <td><a href={{upload.options_link}}><i class={{upload.options_icon}}></i></a></td>
						</tr>
					</tbody>
				</table>
			</div>
			<!-- End Data Row -->
		</div>
		<!--End Second Column -->
	</div>
	<!--End Outer Row -->
</div>
<!--End Container -->
