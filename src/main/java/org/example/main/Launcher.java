package org.example.main;

import org.example.main.figurants.GameObserver;
import org.example.main.figurants.Player;
import org.example.model.GameGrid;

import java.util.Set;

public interface Launcher extends MovesReceiver {

    void setPlayerMessagesPublishers(Player[] players);
    
    void setGameObservers(Set<GameObserver> observers);

    void addGameBoard(GameGrid gameBoard);

    void startGame();

    /**
     * Method to perform setup that should be called before
     * start of the game.
     */
    void init();


}
