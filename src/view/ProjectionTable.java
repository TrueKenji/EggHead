package view;

import bot.ConditionalInformation;
import bot.StackScenario;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import model.Stack;

public class ProjectionTable extends JPanel {

    private List<Page> pages = new ArrayList();
    private ConditionalInformation currentInfo;
    private Page currentPage;
    private JScrollPane scrollPane;
    private int currentPageNumber = 0;
    private JButton hideButton;
    private JButton prevButton;
    private JButton nextButton;
    private JLabel pageLabel;
    private Component parent;

    public ProjectionTable(Component parent) {
        setLayout(new BorderLayout());
        initComponents();
        this.parent = parent;
    }

    private void initComponents() {
        scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        hideButton = new JButton("Hide");
        hideButton.addActionListener(e -> hidePage());

        prevButton = new JButton("Previous");
        prevButton.addActionListener(e -> showPreviousPage());

        nextButton = new JButton("Next");
        nextButton.addActionListener(e -> showNextPage());

        pageLabel = new JLabel("Page: 1/1");

        updateButtons();

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(hideButton);
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(pageLabel);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

    }

    private void hidePage() {
        scrollPane.setVisible(!scrollPane.isVisible());
        hideButton.setText(scrollPane.isVisible() ? "Hide" : "Show");
        parent.repaint();
    }

    private void showPreviousPage() {
        if (currentPageNumber > 0) {
            currentPageNumber--;
            showPage(currentPageNumber);
        }
    }

    private void showNextPage() {
        if (currentPageNumber < pages.size() - 1) {
            currentPageNumber++;
            showPage(currentPageNumber);
        }
    }

    private void showPage(int number) {
        currentPage = pages.get(number);
        scrollPane.setViewportView(currentPage);
        updateButtons();
        pageLabel.setText("Page: " + (number + 1) + "/" + pages.size());
        parent.repaint();
    }

    private void updateButtons() {
        nextButton.setEnabled(currentPageNumber < pages.size() - 1);
        prevButton.setEnabled(currentPageNumber > 0);
    }

    void updateCommonInfo(ConditionalInformation info) {
        if (currentInfo == null) {
            currentPage = new Page();
            currentPage.setInfo(info);
            pages.add(currentPage);
            currentInfo = info.deepDummyCopy();
            scrollPane.setViewportView(currentPage);
        } else {
            Page latestPage = pages.get(pages.size() - 1);
            latestPage.displayDifference(currentInfo, info);
            Page newPage = new Page();
            newPage.setInfo(info);
            pages.add(newPage);
            currentInfo = info.deepDummyCopy();
        }
        pageLabel.setText("Page: " + (currentPageNumber + 1) + "/" + pages.size());
        updateButtons();
    }

    /**
     * Display the difference of two ConditionalInformation objects assuming the
     * 2nd is a partition of the 1st. Prevent line breaks for readability
     */
    private class Page extends JTextPane {

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return getSize().width < getParent().getSize().width;
        }

        @Override
        public void setSize(Dimension d) {
            if (d.width < getParent().getSize().width) {
                d.width = getParent().getSize().width;
            }
            super.setSize(d);
        }

        public void setInfo(ConditionalInformation info) {
            StyledDocument doc = getStyledDocument();

            Style style = addStyle("Style", null);
            StyleConstants.setForeground(style, Color.BLACK);

            try {
                Iterator<StackScenario> iterator = info.iterator();
                while (iterator.hasNext()) {
                    String line = iterator.next().toFullString() + "\n";
                    doc.insertString(doc.getLength(), line, style);
//                }
//                for (Map.Entry<StackScenario, List<Stack>> entry : info.entrySet()) {
//                    String line = entry.getKey().toString() + entry.getValue().toString() + "\n";
//                    doc.insertString(doc.getLength(), line, style);
                    //StyleConstants.setForeground(style, Color.GREEN);
                }
            } catch (BadLocationException e) {
            }
        }

        public void displayDifference(ConditionalInformation old, ConditionalInformation update) {
            super.setText("");

            StyledDocument doc = getStyledDocument();

            Style style = addStyle("Style", null);
            StyleConstants.setForeground(style, Color.BLACK);

            try {
                Iterator<StackScenario> iterator = old.iterator();
                while (iterator.hasNext()) {
                    StackScenario scenario = iterator.next();
                    List<Stack> newValids = update.get(scenario).getDummy().getValids();
                    StyleConstants.setForeground(style, Color.BLACK);
                    String linePart = scenario.toString() + ": [";
                    doc.insertString(doc.getLength(), linePart, style);
                    for (Stack s : scenario.getDummy().getValids()) {
                        if (!newValids.contains(s)) {
                            StyleConstants.setForeground(style, Color.RED);
                        } else {
                            StyleConstants.setForeground(style, Color.BLACK);
                        }
                        String _linePart = s.toString() + ",";
                        doc.insertString(doc.getLength(), _linePart, style);
                    }
                    doc.insertString(doc.getLength(), "]\n", style);
                }
//                for (Map.Entry<StackScenario, List<Stack>> entry : old.entrySet()) {
//                    if (!update.containsKey(entry.getKey())) { // whole line removed
//                        StyleConstants.setForeground(style, Color.RED);
//                        String line = entry.getKey().toString() + ": " + entry.getValue().toString() + "\n";
//                        doc.insertString(doc.getLength(), line, style);
//                    } else {    // red color for the removed stacks
//                        StyleConstants.setForeground(style, Color.BLACK);
//                        String linePart = entry.getKey().toString() + ": [";
//                        doc.insertString(doc.getLength(), linePart, style);
//                        for (Stack s : entry.getValue()) {
//                            if (!update.get(entry.getKey()).contains(s)) {
//                                StyleConstants.setForeground(style, Color.RED);
//                            } else {
//                                StyleConstants.setForeground(style, Color.BLACK);
//                            }
//                            String _linePart = s.toString() + ",";
//                            doc.insertString(doc.getLength(), _linePart, style);
//                        }
//                        doc.insertString(doc.getLength(), "]\n", style);
//                    }
//                }
            } catch (BadLocationException e) {
            }
        }
    }

}
