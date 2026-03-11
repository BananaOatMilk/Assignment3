public class ComputerPlayer implements Player {

    private String name;

    public ComputerPlayer(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    @Override
    public Move makeMove(int round){
        int random = (int) (Math.random() * 3) + 1;
        switch(random) {
            case 1:
                return Move.ROCK;
            case 2:
                return Move.PAPER;
            default:
                return Move.SCISSORS;
        }
    }

    @Override
    public String toString(){
        return "Player: " + name;
    }
}
