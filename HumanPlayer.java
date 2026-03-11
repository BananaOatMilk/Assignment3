import java.util.Scanner;

public class HumanPlayer implements Player {
    private String name;
    private final Scanner scanner;


    public HumanPlayer(String name, Scanner scanner) {
        this.name = name;
        this.scanner = scanner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Move makeMove(int round) {
        while (true) {
            System.out.print("Round " + round + " - Choose (1=rock, 2=paper, 3=scissors): ");

            if (!scanner.hasNextInt()) {
                scanner.next();
                System.out.println("Invalid choice. Please try again.");
                continue;
            }

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    return Move.ROCK;
                case 2:
                    return Move.PAPER;
                case 3:
                    return Move.SCISSORS;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    @Override
    public String toString() {
        return "Player: " + name;
    }
}
