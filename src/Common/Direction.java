package Common;

public enum Direction {

    LEFT,
    RIGHT,
    DOWN,
    UP;

    private Direction rotated;
    private int[] directionVector;
    private Direction opposite;

    static {
        LEFT.directionVector = new int[]{0,-1};
        //LEFT.rotated = UP;
        LEFT.opposite = RIGHT;

        RIGHT.directionVector = new int[]{0,1};
        RIGHT.rotated = DOWN;
        RIGHT.opposite = LEFT;

        DOWN.directionVector = new int[]{1,0};
        DOWN.rotated = RIGHT;
        DOWN.opposite = UP;

        UP.directionVector = new  int[]{-1,0};
        //UP.rotated = RIGHT;
        UP.opposite = DOWN;
    }

    public Direction getOpposite(){
        return opposite;
    }

    public Direction getRotated(){
        return rotated;
    }

    public int[] getDirectionVector(){
        return directionVector;
    }

}
