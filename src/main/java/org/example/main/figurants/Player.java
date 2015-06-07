package org.example.main.figurants;

import org.example.main.MovesReceiver;
import org.example.net.WebsocketListener;

public interface Player extends GameObserver, WebsocketListener {

  String getNickname();

  void addListener(MovesReceiver listener);


}
