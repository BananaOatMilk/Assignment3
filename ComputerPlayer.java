public class ComputerPlayer implements Player {
    private final ComputerChoiceStrategy strategy;

    public ComputerPlayer(ComputerChoiceStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public Move makeMove(int round) {
        return strategy.chooseMove(round);
    }

    public void recordRound(Move humanMove, Move computerMove) {
        strategy.recordRound(humanMove, computerMove);
    }

    public void saveLearning() {
        strategy.saveLearning();
    }
}
