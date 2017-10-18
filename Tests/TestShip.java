public class TestShip {

    public static void main(String[] args){
        PlayerBoard pb = new PlayerBoard();
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

        /*
        for(int i = 0; i < 10000; i++){
            for(Ship ship : Ship.getRandomShips()){
                for(ShipPiece shipPiece : ship.getPieces()){
                    if(shipPiece == null){
                        System.err.println("!!!!");
                    }
                }
            }
        }

        */

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
