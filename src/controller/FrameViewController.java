package controller;

import bot.CommonInformation;
import utils.Option;
import question.QuestionUtilities;
import bot.PlayerBot;
import question.QuestionList.Question;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import listeners.UpdateEvent;
import model.GameModel;
import model.Stack;
import view.GamePanel;
import view.InGameMenuPanel;
import view.MainMenuPanel;
import view.OptionsPanel;

public class FrameViewController extends ViewController implements ActionListener {

    public static final ImageIcon BG = new ImageIcon(GamePanel.class.getResource("Title.jpg"));

    protected JLabel backgroundLabel = new JLabel(BG);
    protected JFrame frame;
    protected JPanel mainPanel;
    protected JComponent previousMainComponent;
    protected JComponent currentMainComponent;
    private GamePanel gamePanel;
    private final OptionsPanel optionsPanel;
    private final MainMenuPanel mainMenuPanel;
    private final InGameMenuPanel inGameMenuPanel;

    public FrameViewController(EggHeadController eggHeadController) {
        super(eggHeadController);
        this.mainMenuPanel = new MainMenuPanel();
        this.inGameMenuPanel = new InGameMenuPanel();
        mainMenuPanel.addActionListener(this);
        inGameMenuPanel.addActionListener(this);
        optionsPanel = new OptionsPanel(this);
        optionsPanel.init(eggHeadController.getOptions());
        initFrame();
    }

    public final void initFrame() {
        frame = new JFrame("EggHead");
        frame.setFocusable(true);
        frame.setBackground(Color.black);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.DARK_GRAY);

        backgroundLabel.setPreferredSize(new Dimension(BG.getIconWidth(), BG.getIconHeight()));

        switchMainComponent(backgroundLabel);
        mainPanel.add(mainMenuPanel, BorderLayout.WEST);

        frame.add(mainPanel);
        sizeAndPositionFrame(false);
    }

    public void optionsOpened() {
        eggHeadController.optionsOpened();
        inGameMenuPanel.optionsOpened();
        optionsPanel.applyOptions(eggHeadController.getOptions());
        switchMainComponent(optionsPanel);
    }

    private void sizeAndPositionFrame(boolean extend) {
        if (extend) {
            // give everything some room to breathe
            Dimension size = frame.getSize();
            int newWidth = (int) (size.width * 1.8);
            int newHeight = (int) (size.height * 1.8);
            frame.setSize(newWidth, newHeight);
        } else {
            frame.pack();
        }
        frame.setLocationRelativeTo(null);
    }

    @Override
    public void endTurn() {
        updateValidSizes();
        preventInput();
        deleteAnswer(model.getCurrentPlayer().getID());
        gamePanel.resetReactions();
    }

    @Override
    public void show() {
        frame.setVisible(true);
    }

    @Override
    public void reset() {
        switchMainComponent(backgroundLabel);
        mainPanel.remove(inGameMenuPanel);
        mainPanel.add(mainMenuPanel, BorderLayout.WEST);
        frame.pack();
    }

    @Override
    public void startGame() {
        gamePanel = new GamePanel();
        gamePanel.init(model.getPlayers());
        mainPanel.add(inGameMenuPanel, BorderLayout.WEST);
        switchMainComponent(gamePanel);
        mainPanel.remove(mainMenuPanel);
        inGameMenuPanel.setEnabled(true);
        sizeAndPositionFrame(true);
        frame.revalidate();
    }

    @Override
    public void setMode(Option mode) {
        inGameMenuPanel.setMode(mode);
    }

    @Override
    public void showQuestion(Question question) {
        gamePanel.showQuestion(QuestionUtilities.getQuestionText(question));
    }

    @Override
    public void setModel(GameModel model) {
        super.setModel(model);
    }

    @Override
    public void displayAnswer(int answer, int id) {
        gamePanel.displayAnswer(answer, id);
    }

    @Override
    public void askForInput() {
        gamePanel.askForInput();
        inGameMenuPanel.askForInput();
    }

    @Override
    public void showReactions(Map<PlayerBot, Boolean> reactions) {
        gamePanel.showReactions(reactions);
    }

    @Override
    public void playerSolved(PlayerBot player) {
        gamePanel.setCards(player.getID(), model.getPlayerStack(player));
        gamePanel.setPoints(player.getID(), model.getPlayerPoints(player));
    }

    public void switchMainComponent(JComponent newComponent) {
        if (newComponent != null && newComponent != currentMainComponent) {
            mainPanel.add(newComponent);
            if (currentMainComponent != null) {
                mainPanel.remove(currentMainComponent);
            }
            previousMainComponent = currentMainComponent;
            currentMainComponent = newComponent;
            frame.revalidate();
            frame.repaint();
        }
    }

    public void switchMainComponent() {
        // used to toggle back to start screen
        switchMainComponent(previousMainComponent);
    }

    @Override
    public void updateValidSizes() {
        Map<PlayerBot, Stack> players = model.getPlayers();
        for (PlayerBot player : players.keySet()) {
            gamePanel.updateValids(player, player.getValids());
        }
    }

    @Override
    public void updateCommonInfo(CommonInformation newCommonInfo) {
        gamePanel.updateCommonInfo(newCommonInfo);
    }

    @Override
    public void notify(UpdateEvent e) {
        if (e.getPropertyName().equals("GameState")) {
            gamePanel.setGameState("GameState: " + e.getOldValue() + " -> " + e.getNewValue());
        }
    }

    public void deleteAnswer(int id) {
        gamePanel.deleteAnswer(id);
    }

    public void preventInput() {
        gamePanel.preventInput();
    }

    public void applyOptions(Map<Class<? extends Option>, Option> options) {
        eggHeadController.applyOptions(options);
        optionsClosed();
    }

    public void optionsClosed() {
        inGameMenuPanel.setEnabled(true);
        switchMainComponent();
    }

    public void processButtonEnabled(boolean b) {
        inGameMenuPanel.processButtonEnabled(b);
    }

    public void processButtonPressed() {
        eggHeadController.step();
    }

    public void pauseButtonPressed() {
        eggHeadController.stopTimer();
        inGameMenuPanel.processButtonPressed();
    }

    public void resumeButtonPressed() {
        eggHeadController.resume();
    }

    public void resolveButtonPressed() {
        eggHeadController.resolveButtonPressed();
    }

    public void playerTurn(int playerID) {
        gamePanel.resetPlayerPanelBorders();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "New Game":
                newGameTriggered();
                break;
            case "Options":
                optionsOpened();
                break;
            case "Resolve":
                resolveButtonPressed();
                break;
            case "Pause":
                pauseButtonPressed();
                break;
            case "Resume":
                resumeButtonPressed();
                inGameMenuPanel.setProcessButtonText("Pause");
                break;
            case "Continue":
                processButtonPressed();
                break;
            case "Quit":
                eggHeadController.quit();
        }
    }

}
