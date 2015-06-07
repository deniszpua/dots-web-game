package org.example.net;

import org.example.main.Launcher;
import org.example.main.WebsocketP2PGameBuilder;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/msg")
public class WebsocketEndpoint {

  public static final String GUEST_PREFIX = "Player ";
  public static final String ERROR_ON_SOCKET_CONNECTION = 
      "Error on socket %s connection.";
  public static final String PLAYER_HAS_DISCONNECTED = 
     "Player %s has disconnected.";
  
  private static final AtomicInteger connectionIds = new AtomicInteger(0);
  private static final List<WebsocketEndpoint> connections =
      new CopyOnWriteArrayList<>();
  private static final Object connectionsLock = new Object();
  private static final Set<Launcher> games = new CopyOnWriteArraySet<>();
  

  private final String nickname;
  private Session session;
  private WebsocketListener listener;

  public WebsocketEndpoint() {
    nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
  }


  @OnOpen
  public void start(Session session) {
    this.session = session;
    connections.add(this);
    Logger logger = Logger.getGlobal();
    logger.info(String.format("%s connected", nickname
    ));

    //detect when second player connected and launch new game
    if (connections.size() >= 2) {
      boolean gameStarted = false;
      WebsocketP2PGameBuilder builder = new WebsocketP2PGameBuilder();
      synchronized (connectionsLock) {
        if (connections.size() >= 2) {
          //Start new game
          List<WebsocketEndpoint> players = connections.subList(0, 2);
          games.add(builder.startNewGame(players));
          connections.removeAll(players);
          gameStarted = true;
        }
        if (gameStarted) {
          logger.info("New game started"); 
        }
      }
    }
  }


  @OnClose
  public void end() {
    //notify listener, that player have closed connection
    listener.connectionTerminated();
    Logger.getGlobal().info("Connection with " 
        + nickname + " terminated.\n"); //$NON-NLS-1$ //$NON-NLS-2$
  }


  @OnMessage
  public void incoming(String message) {
    //notify listener
    listener.messageReceived(message);
    Logger.getGlobal().info(String.format(
        "Message %s received from %s\n", message, nickname
    )); //$NON-NLS-1$
  }

  @OnError
  public void onError(Throwable throwable) throws Throwable {
    listener.errorOnConnection(throwable);
    Logger.getGlobal().severe(
        String.format(ERROR_ON_SOCKET_CONNECTION, nickname));
    throw new Throwable(throwable);
  }


  public Session getSession() {
    return session;
  }

  public void addListener(WebsocketListener listener) {
    this.listener = listener;
  }


  public String getNickname() {
    return nickname;
  }

}