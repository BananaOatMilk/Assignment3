import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.Test;

public class RPGGameTest {
    private static final class FixedPlayer implements Player {
        private final Move move;
        FixedPlayer(Move move) { this.move = move; }
        @Override public Move makeMove(int round) { return move; }
    }

    // ---------- A) Rule Engine Tests ----------
    @Test
    public void ruleEngine_drawsWhenSameMove() {
        RuleEngine rules = new StandardRpsRuleEngine();
        assertEquals(RoundResult.DRAW, rules.decide(Move.ROCK, Move.ROCK));
        assertEquals(RoundResult.DRAW, rules.decide(Move.PAPER, Move.PAPER));
        assertEquals(RoundResult.DRAW, rules.decide(Move.SCISSORS, Move.SCISSORS));
    }

    @Test
    public void ruleEngine_humanWinCombos() {
        RuleEngine rules = new StandardRpsRuleEngine();
        assertEquals(RoundResult.HUMAN_WIN, rules.decide(Move.ROCK, Move.SCISSORS));
        assertEquals(RoundResult.HUMAN_WIN, rules.decide(Move.PAPER, Move.ROCK));
        assertEquals(RoundResult.HUMAN_WIN, rules.decide(Move.SCISSORS, Move.PAPER));
    }

    @Test
    public void ruleEngine_computerWinCombos() {
        RuleEngine rules = new StandardRpsRuleEngine();
        assertEquals(RoundResult.COMPUTER_WIN, rules.decide(Move.SCISSORS, Move.ROCK));
        assertEquals(RoundResult.COMPUTER_WIN, rules.decide(Move.ROCK, Move.PAPER));
        assertEquals(RoundResult.COMPUTER_WIN, rules.decide(Move.PAPER, Move.SCISSORS));
    }

    @Test
    public void ruleEngine_rejectsNullMoves() {
        RuleEngine rules = new StandardRpsRuleEngine();
        assertThrows(NullPointerException.class, () -> rules.decide(null, Move.ROCK));
        assertThrows(NullPointerException.class, () -> rules.decide(Move.ROCK, null));
    }

    // ---------- B) Scoreboard Tests ----------
    @Test
    public void scoreboard_recordsHumanWin() {
        Scoreboard sb = new Scoreboard();
        sb.record(RoundResult.HUMAN_WIN);
        assertEquals(1, sb.getP1Wins());
        assertEquals(0, sb.getP2Wins());
        assertEquals(0, sb.getDraws());
    }

    @Test
    public void scoreboard_recordsComputerWin() {
        Scoreboard sb = new Scoreboard();
        sb.record(RoundResult.COMPUTER_WIN);
        assertEquals(0, sb.getP1Wins());
        assertEquals(1, sb.getP2Wins());
        assertEquals(0, sb.getDraws());
    }

    @Test
    public void scoreboard_recordsDraw() {
        Scoreboard sb = new Scoreboard();
        sb.record(RoundResult.DRAW);
        assertEquals(0, sb.getP1Wins());
        assertEquals(0, sb.getP2Wins());
        assertEquals(1, sb.getDraws());
    }

    @Test
    public void scoreboard_recordsMultipleResults() {
        Scoreboard sb = new Scoreboard();
        sb.record(RoundResult.HUMAN_WIN);
        sb.record(RoundResult.COMPUTER_WIN);
        sb.record(RoundResult.DRAW);
        sb.record(RoundResult.DRAW);

        assertEquals(1, sb.getP1Wins());
        assertEquals(1, sb.getP2Wins());
        assertEquals(2, sb.getDraws());
    }

    @Test
    public void scoreboard_overallWinner_humanLeading() {
        Scoreboard sb = new Scoreboard();
        sb.record(RoundResult.HUMAN_WIN);
        sb.record(RoundResult.HUMAN_WIN);
        sb.record(RoundResult.COMPUTER_WIN);

        assertEquals(RoundResult.HUMAN_WIN, sb.getOverallWinner());
    }

    @Test
    public void scoreboard_overallWinner_computerLeading() {
        Scoreboard sb = new Scoreboard();
        sb.record(RoundResult.COMPUTER_WIN);
        sb.record(RoundResult.COMPUTER_WIN);
        sb.record(RoundResult.HUMAN_WIN);

        // This test will FAIL until you fix getOverallWinner()'s else-if condition
        assertEquals(RoundResult.COMPUTER_WIN, sb.getOverallWinner());
    }

