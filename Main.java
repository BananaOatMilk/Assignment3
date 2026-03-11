import java.util.Scanner;

public final class Main {
    private static final int TOTAL_ROUNDS = 20;

    private Main() {
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Player humanPlayer = new HumanPlayer("Human", scanner);
        Player computerPlayer = new ComputerRandomPlayer();
        RuleEngine ruleEngine = new StandardRpsRuleEngine();
        GameEngine gameEngine = new GameEngine(ruleEngine);
        Scoreboard scoreboard = new Scoreboard();

        System.out.println("Command-Line Rock-Paper-Scissors");
        System.out.println("--------------------------------");

        for (int round = 1; round <= TOTAL_ROUNDS; round++) {
            RoundOutcome outcome = gameEngine.playRound(humanPlayer, computerPlayer, round);
            scoreboard.record(outcome.getResult());
            printRoundSummary(outcome, scoreboard);
        }

        printFinalSummary(scoreboard);
        scanner.close();
    }

    private static void printRoundSummary(RoundOutcome outcome, Scoreboard scoreboard) {
        System.out.println("You chose " + formatMove(outcome.getHumanMove()) + ".");
        System.out.println("The computer chose " + formatMove(outcome.getComputerMove()) + ".");
        System.out.println(toRoundMessage(outcome.getResult()));
        System.out.println(
            "Score: Human:"
                + scoreboard.getP1Wins()
                + " Computer:"
                + scoreboard.getP2Wins()
                + " Draws:"
                + scoreboard.getDraws()
        );
        System.out.println();
    }

    private static void printFinalSummary(Scoreboard scoreboard) {
        System.out.println("Final Score");
        System.out.println(
            "Human:"
                + scoreboard.getP1Wins()
                + " Computer:"
                + scoreboard.getP2Wins()
                + " Draws:"
                + scoreboard.getDraws()
        );

        switch (scoreboard.getOverallWinner()) {
            case HUMAN_WIN:
                System.out.println("Overall Winner: Human");
                break;
            case COMPUTER_WIN:
                System.out.println("Overall Winner: Computer");
                break;
            case DRAW:
                System.out.println("Overall Result: Draw");
                break;
            default:
                throw new IllegalStateException("Unexpected overall winner");
        }
    }

    private static String formatMove(Move move) {
        switch (move) {
            case ROCK:
                return "Rock";
            case PAPER:
                return "Paper";
            case SCISSORS:
                return "Scissors";
            default:
                throw new IllegalStateException("Unexpected move: " + move);
        }
    }

    private static String toRoundMessage(RoundResult result) {
        switch (result) {
            case HUMAN_WIN:
                return "Human Wins!";
            case COMPUTER_WIN:
                return "Computer Wins!";
            case DRAW:
                return "Draw!";
            default:
                throw new IllegalStateException("Unexpected round result: " + result);
        }
    }
}
