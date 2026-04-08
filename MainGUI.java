import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainGUI extends Application {

    private static final int DEFAULT_TOTAL_ROUNDS = 20;
    private static final int ML_WINDOW_SIZE = 5;
    private static final Path ML_DATA_FILE = Path.of("ml_frequency_data.txt");

    private int totalRounds = DEFAULT_TOTAL_ROUNDS;
    private int currentRound = 1;

    private Stage stage;
    private Scoreboard scoreboard;

    private Label roundLabel;
    private Label roundsPerGameLabel;
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
        this.stage = stage;
        initializeGame();

        VBox root = new VBox(10);
        MenuBar menuBar = buildMenuBar();

        roundLabel = new Label();
        roundsPerGameLabel = new Label();
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
            menuBar,
            roundLabel,
            roundsPerGameLabel,
            rockButton,
            paperButton,
            scissorsButton,
            humanMoveLabel,
            predictionLabel,
            computerMoveLabel,
            resultLabel,
            scoreLabel
        );

        updateRoundLabels();

        Scene scene = new Scene(root, 450, 340);
        stage.setTitle("Rock Paper Scissors - ML Mode");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(e -> saveAndExit());
    }

    private MenuBar buildMenuBar() {
        Menu menu = new Menu("Menu");

        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> {
            saveAndExit();
            Platform.exit();
        });

        MenuItem startNewGameItem = new MenuItem("Start New Game");
        startNewGameItem.setOnAction(e -> promptAndStartNewGame());

        menu.getItems().addAll(aboutItem, exitItem, startNewGameItem);
        return new MenuBar(menu);
    }

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Rock Paper Scissors");
        alert.setContentText(
            "JavaFX game with machine-learning computer mode.\n"
                + "Play Rock, Paper, Scissors against a computer that adapts based on your recent moves.\n"
                + "Track wins, losses, and ties each round.\n"
                + "Default rounds per game: " + DEFAULT_TOTAL_ROUNDS
        );
        alert.initOwner(stage);
        alert.showAndWait();
    }

    private void promptAndStartNewGame() {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(totalRounds));
        dialog.setTitle("Start New Game");
        dialog.setHeaderText("Set rounds before starting the next game");
        dialog.setContentText("Rounds (default " + DEFAULT_TOTAL_ROUNDS + "): ");
        dialog.initOwner(stage);

        Optional<String> response = dialog.showAndWait();
        if (response.isEmpty()) {
            return;
        }

        int requestedRounds;
        try {
            requestedRounds = Integer.parseInt(response.get().trim());
        } catch (NumberFormatException ex) {
            showInvalidRoundsAlert();
            return;
        }

        if (requestedRounds <= 0) {
            showInvalidRoundsAlert();
            return;
        }

        startNewGame(requestedRounds);
    }

    private void showInvalidRoundsAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Round Count");
        alert.setHeaderText("Rounds must be a positive integer");
        alert.setContentText("Enter a value greater than 0.");
        alert.initOwner(stage);
        alert.showAndWait();
    }

    private void startNewGame(int requestedRounds) {
        saveGameData();

        totalRounds = requestedRounds;
        initializeGame();
        enableMoveButtons();

        humanMoveLabel.setText("Human move: None yet");
        predictionLabel.setText("Computer prediction: None yet");
        computerMoveLabel.setText("Computer move: None yet");
        resultLabel.setText("Make your move!");
        scoreLabel.setText("Human: 0 Computer: 0 Ties: 0");
        updateRoundLabels();
    }

    private void initializeGame() {
        scoreboard = new Scoreboard();
        ruleEngine = new StandardRpsRuleEngine();
        mlAlgorithm = new MachineLearningChoiceAlgorithm(ML_WINDOW_SIZE, ML_DATA_FILE);
        computerPlayer = new ComputerPlayer("Computer", mlAlgorithm);
        currentRound = 1;
    }

    private void updateRoundLabels() {
        roundLabel.setText("Round: " + currentRound + " / " + totalRounds);
        roundsPerGameLabel.setText("Rounds per game: " + totalRounds);
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
            "Computer prediction: " + (predictedHumanMove == null ? "None yet" : formatMove(predictedHumanMove))
        );
        computerMoveLabel.setText("Computer move: " + formatMove(computerMove));
        resultLabel.setText(toRoundMessage(result));

        scoreLabel.setText(
            "Human: " + scoreboard.getP1Wins()
                + " Computer: " + scoreboard.getP2Wins()
                + " Ties: " + scoreboard.getDraws()
        );

        currentRound++;
        if (currentRound <= totalRounds) {
            updateRoundLabels();
            return;
        }

        roundLabel.setText("Round: " + totalRounds + " / " + totalRounds);
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

    private void disableMoveButtons() {
        rockButton.setDisable(true);
        paperButton.setDisable(true);
        scissorsButton.setDisable(true);
    }

    private void enableMoveButtons() {
        rockButton.setDisable(false);
        paperButton.setDisable(false);
        scissorsButton.setDisable(false);
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
