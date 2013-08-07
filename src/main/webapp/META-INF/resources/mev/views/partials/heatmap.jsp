<div class="container">
	<div class="row">
		
		<div class="span8">
		    <vis-Heatmap 
				inputdata="transformeddata"
				inputcolor="0"
				pushtomarked="markRow(inputindecies, inputdimension)">
			</vis-Heatmap>

		</div>
		
		<!--End Visualization Column -->
		<div class="span4">
			
		    <div class="row">
		    	<p>
					<button class="btn btn-mini" type="button" onclick="pageLeft()"><i class="icon-chevron-left"></i></button>
					<button class="btn btn-mini" type="button" onclick="pageRight()">button class="btn btn-mini" type="button"><i class="icon-chevron-right"></i></button>
				</p>
		    </div>
		    <div class="row">
		    	<p>
					<button class="btn btn-mini" type="button" onclick="pageUp()"><i class="icon-chevron-up"></i></button>
					<button class="btn btn-mini" type="button" onclick="pageDown()"><i class="icon-chevron-down"></i></button>
				</p>
		    </div>
			
		</div>
		<!--End Column 2 -->
	</div>
</div>
