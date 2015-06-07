package org.example.net.messaging;

import java.util.Arrays;
import java.util.List;

/**
 * Bean that represents data, that delivered to client to
 * display board and current game status.
 */
public class GameViewUpdate {
    private boolean gameTerminated;
    private boolean moveAllowed;
    private int[] redDots;
    private int[] blueDots;
    private int[][] redCircuits;
    private int[][] blueCircuits;
    private String infoMessage;

    public void setGameInProgress(boolean gameTerminated) {
        this.gameTerminated = gameTerminated;
    }

    public void setMoveAllowed(boolean moveAllowed) {
        this.moveAllowed = moveAllowed;
    }

    public void setRedDots(List<Integer> redDots) {
        this.redDots = toArray(redDots);
    }

    public void setBlueDots(List<Integer> blueDots) {
        this.blueDots = toArray(blueDots);
    }

    public void setRedCircuits(List<List<Integer>> redCircuits) {
        this.redCircuits = circuitsToArray(redCircuits);
    }

    public void setBlueCircuits(List<List<Integer>> blueCircuits) {
        this.blueCircuits = circuitsToArray(blueCircuits);
    }

    public boolean isGameInProgress() {
        return gameTerminated;
    }

    public boolean isMoveAllowed() {
        return moveAllowed;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
    }

    public int[] getRedDots() {
        return redDots;
    }

    public int[] getBlueDots() {
        return blueDots;
    }

    public int[][] getBlueCircuits() {
        return blueCircuits;
    }

    public int[][] getRedCircuits() {
        return redCircuits;
    }

    private int[] toArray(List<Integer> dotsList) {
        int[] result = new int[dotsList.size()];
        for (int i = 0; i < dotsList.size(); i++) {
            result[i] = dotsList.get(i);
        }
        return result;
    }

    private int[][] circuitsToArray(List<List<Integer>> circuitsList) {
        int[][] result = new int[circuitsList.size()][];
        int i=0;
        for (List<Integer> circuit : circuitsList) {
            result[i++] = toArray(circuit);
        }
        return result;
    }

    @Override
    public String toString() {
        return "GameViewUpdate{" +
                "gameTerminated=" + gameTerminated +
                ", moveAllowed=" + moveAllowed +
                ", redDots=" + Arrays.toString(redDots) +
                ", blueDots=" + Arrays.toString(blueDots) +
                ", redCircuits=" + Arrays.toString(redCircuits) +
                ", blueCircuits=" + Arrays.toString(blueCircuits) +
                ", infoMessage='" + infoMessage + '\'' +
                '}';
    }
}
