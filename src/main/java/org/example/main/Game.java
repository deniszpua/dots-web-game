package org.example.main;

import org.codehaus.jackson.map.ObjectMapper;
import org.example.model.GameGrid;
import org.example.model.Player;
import org.example.net.GameConnection;
import org.example.net.messages.GameViewUpdate;
import org.example.net.messages.MessageFromPlayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by development on 04.05.15.
 */
public class Game implements Launcher {

    public static final String MOVES_FIRST = "Player 0";
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

        Logger.getGlobal().info("Starting new game....");

        //send initial BoardPosition to watchers
        //TODO extract separate publisher service that will send messages and handle all connection stuff
        broadCastView("Game started", true);
        Logger.getGlobal().info("Initial gameboard sent");

        //Allow first player to move

        sendMoveRequest(MOVES_FIRST);


    }

    private void sendMoveRequest(String playerNickname) {
        boolean sent = false;
        try {
            for (GameConnection player : watchers) {
                if (!sent) {
                    Logger.getGlobal().info("Sending move request to " + player.getNickname());
                    player.sendMessage("{\"moveAllowed\":true}");
                    sent = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toJsonString(GameViewUpdate data) throws IOException {
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
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

        //Subscribe to player's messages
        for (GameConnection publisher : players.values()) {
            publisher.addListener(this);
        }
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
    public void moveReceived(String messageString, String nickname) {

        //TODO
        // parse json string to message object
        MessageFromPlayer messageObject = null;
        try {
            messageObject = mapper.readValue(messageString, MessageFromPlayer.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if ((messageObject != null) && messageObject.isGameTerminated()) {
            broadCastView(String.format("Game terminated by %s!\n", nickname),
                    false);
            return;
        }

        //if game not terminated by the player, check if it was senders turn
        //(client could be cheated to perform moves without waiting for permission)
        if (playerRoles.indexOf(nickname) == currentPlayer) {
            // add move to game grid, process it and send updated view to watchers
            gameGrid.addDot(currentPlayer, messageObject.getNewDotPosition());
            broadCastView(
                    String.format("Player %s performed moved at %d", nickname, messageObject.getNewDotPosition()),
                    true
            );
            // if game not finished - switch player and send invitation to move
            switchCurrentPlayer();
            sendMoveRequest(playerRoles.get(currentPlayer));

        }



    }

    private void broadCastView(String infoMessage, boolean gameInProgress) {
        Logger.getGlobal().info("Preparing initial game view");
        GameViewUpdate data = getCurrentGameDataSnapshot();
        data.setGameInProgress(false);
        data.setInfoMessage(infoMessage);

        Logger.getGlobal().info("Sendining initial board position:\n" + data);
        for (GameConnection watcher : watchers) {
            try {
                watcher.sendMessage(toJsonString(data));
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    @Override
    public void connectionTerminated(String message, String nickname) {

        //TODO
        //remove  player from watcher list and send updated game view to watchers

    }
}
