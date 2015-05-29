package org.example.net.messages;

/**
 * Bean, that represents player response data.
 */
public class MessageFromPlayer {

    private boolean gameTerminated;
    private int newDotPosition;

    public MessageFromPlayer() {
        gameTerminated = false;
        newDotPosition = -1;
    }

    /**
     *
     * @return true if player have terminated game
     * and false otherwise
     */
    public boolean isGameTerminated() {
        return gameTerminated;
    }

    /**
     *
     * @return position, where new dot placed.
     */
    public int getNewDotPosition() {
        return newDotPosition;
    }

    public void setGameTerminated(boolean gameTerminated) {
        this.gameTerminated = gameTerminated;
    }

    public void setNewDotPosition(int newDotPosition) throws  IllegalArgumentException {
        if (newDotPosition < 0) {
            throw new IllegalArgumentException();
        }
        this.newDotPosition = newDotPosition;
    }
}
