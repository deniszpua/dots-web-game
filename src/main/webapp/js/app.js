'use strict';

/* App module */

var dotsGame = angular.module('dotsGame', []);

/* Controller */
dotsGame.controller('GameController', ['$scope', '$http', 'WebSocketConnection', function ($scope, $http, ws) {

	//Game field setup
	$http.get('games/init_config.json').success(function(data) {
        // game field configuration
        $scope.cellSize = data.cellSize; //grid cell size in px
        $scope.gridWidth = data.gridDimensions.width; //number of columns
        $scope.gridHeight = data.gridDimensions.height; //number of rows
        $scope.redDots = []; //player's moves

        //Initializing grid
        var row = $scope.gridHeight-1;
        $scope.rows = [];
        while (row -- > 0) $scope.rows[row] = row;

        var col = $scope.gridWidth-1;
        $scope.columns = [];
        while (col-- > 0) $scope.columns[col] = col;

        $scope.gameFieldWidth = $scope.cellSize * ($scope.gridWidth - 1);
        $scope.gameFieldHeight = $scope.cellSize * ($scope.gridHeight - 1);

        //Pointers array init
        $scope.pointersArray = [];
        var idx = $scope.gridHeight * $scope.gridWidth;
        while (idx-- > 0) $scope.pointersArray[idx] = idx;

        $scope.moveAllowed = false;
        $scope.gameInProgress = false;
	});

    //Update game data after receiving it from webSocket
    $scope.$on('gameDataChanged', function(event, message) {
        updateGameView(angular.fromJson(message.data));
        console.log('View data changed: ' + message.data)

    });
    //Players move handler
    $scope.performMove = function(cellNumber) {

        if ($scope.moveAllowed) {
            $scope.moveAllowed = false;
            ws.sendMove(cellNumber);
            console.log('Performed move: ' + cellNumber);
        }
    }
	//Position dots on game field
	$scope.computeCellPos = function(cellNumber) {
		var coordinates = computeRowCol(cellNumber);
		return ("top: " +coordinates.row + "px; left: " + coordinates.col + "px;");
	}


	//Position circuit segments
	$scope.computeSegmentPos = function(segment) {
	    var from = computeRowCol(segment.from);
	    var to = computeRowCol(segment.to);
        //Always position to top vertex, or leftmost, if they placed in one row
	    if (from.row == to.row)
	        {if (from.col < to.col) {
	            return $scope.computeCellPos(segment.from);
	            }
	         else {
	            return $scope.computeCellPos(segment.to);
	         }
	        }
	     if (from.row < to.row) {
	            return $scope.computeCellPos(segment.from);
	        }
	     else {
	            return $scope.computeCellPos(segment.to);
	        }
	}

    //Detect circuit segment type
    $scope.getClass = function(segment) {
        var from = computeRowCol(segment.from);
        var to = computeRowCol(segment.to);

        if (from.row == to.row) {
            return 'horizontal'
        }
        else if (from.col == to.col) {
            return 'vertical'
        }

        if (from.row < to.row) {
            var top = from;
            var bottom = to;
        }
        else {
            var top = to;
            var bottom = from;
        }

        if (top.col < bottom.col) {
            return 'backslash';
        }
        return 'slash';
    }

    /* Helper functions */

	//Compute row and col from vertex number
	var computeRowCol = function(number) {
	    var result = {};
	    result.row = (number - number%$scope.gridWidth)*$scope.cellSize/$scope.gridWidth;
	    result.col = (number % $scope.gridWidth)*$scope.cellSize;
	    return result;
	}

    //Convert circuits array from edge enumeration to adjacent edges pairs
    var convertCircuitFormat = function(edgeEnumeration) {
        var adjacentVertices = [];
        var i = 0;
        while (++i < edgeEnumeration.length) {
            adjacentVertices.push(
                {from: edgeEnumeration[i - 1],
                 to: edgeEnumeration[i]
                 }
                );
        }
        adjacentVertices.push({
            from:edgeEnumeration[0],
            to: edgeEnumeration[edgeEnumeration.length - 1]
        });
        return adjacentVertices;
    }

    //Update view based on data received from server
    var updateGameView = function(data) {

        //update board only if appropriate data present
        if ($scope.redDots != null) {
            //player's dots and circuits
            $scope.redDots = data.redDots;
            $scope.blueDots = data.blueDots;
            $scope.redCircuits  = convertCircuitFormat(data.redCircuits);
            $scope.blueCircuits = convertCircuitFormat(data.blueCircuits);

            //delete pointers on positions, where dots are already placed
            var existingDots = $scope.blueDots.concat($scope.redDots);
            var freeNodes = [];
            for (var node of $scope.pointersArray) {
                if (existingDots.indexOf(node) == -1) {
                    freeNodes.push(node);
                }
            }
            $scope.pointersArray = freeNodes;
        }


        //set game flow flags
        $scope.gameInProgress = data.gameInProgress;
        $scope.moveAllowed = data.moveAllowed;
        $scope.$apply();
        console.log('View update finished');
    }


}]);
