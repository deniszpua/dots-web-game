'use strict';

/* Services */

dotsGame.factory('WebSocketConnection', ['$rootScope', function($rootScope) {
    var ws = new WebSocket('ws://' + window.location.host + '/game/msg');
    //interface methods: send(stringData), close()

    //event listeners
    ws.onopen = function(event){
        console.log('    Websocket connection established');
        if(event.data === undefined)
            return;

        //maybe display some connection status (later)
    };

    ws.onmessage = function(message){
        console.log("Websocket connection received message from sever:", message);
        $rootScope.$broadcast('gameDataChanged', message);
    };

    ws.onclose = function(event){
        //display connection status
        console.log('   Websocket connection closed');
        ws.sendMove = null;
    };

    ws.sendMove = function(cellNumber) {
        var move = {};
        move.cellNumber = cellNumber;
        ws.send(angular.toJson(move));
    }

    return ws;

}]);