import java.util.ArrayList;

class Turns {

    private ArrayList<Integer> playersIndex;
    private int latestIndex;

    Turns(){
        latestIndex = 0;
        playersIndex = new ArrayList<>();
    }

    void setLatestIndex(int who){
        latestIndex = who;
    }

    int getCurrent(){
        return latestIndex;
    }

    int remaining(){
        return playersIndex.size();
    }

    void addPlayer(int who){
        playersIndex.add(who);
    }

    void removePlayer(int who){
        playersIndex.remove(who);
    }

    int nextPlayerIndex(){

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
