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
				
				<div class="span3">
				
					<div class="row">
						<input id="file" type="file" name="upload" />
					</div>
					
					<div class="row">
						<div class="progress progress-striped active" id="progbox" style="visibility: hidden;">
							<div class="bar" id="progbar"></div>
							
						</div>
					</div>
					
					<div class="row">
						<button ng-click="sendFile(0)">Submit Files</button>
						<div id="output"></div>
					</div>
					
				</div>
				
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
						<tr id="{{visualization}}" ng-repeat="visualization in heatmaplist">
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
