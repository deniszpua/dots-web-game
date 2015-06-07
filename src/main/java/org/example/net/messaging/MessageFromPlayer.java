package org.example.net.messaging;

/**
 * Bean, that represents player response data.
 */
public class MessageFromPlayer {

    private boolean gameTerminated;
    private int cellNumber;

    public MessageFromPlayer() {
        gameTerminated = false;
        cellNumber = -1;
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
    public int getCellNumber() {
        return cellNumber;
    }

    public void setGameTerminated(boolean gameTerminated) {
        this.gameTerminated = gameTerminated;
    }

    public void setCellNumber(int cellNumber) throws  IllegalArgumentException {
        if (cellNumber < 0) {
            throw new IllegalArgumentException();
        }
        this.cellNumber = cellNumber;
    }

    @Override
    public String toString() {
        return "MessageFromPlayer{" +
                "gameTerminated=" + gameTerminated +
                ", cellNumber=" + cellNumber +
                '}';
    }
}
