public class TestShip {

    public static void main(String[] args){
        PlayerBoard pb = new PlayerBoard(1);
        /*System.out.println(
                pb.canShipBeHere(new Ship(4, 5, Direction.DOWN, ShipType.Four)
                )
        );
        System.out.println(
                pb.canShipBeHere(new Ship(0, 0, Direction.UP, ShipType.Four)
                )
        );
        System.out.println(
                pb.canShipBeHere(new Ship(2, 2, Direction.LEFT, ShipType.Four)
                )
        );
        System.out.println(
                pb.canShipBeHere(new Ship(0, 0, Direction.RIGHT, ShipType.Four)
                )
        );*/
        pb.placeShip(new Ship(4,5,Direction.UP, Ship.ShipType.Three));
        pb.placeShip(new Ship(0, 0, Direction.RIGHT, Ship.ShipType.Four));
        System.out.println(
                pb.canShipBeHere(new Ship(0, 0, Direction.RIGHT, Ship.ShipType.Four)
                )
        );
        System.out.println(pb.toString());
        /*
        pb.getAttacked(0,0);
        pb.getAttacked(0,1);
        pb.getAttacked(0,2);
        pb.getAttacked(0,3);
        pb.getAttacked(4,5);
        System.out.println(pb.toString());
        */
    }

}
