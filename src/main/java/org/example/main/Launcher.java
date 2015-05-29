package org.example.main;

import org.example.model.GameGrid;
import org.example.net.GameConnection;
import org.example.net.MessagesListener;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by deniszpua on 29.05.15.
 */
public interface Launcher extends MessagesListener {
    /*
    Setters for initial setup
     */
    void setPlayerMessagesPublishers(HashMap<String, GameConnection> players);
    void setGameWatchers(Set<GameConnection> watchers);

    void addGameBoard(GameGrid gameBoard);

    /*
    game launcher
     */
    void startGame();


}
