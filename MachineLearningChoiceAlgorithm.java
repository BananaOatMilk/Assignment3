import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public final class MachineLearningChoiceAlgorithm implements ComputerChoiceAlgorithm {
    private final int windowSize;
    private final Path dataFile;
    private final Random random;
    private final Deque<Move> recentChoices;
    private final Map<String, Integer> sequenceFrequency;
    private Move lastPredictedHumanMove;

    public MachineLearningChoiceAlgorithm(int windowSize, Path dataFile) {
        this(windowSize, dataFile, new Random());
    }

    MachineLearningChoiceAlgorithm(int windowSize, Path dataFile, Random random) {
        if (windowSize < 3 || windowSize % 2 == 0) {
            throw new IllegalArgumentException("windowSize must be an odd number >= 3");
        }

        this.windowSize = windowSize;
        this.dataFile = Objects.requireNonNull(dataFile, "dataFile cannot be null");
        this.random = Objects.requireNonNull(random, "random cannot be null");
        this.recentChoices = new ArrayDeque<>();
        this.sequenceFrequency = new HashMap<>();
        loadData();
    }

    public Move chooseMove() {
        lastPredictedHumanMove = null;

        if (recentChoices.size() < windowSize - 1) {
            return randomMove();
        }

        String context = encodeTail(windowSize - 1);
        int bestFrequency = 0;
        List<Move> bestPredictions = new ArrayList<>();

        for (Move predictedHumanMove : Move.values()) {
            String key = context + encodeMove(predictedHumanMove);
            int frequency = sequenceFrequency.getOrDefault(key, 0);
            if (frequency > bestFrequency) {
                bestFrequency = frequency;
                bestPredictions.clear();
                bestPredictions.add(predictedHumanMove);
            } else if (frequency == bestFrequency && frequency > 0) {
                bestPredictions.add(predictedHumanMove);
            }
        }

        if (bestFrequency == 0) {
            return randomMove();
        }

        lastPredictedHumanMove = bestPredictions.get(random.nextInt(bestPredictions.size()));
        return counterMove(lastPredictedHumanMove);
    }

    @Override
    public Move chooseMove(int round) {
        return chooseMove();
    }

    @Override
    public void recordRound(Move humanMove, Move computerMove) {
        Objects.requireNonNull(humanMove, "humanMove cannot be null");
        Objects.requireNonNull(computerMove, "computerMove cannot be null");

        appendChoice(humanMove);
        if (recentChoices.size() == windowSize) {
            String sequence = encodeChoices(recentChoices);
            sequenceFrequency.merge(sequence, 1, Integer::sum);
        }
        appendChoice(computerMove);
    }

    @Override
    public void finishGame() throws IOException {
        saveData();
    }

    public void saveData() throws IOException {
        List<String> rows = new ArrayList<>();
        List<String> keys = new ArrayList<>(sequenceFrequency.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            rows.add(key + ":" + sequenceFrequency.get(key));
        }

        if (dataFile.getParent() != null) {
            Files.createDirectories(dataFile.getParent());
        }
        Files.write(
            dataFile,
            rows,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.WRITE
        );
    }

    private void loadData() {
        if (!Files.exists(dataFile)) {
            return;
        }

        try {
            for (String line : Files.readAllLines(dataFile, StandardCharsets.UTF_8)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }

                int separator = trimmed.indexOf(':');
                if (separator <= 0 || separator == trimmed.length() - 1) {
                    continue;
                }

                String key = trimmed.substring(0, separator).trim();
                if (key.length() != windowSize) {
                    continue;
                }

                int value = Integer.parseInt(trimmed.substring(separator + 1).trim());
                if (value > 0) {
                    sequenceFrequency.put(key, value);
                }
            }
        } catch (IOException | NumberFormatException ignored) {
            // Ignore invalid persisted data and continue with whatever could be loaded.
        }
    }

    private void appendChoice(Move move) {
        recentChoices.addLast(move);
        while (recentChoices.size() > windowSize) {
            recentChoices.removeFirst();
        }
    }

    private Move randomMove() {
        return Move.values()[random.nextInt(Move.values().length)];
    }

    private String encodeTail(int tailSize) {
        int skip = recentChoices.size() - tailSize;
        StringBuilder sb = new StringBuilder(tailSize);
        int index = 0;
        for (Move move : recentChoices) {
            if (index >= skip) {
                sb.append(encodeMove(move));
            }
            index++;
        }
        return sb.toString();
    }

    private String encodeChoices(Deque<Move> choices) {
        StringBuilder sb = new StringBuilder(choices.size());
        for (Move move : choices) {
            sb.append(encodeMove(move));
        }
        return sb.toString();
    }

    private char encodeMove(Move move) {
        switch (move) {
            case ROCK:
                return 'R';
            case PAPER:
                return 'P';
            case SCISSORS:
                return 'S';
            default:
                throw new IllegalStateException("Unexpected move: " + move);
        }
    }

    private Move counterMove(Move predictedHumanMove) {
        switch (predictedHumanMove) {
            case ROCK:
                return Move.PAPER;
            case PAPER:
                return Move.SCISSORS;
            case SCISSORS:
                return Move.ROCK;
            default:
                throw new IllegalStateException("Unexpected move: " + predictedHumanMove);
        }
    }

    public Move getLastPredictedHumanMove() {
        return lastPredictedHumanMove;
    }
}
