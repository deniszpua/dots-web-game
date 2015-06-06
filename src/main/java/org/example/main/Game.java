package org.example.main;

import org.example.main.figurants.GameObserver;
import org.example.main.figurants.Player;
import org.example.model.GameGrid;
import org.example.model.PlayerType;
import org.example.net.messages.GameViewUpdate;
import org.example.net.messages.MessageFromPlayer;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by development on 04.05.15.
 */
public class Game implements Launcher {

  private Player[] players;
  private Set<GameObserver> watchers;

  private GameGrid gameGrid;
  String currentPlayer;
  String redPlayerNickname;
  String bluePlayerNickname;


  @Override
  public void init() {
    redPlayerNickname = players[0].getNickname();
    bluePlayerNickname = players[1].getNickname();
    currentPlayer = redPlayerNickname;
    Logger.getGlobal().info("Red player nickname is " + redPlayerNickname
        + ". Blue player nickname is " + bluePlayerNickname + ".\n");
  }

  @Override
  public void startGame() {

    Logger.getGlobal().info("Starting new game....");

    //send initial BoardPosition to watchers
    broadCastView("Game started", true);
    Logger.getGlobal().info("Initial gameboard sent");

    //Allow first player to move
    sendMoveRequest(currentPlayer);


  }

  private void sendMoveRequest(String playerNickname) {
    GameViewUpdate data = new GameViewUpdate();
    data.setMoveAllowed(true);
    for (Player player : players) {
      if (player.getNickname().equals(playerNickname)) {
        player.receiveNewGameState(data);
      }
    }
  }

  void switchCurrentPlayer() {
    currentPlayer = (currentPlayer.equals(redPlayerNickname)) 
        ? bluePlayerNickname : redPlayerNickname;
  }


  private GameViewUpdate getCurrentGameDataSnapshot() {

    GameViewUpdate data = new GameViewUpdate();
    data.setRedDots(gameGrid.getDots(PlayerType.RED_PLAYER));
    data.setBlueDots(gameGrid.getDots(PlayerType.BLUE_PLAYER));
    data.setRedCircuits(gameGrid.getCircuits(PlayerType.RED_PLAYER));
    data.setBlueCircuits(gameGrid.getCircuits(PlayerType.BLUE_PLAYER));
    data.setMoveAllowed(false);
    data.setGameInProgress(true);

    return data;
  }

  @Override
  public void setPlayerMessagesPublishers(Player[] players) {
    assert (players.length == 2);
    this.players = players;

  }

  @Override
  public void setGameObservers(Set<GameObserver> watchers) {
    this.watchers = watchers;

  }

  @Override
  public void addGameBoard(GameGrid gameGrid) {
    assert (gameGrid != null);

    this.gameGrid = gameGrid;
  }

  @Override
  public void moveReceived(MessageFromPlayer messageObject, String nickname) {

    if (messageObject.isGameTerminated()) {
      broadCastView(String.format("Game terminated by %s!\n", nickname),
              false);
      return;
    }

    //if game not terminated by the player, check if it was senders turn
    //(client may try to cheat by perform moves without waiting for permission)
    if (nickname.equals(currentPlayer)) {
      // add move to game grid, process it and send updated view to watchers
      Logger.getGlobal().info(
          String.format("Adding move at point %d to board", 
              messageObject.getCellNumber())
      );
      gameGrid.addDot(getPlayerIndex(currentPlayer), messageObject.getCellNumber());
      broadCastView(
          String.format("Player %s performed moved at %d", nickname, 
              messageObject.getCellNumber()), true
      );
      // if game not finished - switch player and send invitation to move
      switchCurrentPlayer();
      sendMoveRequest(currentPlayer);
    } else {
      Logger.getGlobal().info(
            String.format("Message was recieved from %s, but current player is %s%n",
                 nickname, currentPlayer)
      );
    }

  }

  
  private int getPlayerIndex(String currentPlayer) {
    return (currentPlayer.equals(redPlayerNickname))
        ? PlayerType.RED_PLAYER : PlayerType.BLUE_PLAYER;
  }

  private void broadCastView(String infoMessage, boolean gameInProgress) {
    GameViewUpdate data = getCurrentGameDataSnapshot();
    data.setGameInProgress(false);
    data.setInfoMessage(infoMessage);
    for (GameObserver watcher : watchers) {
      watcher.receiveNewGameState(data);
    }
  }

  @Override
  public void connectionTerminated(String message, String nickname) {
    String infoMessage = nickname 
        + " connection have terminated. " + message;
    GameViewUpdate gameTerminatedMessage = new GameViewUpdate();
    gameTerminatedMessage.setInfoMessage(infoMessage);
    gameTerminatedMessage.setGameInProgress(false);
    for (Player player : players) {
      if (player.getNickname().equals(nickname)) {
        player.receiveNewGameState(gameTerminatedMessage);
      }
    }
    for (GameObserver observer : watchers) {
      observer.receiveNewGameState(gameTerminatedMessage);
    }

  }
}
