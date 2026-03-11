public final class RoundOutcome {
    private final Move humanMove;
    private final Move computerMove;
    private final RoundResult result;

    public RoundOutcome(Move humanMove, Move computerMove, RoundResult result) {
        this.humanMove = humanMove;
        this.computerMove = computerMove;
        this.result = result;
    }

    public Move getHumanMove() {
        return humanMove;
    }

    public Move getComputerMove() {
        return computerMove;
    }

    public RoundResult getResult() {
        return result;
    }
}
