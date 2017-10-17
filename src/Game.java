import java.util.ArrayList;
import java.util.Scanner;

public class Game {

    private static final int MAX_PLAYERS = 3;

    private Turns turns;
    private int currentPlayer;
    private PlayerBoard[] playerBoards;

    Game(){
        turns = new Turns();
        start();
    }

    void attack(int id, int x, int y){
        playerBoards[id].getAttacked(x, y);
        checkIfHitAnything(id);
        checkGameOver(id);
    }

    //TODO: SOMETHING ABOUT HITS

    private void checkIfHitAnything(int id) {
        //playerBoards[id].getLastHit();
    }

    //TODO: SOMETHING ABOUT GAME OVER

    private void checkGameOver(int id) {
        //playerBoards[id].isGameOver();
    }


    private void start() {
        playerBoards = new PlayerBoard[MAX_PLAYERS];
        for(int i = 0; i < MAX_PLAYERS; i++){
            playerBoards[i] = new PlayerBoard(i);
            addRandomBoatsTo(playerBoards[i]);
            turns.addPlayer(i);
            //playerBoards[i].lightItUp();
        }
        //currentPlayer = new Random().nextInt(allowed.size());
        currentPlayer = 0;

    }

    /*

    public void playerTurn(){
        System.out.println("Player nº " + (allowed.get(currentPlayer)+ 1) + " playing");
        System.out.println("Who to getAttacked?");
        int id;
        while (true){
            id = s.nextInt() - 1;
            if(id == allowed.get(currentPlayer)){
                System.out.println("Can't getAttacked self");
            }
            else if(id >= playerBoards.length){
                System.out.println("3 players max");
            }
            else if(id < 0){
                System.out.println("A player!");
            }
            else {
                break;
            }
        }
        System.out.println(playerBoards[id]);
        System.out.println("X coord");
        int x = s.nextInt();
        System.out.println("Y coord");
        int y = s.nextInt();
        System.out.println(playerBoards[id]);
        //System.out.println(hit.gotHit());
        if(playerBoards[id].isGameOver()){
            allowed.remove(id);
        }
        if(!playerBoards[id].goAgain()){
            changeTurn();
        }
    }

    */

    public void changeTurn(){
        currentPlayer = turns.nextPlayerIndex();
    }

    /*

    public void run(){
        while (playing){
            //System.out.println(this);
            //player x, play
            playerTurn();
        }
    }

    */

    public void addRandomBoatsTo(PlayerBoard pb){
        pb.placeShips(Ship.getRandomShips());
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < playerBoards.length; i++){
            s += "Player " + (i + 1) + ": \n";
            s += playerBoards[i].toString();
        }
        return s;
    }

    public PlayerBoard[] getPlayerBoards() {
        return playerBoards;
    }

}
