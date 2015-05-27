package org.example.net;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint(value = "/msg")
public class WebsocketEndpoint {


    private static final String GUEST_PREFIX = "Player";
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final Set<WebsocketEndpoint> connections =
            new CopyOnWriteArraySet<>();

    private final String nickname;
    private Session session;

    public WebsocketEndpoint() {
        nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
    }


    @OnOpen
    public void start(Session session) {
        this.session = session;
        connections.add(this);
        Logger logger = Logger.getGlobal();
        logger.log(Level.INFO, String.format("%s connected", nickname));
        if (connections.size() == 2) {
            logger.log(Level.INFO, "New game started");
            WebsocketEndpoint[] players = connections.toArray(new WebsocketEndpoint[0]);
            try {
                players[0].getSession().getBasicRemote().sendText("{" +
                        "\"gameInProgress\":true, \"moveAllowed\":true" +
                        "}");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Start new game
        }
    }


    @OnClose
    public void end() {
        connections.remove(this);
        String message = String.format("* %s %s",
                nickname, "has disconnected.");
        Logger.getGlobal().info(message);
    }


    @OnMessage
    public void incoming(String message) {
        Logger.getGlobal().info(String.format("Message %s recieved", message));
        broadcast("{" +
                "\"redDots\":[12, 22, 24, 26, 34], \"blueDots\":[15, 23, 25, 27, 36, 37], " +
                "\"redCircuits\":[12, 24, 34, 22], \"blueCircuits\":[15, 27, 37, 36, 25], " +
                "\"gameInProgress\":true, \"moveAllowed\":true"
                +"}");
        /**
         *$scope.redCircuits  = convertCircuitFormat(data.redCircuits);
         $scope.blueCircuits = convertCircuitFormat(data.blueCircuits);
         $scope.gameInProgress = data.gameInProgress;
         $scope.moveAllowed = data.moveAllowed;
         */
    }

    @OnError
    public void onError(Throwable t) throws Throwable {
        t.printStackTrace();
    }


    private static void broadcast(String msg) {
        for (WebsocketEndpoint client : connections) {
            try {
                synchronized (client) {
                    client.session.getBasicRemote().sendText(msg);
                }
            } catch (IOException e) {
                connections.remove(client);
                try {
                    client.session.close();
                } catch (IOException e1) {
                    // Ignore
                }
                String message = String.format("* %s %s",
                        client.getNickname(), "has been disconnected.");
                broadcast(message);
            }
        }
    }

    public String getNickname() {
        return nickname;
    }

    public Session getSession() {
        return session;
    }
}