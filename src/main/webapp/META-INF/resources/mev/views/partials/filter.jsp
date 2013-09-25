<div class="page-header">

	<h1>Annotations Filter</h1>

</div>

<div class="row">
	<div class="span4">

		<ul class="thumbnails">
			<li class="span3" ng-repeat="field in fieldFilters">
				<div class="row">
					<p>{{field.variable}}</p><i class="icon-remove" ng-click="remFilter(field)"></i>
					<p>{{field.value}}</p>
					<p>{{field.operator}}</p>
					
				</div>
			</li>
			<li>
				<button class="btn btn-small btn-danger" ng-click="remAll()">Clear</button>
				<button class="btn btn-small btn-primary" ng-click="reqQuery(1)">Filter</button>
			</li>
		</ul>
		
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
	              		{{cell}}
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
