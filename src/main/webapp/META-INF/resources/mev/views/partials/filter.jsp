<div class="page-header">

	<h1>Annotations Filter</h1>

</div>

<div class="row">

	<div class="btn-group">
		<button class="btn" ng-click="changeDimension('row')">Rows</button>
		<button class="btn" ng-click="changeDimension('column')">Columns</button>
	</div>

</div>

<div class="row">
	<div class="span3">

		<ul class="thumbnails">
			<li class="span3" ng-repeat="field in fieldFilters">
			
				<div class="alert alert-info">
				
					<a class="pull-right">
						<i class="icon-remove" ng-click="remFilter(field)"></i>
					</a>
					
					<h4 class="media-heading">{{field.variable}}</h4>
					<hr>
					<p>Query: {{field.value}}</p>
					<p>Operator: {{field.operator}}</p>
				
				</div>
				
			</li>
			
		</ul>
		
		<button class="btn btn-block btn-primary" ng-click="reqQuery(1)">Filter</button>
		<button class="btn btn-block btn-danger" ng-click="remAll()">Clear</button>
		
	</div>
	
	<!-- Data Table -->
	<div class="span8">
	    <table class='table table-hover table-bordered'>
	    	<thead>
		        <tr>
		          <th ng-repeat="header in headers">{{header}}<i class="icon-plus-sign" data-target="#myModal" data-toggle="modal" ng-click="selectfilter( header )"></i></th>
		        </tr>
	    	</thead>
	    	<tbody>
	        	<tr ng-repeat="row in tuples">
	            	<td ng-repeat="cell in row">
	              		{{cell.value}}
	            	</td>
	    		</tr>
	    	</tbody>
	    </table>
	</div>
	
</div>

<!-- Modal -->
<div id="myModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">

  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true"><i class="icon-remove"></i></button>
    <h3 id="myModalLabel">Add New Filter</h3>
  </div>
  
  <div class="modal-body">
	<p>Select Filter Term</p>
		<input ng-model='modalinput.value' />
	<p>Select Operator Term</p>
		<input ng-model='modalinput.operator' />

  </div>
  
  <div class="modal-footer">
    <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
    <button class="btn btn-primary"  data-dismiss="modal" aria-hidden="true" ng-click="addFilter( modalinput )">Save changes</button>
  </div>
  
</div>
