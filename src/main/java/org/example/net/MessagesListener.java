package org.example.net;

/**
 * Created by deniszpua on 29.05.15.
 */
public interface MessagesListener {
    public void moveReceived(String message, String nickname);
    public void connectionTerminated(String message, String nickname);
}
