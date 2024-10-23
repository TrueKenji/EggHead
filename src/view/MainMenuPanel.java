package view;

import java.awt.GridLayout;
import java.awt.event.ActionListener;

public class MainMenuPanel extends MenuPanel {

    EButton newGameButton = new EButton("New Game");
    EButton optionsButton = new EButton("Options");

    public MainMenuPanel() {
        super(new GridLayout(2, 1));
        add(newGameButton);
        add(optionsButton);
    }

    @Override
    public void setEnabled(boolean b) {
        //mainMenu is never disabled
    }

    @Override
    public void addActionListener(ActionListener al) {
        newGameButton.addActionListener(al);
        optionsButton.addActionListener(al);
    }

}
