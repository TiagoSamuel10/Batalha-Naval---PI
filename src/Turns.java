import java.util.ArrayList;

public class Turns {

    private ArrayList<Integer> playersIndex;
    private int latestIndex;

    public Turns(){
        latestIndex = 0;
        playersIndex = new ArrayList<>();
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

        boolean foundNew = false;
        boolean foundLatest = false;
        while (!foundNew){
            for(Integer integer : playersIndex){
                if(foundLatest){
                    latestIndex = integer;
                    foundNew = true;
                    break;
                }
                if(integer.equals(latestIndex)){
                    foundLatest = true;
                }
            }
        }
        return latestIndex;
    }


}
