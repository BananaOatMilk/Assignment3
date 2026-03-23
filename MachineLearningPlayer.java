import java.util.Objects;

public final class MachineLearningPlayer implements Player {
    private final MachineLearningChoiceAlgorithm algorithm;

    public MachineLearningPlayer(MachineLearningChoiceAlgorithm algorithm) {
        this.algorithm = Objects.requireNonNull(algorithm, "algorithm cannot be null");
    }

    @Override
    public Move makeMove(int round) {
        return algorithm.chooseMove();
    }
}
