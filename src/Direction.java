/**
 * Direction é um enum para mais fácil dizer a direção do barco;
 * <p>
 *     Pode começar em (1,1) e depois ir para (1,2) ou (1,0) ou (0,1) ou (2,1);
 *     Ou seja: Para baixo, para cima, para esquerda ou para a direita
 * </p>
 */

public enum Direction {
    UP,
    DOWN,
    RIGHT,
    LEFT;

    private int[] directionVector;

    static {
        UP.directionVector = new int[]{-1,0};
        DOWN.directionVector = new int[]{1,0};
        RIGHT.directionVector = new int[]{0,1};
        LEFT.directionVector = new  int[]{0,-1};
    }

    public int[] getDirectionVector(){
        return directionVector;
    }

}
