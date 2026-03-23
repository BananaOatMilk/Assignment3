import java.io.IOException;
import java.util.Objects;

public final class ComputerPlayer implements Player {
    private final String name;
    private final ComputerChoiceAlgorithm choiceAlgorithm;

    public ComputerPlayer(String name, ComputerChoiceAlgorithm choiceAlgorithm) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.choiceAlgorithm = Objects.requireNonNull(choiceAlgorithm, "choiceAlgorithm cannot be null");
    }

    public String getName() {
        return name;
    }

    @Override
    public Move makeMove(int round) {
        return choiceAlgorithm.chooseMove(round);
    }

    public void recordRound(Move humanMove, Move computerMove) {
        choiceAlgorithm.recordRound(humanMove, computerMove);
    }

    public void finishGame() throws IOException {
        choiceAlgorithm.finishGame();
    }

    @Override
    public String toString() {
        return "Player: " + name;
    }
}
