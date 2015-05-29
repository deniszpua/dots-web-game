package org.example.main;

import org.example.model.Grid;
import org.example.net.GameConnection;
import org.example.net.WebsocketEndpoint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by deniszpua on 29.05.15.
 */
public class LauncherBuilder {

    private static final int[] BOARD_DIMENSIONS= {11, 11};

    private static LauncherBuilder builder;

    private LauncherBuilder() {};

    public static LauncherBuilder getBuilder() {
        if (builder == null) {
            builder = new LauncherBuilder();
        }
        return builder;
    }

    public Launcher startNewGame(List<WebsocketEndpoint> playerConnections) {

        Game game = new Game();
        Launcher launcher = (Launcher) game;

        //Populate launcher object
        launcher.addGameBoard(new Grid(BOARD_DIMENSIONS[0], BOARD_DIMENSIONS[1]));

        assert (playerConnections.size() == 2);

        HashMap<String, GameConnection> players = new HashMap<>(2);
        for (WebsocketEndpoint connection : playerConnections) {
            GameConnection publisher = connection;
            String name = connection.getNickname();
            players.put(name, publisher);
        }
        launcher.setPlayerMessagesPublishers(players);

        launcher.setGameWatchers(new HashSet<GameConnection>(playerConnections));

        launcher.startGame();

        return launcher;
    }
}
