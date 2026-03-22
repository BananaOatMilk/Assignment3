public interface ComputerChoiceStrategy {
    Move chooseMove(int round);
    void recordRound(Move humanMove, Move computerMove);
    void saveLearning();
}
