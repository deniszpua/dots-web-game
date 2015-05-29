package org.example.net;

import java.io.IOException;

/**
 * Created by development on 08.05.15.
 */
public interface GameConnection {

    void sendMessage(String message) throws IOException;

    String getNickname();

    void addListener(MessagesListener listener);


}
