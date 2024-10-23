package view;

import bot.PlayerBot;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import model.Stack;

public class PlayerBoxPanel extends JPanel {

    private final JLabel scoreLabel = new JLabel("0", SwingConstants.CENTER);
    private final JLabel answerLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel reactionLabel = new JLabel("Reaction...", SwingConstants.CENTER);
    private final JLabel validsSizeLabel = new JLabel("" + PlayerBot.ALL_STACKS.size(), SwingConstants.CENTER);
    private final JLabel remainingValidsLabel = new JLabel("<html>Remaining: " + PlayerBot.ALL_STACKS.toString() + "</html>");
    private final ArrayList<JLabel> cardsLabels = new ArrayList();

    public PlayerBoxPanel(Stack s, int playerId) {
        super(new BorderLayout());

        JPanel north = new JPanel(new GridLayout(1, 2));
        north.setOpaque(false);
        north.add(new JLabel("Player: " + playerId));

        JPanel scorePanel = new JPanel();
        scorePanel.setOpaque(false);

        scorePanel.add(new JLabel("<html><b>Score:</b></html>"));
        scorePanel.add(scoreLabel);
        north.add(scorePanel);
        add(north, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);

        JLabel maxDimension = new GradientLabel("0");

        for (int i = 0; i < Stack.STACKSIZE; i++) {
            cardsLabels.add(new GradientLabel("" + s.get(i)));
            center.add(cardsLabels.get(i));
            cardsLabels.get(i).setPreferredSize(maxDimension.getPreferredSize());
            cardsLabels.get(i).setHorizontalAlignment(SwingConstants.CENTER);
        }
        add(center, BorderLayout.CENTER);

        JPanel south = new JPanel(new GridLayout(2, 1));

        south.add(remainingValidsLabel);

        JPanel bottomSouth = new JPanel(new GridLayout(1, 2));
        bottomSouth.setOpaque(false);

        reactionLabel.setOpaque(true);
        reactionLabel.setBorder(BorderFactory.createDashedBorder(null));
        Font font = reactionLabel.getFont();
        reactionLabel.setFont(font.deriveFont(Font.ITALIC));
        reactionLabel.setForeground(Color.GRAY);
        bottomSouth.add(reactionLabel);

        JPanel bottomRight = new JPanel(new GridLayout(2, 1));
        bottomRight.setOpaque(false);
        bottomRight.setBackground(Color.white);
        bottomRight.add(new JLabel("<html><i>Remaining</i></html>", SwingConstants.CENTER));
        bottomRight.add(validsSizeLabel);

        bottomSouth.add(bottomRight);

        south.add(bottomSouth);

        add(south, BorderLayout.SOUTH);
    }

    public void setAnswer(String answer) {
        answerLabel.setText(answer);
    }

    public void setReaction(boolean reaction) {
        reactionLabel.setBackground(reaction ? Color.GREEN : Color.RED);
    }

    public void setCards(Stack s) {
        for (int i = 0; i < s.size(); i++) {
            cardsLabels.get(i).setText("" + s.get(i));
        }
    }

    public void resetReaction() {
        reactionLabel.setBackground(Color.WHITE);
    }

    public void setValidsSize(String txt) {
        validsSizeLabel.setText(txt);
    }
    
    public void setValids(String txt){
        remainingValidsLabel.setText("<html>Remaining: " + txt + "</html>");
    }

    public void setPoints(int points) {
        scoreLabel.setText(points + "");
    }

    public static class GradientLabel extends JLabel {

        static Map<String, List<Color>> colorMap = initColorMap();

        public GradientLabel(String txt) {
            super(txt);
            setFont(new Font("Comic Sans MS", Font.PLAIN, 50));
            setHorizontalAlignment(SwingConstants.CENTER);
            Border border = BorderFactory.createLineBorder(Color.GRAY, 2);
            setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        }

        @Override
        public void setText(String txt) {
            super.setText(txt);
            if (!txt.isEmpty()) {
                setForeground(colorMap.get(txt).get(0));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            int width = getWidth();
            int height = getHeight();
            GradientPaint gradient = new GradientPaint(0, 0, colorMap.get("2").get(1), width, height, colorMap.get("2").get(2));
            g2d.setPaint(gradient);
            g2d.fill(new Rectangle2D.Double(0, 0, width, height));
            g2d.dispose();
            super.paintComponent(g);
        }

        public static Map<String, List<Color>> initColorMap() {
            Map<String, List<Color>> colorMap = new HashMap();
            int alpha = 200;
            List<Color> l = new ArrayList();
            l.add(Color.RED);
            l.add(new Color(0, 255, 255, alpha));
            l.add(new Color(0, 0, 255, alpha));
            colorMap.put("0", l);

            l = new ArrayList();
            l.add(Color.BLUE);
            l.add(new Color(255, 255, 0, alpha));
            l.add(new Color(255, 0, 0, alpha));
            colorMap.put("1", l);

            l = new ArrayList();
            l.add(Color.BLACK);
            l.add(new Color(255, 255, 255, alpha));
            l.add(new Color(100, 100, 100, alpha));
            colorMap.put("2", l);

            l = new ArrayList();
            l.add(Color.YELLOW);
            l.add(new Color(0, 0, 255, alpha));
            l.add(new Color(255, 0, 255, alpha));
            colorMap.put("3", l);

            l = new ArrayList();
            l.add(Color.ORANGE);
            l.add(new Color(0, 255, 255, alpha));
            l.add(new Color(0, 0, 255, alpha));
            colorMap.put("4", l);

            l = new ArrayList();
            l.add(Color.CYAN);
            l.add(new Color(255, 0, 0, alpha));
            l.add(new Color(0, 255, 0, alpha));
            colorMap.put("5", l);

            return colorMap;
        }

    }

}
