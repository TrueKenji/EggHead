package view;

import bot.CommonInformation;
import bot.ConditionalInformation;
import bot.PlayerBot;
import bot.PlayerBotTuple;
import bot.ProjectionTables;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import logger.Logger;
import model.Stack;

public class GamePanel extends JPanel {

    JPanel[] boxes;
    PlayerBoxPanel[] playerPanels;
    ProjectionTable[] projectionTables;
    int[] mapping = {1, 5, 7, 3}; // players are located topMiddle, middleEast, southMiddle, middleWest
    int[] PTMapping = {2, 8, 6, 0};
    JLabel questionLabel = new JLabel();
    JLabel stateLabel = new JLabel();
    JTextField answerTextField = new JTextField("Insert Answer");
    private final Logger logger = Logger.getLogger();

    public GamePanel() {
        super(new GridLayout(3, 3));
    }

    public void init(Map<PlayerBot, Stack> players) {
        boxes = new JPanel[9];
        for (int i = 0; i < boxes.length; i++) {
            boxes[i] = new JPanel(new BorderLayout());
            add(boxes[i]);
        }

        answerTextField.setEnabled(false);
        boxes[4].add(questionLabel, BorderLayout.NORTH);
        boxes[4].add(stateLabel, BorderLayout.CENTER);
        boxes[4].add(answerTextField, BorderLayout.SOUTH);

        boxes[4].setBackground(Color.white);

        playerPanels = new PlayerBoxPanel[players.size()];
        projectionTables = new ProjectionTable[players.size()];
        int[][] rgb = {{106, 90, 205}, {143, 188, 143}, {211, 211, 211}, {245, 245, 220}};
        for (PlayerBot player : players.keySet()) {
            int i = player.getID();
            Color bg = new Color(rgb[i][0], rgb[i][1], rgb[i][2], 50);

            playerPanels[i] = new PlayerBoxPanel(players.get(player), player.getID());
            playerPanels[i].setBackground(bg);
            boxes[mapping[i]].add(playerPanels[i]);

            projectionTables[i] = new ProjectionTable(this);
            projectionTables[i].setBackground(bg);
            boxes[PTMapping[i]].add(projectionTables[i]);
        }
    }

    public void showQuestion(String txt) {
        questionLabel.setText("<html><p>" + txt + "</p></html>");
    }

//    public void addActionListener(ActionListener tl) {
//        answerTextField.addActionListener(tl);
//    }
    public void resetPlayerPanelBorders() {
        for (int i = 0; i < mapping.length; i++) {
            boxes[mapping[i]].setBorder(BorderFactory.createLineBorder(getBackground()));
        }
    }

    public void askForInput() {
        answerTextField.setEnabled(true);
    }

    public void preventInput() {
        logger.println("Prevent Input", Logger.VIEW);
        answerTextField.setEnabled(false);
    }

    public void displayAnswer(int answer, int id) {
        //playerPanels[id].setAnswer("" + answer);
        answerTextField.setText(answer + "");
    }

    public void deleteAnswer(int id) {
        playerPanels[id].setAnswer("");
    }

    public void showReactions(Map<PlayerBot, Boolean> reactions) {
        for (Map.Entry<PlayerBot, Boolean> entry : reactions.entrySet()) {
            playerPanels[entry.getKey().getID()].setReaction(entry.getValue());
        }
    }

    public void updateValids(PlayerBot player, List<Stack> valids) {
        playerPanels[player.getID()].setValids(valids.toString());
        playerPanels[player.getID()].setValidsSize("V: " + valids.size());
        repaint();
    }

    public void updateCommonInfo(CommonInformation newCommonInfo) {
        ProjectionTables pt = newCommonInfo.getProjectionTables();
        for (Map.Entry<PlayerBotTuple, ConditionalInformation> entry : pt.conditionalInformationMap.entrySet()) {
            PlayerBot player = pt.getMissingPlayer(entry.getKey());
            projectionTables[player.getID()].updateCommonInfo(entry.getValue());
        }
    }

    public void setCards(int playerID, Stack stack) {
        playerPanels[playerID].setCards(stack);
    }

    public void setPoints(int playerID, int points) {
        playerPanels[playerID].setPoints(points);
    }

    public void resetReactions() {
        for (PlayerBoxPanel playerPanel : playerPanels) {
            playerPanel.resetReaction();
        }
    }

    public void setGameState(String state) {
        stateLabel.setText(state);
    }

}
