package model;

import bot.PlayerBot;

public abstract class Player {
    
    public static boolean MANUALMODE = true;
    public static boolean AUTOMATICMODE = false;
    private Stack stack;
//    private boolean mode;
    
    public Player(){
    }
    
    public Player(Stack stack){
        setStack(stack);
    }
        
    public void setStack(Stack stack){
        this.stack = stack;
    }
    
//    public void setMode(boolean mode){
//        this.mode = mode;
//    }
    
    public Stack getStack(){
        return stack;
    }
    
//    public boolean getMode(){
//        return mode;
//    }
    
    public abstract void produceAnswer();
    
//    public static Player createPlayerFromMode(boolean mode){
//        if(mode == AUTOMATICMODE){
//            return new PlayerBot();
//        }
//        return new ManualPlayer();
//    }
//    
//    public static Player createPlayerFromMode(boolean mode, Stack stack){
//        Player p = createPlayerFromMode(mode);
//        p.setStack(stack);
//        return p;
//    }
}
