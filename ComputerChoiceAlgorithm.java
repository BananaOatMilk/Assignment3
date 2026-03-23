import java.io.IOException;

public interface ComputerChoiceAlgorithm {
    Move chooseMove(int round);

    default void recordRound(Move humanMove, Move computerMove) {
        // Default no-op for non-learning algorithms.
    }

    default void finishGame() throws IOException {
        // Default no-op for algorithms without persistence.
    }
}
