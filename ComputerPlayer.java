public class Computer {
    public Move makeMove(){
        int random = (int) (Math.random() * 3) + 1;
        switch (random) {
            case 1:
                return Move.ROCK;
            case 2:
                return Move.PAPER;
            default:
                return Move.SCISSORS;
        }
    }
}
