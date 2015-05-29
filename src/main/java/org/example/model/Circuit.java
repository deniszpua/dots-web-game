package org.example.model;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;

/**
 * Created by deniszpua on 14.05.15.
 */
public class Circuit implements Comparable<Circuit> {

    private static final int TOP_BORDER = 0;
    private static final int RIGHT_BORDER = 1;
    private static final int BOTTOM_BORDER = 2;
    private static final int LEFT_BORDER = 3;

    public static final double CELL_SQUARE = 1.0;
    public static final double HALF_OF_CELL_SQUARE = 0.5;

    private final List<Integer> vertices;
    private final Set<Integer> captives;
    private final int boardWidth;

    public Circuit(List<Integer> vertices, int boardWidth, Set<Integer> captives) {
        this.boardWidth = boardWidth;
        this.vertices = vertices;
        this.captives = captives;
    }

    public Circuit(List<Integer> vertices, int boardWidth) {
        this.boardWidth = boardWidth;
        this.vertices = vertices;
        this.captives = new HashSet<Integer>();
    }

    /**
     *
     * @return area enclosed by given circuit. Real area may be slightly
     * smaller, than computed by this method, since simplifiying algorythm
     * (all squares, that have their four corners which are circuit vertices
     * counts as placed inside circuit, which is not exactly so for some irrational
     * circuits). But this does not affects on comparing logic.
     */
    public double getEnclosedArea() {
        //divide rectangle, that encloses circuit in squares
        //for each square test if it has diagonal edge, than increment area by 0.5
        //if has no, than test if there are corners, that are not vertices of circuit
        //if such vertex is incide circuit, or if all corners are vertices
        // increment area by 1

        double result = 0.0;

        int[] borders = getEnclosingRectBorders();
        for (int row = borders[TOP_BORDER]; row < borders[BOTTOM_BORDER]; row++) {
            for (int col = borders[LEFT_BORDER]; col < borders[RIGHT_BORDER]; col++) {

                int topLeft = row * boardWidth + col;
                int topRight = row * boardWidth + col + 1;
                int bottomLeft = (row + 1) * boardWidth + col;
                int bottomRight = (row + 1) * boardWidth + col + 1;

                if (containsEdge(topLeft, bottomRight) ||
                        containsEdge(bottomLeft, topRight)) {
                    result += HALF_OF_CELL_SQUARE;
                }
                else {
                    int[] corners = new int[] {topLeft, topRight, bottomLeft, bottomRight};
                    int i = 0;
                    while (i < corners.length) {
                        if (! vertices.contains(corners[i])) {
                            if (embracesPosition(corners[i])) {
                                result += CELL_SQUARE;
                            }
                            break;
                        }
                        i++;
                    }
                    if (i == corners.length) {
                        //all four corners inside path
                        result += CELL_SQUARE;
                    }
                }
            }
        }

        return result;
    }

    public boolean embracesPosition(int pointPosition) {
        // pointPosition is inside circuit if we need to cross circuit
        // odd number of times counting from border
        // until reach pointPosition

        int row = pointPosition / boardWidth;
        int current = vertices.get(0);

        // starting point should not be in the same row as pointPosition
        // because we have to eliminate case, when previous from start
        // vertex in the same row as start and pointPosition
        while (current / boardWidth == row) {
            current = getNext(current);
        }

        List<Integer> inTheSameRow = new ArrayList<Integer>();
        for (int i = 0; i++ < vertices.size(); current = getNext(current)) {

            if ((current / boardWidth) == row) {
                //current vertex in same row as pointPosition
                int fromRow = getPrevious(current) / boardWidth;
                int toRow = getNext(current) / boardWidth;
                if (fromRow != toRow) {
                    if (toRow != row) {
                        //intersection found
                        inTheSameRow.add(current);
                    }
                    else {
                        //next vertex in the same row as current
                        while (toRow == row) {
                            i++;
                            current = getNext(current);
                            toRow = getNext(current) / boardWidth;
                        }
                        if (fromRow != toRow) {
                            //intersecton
                            inTheSameRow.add(current);
                        }
                        //else - no intersection, skip vertex
                    }
                }

            }
        }
        inTheSameRow.add(pointPosition);
        Collections.sort(inTheSameRow);

        return inTheSameRow.indexOf(pointPosition) % 2 == 1;
    }

