package Common;

/**
 * Common.Direction é um enum para mais fácil dizer a direção do barco;
 * <p>
 *     Pode começar em (1,1) e depois ir para (1,2) ou (1,0) ou (0,1) ou (2,1);
 *     Ou seja: Para baixo, para cima, para esquerda ou para a direita
 * </p>
 */

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
