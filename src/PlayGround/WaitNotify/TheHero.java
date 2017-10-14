package PlayGround.WaitNotify;

import java.util.Scanner;

public class TheHero implements Runnable {

    final TheVictim theVictim;

    public TheHero(TheVictim toSave){
        theVictim = toSave;
    }

    @Override
    public void run() {
        System.out.println("The hero is here to save, just say the word.");
        synchronized(theVictim){
            try{
                Thread.sleep(500);
                Scanner s = new Scanner(System.in);
                s.next();
                theVictim.notify();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
