package Server;

import Common.PlayerBoard;
import Common.Ship;

public class Game {

    private static final int MAX_PLAYERS = 3;

    private Turns turns;
    private int currentPlayer;
    private PlayerBoard[] playerBoards;
    boolean isOver;

    Game(){
        turns = new Turns();
        playerBoards = new PlayerBoard[MAX_PLAYERS];
        isOver = false;
    }

    public static PlayerBoard getRandomPlayerBoard(){
        PlayerBoard pb = new PlayerBoard();
        pb.placeShips(Ship.getRandomShips());
        //pb.allPieces();
        return pb;
    }

    //TODO: ATTACK

    void attack(int id, int x, int y){
        playerBoards[id].getAttacked(x, y);
        checkIfHitAnything(id);
        checkGameOverFor(id);
        checkGameOver();
    }

    //TODO: GAME OVER

    private void checkGameOver() {
    }

    //TODO: SOMETHING ABOUT HITS

    private void checkIfHitAnything(int id) {
        //playerBoards[id].getLastHit();
    }

    //TODO: SOMETHING ABOUT GAME OVER

    private void checkGameOverFor(int id) {
        if(playerBoards[id].isGameOver()){
            // TODO: SEND TO CLIENT THAT HE HAS LOST
            // TODO: REMOVE THE OPTION TO ATTACK HIM ON OTHERS
            turns.removePlayer(id);
        }
    }

    private void start() {
        for(int i = 0; i < MAX_PLAYERS; i++){
            playerBoards[i] = new PlayerBoard();
            addRandomBoatsTo(playerBoards[i]);
            turns.addPlayer(i);
            //playerBoards[i].lightItUp();
        }
        //currentPlayer = new Random().nextInt(allowed.size());
        currentPlayer = 0;

    }

    private void changeTurn(){
        currentPlayer = turns.nextPlayerIndex();
    }

    private void addRandomBoatsTo(PlayerBoard pb){
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

    void setPlayerBoard(PlayerBoard playerBoard, int i) {
        playerBoards[i] = playerBoard;
    }
}
