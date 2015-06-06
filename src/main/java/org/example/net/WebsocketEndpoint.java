package org.example.net;

import org.example.main.Launcher;
import org.example.main.LauncherBuilder;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint(value = "/msg")
public class WebsocketEndpoint implements GameConnection {


    private static final String GUEST_PREFIX = "Player";
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final List<WebsocketEndpoint> connections =
            new CopyOnWriteArrayList<>();
    private static final Set<Launcher> games = new CopyOnWriteArraySet<>();
    public static final String ERROR_ON_SOCKET_CONNECTION = "Error on socket %s connection.";
    public static final String PLAYER_HAS_DISCONNECTED = "Player %s has disconnected.";

    private final String nickname;
    private Session session;
    private MessagesListener listener;

    public WebsocketEndpoint() {
        nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
    }


    @OnOpen
    public void start(Session session) {
        this.session = session;
        connections.add(this);
        Logger logger = Logger.getGlobal();
        logger.info(String.format("%s connected", nickname));

        //detect when second player connected and launch new game
        if (connections.size() >= 2) {
        	boolean gameStarted = false;
        	LauncherBuilder builder = LauncherBuilder.getBuilder();
          synchronized (connections) {
            if (connections.size() >= 2) {
              //Start new game
              List<WebsocketEndpoint> players = connections.subList(0, 2);
              games.add(builder.startNewGame(players));
              connections.removeAll(players);
              gameStarted = true;
            }
            if (gameStarted) {
            	logger.info("Getting game builder...");
            	logger.info("Building new game...");
            	logger.info("New game started");
				
			}
		  }
      

        }
    }


    @OnClose
    public void end() {
        connections.remove(this);
        String message = String.format(PLAYER_HAS_DISCONNECTED,nickname);
        Logger.getGlobal().info(message);
        //notify listener, that player have closed connection
        listener.connectionTerminated(message, nickname);
    }


    @OnMessage
    public void incoming(String message) {
        Logger.getGlobal().info(String.format("Message %s recieved from %s\n", message, nickname));
        //notify listener
        listener.moveReceived(message, nickname);
    }

    @OnError
    public void onError(Throwable t) throws Throwable {
        Logger.getGlobal().severe(String.format(ERROR_ON_SOCKET_CONNECTION, nickname));
//        listener.connectionTerminated(String.format(ERROR_ON_SOCKET_CONNECTION, nickname), nickname);
//        connections.remove(this);
    }


    @Override
    public String getNickname() {
        return nickname;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public void sendMessage (String message) throws IOException{

        session.getBasicRemote().sendText(message);
        Logger.getGlobal().info("Message\n" + message + "\nhave been sent to" + nickname + "!");

    }

    @Override
    public void addListener(MessagesListener listener) {
        this.listener = listener;
    }

}