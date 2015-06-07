package org.example.net;

public interface WebsocketListener {
  
  void messageReceived(String message);

  void errorOnConnection(Throwable cause);

  void connectionTerminated();

}
