package Server;

import java.util.ArrayList;
import java.util.Arrays;

public class Turns {

    private int[] playersIndex;
    private int count;
    private int latestIndex;
    private int lastTurn;

    private static final int REMOVED = -1;

    //private ArrayList<Integer> playersIndex;


    public Turns(){
        lastTurn = 0;
        count = 0;
        playersIndex = new int[3];
    }

    public void setLatestIndex(int who){
        lastTurn = who;
    }

    public int getCurrent(){
        return lastTurn;
    }

    public int remaining(){
        return count;
    }

    public void addPlayer(int who){
        playersIndex[latestIndex] = who;
        latestIndex++;
    }

    public void removePlayer(int who){
        playersIndex[who] = REMOVED;
    }

    public int nextPlayerIndex() {

        //1 -> 2 -> 3 -> 1 -> 2
        // 1 -> /2/ -> 3 = 1 -> 3 -> 1 -> 3
        for (int i = 0; i < playersIndex.length; i++) {
            if (!(playersIndex[i] == REMOVED)) {
                if (playersIndex[i] == lastTurn) {
                    lastTurn = (i + 1) % (playersIndex.length);
                    System.out.println(lastTurn);
                    return lastTurn;
                }
            }
        }
        return REMOVED;
    }

    @Override
    public String toString() {
        return Arrays.toString(playersIndex);
    }
}
