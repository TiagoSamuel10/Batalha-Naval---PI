public class TestGraph {

    public static void main(String[] args) {

        for(int i = 0; i < 10000; i++){
            PlayerBoard pb = Game.getRandomPlayerBoard();
            pb.lightItUp();
            System.out.println(pb);
        }

    }

}
