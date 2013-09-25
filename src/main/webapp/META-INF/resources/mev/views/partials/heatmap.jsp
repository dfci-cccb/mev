<div class="container">
	<div class="row">
		
		<div class="span8">
		    <vis-Heatmap 
				inputdata="transformeddata"
				inputcolor="red"
				showlabels="true"
				width="800"
				height="800"
				marginleft="80"
				marginright="80"
				margintop="80"
				marginbottom="80"
				celllink="selectedcells"
				pushtomarked="markRow(inputindecies, inputdimension)">
			</vis-Heatmap>

		</div>
		
		<!--End Visualization Column -->
		<div class="span4">
			
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
		    	{{selectedcells}}
		    </div>
			
		</div>
		<!--End Column 2 -->
	</div>

</div>
