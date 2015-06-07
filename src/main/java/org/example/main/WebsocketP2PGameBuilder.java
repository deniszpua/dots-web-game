package org.example.main;

import org.example.main.figurants.Player;
import org.example.main.figurants.WebsocketPlayer;
import org.example.model.Grid;
import org.example.net.WebsocketEndpoint;

import java.util.List;

public class WebsocketP2PGameBuilder {

    private static final int[] BOARD_DIMENSIONS= {11, 11};

    public Launcher startNewGame(List<WebsocketEndpoint> playerConnections) {

    	//Only two players accepted
    	assert(playerConnections.size() == 2);

    	//Create game instance
        Launcher launcher = new Game();

        //Populate launcher object
        Player[] players = getPlayersArray(playerConnections);
        //TODO extract board dimensions from context, that should be injected
        launcher.addGameBoard(
        		new Grid(BOARD_DIMENSIONS[0], BOARD_DIMENSIONS[1]));
        //add players and subscribe launcher on their messages
        launcher.setPlayerMessagesPublishers(players);
        for (Player player : players) {
            player.addListener(launcher);
        }

//        launcher.setGameWatchers();
        launcher.init();
        launcher.startGame();

        return launcher;
    }

    /**
     * Creates WebsocketPlayersArray and subscribes them to web-socket messages.
     * @param playerConnections - list of web-socket sessions
     * @return array consisting of two players
     */
	private Player[] getPlayersArray(List<WebsocketEndpoint> playerConnections) {
		Player[] players = new Player[2];
		int i = 0;
		for (WebsocketEndpoint ws : playerConnections) {
			players[i] = new WebsocketPlayer(ws);
			ws.addListener(players[i++]);
		}
		return players;
	}
}
