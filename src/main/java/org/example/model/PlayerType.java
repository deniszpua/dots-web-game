package org.example.model;

import java.util.Arrays;
import java.util.List;

/**
 * Created by deniszpua on 08.05.15.
 */
public class PlayerType {
    public static final int RED_PLAYER = 1;
    public static final int BLUE_PLAYER = 2;

    public static int opponent(int player) {
        return player == RED_PLAYER ? BLUE_PLAYER : RED_PLAYER;
    }

    public static List<Integer> getPlayers() {
        return Arrays.asList(PlayerType.RED_PLAYER, PlayerType.BLUE_PLAYER);
    }

}
