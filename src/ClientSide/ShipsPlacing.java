package ClientSide;

import Common.PlayerBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class ShipsPlacing extends JLayeredPane{

    private ShipsPlacing me;
    private PlayerBoard playerBoard;
    private GraphicalBoard graphicalBoard;
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

        graphicalBoard = new GraphicalBoard(playerBoard.getToSendToPaint());
        graphicalBoard.visibleForPlayer();

        add(graphicalBoard, 0, 7);

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
        remove(graphicalBoard);
        graphicalBoard = new GraphicalBoard(pb.getToSendToPaint());
        graphicalBoard.visibleForPlayer();
        add(graphicalBoard, 0, 7);
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
            try{
                currentFocused = (GraphShip) me.findComponentAt(e.getPoint());
                currentFocused.setBackground(Color.BLACK);
                System.out.println(currentFocused);
                if(currentFocused.alreadyPlaced){
                    playerBoard.removeShip(currentFocused.getShip());
                    remove(graphicalBoard);
                    graphicalBoard = new GraphicalBoard(playerBoard.getToSendToPaint());
                    graphicalBoard.visibleForPlayer();
                    add(graphicalBoard, 0,7);
                    me.repaint();
                }
            }catch (ClassCastException exc){
                currentFocused = null;
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);
            //System.out.println(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (currentFocused != null) {
                currentFocused.setLocation(e.getPoint());
                Point coordinatesFromClick = GameClient.getCoordinatesFromClick(e.getPoint());
                if (coordinatesFromClick != null) {
                    currentFocused.changeShipPosition(coordinatesFromClick);
                    if (playerBoard.canShipBeHere(currentFocused.getShip())) {
                        playerBoard.placeShip(currentFocused.getShip());
                        currentFocused.alreadyPlaced = true;
                        currentFocused.setBackground(new Color(1f, 0f, 0f, 0.0f));
                        remove(graphicalBoard);
                        graphicalBoard = new GraphicalBoard(playerBoard.getToSendToPaint());
                        graphicalBoard.visibleForPlayer();
                        add(graphicalBoard, 0, 7);
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
        }

    }
}
