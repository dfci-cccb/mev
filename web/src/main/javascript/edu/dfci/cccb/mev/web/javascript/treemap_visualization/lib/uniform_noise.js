(function(){

    define([],function(){
        return function (range, strength){
            //create some noise for the points to help alleviate overlapping edges problem
            var neg = (Math.random() > .5) ? 1 : -1
            return strength * (range) * Math.random() * (neg)
        }
    })

})();
