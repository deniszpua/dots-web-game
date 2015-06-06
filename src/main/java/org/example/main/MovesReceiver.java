package org.example.main;

import org.example.net.messages.MessageFromPlayer;


public interface MovesReceiver {
    public void moveReceived(MessageFromPlayer message, String nickname);
    public void connectionTerminated(String message, String nickname);
}
