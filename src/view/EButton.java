package view;

import javax.swing.JButton;

public class EButton extends JButton{
    
    public EButton(String s){
        super(s);
    }
    
    @Override
    public String getActionCommand(){
        String s = super.getActionCommand();
        if(s.isEmpty()){
            return super.getText();
        }
        return s;
    }
    
}
