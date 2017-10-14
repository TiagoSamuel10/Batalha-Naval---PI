package PlayGround.WaitNotify;

public class TheHeroAndVictim {

    public static void main(String[] args) {
        TheVictim victim = new TheVictim();
        TheCrowd crowd = new TheCrowd(victim);
        new Thread(crowd,"crowd").start();
        new Thread(crowd,"crowd2").start();

        TheHero hero = new TheHero(victim);
        TheHero hero1 = new TheHero(victim);

        new Thread(hero, "hero").start();
        new Thread(hero1, "hero1").start();
    }

}
