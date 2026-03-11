import java.util.Objects;

public final class StandardRpsRuleEngine implements RuleEngine {
    @Override
    public RoundResult decide(Move humanMove, Move computerMove) {
        Objects.requireNonNull(humanMove, "humanMove cannot be null");
        Objects.requireNonNull(computerMove, "computerMove cannot be null");

        if (humanMove == computerMove) {
            return RoundResult.DRAW;
        }

        boolean humanWins =
            (humanMove == Move.ROCK && computerMove == Move.SCISSORS)
                || (humanMove == Move.PAPER && computerMove == Move.ROCK)
                || (humanMove == Move.SCISSORS && computerMove == Move.PAPER);

        return humanWins ? RoundResult.HUMAN_WIN : RoundResult.COMPUTER_WIN;
    }
}
