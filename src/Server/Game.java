package Server;

import Common.PlayerBoard;
import Common.Ship;

public class Game {

    private PlayerBoard[] playerBoards;
    private boolean isOver;

    Game(){
        playerBoards = new PlayerBoard[3];
        isOver = false;
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
        //checkGameOver();
    }

    //TODO: GAME OVER

    private void checkGameOver() {
    }

    //TODO: SOMETHING ABOUT GAME OVER

    private void checkGameOverFor(int id) {
        if(playerBoards[id].isGameOver()){
            // TODO: SEND TO CLIENT THAT HE HAS LOST
            // TODO: REMOVE THE OPTION TO ATTACK HIM ON OTHERS
        }
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
}
