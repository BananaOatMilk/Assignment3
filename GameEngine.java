import java.util.Objects;

public final class GameEngine {
    private final RuleEngine ruleEngine;

    public GameEngine(RuleEngine ruleEngine) {
        this.ruleEngine = Objects.requireNonNull(ruleEngine, "ruleEngine cannot be null");
    }

    public RoundOutcome playRound(Player humanPlayer, Player computerPlayer, int round) {
        Objects.requireNonNull(humanPlayer, "humanPlayer cannot be null");
        Objects.requireNonNull(computerPlayer, "computerPlayer cannot be null");

        Move humanMove = humanPlayer.makeMove(round);
        Move computerMove = computerPlayer.makeMove(round);
        RoundResult result = ruleEngine.decide(humanMove, computerMove);
        return new RoundOutcome(humanMove, computerMove, result);
    }
}
