package gui;

import battlefield.*;
import exception.*;
import network.*;

import java.awt.event.KeyEvent;

public class Controller {
    private BattleField userBoard;
    private BattleField opponentBoard;
    private View view;
    private Client client;
    private final int WIDTH = 1100;
    private final int HEIGHT = 650;
    private final Ship[] ships = {new Submarine(), new BattleShip(), new Carrier(), new Destroyer()};
    private int shipCount;

    public Controller(BattleField board, Client client) {
        initGame(board, client);
    }

    private void initGame(BattleField board, Client client) {
        this.userBoard = board;
        this.opponentBoard = new BattleField();
        this.view = new View(this);
        this.client = client;
        this.shipCount = 0;
        Thread tt = new Thread(client);
        tt.start();
    }

    public void startGame() {
        view.setSize(WIDTH, HEIGHT);
        view.setVisible(true);
        instructionMessage();
    }

    public void keyClicked(int key) {
        if (key == KeyEvent.VK_Q) {
            quitGameMessage();
        } else if (key == KeyEvent.VK_R) {
            restartGameMessage();
        }
    }

    public void userBoardClicked(int row, int column, int direction) {
        try {
            if (shipCount >= 4) throw new AllPlacedException();
            Ship ship = ships[shipCount++];
            userBoard.put(row - 1, column - 1, direction, ship);
            view.placeShipUserView(row, column, direction, ship);
            //userBoard.printField(); // For debugging
            if (shipCount == 4) allShipsPlaced();
        } catch (ShipExistException exception) {
            existMessage();
        } catch (ShipBoundException exception) {
            boundMessage();
        } catch (AllPlacedException exception) {
            placedMessage();
        }
    }

    public void opponentBoardClicked(int row, int column) {
        try {
            opponentBoard.shoot(--row, --column);
            client.sendBoard(boardToString(opponentBoard.getBoard()) + "Movee");
            view.updateOpponentView(opponentBoard.getBoard());
            //opponentBoard.printField();                       // For debugging
        } catch (ShotException exception) {
            shotMessage();
        }
    }

    private void allShipsPlaced() {
        Thread incomingMove = new Thread(new IncomingMove());
        incomingMove.start();
        view.disableUserView();
        client.sendBoard(boardToString(userBoard.getBoard()) + "First");
    }

    private String boardToString(int[][] field) {
        StringBuilder builder = new StringBuilder();
        for (int[] row : field) {
            for (int cell : row) {
                builder.append(cell).append(":");
            }
            builder.append("/");
        }
        return builder.toString();
    }

    private int[][] stringToBoard(String message) {
        String[] rows = message.split("/");
        String[][] cells = new String[rows.length][10];
        for (int i = 0; i < rows.length; i++) {
            cells[i] = rows[i].split(":");
        }
        int[][] field = new int[10][10];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                field[i][j] = Integer.valueOf(cells[i][j]);
            }
        }
        return field;
    }

    private boolean checkFirstMessage(String message) {
        return message.endsWith("First");
    }

    private boolean checkMoveMessage(String message) {
        return message.endsWith("Movee");
    }

    private String translateMessage(String message) {
        return message.substring(0, message.length() - 5);
    }

    public class IncomingMove implements Runnable {
        private boolean isFirstTime = true;

        @Override
        public void run() {
            try {
                String message;
                while ((message = client.getMessage()) != null) {
                    if (checkFirstMessage(message) && isFirstTime) {
                        opponentBoard.setBoard(stringToBoard(translateMessage(message)));
//                        System.out.println("Opp Board"); // For debugging
//                        opponentBoard.printField(); // For debugging
                        view.updateOpponentView(opponentBoard.getBoard());
                        isFirstTime = false;
                    } else if (checkMoveMessage(message)) {
                        userBoard.setBoard(stringToBoard(translateMessage(message)));
                        view.updateUserView(userBoard.getBoard());
//                        System.out.println("User Board"); // For debugging
//                        userBoard.printField(); // For debugging
                    }
                }
            } catch (Exception e) {
                System.out.println("no new field");
            }
        }
    }

    private void quitGameMessage() {
        String message = "Are you sure to quit game?";
        String title = "Quit";
        int selected = view.sendQuestionMessage(message, title);
        if (selected == 0)
            System.exit(0);
    }

    private void restartGameMessage() {
        String message = "Are you sure to restart game?";
        String title = "Restart";
        int selected = view.sendQuestionMessage(message, title);
        if (selected == 0) {
            view.dispose();
            initGame(new BattleField(), new Client());
            startGame();
        }
    }

    private void existMessage() {
        shipCount--;
        String message = "There is a ship already. \nPlease choose another place!";
        String title = "Warning";
        view.sendMessage(message, title);
    }

    private void boundMessage() {
        shipCount--;
        String message = "Ship don't fit bounds. \nPlease choose another place!";
        String title = "Warning";
        view.sendMessage(message, title);
    }

    private void placedMessage() {
        String message = "You placed all your ships. \nWait for your opponent!";
        String title = "All Placed";
        view.sendMessage(message, title);
    }

    private void instructionMessage() {
        String message = " ***   During all game play   ***\n"
                + " * Q - Quit Game\n"
                + " * R - Restart Game\n\n"
                + " * Left Click ----> Place Horizontally\n"
                + " * Right Click ---> Place Vertically\n"
                + " * Middle Click -> Shoot";
        String title = "Welcome BattleShip";
        view.sendInfoMessage(message, title);
    }

    private void shotMessage() {
        String message = "You shot there before. \nPlease try another place!";
        String title = "Wrong Shoot";
        view.sendMessage(message, title);
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }
}