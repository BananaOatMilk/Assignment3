import rps.RoundResult;

public class Scoreboard {

    private int p1Wins;
    private int p2Wins;
    private int draws;

    public Scoreboard(){
        p1Wins = 0;
        p2Wins = 0;
        draws = 0;
    }

    public void record(RoundResult result){
        switch(result){
            case RoundResult.HUMAN_WIN:
                p1Wins++;
                break;
            case RoundResult.COMPUTER_WIN:
                p2Wins++;
                break;
            case DRAW:
                draws++;
                break;
        }  
             
    }

    public RoundResult getOverallWinner(){
        if(p1Wins > p2Wins){
            return RoundResult.HUMAN_WIN;
        } else if (p1Wins > p2Wins){
            return RoundResult.COMPUTER_WIN;
        } else {
            return RoundResult.DRAW;
        }
    }

    public int getP1Wins() {
        return p1Wins;
    }

    public int getP2Wins() {
        return p2Wins;
    }

    public int getDraws() {
        return draws;
    }
}
