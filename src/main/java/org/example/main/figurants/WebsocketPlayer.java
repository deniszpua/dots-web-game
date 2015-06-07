package org.example.main.figurants;

import org.example.main.MovesReceiver;
import org.example.net.WebsocketEndpoint;
import org.example.net.WebsocketListener;
import org.example.net.messaging.GameViewUpdate;
import org.example.net.messaging.MessageFromPlayer;

import java.io.IOException;

public class WebsocketPlayer extends WebsocketGameObserver implements Player,
    WebsocketListener {

  private final String nickname;
  private MovesReceiver listener;
  
  

  public WebsocketPlayer(WebsocketEndpoint ws) {
    super(ws);
    this.nickname = ws.getNickname();
  }


  @Override
  public String getNickname() {
    return nickname;
  }

  @Override
  public void addListener(MovesReceiver listener) {
    this.listener = listener;

  }


  @Override
  public void messageReceived(String message) {
    try {
      listener.moveReceived(
          mapper.readValue(message, MessageFromPlayer.class),
          nickname);
    } catch (IOException e) {
      e.printStackTrace();
      GameViewUpdate moveRequest = new GameViewUpdate();
      moveRequest.setMoveAllowed(true);
      moveRequest.setInfoMessage("Error reading your previous"
          + " move, please repeat it.");
      try {
        ws.getSession().getBasicRemote().sendText(
            mapper.writeValueAsString(moveRequest));
      } catch (IOException e1) {
        e1.printStackTrace();
        errorOnConnection(e1);
      }
    }
    
  }


  @Override
  public void errorOnConnection(Throwable cause) {
    listener.connectionTerminated(cause.getMessage(), nickname);
  }


  @Override
  public void connectionTerminated() {
    listener.connectionTerminated("Player teminated connection", nickname);
        
  }

}
