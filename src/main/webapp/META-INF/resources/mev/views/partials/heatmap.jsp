<div class="container">
	<div class="row">
		
		<div class="span8">
		    <vis-Heatmap 
				inputdata="heatmapdata"
				inputcolor="vizcolor"
				pushtomarked="storeRow(input)">
			</vis-Heatmap>

		</div>
		
		<!--End Visualization Column -->
		<div class="span4">
			
		    <div class="row">	
				<div class="btn-group">
					<button class="btn" ng-repeat='page in allpages' ng-click="requestPage(page.index)">{{page.title}}</button>
				</div>
		    </div>

		    <!-- End Heatmap Navigation -->
		    
		    <div class="row">
				<p class="lead">Options:</p>
			</div>
			
			<div class="row">
				<div class="btn-group">
					<button class="btn" ng-click="updateColor('red')">Red-Green</button>
					<button class="btn" ng-click="updateColor('blue')">Yellow-Blue</button>
				</div>
			</div>
			
			<!-- End Color Scheme Selection -->
			<div class="row">
				<p class="lead">Selected Rows:</p>
				<table class="table table-striped">

					<thead>
						<th>Symbol</th>
						<th>Options</th>
					</thead>

					<tbody>
						<tr ng-repeat="gene in markedRows">
							<td>{{gene}}</td>
							<td><button class="btn" ng-click="removeRow(gene)"><i class='icon-remove'></i></button></td>
						</tr>
					</tbody>
				</table>
				<button class="btn" ng-click="clearAllRows()">Clear All</button>
				<button class="btn" ng-click="">Save Selections</button>
			</div>
			
		</div>
		<!--End Column 2 -->
	</div>
</div>
