package ClientSide;

import Common.PlayerBoard;
import Common.Ship;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class ShipsPlacing extends JLayeredPane{

    private ShipsPlacing me;
    private PlayerBoard playerBoard;
    private MyGraphBoard selfGraphBoard;
    private GameClient gameClient;
    private GraphShip[] ships;

    ShipsPlacing(GameClient _gameClient){

        gameClient = _gameClient;

        setLayout(null);
        playerBoard = new PlayerBoard();
        me = this;
        setLocation(0,0);
        setSize(GameClient.DIMENSION);
        setBackground(Color.WHITE);

        addMouseListener(new SpecialMouseListener());
        addMouseMotionListener(new SpecialMouseListener());

        ships = GraphShip.getAll();

        for(GraphShip graphShip : ships) {
            add(graphShip, 1, 5);
        }

        selfGraphBoard = new MyGraphBoard(playerBoard.getToSendToPaint());

        add(selfGraphBoard);

    }

    void removeShips(){
        for(int i = 0; i < ships.length; i++){
            if(ships[i] != null){
                remove(ships[i]);
                ships[i] = null;
            }
        }
    }

    PlayerBoard getPlayerBoard(){
        return playerBoard;
    }

    void setPlayerBoard(PlayerBoard pb){
        playerBoard = pb;
        remove(selfGraphBoard);
        selfGraphBoard = new MyGraphBoard(pb.getToSendToPaint());
        add(selfGraphBoard, 0, 7);
        repaint();
    }

    private class SpecialMouseListener extends MouseAdapter {

        GraphShip currentFocused;

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            if(SwingUtilities.isRightMouseButton(e)){
                currentFocused.rotate();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);

            //WHICH MEANS I GOT A SHIP
            if(currentFocused != null){
                if(selfGraphBoard.getComponentAt(e.getPoint()) instanceof GraphTile){
                    GraphTile c = (GraphTile)selfGraphBoard.getComponentAt(e.getPoint());
                    System.out.println("C: " + c.getC() + "L: " + c.getL());
                    Ship copy = currentFocused.getShip().clone();
                    copy.setPoint(new Point(c.getL(), c.getC()));

                    if (playerBoard.canShipBeHere(copy)) {
                        currentFocused.changeShipPosition(c.getL(), c.getC());
                        playerBoard.placeShip(currentFocused.getShip());
                        currentFocused.alreadyPlaced = true;
                        currentFocused.setBackground(new Color(1f, 0f, 0f, 0.0f));
                        me.remove(selfGraphBoard);
                        selfGraphBoard = new MyGraphBoard(playerBoard.getToSendToPaint());
                        me.add(selfGraphBoard);
                        if (playerBoard.fullOfShips()) {
                            gameClient.shipsSet = true;
                        }
                        me.remove(currentFocused);
                    }
                }
                else{
                    try {
                        currentFocused.setBackground(Color.BLACK);
                        currentFocused = (GraphShip) me.findComponentAt(e.getPoint());
                        currentFocused.setBackground(Color.GRAY);
                        //System.out.println(currentFocused);
                    /*
                    if (currentFocused.alreadyPlaced) {
                        playerBoard.removeShip(currentFocused.getShip());
                        remove(selfGraphBoard);
                        selfGraphBoard = new MyGraphBoard(playerBoard.getToSendToPaint());
                        add(selfGraphBoard, 0, 7);
                        me.repaint();
                    }
                    */
                    } catch (ClassCastException exc) {
                        currentFocused = null;
                    }
                }
            }

            else {

                try {
                    currentFocused = (GraphShip) me.findComponentAt(e.getPoint());
                    currentFocused.setBackground(Color.GRAY);
                    //System.out.println(currentFocused);
                    /*
                    if (currentFocused.alreadyPlaced) {
                        playerBoard.removeShip(currentFocused.getShip());
                        remove(selfGraphBoard);
                        selfGraphBoard = new MyGraphBoard(playerBoard.getToSendToPaint());
                        add(selfGraphBoard, 0, 7);
                        me.repaint();
                    }
                    */
                } catch (ClassCastException exc) {
                    currentFocused = null;
                }
            }

            repaint();
            validate();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);
            //System.out.println(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {

            /*

            if (currentFocused != null) {
                currentFocused.setLocation(e.getPoint());
                //Point coordinatesFromClick = GameClient.getCoordinatesFromClick(e.getPoint());
                Component c = gameClient.getComponentAt(e.getPoint());
                if (c instanceof GraphTile) {
                    currentFocused.changeShipPosition(((GraphTile) c).getL(), ((GraphTile) c).getC());
                    if (playerBoard.canShipBeHere(currentFocused.getShip())) {
                        playerBoard.placeShip(currentFocused.getShip());
                        currentFocused.alreadyPlaced = true;
                        currentFocused.setBackground(new Color(1f, 0f, 0f, 0.0f));
                        remove(selfGraphBoard);
                        selfGraphBoard = new MyGraphBoard(playerBoard.getToSendToPaint());
                        add(selfGraphBoard, 0, 7);
                        me.repaint();
                        if (playerBoard.fullOfShips()) {
                            gameClient.shipsSet = true;
                        }
                    }
                } else {
                    currentFocused.alreadyPlaced = false;
                }
            }
            //System.out.println(e);

            */
        }

    }
}
