import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainGUI extends Application {

    private int totalRounds = 20;
    private static final int ML_WINDOW_SIZE = 5;
    private static final Path ML_DATA_FILE = Path.of("ml_frequency_data.txt");

    private int currentRound = 1;
    private Scoreboard scoreboard;

    private Label roundLabel;
    private Label predictionLabel;
    private Label computerMoveLabel;
    private Label humanMoveLabel;
    private Label resultLabel;
    private Label scoreLabel;

    private Button rockButton;
    private Button paperButton;
    private Button scissorsButton;

    private MachineLearningChoiceAlgorithm mlAlgorithm;
    private ComputerPlayer computerPlayer;
    private RuleEngine ruleEngine;

    @Override
    public void start(Stage stage) {
        initializeGame();

        VBox root = new VBox(10);

        roundLabel = new Label("Round: 1");
        predictionLabel = new Label("Computer prediction: None yet");
        computerMoveLabel = new Label("Computer move: None yet");
        resultLabel = new Label("Make your move!");
        scoreLabel = new Label("Human: 0 Computer: 0 Ties: 0");
        humanMoveLabel = new Label("Human move: None yet");

        rockButton = new Button("Rock");
        paperButton = new Button("Paper");
        scissorsButton = new Button("Scissors");

        rockButton.setOnAction(e -> playRound(Move.ROCK));
        paperButton.setOnAction(e -> playRound(Move.PAPER));
        scissorsButton.setOnAction(e -> playRound(Move.SCISSORS));

        root.getChildren().addAll(
            roundLabel,
            rockButton,
            paperButton,
            scissorsButton,
            humanMoveLabel,
            predictionLabel,
            computerMoveLabel,
            resultLabel,
            scoreLabel
        );

        Scene scene = new Scene(root, 450, 300);
        stage.setTitle("Rock Paper Scissors - ML Mode");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(e -> saveAndExit());
    }

    private void initializeGame() {
        scoreboard = new Scoreboard();
        ruleEngine = new StandardRpsRuleEngine();
        mlAlgorithm = new MachineLearningChoiceAlgorithm(ML_WINDOW_SIZE, ML_DATA_FILE);
        computerPlayer = new ComputerPlayer("Computer", mlAlgorithm);
        currentRound = 1;
    }

    private void playRound(Move humanMove) {
        if (currentRound > totalRounds) {
            resultLabel.setText("Game over!");
            disableMoveButtons();
            return;
        }
        humanMoveLabel.setText("Human move: " + formatMove(humanMove));

        Move computerMove = computerPlayer.makeMove(currentRound);
        Move predictedHumanMove = mlAlgorithm.getLastPredictedHumanMove();

        RoundResult result = ruleEngine.decide(humanMove, computerMove);
        scoreboard.record(result);

        computerPlayer.recordRound(humanMove, computerMove);

        predictionLabel.setText(
            "Computer prediction: "
                + (predictedHumanMove == null ? "None yet" : formatMove(predictedHumanMove))
        );

        computerMoveLabel.setText("Computer move: " + formatMove(computerMove));

        resultLabel.setText(
            toRoundMessage(result)
        );

        scoreLabel.setText(
            "Human: " + scoreboard.getP1Wins()
                + " Computer: " + scoreboard.getP2Wins()
                + " Ties: " + scoreboard.getDraws()
        );

        currentRound++;
        if (currentRound <= totalRounds) {
            roundLabel.setText("Round: " + currentRound);
        } else {
            roundLabel.setText("Round: " + totalRounds);
            RoundResult overall = scoreboard.getOverallWinner();
            String overallText;
            switch (overall) {
                case HUMAN_WIN:
                    overallText = "Overall Winner: Human";
                    break;
                case COMPUTER_WIN:
                    overallText = "Overall Winner: Computer";
                    break;
                case DRAW:
                    overallText = "Overall Result: Draw";
                    break;
                default:
                    throw new IllegalStateException("Unexpected result");
            }
            resultLabel.setText(resultLabel.getText() + " | Game over! " + overallText);
            disableMoveButtons();
            saveGameData();
        }
    }

    private void disableMoveButtons() {
        rockButton.setDisable(true);
        paperButton.setDisable(true);
        scissorsButton.setDisable(true);
    }

    private void saveGameData() {
        try {
            computerPlayer.finishGame();
        } catch (IOException e) {
            resultLabel.setText(resultLabel.getText() + " Could not save ML data.");
        }
    }

    private void saveAndExit() {
        saveGameData();
    }

    private String formatMove(Move move) {
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

    private String toRoundMessage(RoundResult result) {
        switch (result) {
            case HUMAN_WIN:
                return "Human Wins";
            case COMPUTER_WIN:
                return "Computer Wins";
            case DRAW:
                return "Draw";
            default:
                throw new IllegalStateException("Unexpected round result: " + result);
        }
    }
    public static void main(String[] args) {
        launch();
    }
}