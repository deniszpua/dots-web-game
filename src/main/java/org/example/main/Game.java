package org.example.main;

import org.codehaus.jackson.map.ObjectMapper;
import org.example.model.GameGrid;
import org.example.model.Player;
import org.example.net.GameConnection;
import org.example.net.messages.GameViewUpdate;
import org.example.net.messages.MessageFromPlayer;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by development on 04.05.15.
 */
public class Game implements Launcher {

    private Map<String, GameConnection> players;
    private Set<GameConnection> watchers;

    private GameGrid gameGrid;
    String currentPlayer;
    String redPlayerNickname;
    String bluePlayerNickname;

    //Helper jaxon object
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() {
        List<String> nicknames = new ArrayList<>(players.keySet());
        assert (nicknames.size() == 2);
        redPlayerNickname = nicknames.get(0);
        bluePlayerNickname = nicknames.get(1);
        currentPlayer = redPlayerNickname;
        Logger.getGlobal().info("Red player nickname is " + redPlayerNickname
        + ". Blue player nickname is " + bluePlayerNickname + ".\n");
    }

    @Override
    public void startGame() {

        Logger.getGlobal().info("Starting new game....");

        //send initial BoardPosition to watchers
        //TODO extract separate publisher service that will send messages and handle all connection stuff
        broadCastView("Game started", true);
        Logger.getGlobal().info("Initial gameboard sent");

        //Allow first player to move

        sendMoveRequest(currentPlayer);


    }

    private void sendMoveRequest(String playerNickname) {
        try {
            for (GameConnection player : watchers) {
                if (player.getNickname().equals(playerNickname)) {
                    player.sendMessage("{\"moveAllowed\":true}");
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
        currentPlayer = (currentPlayer.equals(redPlayerNickname)) ?
                bluePlayerNickname : redPlayerNickname;
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
    public void moveReceived(String messageString, String nickname) {

        // parse json string to message object
        Logger.getGlobal().info(
                String.format("Message %s received from %s\n", messageString, nickname)
        );
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
        if (nickname.equals(currentPlayer)) {
            // add move to game grid, process it and send updated view to watchers
            Logger.getGlobal().info(
                    String.format("Adding move at point %d to board",
                            messageObject.getCellNumber())
            );
            gameGrid.addDot(getPlayerIndex(currentPlayer), messageObject.getCellNumber());
            broadCastView(
                    String.format("Player %s performed moved at %d", nickname, messageObject.getCellNumber()),
                    true
            );
            // if game not finished - switch player and send invitation to move
            switchCurrentPlayer();
            sendMoveRequest(currentPlayer);
        }
        else {
            Logger.getGlobal().info(
                    String.format("Message was recieved from %s, but current player is %s%n",
                            nickname, currentPlayer)
            );
        }

    }

    private int getPlayerIndex(String currentPlayer) {
        return (currentPlayer.equals(redPlayerNickname))?
                Player.RED_PLAYER : Player.BLUE_PLAYER;
    }

    private void broadCastView(String infoMessage, boolean gameInProgress) {
        GameViewUpdate data = getCurrentGameDataSnapshot();
        data.setGameInProgress(false);
        data.setInfoMessage(infoMessage);

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
