package org.example.model;

import java.util.*;

import static org.example.model.Player.*;

/**
 * Created by deniszpua on 09.05.15.
 */
public class Grid implements GameGrid {
    private int boardWidth;
    private int boardHeight;
    private int[] dots;
    private List<List<List<Integer>>> circuits;
    private List<Integer> gameScore;
    private List<Double> enclosedArea;


    public Grid(int boardWidth, int boardHeight) {
        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;
        //create an array filled with zeros
        dots = new int[boardHeight * boardWidth];

        circuits = new ArrayList<List<List<Integer>>>(3);
        circuits.add(null);
        circuits.add(RED_PLAYER, new ArrayList<List<Integer>>());
        circuits.add(BLUE_PLAYER, new ArrayList<List<Integer>>());
        gameScore = new ArrayList<Integer>(3);
        gameScore.add(0, 0);
        gameScore.add(Player.RED_PLAYER, 0);
        gameScore.add(Player.BLUE_PLAYER, 0);
        enclosedArea = new ArrayList<Double>(3);
        for (int i = 0; i < 3; i++) {
            enclosedArea.add(0.0);
        }
    }

    public boolean isGameFinished() throws UnsupportedOperationException {
//        throw new UnsupportedOperationException("method not supported");
        return false;
    }


    public List<Integer> getDots(int player) {
        List<Integer> dotsList = new ArrayList<Integer>();
        for (int pos = 0; pos < dots.length; pos++) {
            //
            if (Math.abs(dots[pos]) == player) {
                dotsList.add(pos);
            }
        }
        return dotsList;
    }

    public List<List<Integer>> getCircuits(int player) {
        return circuits.get(player);
    }

    public List<Integer> getGameScore() {
        return gameScore;
    }

    public void addDot(int player, int newDot) {
        //validate position
        if (newDot >= dots.length || newDot < 0 ||
                (player != RED_PLAYER && player != BLUE_PLAYER)
                ) {
            throw new IllegalArgumentException();
        }

        //add dot
        dots[newDot] = player;

        List<Circuit> newCircuits = findNewCircuits(newDot, player);
        for (Circuit newCircuit : newCircuits) {
            addCircuit(newCircuit.getVertices(), player);

            // also we need deactivate our points inside circuits
            for (int pos : newCircuit.getEmbracedPositions()) {
                if (placedByPlayer(dots[pos],player) &&
                        !isActive(dots[pos])) {
                    deactivate(pos);

                }
            }
        }

        updateScore();
    }

    private List<Circuit> findNewCircuits(int newDot, int player) {

        Queue<List<Integer>> pathes = new ArrayDeque<List<Integer>>();
        for (int originNeighbour : eightNeighbours(newDot, player)) {
            List<Integer> pathToOrigin = new ArrayList<Integer>();
            pathToOrigin.add(newDot);
            pathToOrigin.add(originNeighbour);
            pathes.add(pathToOrigin);
        }

        Set<Circuit> circuitsSet = new HashSet<Circuit>();

        //Walk forward through dots neighbours adding it to path and creating new path if
        //fork detected
        while (!pathes.isEmpty()) {
            List <Integer> pathToOrigin = pathes.remove();

            while (true) {
                int current = pathToOrigin.get(pathToOrigin.size() - 1);
                if (current == newDot) {
                    //reached origin
                    pathToOrigin.remove(pathToOrigin.size() - 1);
                    Set<Integer> captiveDots =
                            getCaptiveDots(pathToOrigin, player);
                    if (captiveDots.size() != 0) {
                        circuitsSet.add(new Circuit(pathToOrigin, boardWidth, captiveDots));
                    }
                    break;
                }

                List<Integer> neighbours = eightNeighbours(current, player);
                //remove parent to current from origin
                neighbours.remove(pathToOrigin.get(pathToOrigin.size() - 2));
                if (neighbours.size() == 0) {
                    //dead end
                    break;
                }
                if (neighbours.size() > 1) {
                    //path fork
                    for (int i = 1; i < neighbours.size(); i++) {
                        if (!pathToOrigin.contains(neighbours.get(i))) {
                            //new path discovered
                            List<Integer> clone = new ArrayList<Integer>(pathToOrigin);
                            clone.add(neighbours.get(i));
                            pathes.add(clone);
                        }
                        else processPathCycle(player, circuitsSet,
                                pathToOrigin, neighbours.get(i));
                    }
                }
                //go forward
                if (pathToOrigin.contains(neighbours.get(0))) {
                    processPathCycle(player, circuitsSet,
                            pathToOrigin, neighbours.get(0));
                    break;
                }
                else {
                    pathToOrigin.add(neighbours.get(0));
                }

            }

        }

        //remove circuits with identical captioned points and smaller area
        List<Circuit> circuitsList = new ArrayList<Circuit>(circuitsSet);
        Collections.sort(circuitsList);
        Collections.reverse(circuitsList);

        List<Circuit> duplicates = new ArrayList<Circuit>();
        for (int i = 0; i < circuitsList.size(); i++) {
            for (int j = i + 1; j < circuitsList.size(); j++) {
                if (circuitsList.get(i).getCaptives().containsAll(
                        circuitsList.get(j).getCaptives()
                )) {
                    duplicates.add(circuitsList.get(j));
                }
            }
        }
        circuitsList.removeAll(duplicates);

        return circuitsList;
    }