    public boolean embracesCircuit(List<Integer> circuit) {
        // since circuits cannot crosses
        // enough to test one point
        return this.embracesPosition(circuit.get(0));
    }

    public List<Integer> getVertices() {
        return vertices;
    }

    public Set<Integer> getCaptives() {
        return captives;
    }

    public Set<Integer> getEmbracedPositions() {
        int[] borders = getEnclosingRectBorders();
        List<Integer> rectPositions = new ArrayList<Integer>((borders[BOTTOM_BORDER] - borders[TOP_BORDER]) *
                (borders[RIGHT_BORDER] - borders[LEFT_BORDER]));
        for (int row = borders[TOP_BORDER]; row <= borders[BOTTOM_BORDER] ; row++) {
            for (int col = borders[LEFT_BORDER]; col <= borders[RIGHT_BORDER]; col++) {
                rectPositions.add(row * boardWidth + col);
            }
        }

        rectPositions.removeAll(vertices);
        List<Integer> outside = new ArrayList<Integer>();
        for (int dot : rectPositions) {
            if (!this.embracesPosition(dot)) {
                outside.add(dot);
            }
        }
        rectPositions.removeAll(outside);
        return new HashSet<Integer>(rectPositions);
    }

    public int compareTo(Circuit circuit) {
        return Double.compare(this.getEnclosedArea(), circuit.getEnclosedArea());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null ||
                !(obj instanceof Circuit)) {
            return false;
        }

        if (obj == this) {
            return true;
        }
        //Two circuits are equal if they consist from the same vertices
        Circuit circuit = (Circuit) obj;
        return circuit.getVertices().containsAll(this.getVertices()) &&
                this.getVertices().containsAll(circuit.getVertices());
    }

    @Override
    public int hashCode() {
        List<Integer> vertices = new ArrayList<Integer>(this.getVertices());
        Collections.sort(vertices);
        return new HashCodeBuilder(73, 83)
                .append(vertices.toArray())
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Circuit{" +
                "vertices=" + vertices +
                ", captives=" + captives +
                '}';
    }


    /***********************************
     *Helper methods
     *******************************/

    /**
     *
     * @return array of borders in following order: top, right, bottom, left
     */
    private int[] getEnclosingRectBorders() {
        List<Integer> rows = new ArrayList<Integer>(vertices.size());
        List<Integer> columns = new ArrayList<Integer>(vertices.size());
        for (int dot : vertices) {
            rows.add(dot / boardWidth);
            columns.add(dot % boardWidth);
        }
        Collections.sort(rows);
        Collections.sort(columns);

        return new int[]{rows.get(0),
                columns.get(columns.size() - 1),
                rows.get(rows.size() - 1),
                columns.get(0)
        };

    }

    private boolean containsEdge(int fromPoint, int toPoint) {
        return vertices.containsAll(Arrays.asList(fromPoint, toPoint)) &&
                (Math.abs(vertices.indexOf(fromPoint) - vertices.indexOf(toPoint)) == 1 ||
                        (vertices.get(0) == fromPoint && vertices.get(vertices.size() - 1) == toPoint) ||
                        (vertices.get(0) == toPoint && vertices.get(vertices.size() - 1) == fromPoint));
    }

    private int getPrevious(int vertex) {
        return vertices.indexOf(vertex) != 0 ?
                vertices.get(vertices.indexOf(vertex) - 1) : vertices.get(vertices.size() - 1);
    }

    private int getNext(int vertex) {
        return vertices.indexOf(vertex) != vertices.size() - 1 ?
                vertices.get(vertices.indexOf(vertex) + 1) : vertices.get(0);
    }
}