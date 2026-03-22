import java.util.Random;

public class RandomChoiceStrategy implements ComputerChoiceStrategy{
    
    private Random random;

    public RandomChoiceStrategy() {
        random = new Random();
    }

    @Override
    public Move chooseMove(int round) {
        int move = random.nextInt(3) + 1; // Randomly choose between 1, 2, or 3
        return Move.values()[move - 1];
    }

    @Override
    public void recordRound(Move humanMove, Move computerMove){

    }
    @Override
    public void saveLearning(){

    }
}
