package org.example.main;

import org.codehaus.jackson.map.ObjectMapper;
import org.example.model.GameGrid;
import org.example.model.Player;
import org.example.net.GameConnection;
import org.example.net.messages.GameViewUpdate;

import java.io.IOException;
import java.util.*;

/**
 * Created by development on 04.05.15.
 */
public class Game implements Launcher {

    public static final int MOVES_FIRST = Player.RED_PLAYER;
    private Map<String, GameConnection> players;
    private List<String> playerRoles;
    private Set<GameConnection> watchers;

    //gameData fields
    private GameGrid gameGrid;
    int currentPlayer;

    //Helper jaxon object
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void startGame() {

        //Subscribe to player's messages
        for (GameConnection publisher : players.values()) {
            publisher.addListener(this);
        }

        //Create bimap to find players role by their nicknames and vice versa
        playerRoles = new ArrayList<>(Math.max(Player.BLUE_PLAYER, Player.RED_PLAYER));
        int role = Player.RED_PLAYER;
        for (String nickname : players.keySet()) {
            playerRoles.add(role, nickname);
            role = Player.opponent(role);
        }

        //send initial BoardPosition to watchers
        //TODO extract separate publisher service that will send messages and handle all connection stuff
        GameViewUpdate data = getCurrentGameDataSnapshot();
        data.setInfoMessage("Game started");
        String message = null;
        try {
            message = toJsonString(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (GameConnection watcher : watchers) {
            try {
                watcher.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                watchers.remove(watcher);
            }
        }

        //Allow first player to move

        String redNickname = playerRoles.get(MOVES_FIRST);
        try {
            players.get(redNickname).sendMessage("{moveAllowed: true}");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private String toJsonString(GameViewUpdate data) throws IOException {
        return mapper.writeValueAsString(data);
    }

    void switchCurrentPlayer() {
        currentPlayer = Player.opponent(currentPlayer);
    }


    private GameViewUpdate getCurrentGameDataSnapshot() {

        GameViewUpdate data = new GameViewUpdate();
        data.setRedDots(gameGrid.getDots(Player.RED_PLAYER));
        data.setBlueDots(gameGrid.getDots(Player.BLUE_PLAYER));
        data.setRedCircuits(gameGrid.getCircuits(Player.RED_PLAYER));
        data.setBlueCircuits(gameGrid.getCircuits(Player.BLUE_PLAYER));
        data.setMoveAllowed(false);
        data.setGameInProgress(true);

        return data;
    }

    //helper method for unit test
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public void setPlayerMessagesPublishers(HashMap<String, GameConnection> players) {
        this.players = players;

    }

    @Override
    public void setGameWatchers(Set<GameConnection> watchers) {
        this.watchers = watchers;

    }

    @Override
    public void addGameBoard(GameGrid gameGrid) {
        assert (gameGrid != null);

        this.gameGrid = gameGrid;
    }

    @Override
    public void moveReceived(String message, String nickname) {

        // parse json string to message object

        //if game not terminated by the player, check if it was senders turn
        // (client could be cheated to perform moves without waiting for permission)

        // add move to game grid, process it and send updated view to watchers

        // if game not finished - switch player and send invitation to move


    }

    @Override
    public void connectionTerminated(String message, String nickname) {

        //remove  player from watcher list and send updated game view to watchers

    }
}
