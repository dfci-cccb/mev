define(['./cellFilter'], function(cellFilter){
	
	//drawCells !View, !shownCells, !scales, Array [String], -> null
	//	draws cells on heatmapvisualization object
	return function(labels, ds){
		var self = this;
		

		var labelPairs = [];
		
		labels.row.map(function(row){
		    return labels.column.map(function(col){
		        labelPairs.push([row, col]);
		    })
		});
		
		self.shownCells = labelPairs.map(function(pair){ return ds.expression.get(pair)});
		
		
		
		var newCells = self.DOM.heatmapCells.selectAll('rect').data(self.shownCells, function(k){
			return [k.row, k.column]
		})
		
		newCells.enter().append('rect')
			.attr({
				x : function(d){ return self.scales.cells.xScale(d.column) },
				y : function(d){ return self.scales.cells.yScale(d.row) },
				height: self.params.cell.height - self.params.cell.padding,
				width: self.params.cell.width - self.params.cell.padding,
				fill: function(d){return self.scales.cells.colorScale(d.value) }
				
			})
		
		newCells.exit().remove()
		
		var rowLabels = self.DOM.labels.row.selectAll('text').data(labels.row, function(k){return k}),
		colLabels = self.DOM.labels.column.selectAll('text').data(labels.column, function(k){return k});
			
		rowLabels.enter().append('text')
            .attr({
            	x: self.params.panel.side.width
              	+ ( self.view.labels.column.keys.length * self.params.cell.width)
              	+ self.params.selections.row.width,
            	y: function(d){return self.scales.cells.yScale(d) + self.params.cell.height },
            	"text-anchor": "bottom"
            })
            .text(function(d){
            	return d
            })
            .append('title').text(function(d){ return d });
		
		colLabels.enter().append('text')
		    .attr('transform', 'rotate(-90)')
            .attr({
                x: - (self.params.panel.top.height
                + self.params.labels.column.height),
                y: function(d){return  self.scales.cells.xScale(d) + .68*self.params.cell.width },
                "text-anchor": "start"
            })
            
            .text(function(d){
                return d.slice(0, 13);
            })
            .append('title').text(function(d){ return d });
        
        rowLabels.exit().remove();
		
		colLabels.exit().remove();
	}
})