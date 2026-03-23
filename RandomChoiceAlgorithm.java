import java.util.Random;

public final class RandomChoiceAlgorithm implements ComputerChoiceAlgorithm {
    private final Random random;

    public RandomChoiceAlgorithm() {
        this.random = new Random();
    }

    @Override
    public Move chooseMove(int round) {
        return Move.values()[random.nextInt(Move.values().length)];
    }
}
