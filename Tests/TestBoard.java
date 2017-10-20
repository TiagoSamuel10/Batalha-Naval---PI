public class TestBoard {

    public static void main(String[] args) {

        //CHECK IT HAS 20 PIECES
        for(int i = 0; i < 100000; i++){
            Game.getRandomPlayerBoard().forTests();
        }
    }
}
