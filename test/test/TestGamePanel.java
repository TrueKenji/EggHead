//package view;
//
//import bot.PlayerBot;
//import java.awt.BorderLayout;
//import java.awt.GridLayout;
//import java.awt.LayoutManager;
//import java.util.ArrayList;
//import java.util.HashMap;
//import javax.swing.JButton;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextArea;
//import listeners.ButtonListener;
//import model.Stack;
//
///*
//This class implements additional testing functionality and graphics
//First step: make the valids buttons
//on click, show the valids for the other players, assuming the chosen valid
// */
//public class TestGamePanel extends GamePanel {
//
//    protected ArrayList<EButton> showAllButtons;
//    protected int[] validsMapping = {0, 2, 6, 8};
//    protected ArrayList<JTextArea> validsTextFields = new ArrayList();
//    protected ButtonListener bl;
//
//    public TestGamePanel(LayoutManager lm, HashMap<PlayerBot, Stack> players) {
//        super(lm, players);
//        showAllButtons = new ArrayList();
//        for (PlayerBot player : players.keySet()) {
//            int i = player.getID();
//            EButton button = createShowAllButton(i);
//            showAllButtons.add(button);
//            boxes[mapping[i]].add(button);
//
//            JTextArea textField = new JTextArea();
//            boxes[validsMapping[i]].setLayout(new BorderLayout());
//            JScrollPane scrollPane = new JScrollPane(textField);
//            boxes[validsMapping[i]].add(scrollPane);
//            validsTextFields.add(textField);
//        }
//    }
//
//    public EButton createShowAllButton(int id) {
//        EButton button = new EButton("Show All");
//        button.setActionCommand("Valids " + id);
//        return button;
//    }
//
//    public void addButtonListener(ButtonListener bl) {
//        this.bl = bl;
//        for (EButton button : showAllButtons) {
//            button.addActionListener(bl);
//        }
//    }
//
//    public void setValidsBoxText(int id, String txt) {
//        validsTextFields.get(id).setText(txt);
//    }
//
//    public void setValidsBoxText(int id, ArrayList<String> valids) {
//        boxes[validsMapping[id]].removeAll();
//        JPanel panel = new JPanel(new GridLayout(valids.size(), 1));
//        for (String valid : valids) {
//            EButton button = new EButton(id + ":" + valid);
//            button.setActionCommand("Valid:" + id + ":" + valid);
//            button.addActionListener(bl);
//            boxes[validsMapping[id]].add(button);
//            panel.add(button);
//        }
//        JScrollPane scrollPane = new JScrollPane(panel);
//        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//        boxes[validsMapping[id]].add(scrollPane);
//        boxes[validsMapping[id]].validate();
//    }
//
//}
