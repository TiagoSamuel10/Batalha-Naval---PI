package PlayGround.WaitNotify;

public class TheVictim {

    long started;

    public TheVictim(){
        started = System.currentTimeMillis();
        System.out.println("Victim is waiting! " + started );
    }

    public void tellInstructions(){
        System.out.println("The Victim is waiting, and the crowd seeing what you'll do");
    }

    public void saved(){
        System.out.println("At last! The Victim is safe! " + (System.currentTimeMillis()-started));
    }

}
