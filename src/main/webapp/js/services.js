'use strict';

/* Services */

dotsGame.factory('WebSocketConnection', ['$rootScope', function($rootScope) {
    var ws = new WebSocket('ws://' + window.location.host + '/game/msg');
    //interface methods: send(stringData), close()

    //event listeners
    ws.onopen = function(event){
        if(event.data === undefined)
            return;

        //maybe display some connection status (later)
    };

    ws.onmessage = function(message){
        $rootScope.$broadcast('gameDataChanged', message);
    };

    ws.onclose = function(event){
        //display connection status
    };

    ws.sendMove = function(cellNumber) {
        var message;
        message = '{"lastMove":' + cellNumber + ' }';
        ws.send(message);
    }

    return ws;

}]);