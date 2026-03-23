import java.io.IOException;

public interface ComputerChoiceAlgorithm {
    Move chooseMove(int round);

    default void recordRound(Move humanMove, Move computerMove) {
    }

    default void finishGame() throws IOException {
    }
}