    private void updateScore() {
        //eliminate circuits, enclosed by bigger circuits
        //count opponents deactivated dots in circuits

        for (int player : Player.getPlayers()) {
            int captiveDots = 0;
            double totalEnclosedArea = 0.0;
            for (List<Integer> circuit : circuits.get(player)) {
                if (isActive(circuit)) {
                    Circuit activeCircuit = new Circuit(circuit, boardWidth);
                    totalEnclosedArea += activeCircuit.getEnclosedArea();
                    for (int pos : activeCircuit.getEmbracedPositions()) {
                        if (isActive(dots[pos]) &&
                                placedByPlayer(dots[pos], Player.opponent(player))) {
                            captiveDots++;
                            deactivate(pos);
                        }
                    }

                }
            }
            if (captiveDots != 0) {
                gameScore.set(player, gameScore.get(player) + captiveDots);
            }
            if (totalEnclosedArea != 0.0) {
                enclosedArea.set(player, enclosedArea.get(player) + totalEnclosedArea);
            }
        }

    }


    private void processPathCycle(int player, Set<Circuit> circuitsSet,
                                  List<Integer> pathToOrigin, int joinDot) {
        //cycle detected
        List<Integer> cycle = new ArrayList<Integer>(
                pathToOrigin.subList(
                    pathToOrigin.indexOf(joinDot),
                    pathToOrigin.size()

                )
        );

        Set<Integer> captiveDots =
                getCaptiveDots(cycle, player);
        if (captiveDots.size() != 0) {
            circuitsSet.add(new Circuit(cycle,boardWidth, captiveDots));
        }
    }

    /**
     *
     * @param possibleCircuit circuit that suspected to close opponents dots
     * @param player player, that closes circuit
     * @return special search result object with
     */
    private Set<Integer> getCaptiveDots(List<Integer> possibleCircuit, int player) {

        Set<Integer> captives = new HashSet<Integer>();
        Circuit circuit = new Circuit(possibleCircuit, boardWidth);

        for (int i : circuit.getEmbracedPositions()) {
            //only opponents active points counts
            if (dots[i] == opponent(player)) {
                captives.add(i);
            }
        }
        return captives;

    }

    private List<Integer> eightNeighbours(int pos, int player) {

        List<Integer> neighbours = new ArrayList<Integer>();
        //Adding neighbours clockwise
        int[] rowOffsets = {-1, -1, 0, 1, 1,  1,  0, -1};
        int[] colOffsets = { 0,  1, 1, 1, 0, -1, -1, -1};
        int row = getRow(pos);
        int col = getColumn(pos);
        int currentRow, currentCol;

        for (int i = 0; i < rowOffsets.length; i++) {
            currentRow = row + rowOffsets[i];
                currentCol = col + colOffsets[i];
                if ( currentCol >= 0 &&
                        currentRow >= 0 &&
                        currentCol < boardWidth &&
                        currentRow < boardHeight &&
                        // only this player's active dots can connect
                        dots[currentRow*boardWidth + currentCol] == player
                        ) {
                    neighbours.add(currentRow*boardWidth + currentCol);
                }
            }
            return neighbours;
    }

    private void addCircuit(List<Integer> circuit, int player) {
        circuits.get(player).add(circuit);
    }

    private void markDotsAsInactive(Set<Integer> captives) {
        for (int pos : captives) {
            deactivate(pos);
        }

    }

    private int getRow(int position) {
        return position / boardWidth;
    }

    private int getColumn(int position) {
        return position % boardWidth;
    }

    private boolean placedByPlayer(int dot, int player) {
        return Math.abs(dot) == player;
    }


    private boolean isActive(int dot) {
        return dot > 0;
    }

    private boolean isActive(List<Integer> circuit) {
        return dots[circuit.get(0)] > 0;
    }

    private void deactivate(int pos) {
        dots[pos] *= -1;
    }


}
