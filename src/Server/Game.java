package Server;

import Common.PlayerBoard;

public class Game {

    private PlayerBoard[] playerBoards;
    private int whoWon;

    Game(){
        playerBoards = new PlayerBoard[3];
    }

    boolean canStart(){
        for (PlayerBoard pb: playerBoards ) {
            if(pb == null){
                return false;
            }
        }
        return true;
    }

    boolean attack(int id, int x, int y){
        return playerBoards[id].getAttacked(x, y);
        //checkGameOverFor(id);
        //gameIsOver();
    }

    boolean gameIsOver() {
        int i = 0;
        int id = 0;
        for (PlayerBoard pb: playerBoards ){
            if(pb.isGameOver()){
                i++;
            }
            if(i == 2){
                whoWon = id;
                return true;
            }
            id++;
        }
        return false;
    }

    //TODO: SOMETHING ABOUT GAME OVER
    //TODO: SEND TO CLIENT THAT HE HAS LOST
    //TODO: REMOVE THE OPTION TO ATTACK HIM ON OTHERS

    boolean isGameOverFor(int id) {
        return (playerBoards[id].isGameOver());
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

    PlayerBoard getPlayerBoard(int i){
        return playerBoards[i];
    }

    public int getWhoWon() {
        return whoWon;
    }
}
