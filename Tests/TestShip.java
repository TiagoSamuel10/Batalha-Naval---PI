public class TestShip {

    public static void main(String[] args){
        PlayerBoard pb = new PlayerBoard();
        System.out.println(
                pb.canShipBeHere(new Ship(4, 5, Direction.RIGHT, Ship.ShipType.Four)
                )
        );
        System.out.println(
                pb.canShipBeHere(new Ship(0, 0, Direction.LEFT, Ship.ShipType.Four)
                )
        );
        System.out.println(
                pb.canShipBeHere(new Ship(2, 2, Direction.UP, Ship.ShipType.Four)
                )
        );
        System.out.println(
                pb.canShipBeHere(new Ship(0, 0, Direction.DOWN, Ship.ShipType.Four)
                )
        );
        pb.placeShip(new Ship(4,5,Direction.LEFT, Ship.ShipType.Three));
        pb.placeShip(new Ship(0, 0, Direction.DOWN, Ship.ShipType.Four));
        System.out.println(
                pb.canShipBeHere(new Ship(0, 0, Direction.DOWN, Ship.ShipType.Four)
                )
        );
    }

}
