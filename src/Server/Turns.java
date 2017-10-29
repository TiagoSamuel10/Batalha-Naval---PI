package Server;

import java.util.ArrayList;

public class Turns {

    private ArrayList<Integer> playersIndex;
    private int latestIndex;

    public Turns(){
        latestIndex = 0;
        playersIndex = new ArrayList<>();
    }

    public void setLatestIndex(int who){
        latestIndex = who;
    }

    public int getCurrent(){
        return latestIndex;
    }

    public int remaining(){
        return playersIndex.size();
    }

    public void addPlayer(int who){
        playersIndex.add(who);
    }

    public void removePlayer(int who){
        playersIndex.remove(who);
    }

    public int nextPlayerIndex(){

        //1 -> 2 -> 3 -> 1 -> 2
        // 1 -> /2/ -> 3 = 1 -> 3 -> 1 -> 3

        boolean foundLatest = false;
        while (true){
            for(Integer integer : playersIndex){
                if(foundLatest){
                    latestIndex = integer;
                    return latestIndex;
                }
                else if(integer.equals(latestIndex)){
                    foundLatest = true;
                }
            }
        }
    }


}
