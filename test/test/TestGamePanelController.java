//package egghead;
//
//import deprecated.ScenarioPlayerBot;
//import bot.PlayerBot;
//import utils.Utilities;
//import java.awt.GridLayout;
//import java.util.ArrayList;
//import java.util.HashMap;
//import listeners.ButtonListener;
//import model.Stack;
//import view.GamePanel;
//import view.ConsoleLogger;
//import view.TestGamePanel;
//
//public class TestGamePanelController extends GamePanelController {
//
//    //TestGamePanel testGamePanel;
//    ButtonListener bl;
//
//    public TestGamePanelController(ViewController viewController) {
//        super(viewController);
////        buttonListener = new ButtonListener();
//    }
//
//    @Override
//    public GamePanel createAndPreparePanel(HashMap<PlayerBot, Stack> players) {
//        super.createAndPreparePanel(players);
////        gamePanel = new TestGamePanel(new GridLayout(3, 3), players);
////gamePanel = super.createAndPreparePanel(players);
////        gamePanel.addActionListener(getActionListener());
//        getPanel().addButtonListener(getButtonListener());
//        return gamePanel;
//    }
//
//    @Override
//    public void createPanel(HashMap<PlayerBot, Stack> players) {
//        gamePanel = new TestGamePanel(new GridLayout(3, 3), players);
//    }
//
//    public ButtonListener getButtonListener() {
//        if (bl == null) {
//            bl = new ButtonListener(this);
//        }
//        return bl;
//    }
//
//    @Override
//    public TestGamePanel getPanel() {
//        return (TestGamePanel) gamePanel;
//    }
//
//    @Override
//    public void actionPerformed(String cmd) {
//        ConsoleLogger.println(cmd, ConsoleLogger.CONTROLLER);
//        if(cmd.startsWith("Valids")){
//            int id = Integer.parseInt(cmd.substring(cmd.length() - 1));
//            ArrayList<Stack> valids = viewController.getValids(id);
//            getPanel().setValidsBoxText(id, Utilities.arrayListToString(valids));
////            String s = new String();
////            for(Stack st : valids){
////                s += st + "\n";
////            }
////            getPanel().setValidsBoxText(id, s);
//        }else if(cmd.startsWith("Valid")){
//            int playerID = Integer.parseInt(cmd.split(":")[1]);
//            String chosenValid = cmd.split(":")[2];
//            ArrayList<Stack> valids = viewController.getValids(playerID);
//            ArrayList<String> v = Utilities.arrayListToString(valids);
//            int j = 0;
//            for(int i = 0 ; i < v.size() ; i++){
//                if(chosenValid.equals(v.get(i))){
//                    j = i;
//                }
//            }
//            Stack stack = valids.get(j);
//            
//            ArrayList<ScenarioPlayerBot> otherViews = viewController.getScenarioBotsGivenStack(playerID, stack); // go into the player and get the valids of his scenariobots, given stack
//            for(ScenarioPlayerBot bot : otherViews){
//                getPanel().setValidsBoxText(bot.getID(), Utilities.arrayListToString(valids));
//            }
//        }else{
//            super.actionPerformed(cmd);
//        }
//    }
//
////    @Override
////    public TestGamePanel getPanel(HashMap<PlayerBot, Stack> players){
////        if(gamePanel == null){
////            gamePanel = new TestGamePanel(new GridLayout(3, 3), players);
////        }
////        return (TestGamePanel) gamePanel;
////    }
//}
