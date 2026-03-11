package rps;

public interface RuleEngine {
    RoundResult decide(Move humanMove, Move computerMove);
}
