import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Game {

    static final int MAX_SHIPS = 10;
    private static final int MAX_PLAYERS = 3;

    private ArrayList<Integer> allowed;
    private int currentPlayer;
    private boolean playing;
    private PlayerBoard[] playerBoards;
    private Scanner s = new Scanner(System.in);
    private boolean again;

    public Game(){
        allowed = new ArrayList<>();
        start();
        playing = true;
    }

    private void start() {
        playerBoards = new PlayerBoard[MAX_PLAYERS];
        for(int i = 0; i < MAX_PLAYERS; i++){
            playerBoards[i] = new PlayerBoard(i);
            addRandomBoatsTo(playerBoards[i]);
            allowed.add(i);
            //playerBoards[i].lightItUp();
        }
        //currentPlayer = new Random().nextInt(allowed.size());
        currentPlayer = 0;

    }

    public void playerTurn(){
        System.out.println("Player nº " + (allowed.get(currentPlayer)+ 1) + " playing");
        System.out.println("Who to attack?");
        int id;
        while (true){
            id = s.nextInt() - 1;
            if(id == allowed.get(currentPlayer)){
                System.out.println("Can't attack self");
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

    public void changeTurn(){
        //1 -> 2 -> 3 -> 1 -> 2
        // 1 -> /2/ -> 3 = 1 -> 3 -> 1 -> 3
        System.out.println(allowed.size());
        System.out.println(currentPlayer);
        if(currentPlayer + 1 >= allowed.size()){
            currentPlayer = allowed.get(0);
        }
        else{
            currentPlayer = allowed.get(currentPlayer + 1);
        }
    }

    public void run(){
        while (playing){
            //System.out.println(this);
            //player x, play
            playerTurn();
        }
    }

    public void addRandomBoatsTo(PlayerBoard pb){
       for (Ship ship:Ship.getRandomShips()) {
            pb.placeShip(ship);
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

    public PlayerBoard[] getPlayerBoards() {
        return playerBoards;
    }

    public boolean getAgain() {
        return again;
    }
}
