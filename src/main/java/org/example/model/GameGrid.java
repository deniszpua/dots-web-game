package org.example.model;

import java.util.List;

/**
 * Created by development on 08.05.15.
 */
public interface GameGrid {

    /**
     * Return list of positions, where players dots are placed
     * @param player - player type according to dots.model.PlayerType class
     * @return list of dots, placed by given player
     */
    List<Integer> getDots(int player);

    /**
     * Return list of circuits
     * @param player - player type according to dots.model.PlayerType class
     * @return list of circluits, closed by given player
     */
    List<List<Integer>> getCircuits(int player);

    /**
     *
     * @param player - player type according to dots.model.PlayerType class
     * @param pos - position in grid (row * width + col)
     */
    void addDot(int player, int pos);

    boolean isGameFinished();

    /**
     *
     * @return array where number at pos[player] is player score
     * (number of captive opponents dots)
     */
    List<Integer> getGameScore();

}
