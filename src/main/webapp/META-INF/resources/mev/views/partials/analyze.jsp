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
			<div id="fileup">
				<input id="file" type="file" name="upload" />
				<input type="text" ng-model="uploadName" />
				<div class="row">
					<button ng-click="sendFile(0)">Submit Files</button>
					<div id="progressbar">
						<div id="bar"></div>
						<div id="percent"></div>
					</div>
				</div>
				<div id="output"></div>
			</div> 
		</div>
		<!--End First Column -->
		<div class="span6">
			<div class="row">
				<p class="lead">Visualizations</p>
				<table class="table table-striped table-bordered">
					<thead>
						<th>Name</th>
						<th>Options</th>
					</thead>
					<tbody>
						<tr ng-repeat="visualization in heatmaplist">
						  <td>{{visualization}}</td>
						  <td><a href="#/heatmap/{{visualization}}"><i class="icon-play"></i></a></td>
						</tr>
					</tbody>
				</table>
			</div>
			<!-- End Visualization Row -->			<!-- End Data Row -->
		</div>
		<!--End Second Column -->
	</div>
	<!--End Outer Row -->
</div>
<!--End Container -->
