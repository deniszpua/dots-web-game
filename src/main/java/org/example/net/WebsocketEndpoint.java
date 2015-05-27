package org.example.net;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint(value = "/msg")
public class WebsocketEndpoint {


    private static final String GUEST_PREFIX = "Player ";
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
        String message = String.format("* %s %s", nickname, "has joined.");
        broadcast(message);
    }


    @OnClose
    public void end() {
        connections.remove(this);
        String message = String.format("* %s %s",
                nickname, "has disconnected.");
        broadcast(message);
    }


    @OnMessage
    public void incoming(String message) {
        broadcast(message + " message recieved!");
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
                        client.nickname, "has been disconnected.");
                broadcast(message);
            }
        }
    }
}