package view;

import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import javax.swing.JPanel;

public abstract class MenuPanel extends JPanel{
    
    public MenuPanel(){
        super();
    }
    
    public MenuPanel(LayoutManager lm){
        super(lm);
    }
    
    public abstract void addActionListener(ActionListener al);
    
}
