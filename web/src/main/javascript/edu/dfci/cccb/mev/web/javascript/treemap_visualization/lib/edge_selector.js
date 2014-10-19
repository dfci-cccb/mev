(function(){



    define([], function(){

        return function (edges, comparator){
        //Function to find the link in edges
            var winner = undefined;
            for (edge in edges) {

                if (winner === undefined
                    || comparator(edges[edge].value, winner.value) ){

                    winner = edges[edge];
                }
            };
            return [winner.source.toString(), winner.target.toString()];

        };

    })

})();
