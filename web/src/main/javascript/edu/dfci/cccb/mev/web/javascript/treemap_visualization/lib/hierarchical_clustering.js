(function(){

    define([],function(){

        return function(graph, comparator, linkageStrategy, mergingNodeEdgeSelector, randomId, debug){

            var nodeRoot = {};

            graph.nodes.map(function(node){
                nodeRoot[node.id.toString()] = node;
            });

            var edgesList = Object.create(graph.edges);

            while (Object.keys(nodeRoot).length > 1){
            //for (var i = 0; i < 1; i++) {  //Here for small run checking

                (debug) ? console.log("--------------------------") : null

                //find greatest distance edge
                var mergingNodes = mergingNodeEdgeSelector(edgesList, comparator);

                if (debug) {
                    console.log("Merging: " + mergingNodes)
                    console.log(nodeRoot[mergingNodes[0]])
                    console.log(nodeRoot[mergingNodes[1]])
                }

                var edgesLinkedToMerging = edgesList.filter(function(edge){

                    if (debug) {
                        console.log("Checking edge: ")
                        console.log(edge)
                        console.log('Is edge index in merging nodes? Source: ' + mergingNodes.indexOf(edge.source.toString()) )
                        console.log('Is edge index in merging nodes? Target: ' + mergingNodes.indexOf(edge.target.toString()) )
                    }

                    //if edge is collapsing edge do nothing
                    if ((mergingNodes.indexOf(edge.source.toString()) >= 0) &&
                        (mergingNodes.indexOf(edge.target.toString()) >= 0) ) {
                        (debug) ? console.log("Collapsing Edge! Not adding edge") : null
                        return false
                    }
                    //if edge is linked to a merging edge, add
                    if ((mergingNodes.indexOf(edge.source.toString()) >= 0) ||
                        (mergingNodes.indexOf(edge.target.toString()) >= 0) ) {

                        (debug) ? console.log("Adding edge") : null;
                        return true
                    }

                    //else false
                    (debug) ? console.log("Not adding edge") : null
                    return false
                });

                if (debug) {
                    console.log("Edges Linked to merging -->")
                    console.log(edgesLinkedToMerging)
                }

                //build new edge table using tournament
                var table = {};
                edgesLinkedToMerging.map(function(linkingEdge){

                    //get connecting node id
                    var linkingTo =
                        (mergingNodes.indexOf(linkingEdge.source.toString()) < 0) ?
                            linkingEdge.source.toString() : linkingEdge.target.toString();

                    //if first node encountered or current link is stronger
                    //	apply binary linkage strategy
                    if (table[linkingTo] === undefined) {
                        table[linkingTo] = parseFloat(linkingEdge.value)
                    } else if (comparator(linkingEdge.value, table[linkingTo]) ) {
                            table[linkingTo] = linkageStrategy(parseFloat(linkingEdge.value), parseFloat(table[linkingTo]) );

                    };

                });


                if (debug) {
                    console.log("Linkage Table: ")
                    console.log(table)
                }

                //make new node

                var newNode = {
                    id:randomId(),
                    children: mergingNodes.map(function(index){
                        return Object.create(nodeRoot[index])
                    })
                };

                //delete old nodes
                 mergingNodes.map(function(index){
                     delete nodeRoot[index]
                 });

                //add new node to nodeRoot
                nodeRoot[newNode.id] = newNode;

                //delete edges that link to old node
                edgesList = edgesList.filter(function(edge){

                    //if edge is collapsing edge remove
                    if (mergingNodes.indexOf(edge.source.toString()) >= 0 &&
                        mergingNodes.indexOf(edge.target.toString()) >= 0 ) {
                        return false
                    }
                    //if edge is linked to a merging edge, remove
                    if (mergingNodes.indexOf(edge.source.toString()) >= 0 ||
                        mergingNodes.indexOf(edge.target.toString()) >= 0 ) {
                        return false
                    }

                    //else keep
                    return true
                });
                //make new edges from table and add to edgesList
                Object.keys(table).map(function(linkTo){
                    edgesList.push({
                        source:linkTo,
                        target:newNode.id,
                        value:table[linkTo]
                    })
                });

            }; // End loop

            //Return nodeRoot
            return nodeRoot[Object.keys(nodeRoot)[0]]


        }; // End function



    });

})();
