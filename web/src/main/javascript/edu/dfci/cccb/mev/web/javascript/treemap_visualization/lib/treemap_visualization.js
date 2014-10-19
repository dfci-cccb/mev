(function(){define(['angular', 'd3'], function (angular, d3) {


    function visualization(rootNode, graph, noise, params, behaviors){

        var self = this;

        if (!rootNode || !graph || !self.domSelection || !noise || !params){
            throw new TypeError("Dependencies not met for visualization drawing. " +
                                "Root Node: " + typeOf(rootNode) + ", " +
                                "Graph: " + typeOf(graph) + ", " +
                                "DOM Selection: " + typeOf(self.domSelection) + ", " +
                                "Noise Function: " + typeOf(noise) + ", " +
                                "Params: " + typeOf(params) + "."
                               )
        }

        var margin, width, height;

        console.log(params)
        if(params.margin && params.width && params.height){

            margin = params.margin;
            width = params.width - margin.left - margin.right;
            height = params.height - margin.top - margin.bottom;
        } else {
            margin = {top: 40, right: 10, bottom: 10, left: 10};
            width = 960 - margin.left - margin.right;
            height = 500 - margin.top - margin.bottom;
        }



        var color = d3.scale.category20()
            .domain([0,1,2,3,4,5,6,7,8,9, 10,11,12,13,14,15,16,17,18,19]);

        var treemap = d3.layout.treemap()
            .size([width, height])
            .mode( (!params.layoutMethods || params.layoutMethods == '') ? 'squarify' : params.layoutMethods)
            .sticky(true)
        .value(function(d) { return (!params.nodeAreaValue || params.nodeAreaValue == 'uniform')? 1 : d.size; });

        var treemapNodes = treemap.nodes(rootNode).filter(function(elem){return (elem.children)? false : true});

        self.domSelection.selectAll('*').remove()

        self.domSelection.append("svg")

        var svg = self.domSelection.select("svg")
            .attr('id','treemap-visualization')
            .style("position", "relative")
            .style("width", (width + margin.left + margin.right) + "px")
            .style("height", (height + margin.top + margin.bottom) + "px")

          var node = svg.selectAll("rect")
              .data(treemapNodes)
            .enter().append("rect")
              .attr("class", "node")
              .call(position)
              .style("fill", function(d) {
                  return (params.nodeColor) ?
                      params.nodeColor:'#eee';
              })

        function position() {
            this.attr("x", function(d) { return (!d.children) ? d.x + "px" : 0; })
            .attr("y", function(d) { return (!d.children) ? d.y + "px" : 0; })
            .attr("width", function(d) { return (!d.children) ? Math.max(0, d.dx - 1) + "px" : 0 ; })
            .attr("height", function(d) { return (!d.children) ? Math.max(0, d.dy - 1) + "px" : 0; });
        }

        treemapNodePositions = {};
        treemapCircles = treemapNodes.map(function(node){

            treemapNodePositions[node.id] =
                {
                    'id': node.id,
                    'x': node.x + (node.dx/2) + noise(node.dx, .25),
                    'y': node.y + (node.dy/2) + noise(node.dy, .25),
                    'group':node.group
                };

            return treemapNodePositions[node.id]
        })

        function edgePath(edge){
            //lookup source and target node positions
            var source = treemapNodePositions[edge.source],
            target = treemapNodePositions[edge.target]

            return "M " + source.x + " " + source.y +
            " L " + target.x + " " + target.y
        }

        function edgeColor(edge){


            if(!params.nodeGroupValue || !params.colorEdgeByGroup){
                return (params.edgeColor)? params.edgeColor : "blue"
            }

            var cols = d3.scale.category20().domain([0,1,2,3,4,5,6,7,8,9,10, 11,12,13,14,15,16,17,18,19])
            if (treemapNodePositions[edge.source].group ===
                treemapNodePositions[edge.target].group){

                return (treemapNodePositions[edge.source].group) ?
                    cols(Math.floor(treemapNodePositions[edge.source].group) % 20)
                        : 'blue';
            } else {
                return (params.offGroupEdgeColor) ? params.offGroupEdgeColor : 'white'
            }
        }

        svg.selectAll('circle').data(treemapCircles).enter()
        .append('circle')
        .attr({
            'cx':function(d){return d.x},
            'cy':function(d){return d.y},
            'r':4
        })
        .on('click', behaviors['onClick'])
        .on('mouseover', behaviors['onMouseOver'](svg))


        svg.selectAll('path').data(graph.edges).enter()
        .append('path')
        .attr({
            'd':edgePath,
            'stroke':edgeColor
        })
    }

    return function(domSelection){
        this.draw = visualization,
        this.domSelection = domSelection,
        this.erase = function(){this.domSelection.selectAll('*').remove()}
    }



})})();
