package org.example.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by deniszpua on 14.05.15.
 */
public class CircuitTest {
    Circuit circuit1573;
    Circuit complexCircuit;

    @Before
    public void setUp() throws Exception {

        circuit1573 = new Circuit(Arrays.asList(1, 5, 7, 3), 3);

        complexCircuit = new Circuit(
                Arrays.asList(2, 12, 22, 32, 33, 24, 34, 35, 36, 27, 38, 48, 47, 56,
                66, 65, 54, 63, 62, 52, 42, 31, 21, 11),
                10);
    }

    @Test
    public void testGetEnclosedArea() throws Exception {
        //it should compute enclosed area correctly
        double expected = 2.0;
        assertEquals(expected, circuit1573.getEnclosedArea(), 0.1);

        expected = 18.0;
        assertEquals(expected, complexCircuit.getEnclosedArea(), 0.1);

    }

    @Test
    public void testEmbracePoint() throws Exception {
        //it should detect whether point is incide circuit

        assertTrue(circuit1573.embracesPosition(4));
        assertFalse(circuit1573.embracesPosition(8));

        int[] innerDots = {37, 43, 44, 45, 46, 55, 53};
        for (int dot : innerDots) {
            assertTrue(complexCircuit.embracesPosition(dot));
        }

        int[] outerDots = {0, 3, 8, 9, 10, 13, 14, 23, 25, 26, 28, 29,
                41, 57, 64};
        for (int dot : outerDots) {
            assertFalse(complexCircuit.embracesPosition(dot));
        }


    }

    @Test
    public void testCompareTo() throws Exception {
        //it should correctly compare pathes by their enclosed area

        assertTrue(complexCircuit.compareTo(circuit1573) > 0);

        assertTrue(circuit1573.compareTo(new Circuit(Arrays.asList(1, 10, 21, 12), 10)) == 0);

        assertTrue(circuit1573.compareTo(new Circuit(Arrays.asList(1, 2, 6, 7, 11, 10, 6, 5), 4)) == 0);

        assertTrue(circuit1573.compareTo(new Circuit(Arrays.asList(1, 2, 5, 4), 3)) > 0);

        assertTrue((new Circuit(Arrays.asList(1, 2, 5, 4), 3)).compareTo(circuit1573) < 0);


    }

    @Test
    public void testEquals() throws Exception {
        //it should detect whether two circuits are equal based on their vertexes
        assertTrue(circuit1573.equals(new Circuit(Arrays.asList(5, 1, 7, 3), 3)));
        assertFalse(circuit1573.equals(new Circuit(Arrays.asList(5, 0, 1, 7, 3), 3)));
        assertFalse(circuit1573.equals(new Circuit(Arrays.asList(5, 1, 4, 3), 3)));
        assertFalse(circuit1573.equals(complexCircuit));

    }

    @Test
    public void testGetEmbracedPositions() throws Exception {
        //it shoud return list of positions, embrased with this circuit
        Set<Integer> expected = new HashSet<Integer>(Collections.singleton(4));
        assertEquals(expected, circuit1573.getEmbracedPositions());

        expected = new HashSet<Integer>(Arrays.asList(37, 43, 44, 45, 46, 53, 55));
        assertEquals(expected, complexCircuit.getEmbracedPositions());

        Circuit circuit = new Circuit(Arrays.asList(15, 26, 37, 28, 39, 49, 59,
                68, 77, 66, 55, 45, 34, 24),
                10);
        expected = new HashSet<Integer>(Arrays.asList(25, 35, 36, 38, 46, 47, 48, 56, 57, 58, 67));
        assertEquals(expected, circuit.getEmbracedPositions());

    }

}