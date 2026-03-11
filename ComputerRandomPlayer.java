import java.util.Random;
public class ComputerRandomPlayer implements Player {
    private Random random;

    public ComputerRandomPlayer() {
        random = new Random();
    }

    @Override
    public Move makeMove(int round) {
        int move = random.nextInt(3) + 1; // Randomly choose between 1, 2, or 3
        return Move.values()[move - 1];
    }
}
