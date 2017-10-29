package Common;

public enum Direction {

    LEFT,
    RIGHT,
    DOWN,
    UP;

    private Direction rotated;
    private int[] directionVector;

    static {
        LEFT.directionVector = new int[]{-1,0};
        //LEFT.rotated = UP;
        RIGHT.directionVector = new int[]{1,0};
        RIGHT.rotated = DOWN;
        DOWN.directionVector = new int[]{0,1};
        DOWN.rotated = RIGHT;
        UP.directionVector = new  int[]{0,-1};
        //UP.rotated = RIGHT;
    }

    Direction getRotated(){
        return rotated;
    }

    int[] getDirectionVector(){
        return directionVector;
    }

}
