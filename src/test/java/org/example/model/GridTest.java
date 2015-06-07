package org.example.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by development on 09.05.15.
 */
public class GridTest {


    /**
     *
     * @return 10x10 board without circuits with first two rows
     * filled with red dots in even and blue dots in odd positions
     * @throws Exception
     */
    public GameGrid boardSetUp() throws Exception {


        GameGrid gameGrid = new Grid(10, 10);
        //fill two upper rows with dots
        for (int pos = 0; pos < 20; pos++) {
            gameGrid.addDot(
                    ((pos%2 == 0) ? PlayerType.RED_PLAYER : PlayerType.BLUE_PLAYER),
                    pos
            );
        }
        return gameGrid;
    }

    @Test
    /**
     * Empty board should not contain any dots or circuits
     */
    public void testEmptyGameBoardInvariants() {
        //empty board should not contain any elements
        GameGrid emptyGameGrid = new Grid(10, 10);
        //new GameBoard should not contain any dots
        assertEquals(new ArrayList<Integer>(), emptyGameGrid.getDots(PlayerType.RED_PLAYER));
        assertEquals(new ArrayList<Integer>(), emptyGameGrid.getDots(PlayerType.BLUE_PLAYER));
        //new GameBoard should not contain any circuits
        assertEquals(new ArrayList<List<Integer>>(), emptyGameGrid.getCircuits(PlayerType.RED_PLAYER));
        assertEquals(new ArrayList<List<Integer>>(), emptyGameGrid.getCircuits(PlayerType.BLUE_PLAYER));
        //empty board score should be zero-filled array
        int[] threeZero = {0, 0, 0};
        List<Integer> expectedScore = new ArrayList<Integer>(3);
        for (int zero : threeZero) {
            expectedScore.add(zero);
        }
        assertEquals(expectedScore, emptyGameGrid.getGameScore());
    }

    @Test
    /**
     *Board should contain red dots in even positions
     *of first two rows
     */
    public void testGetDots() throws Exception {

        GameGrid gameGrid = boardSetUp();
        List<Integer> expected = new ArrayList<Integer>();
        for (int pos = 0; pos < 20; pos+= 2) {
            expected.add(pos);
        }
        assertEquals(expected, gameGrid.getDots(PlayerType.RED_PLAYER));

        // board should contain blue dots in odd positions of first two rows
        expected.clear();
        for (int pos = 1; pos < 20; pos+= 2) {
            expected.add(pos);
        }
        assertEquals(expected, gameGrid.getDots(PlayerType.BLUE_PLAYER));
    }

    @Test
    public void testGetCircuits() throws Exception {
        //initialized board should not contain circuits
        GameGrid gameGrid = boardSetUp();
        assertEquals(0, gameGrid.getCircuits(PlayerType.RED_PLAYER).size());
        assertEquals(0, gameGrid.getCircuits(PlayerType.BLUE_PLAYER).size());

    }

    @Test
    /**
     * It should add dots at required positions
     */
    public void testAddDot() throws Exception {
        GameGrid gameGrid = new Grid(10, 10);
        assertFalse(gameGrid.getDots(PlayerType.RED_PLAYER).contains(21));
        gameGrid.addDot(PlayerType.RED_PLAYER, 21);
        assertTrue(gameGrid.getDots(PlayerType.RED_PLAYER).contains(21));

        assertFalse(gameGrid.getDots(PlayerType.BLUE_PLAYER).contains(0));
        gameGrid.addDot(PlayerType.BLUE_PLAYER, 0);
        assertTrue(gameGrid.getDots(PlayerType.BLUE_PLAYER).contains(0));

        assertFalse(gameGrid.getDots(PlayerType.RED_PLAYER).contains(99));
        gameGrid.addDot(PlayerType.RED_PLAYER, 99);
        assertTrue(gameGrid.getDots(PlayerType.RED_PLAYER).contains(99));
    }

