package org.example.main.figurants;

import org.example.net.messaging.GameViewUpdate;


/**
 * Created by deniszpua on 29.05.15.
 */
public interface GameObserver {
  public void receiveNewGameState(GameViewUpdate gameViewUpdate);
}
