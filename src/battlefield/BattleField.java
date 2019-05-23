package battlefield;

import exception.ShipBoundException;
import exception.ShipExistException;
import exception.ShotException;

public class BattleField {
    private final int WIDTH = 10;
    private final int HEIGHT = 10;
    private final int EMPTY = 0;
    private final int MISS = -1;
    private final int HIT = 1;
    private final int HORIZONTAL = 0;
    private final int VERTICAL = 1;
    private int[][] field;

    public BattleField() {
        field = new int[WIDTH][HEIGHT];
        createField();
    }

    private void createField() {
        for (int i = 0; i < WIDTH; i++)
            for (int j = 0; j < HEIGHT; j++)
                field[i][j] = 0;
    }

    public void put(int x, int y, int direction, Ship ship) throws ShipBoundException, ShipExistException {
        if (isShipFit(x, y, direction, ship)) throw new ShipBoundException();
        if (isAnyShip(x, y, direction, ship)) throw new ShipExistException();
        addShip(x, y, direction, ship);

    }

    private boolean isShipFit(int x, int y, int direction, Ship ship) {
        if (direction == HORIZONTAL) {
            boolean isHorizontalWidth = y + ship.getWidth() > WIDTH;
            boolean isVerticalHeight = x + ship.getHeight() > HEIGHT;
            return isHorizontalWidth || isVerticalHeight;
        } else {
            boolean isVerticalWidth = y + ship.getHeight() > WIDTH;
            boolean isHorizontalHeight = x + ship.getWidth() > HEIGHT;
            return isVerticalWidth || isHorizontalHeight;
        }
    }

    private boolean isAnyShip(int x, int y, int direction, Ship ship) {
        if (direction == HORIZONTAL) {
            for (int i = y; i < y + ship.getWidth(); i++)
                if (field[x][i] != EMPTY) return true;
        } else if (direction == VERTICAL) {
            for (int i = x; i < x + ship.getWidth(); i++)
                if (field[i][y] != EMPTY) return true;
        }
        return false;
    }

    private void addShip(int x, int y, int direction, Ship ship) {
        if (direction == HORIZONTAL) {
            for (int i = y; i < y + ship.getWidth(); i++)
                field[x][i] = ship.getType();
        } else if (direction == VERTICAL){
            for (int i = x; i < x + ship.getWidth(); i++)
                field[i][y] = ship.getType();
        }
    }

    public void shoot(int x, int y) throws ShotException { // TODO is missed or hitted or empty or hit a ship
        if (isShot(x, y)) throw new ShotException();
        if (isEmpty(x, y)) field[x][y] = MISS;
        else field[x][y] = HIT;
    }

    private boolean isShot(int x, int y) {
        return field[x][y] == MISS || field[x][y] == HIT;
    }

    private boolean isEmpty(int x, int y) {
        return field[x][y] == EMPTY;
    }

    public void printField() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                System.out.print(field[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public int[][] getBoard() {
        return field;
    }

    public void setBoard(int[][] field) {
        this.field = field;
    }

    public int getCell(int x, int y) {
        return field[x][y];
    }
}

