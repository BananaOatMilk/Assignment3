import java.util.Scanner;

public class HumanPlayer {
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
    public Game makeMove(int round){
        while(true){
            System.out.print("Round " + round + "\nChoose: Rock = 1, Paper = 2, Scissors = 3");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    return new Game(Move.ROCK);
                case 2:
                    return new Game(Move.PAPER);
                case 3:
                    return new Game(Move.SCISSORS);
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
