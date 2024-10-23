package view;

import controller.FrameViewController;
import utils.Option;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

public class OptionsPanel extends JPanel {

    JSlider difficulty = new JSlider(0, 3);
    JSlider mode = new JSlider(0, 4);
    JCheckBox stats = new JCheckBox();
    JCheckBox player = new JCheckBox();
    EButton applyButton;
    EButton cancelButton;
    FrameViewController frameViewController;

    public OptionsPanel(FrameViewController frameViewController) {
        this.frameViewController = frameViewController;
    }

    public JPanel init(Map<Class<? extends Option>, Option> options) {
        setLayout(new GridLayout(5, 2));
        Map<JPanel, String> compList = createComponentsAndText();
        for (Map.Entry<JPanel, String> entry : compList.entrySet()) {
            JLabel label = new JLabel(entry.getValue(), SwingConstants.CENTER);
            label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            add(label);
            add(entry.getKey());
        }
        applyButton = new EButton("OK");
        applyButton.addActionListener((ActionEvent e) -> {
            frameViewController.applyOptions(getOptions());
        });
        cancelButton = new EButton("Cancel");
        cancelButton.addActionListener((ActionEvent e) -> {
            frameViewController.optionsClosed();
        });
        add(applyButton);
        add(cancelButton);
        return this;
    }

    public void applyOptions(Map<Class<? extends Option>, Option> options) {
        Option _difficulty = options.get(Option.DIFFICULTY.class);
        this.difficulty.setValue(Option.getOption(Option.DIFFICULTY.class, _difficulty));

        Option _mode = options.get(Option.MODE.class);
        this.mode.setValue(Option.getOption(Option.MODE.class, _mode));

        if (_mode == Option.MODE.TIMER_CONTROLLED) {
            Option speed = options.get(Option.SPEED.class);
            this.mode.setValue(Option.getOption(Option.SPEED.class, speed));
        }

        Option _player = options.get(Option.PLAYER.class);
        this.player.setSelected(Option.getOption(Option.PLAYER.class, _player) == 1);

        Option _stats = options.get(Option.STATS.class);
        this.stats.setSelected(Option.getOption(Option.STATS.class, _stats) == 1);
    }

    public void initSlider(JSlider slider, int majorSpacing, int minorSpacing) {
        slider.setMajorTickSpacing(majorSpacing);
        slider.setMinorTickSpacing(minorSpacing);
        slider.setSnapToTicks(true);
    }

    public JLabel createSliderLabel(String labelText, int alignment, Font f, Color color) {
        JLabel label = new JLabel(labelText, alignment);
        label.setFont(f);
        label.setForeground(color);
        label.setBorder(BorderFactory.createLineBorder(color, 2, true));
        label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        label.setAlignmentY(JLabel.TOP_ALIGNMENT);
        return label;
    }

    public JPanel createSliderLabelsPanel(JSlider slider, List<String> labels, List<String> toolTips) {
        JPanel labelsPanel = new JPanel(new GridLayout(1, labels.size()));
        List<Integer> alignments = new ArrayList();
        alignments.add(SwingConstants.LEFT);
        for (int i = 0; i < labels.size() - 2; i++) {
            alignments.add(SwingConstants.CENTER);
        }
        alignments.add(SwingConstants.RIGHT);
        Font f = new Font("Verdana", Font.BOLD, 8);
        Color infoColor = new Color(184, 207, 229);
        for (int i = 0; i < labels.size(); i++) {
            JLabel label = createSliderLabel(labels.get(i), alignments.get(i), f, infoColor);
            if (toolTips != null) {
                label.setToolTipText(toolTips.get(i));
                label.setText("<html>" + label.getText() + " <font size='3'>&#9432;</font></html>");
            }
            labelsPanel.add(label);
        }
        return labelsPanel;
    }

    public JPanel createSliderPanel(JSlider slider, List<String> labels, List<String> toolTips) {
        JPanel panel = new JPanel(new BorderLayout());
        initSlider(slider, 1, 0);
        panel.add(slider, BorderLayout.CENTER);
        panel.add(createSliderLabelsPanel(slider, labels, toolTips), BorderLayout.SOUTH);
        return panel;
    }

    public Map<Class<? extends Option>, Option> getOptions() {
        Map<Class<? extends Option>, Option> options = new HashMap();
        options.put(Option.DIFFICULTY.class, Option.getOption(Option.DIFFICULTY.class, difficulty.getValue()));
        options.put(Option.MODE.class, Option.getOption(Option.MODE.class, mode.getValue()));
        options.put(Option.SPEED.class, Option.getOption(Option.SPEED.class, ceilFloor(mode.getValue(), 3, 1)));
        options.put(Option.PLAYER.class, Option.getOption(Option.PLAYER.class, player.isSelected() ? 1 : 0));
        options.put(Option.STATS.class, Option.getOption(Option.STATS.class, stats.isSelected() ? 1 : 0));
        return options;
    }

    private int ceilFloor(int value, int ceil, int floor) {
        return Math.max(Math.min(value, ceil), floor);
    }

    public Map<JPanel, String> createComponentsAndText() {
        Map<JPanel, String> map = new LinkedHashMap();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel playerPanel = new JPanel(new GridBagLayout());
        playerPanel.add(player, gbc);

        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.add(stats, gbc);

        JPanel modePanel = createSliderPanel(mode, Arrays.asList("Manual", "Slow", "Normal", "Fast", "Continuous"), null);

        JPanel difficultyPanel = createSliderPanel(difficulty, Arrays.asList("Easy", "Normal", "Hard", "Impossible"), getDifficultyTooltip());

        map.put(playerPanel, "Manual play?");
        map.put(difficultyPanel, "Difficulty");
        map.put(modePanel, "Game Speed");
        map.put(statsPanel, "Gather statistics?");

        return map;
    }

    public List<String> getDifficultyTooltip() {
        return Arrays.asList(
                "Opponents extract little information and frequently do mistakes.",
                "Opponents extract most information and hardly do mistakes.",
                "Opponents extract all information and are never wrong.",
                "Opponents addionally extract information based on behaviour analysis."
        );
    }

}