    @Test
    /**
     * It should detect new circuits
     */
    public void testDetectNewCircuits() {
        GameGrid gameGridWithCircuit = new Grid(10, 10);
        int[] blueDots = {21, 29, 30, 32};
        for (int dot : blueDots) {
            gameGridWithCircuit.addDot(PlayerType.BLUE_PLAYER, dot);
        }
        gameGridWithCircuit.addDot(PlayerType.RED_PLAYER, 31);

        assertTrue(gameGridWithCircuit.getCircuits(PlayerType.BLUE_PLAYER).size() == 0);
        gameGridWithCircuit.addDot(PlayerType.BLUE_PLAYER, 41);

        int [] expectedCircuitVertices = {21, 30, 32, 41};
        List<Integer> expectedCircuit = new ArrayList<Integer>();
        for (int vertex : expectedCircuitVertices) {
            expectedCircuit.add(vertex);
        }
        Collections.sort(expectedCircuit);

        List<List<Integer>> actualCircuits = gameGridWithCircuit.getCircuits(PlayerType.BLUE_PLAYER);
        assertEquals(1, actualCircuits.size());
        List<Integer> actual =  actualCircuits.get(0);
        Collections.sort(actual);
        assertEquals(expectedCircuit, actual);
    }

    @Test
    /**
     * It should count opponents dots incide contour as captive
     */
    public void testNotCaptionDotsOutOfContour() throws Exception {
        int[] blueDots = {15, 24, 26, 28, 34, 37, 39, 45, 47, 49, 55, 59, 65, 66, 68};
        int blueClosingPoint = 77;
        int [] redDots = {27, 29, 36, 69, 93};
        GameGrid gameGrid = new Grid(10, 10);
        for (int blueDot : blueDots) {
            gameGrid.addDot(PlayerType.BLUE_PLAYER, blueDot);
        }
        for (int redDot : redDots) {
            gameGrid.addDot(PlayerType.RED_PLAYER, redDot);
        }


        List<Integer> expectedScore = Arrays.asList(0, 0, 0);
        assertEquals(expectedScore, gameGrid.getGameScore());

        expectedScore.set(PlayerType.BLUE_PLAYER, 1);
        gameGrid.addDot(PlayerType.BLUE_PLAYER, blueClosingPoint);
        assertEquals(expectedScore, gameGrid.getGameScore());

    }

    @Test
    /**
     * It should close maximum area circuit
     */
    public void testAddCircuitsToExistingCircuitsBorder() {
        int[] redDots = {13, 22, 24, 31, 33, 35, 41, 45, 50, 55, 61, 64, 66, 73, 76, 84, 85};
        int[] blueDots = {14, 25, 36, 37, 62, 71, 82, 93, 94, 95, 96};
        int redCircuitClosingDot = 72;
        GameGrid gameGrid = new Grid(10, 10);
        for (int dot : redDots) {
            gameGrid.addDot(PlayerType.RED_PLAYER, dot);
        }
        for (int dot : blueDots) {
            gameGrid.addDot(PlayerType.BLUE_PLAYER, dot);
        }
        assertEquals(0, gameGrid.getCircuits(PlayerType.RED_PLAYER).size());

        gameGrid.addDot(PlayerType.RED_PLAYER, redCircuitClosingDot);
        int[] expectedVertices = {13, 22, 24, 31, 35, 41, 45, 50, 55, 61, 66, 72, 73, 76, 84, 85};
        List<Integer> expectedCircuit = new ArrayList<Integer>();
        for (int dot : expectedVertices) {
            expectedCircuit.add(dot);
        }
        Collections.sort(expectedCircuit);

        assertEquals(1, gameGrid.getCircuits(PlayerType.RED_PLAYER).size());
        List<Integer> actualCircuit = gameGrid.getCircuits(PlayerType.RED_PLAYER).get(0);
        Collections.sort(actualCircuit);
        assertEquals(expectedCircuit, actualCircuit);
    }

    @Test
    /**
     * It should throw an IllegalArgumentException
     * if invalid arguments passed in
     */
    public void testAddDotToThrowExceptions() throws Exception {
        //invalid player
        GameGrid gameGrid = new Grid(10, 10);
        boolean thrown = false;
        try {
            gameGrid.addDot(30, 3);
        }
        catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
        //negative position
        thrown = false;
        try {
            gameGrid.addDot(PlayerType.RED_PLAYER, -1);
        }
        catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
        //exceeding range
        thrown = false;
        try {
            gameGrid.addDot(PlayerType.BLUE_PLAYER, 105);
        }
        catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
        // both
        thrown = false;
        try {
            gameGrid.addDot(100, 200);
        }
        catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
        thrown = false;
        try {
            gameGrid.addDot(-1, -1);
        }
        catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }
}