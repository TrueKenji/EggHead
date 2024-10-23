package view;

import utils.Option;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

public class InGameMenuPanel extends MenuPanel {

    EButton resolveButton = new EButton("Resolve");
    EButton processButton = new EButton("Continue");
    EButton optionsButton = new EButton("Options");
    EButton quitButton = new EButton("Quit");

    public InGameMenuPanel() {
        super(new GridLayout(4, 1));
        add(resolveButton);
        add(processButton);
        add(optionsButton);
        add(quitButton);
    }

    public void setProcessButtonText(String txt) {
        processButton.setText(txt);
    }

    @Override
    public void addActionListener(ActionListener al) {
        resolveButton.addActionListener(al);
        processButton.addActionListener(al);
        optionsButton.addActionListener(al);
        quitButton.addActionListener(al);
    }

    public void processButtonPressed() {
        switch (processButton.getText()){
            case "Pause":
                setProcessButtonText("Resume");
                break;
            case "Resume":
                setProcessButtonText("Pause");
                break;
        }
    }

    public void processButtonEnabled(boolean b) {
        processButton.setEnabled(b);
    }

    public void optionsOpened() {
        if (processButton.getText().equals("Pause")) {
            processButton.setText("Resume");
        }
        setEnabled(false);
    }

    public void setMode(Option mode) {
        if (mode == Option.MODE.TIMER_CONTROLLED) {
            processButton.setText("Resume");
        }else if(mode == Option.MODE.USER_CONTROLLED){
            processButton.setText("Continue");
        }
    }

    public void askForInput() {
        processButton.setEnabled(false);
    }

    @Override
    public void setEnabled(boolean b) {
        resolveButton.setEnabled(b);
        processButton.setEnabled(b);
        optionsButton.setEnabled(b);
    }

}
