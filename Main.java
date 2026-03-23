import java.util.Scanner;
import java.io.IOException;
import java.nio.file.Path;

public final class Main {
    private static final int TOTAL_ROUNDS = 20;
    private static final int ML_WINDOW_SIZE = 5;
    private static final Path ML_DATA_FILE = Path.of("ml-frequency-data.txt");

    private Main() {
    }

    public static void main(String[] args) {
        ComputerMode computerMode = parseComputerMode(args);
        if (computerMode == null) {
            printUsage();
            return;
        }

        Scanner scanner = new Scanner(System.in);

        Player humanPlayer = new HumanPlayer("Human", scanner);
        MachineLearningChoiceAlgorithm mlAlgorithm = null;
        Player computerPlayer;
        if (computerMode == ComputerMode.MACHINE_LEARNING) {
            mlAlgorithm = new MachineLearningChoiceAlgorithm(ML_WINDOW_SIZE, ML_DATA_FILE);
            computerPlayer = new MachineLearningPlayer(mlAlgorithm);
        } else {
            computerPlayer = new ComputerRandomPlayer();
        }

        RuleEngine ruleEngine = new StandardRpsRuleEngine();
        GameEngine gameEngine = new GameEngine(ruleEngine);
        Scoreboard scoreboard = new Scoreboard();

        System.out.println("Command-Line Rock-Paper-Scissors");
        System.out.println("--------------------------------");
        System.out.println("Computer mode: " + (computerMode == ComputerMode.MACHINE_LEARNING ? "ML" : "Random"));
        System.out.println();

        for (int round = 1; round <= TOTAL_ROUNDS; round++) {
            RoundOutcome outcome = gameEngine.playRound(humanPlayer, computerPlayer, round);
            if (mlAlgorithm != null) {
                mlAlgorithm.recordRound(outcome.getHumanMove(), outcome.getComputerMove());
            }
            scoreboard.record(outcome.getResult());
            printRoundSummary(outcome, scoreboard);
        }

        printFinalSummary(scoreboard);
        if (mlAlgorithm != null) {
            saveMlData(mlAlgorithm);
        }
        scanner.close();
    }

    private static ComputerMode parseComputerMode(String[] args) {
        if (args == null || args.length == 0) {
            return ComputerMode.RANDOM;
        }

        if (args.length != 1) {
            return null;
        }

        if ("-r".equalsIgnoreCase(args[0])) {
            return ComputerMode.RANDOM;
        }
        if ("-m".equalsIgnoreCase(args[0])) {
            return ComputerMode.MACHINE_LEARNING;
        }
        return null;
    }

    private static void printUsage() {
        System.out.println("Usage: java Main [-r | -m]");
        System.out.println("  -r  Use random computer choices (default)");
        System.out.println("  -m  Use machine-learning computer choices");
    }

    private static void saveMlData(MachineLearningChoiceAlgorithm mlAlgorithm) {
        try {
            mlAlgorithm.saveData();
        } catch (IOException e) {
            System.out.println("Warning: unable to save ML frequency data.");
        }
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

    private enum ComputerMode {
        RANDOM,
        MACHINE_LEARNING
    }
}
