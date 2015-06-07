package org.example.main.figurants;

import org.codehaus.jackson.map.ObjectMapper;
import org.example.net.WebsocketEndpoint;
import org.example.net.messaging.GameViewUpdate;

import java.io.IOException;

public class WebsocketGameObserver implements GameObserver {
  protected final WebsocketEndpoint ws;
  protected ObjectMapper mapper;
  
  

  public WebsocketGameObserver(WebsocketEndpoint ws) {
    super();
    this.ws = ws;
    
    mapper = new ObjectMapper();
  }



  @Override
  public void receiveNewGameState(GameViewUpdate gameViewUpdate) {
    String message = null;
    try {
      message = mapper.writeValueAsString(gameViewUpdate);
      ws.getSession().getBasicRemote().sendText(message);
    } catch (IOException e) {
      // TODO: handle exception
    }

  }

}
