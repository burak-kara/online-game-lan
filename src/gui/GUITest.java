package gui;

import battlefield.BattleField;
import network.Client;

public class GUITest {
    public static void main(String[] args) {
        BattleField field = new BattleField();
        Client client = new Client();
        Controller controller = new Controller(field, client);
        controller.startGame();
    }
}