    @Test
    public void scoreboard_overallWinner_tie() {
        Scoreboard sb = new Scoreboard();
        sb.record(RoundResult.HUMAN_WIN);
        sb.record(RoundResult.COMPUTER_WIN);

        assertEquals(RoundResult.DRAW, sb.getOverallWinner());
    }

    // ---------- C) GameEngine Tests ----------
    @Test
    public void gameEngine_playRoundUsesPlayersAndRules() {
        RuleEngine rules = new StandardRpsRuleEngine();
        GameEngine engine = new GameEngine(rules);

        Player human = new FixedPlayer(Move.ROCK);
        Player comp = new FixedPlayer(Move.SCISSORS);

        RoundOutcome outcome = engine.playRound(human, comp, 1);

        assertEquals(Move.ROCK, outcome.getHumanMove());
        assertEquals(Move.SCISSORS, outcome.getComputerMove());
        assertEquals(RoundResult.HUMAN_WIN, outcome.getResult());
    }

    @Test
    public void gameEngine_rejectsNullPlayers() {
        GameEngine engine = new GameEngine(new StandardRpsRuleEngine());
        Player p = new FixedPlayer(Move.ROCK);

        assertThrows(NullPointerException.class, () -> engine.playRound(null, p, 1));
        assertThrows(NullPointerException.class, () -> engine.playRound(p, null, 1));
    }

    @Test
    public void gameEngine_rejectsNullRuleEngine() {
        assertThrows(NullPointerException.class, () -> new GameEngine(null));
    }

    // ---------- D) Player Tests ----------
    @Test
    public void computerRandomPlayer_alwaysReturnsValidMove() {
        Player comp = new ComputerRandomPlayer();
        for (int i = 1; i <= 200; i++) {
            Move m = comp.makeMove(i);
            assertNotNull(m);
            assertTrue(m == Move.ROCK || m == Move.PAPER || m == Move.SCISSORS);
        }
    }

    @Test
    public void humanPlayer_acceptsValidInput() {
        String input = "2\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        HumanPlayer human = new HumanPlayer("Alice", scanner);

        assertEquals(Move.PAPER, human.makeMove(1));
    }

    @Test
    public void humanPlayer_retriesOnInvalidThenAccepts() {
        String input = "x\n9\n1\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        HumanPlayer human = new HumanPlayer("Alice", scanner);

        assertEquals(Move.ROCK, human.makeMove(1));
    }

    // ---------- E) Main CLI Tests ----------
    @Test
    public void main_runsGame() throws Exception {
        StringBuilder input = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            input.append("1\n");
        }

        String output = runMainWithInput(input.toString());

        assertTrue(output.contains("Command-Line Rock-Paper-Scissors"));
        assertEquals(20, countOccurrences(output, " - Choose (1=rock, 2=paper, 3=scissors): "));
        assertEquals(20, countOccurrences(output, "Score: Human:"));
        assertTrue(output.contains("Final Score"));
        assertTrue(
            output.contains("Overall Winner: Human")
                || output.contains("Overall Winner: Computer")
                || output.contains("Overall Result: Draw")
        );
    }

    @Test
    public void main_handlesInvalidInput() throws Exception {
        StringBuilder input = new StringBuilder();
        input.append("x\n9\n");
        for (int i = 0; i < 20; i++) {
            input.append("1\n");
        }

        String output = runMainWithInput(input.toString());

        assertTrue(output.contains("Invalid choice. Please try again."));
        assertEquals(2, countOccurrences(output, "Invalid choice. Please try again."));
        assertEquals(22, countOccurrences(output, " - Choose (1=rock, 2=paper, 3=scissors): "));
        assertTrue(output.contains("Final Score"));
    }

    private static String runMainWithInput(String input) throws Exception {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream testOut = new PrintStream(outBytes, true, "UTF-8");

        try {
            System.setIn(testIn);
            System.setOut(testOut);
            Main.main(new String[0]);
            return outBytes.toString("UTF-8");
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
            testOut.close();
        }
    }

    private static int countOccurrences(String text, String token) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(token, index)) != -1) {
            count++;
            index += token.length();
        }
        return count;
    }
}
