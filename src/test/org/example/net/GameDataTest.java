package org.example.net;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Created by development on 08.05.15.
 */
public class GameDataTest {
    GameData gameData;
    int[] redDots = {0, 2, 4, 6, 8, 10};
    int[] blueDots = {10, 12, 14, 16, 18, 20};
    int[][] redCircuits = {
            {2, 13, 22, 11},
            {4, 15, 24, 13}
            };
    int[][] blueCircuits = {
            {12, 23, 32, 21},
            {14, 25, 34, 23}
            };



    @Before
    public void setUp() throws Exception {

        List<Integer> redDotsList = new ArrayList<Integer>();
        for (int redDot : redDots) {
            redDotsList.add(redDot);
        }

        List<Integer> blueDotsList = new ArrayList<Integer>();
        for (int blueDot : blueDots) {
            blueDotsList.add(blueDot);
        }


        List<List<Integer>> redCircuitsList = new ArrayList<List<Integer>>();
        for (int[] redCircuit : redCircuits) {
            List<Integer> list = new ArrayList<Integer>();
            for (int aRedCircuit : redCircuit) {
                list.add(aRedCircuit);
            }
            redCircuitsList.add(list);
        }

        List<List<Integer>> blueCircuitsList = new ArrayList<List<Integer>>();
        for (int[] blueCircuit : blueCircuits) {
            List<Integer> list = new ArrayList<Integer>();
            for (int aBlueCircuit : blueCircuit) {
                list.add(aBlueCircuit);
            }
            blueCircuitsList.add(list);
        }


        gameData = new GameData(true, redDotsList,
                blueDotsList, redCircuitsList, blueCircuitsList);


    }

    @Test
    public void testGetRedDots() throws Exception {
        for (int i = 0; i < redDots.length; i++) {
            assertEquals(redDots[i], gameData.getRedDots()[i]);
        }
    }

    @Test
    public void testGetBlueDots() throws Exception {
        for (int i = 0; i < blueDots.length; i++) {
            assertEquals(blueDots[i], gameData.getBlueDots()[i]);
        }

    }

    @Test
    public void testGetRedCircuits() throws Exception {
        for (int i = 0; i < redCircuits.length; i++) {
            for (int j = 0; j < redCircuits[i].length; j++) {

                assertEquals(redCircuits[i][j], gameData.getRedCircuits()[i][j]);
            }
        }

    }

    @Test
    public void testGetBlueCircuits() throws Exception {
        for (int i = 0; i < blueCircuits.length; i++) {
            for (int j = 0; j < blueCircuits[i].length; j++) {

                assertEquals(blueCircuits[i][j], gameData.getBlueCircuits()[i][j]);
            }
        }

    }
}