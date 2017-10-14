package PlayGround.WaitNotify;

public class TheCrowd implements Runnable{

    final TheVictim theVictim;

    public TheCrowd(TheVictim victim){
        theVictim = victim;
    }

    @Override
    public void run() {
        try {
            synchronized (theVictim) {
                theVictim.tellInstructions();
                theVictim.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        theVictim.saved();
    }
}
