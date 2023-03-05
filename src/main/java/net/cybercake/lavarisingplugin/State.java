package net.cybercake.lavarisingplugin;

public class State {

    private static Game gameState = Game.SERVER_STARTING;

    public enum Game {
        PREGAME, COUNTING, ACTIVE, ENDING, SERVER_STARTING, SERVER_CLOSING;
    }

    public static Game get() {
        return gameState;
    }

    public static void set(Game state) {
        gameState = state;
    }

    public static boolean equals(Game state) {
        return gameState.equals(state);
    }

    public static boolean equals(Game... or) {
        for(Game game : or) {
            if(gameState.equals(game)) {
                return true;
            }
        }
        return false;
    }

}
