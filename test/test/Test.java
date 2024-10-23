//package bot;
//
//import bot.ProjectionTables;
//import bot.PlayerBot;
//import db.EggHeadReader;
//import db.EggHeadWriter;
//import egghead.Option;
//import egghead.QuestionUtilities;
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import model.Stack;
//import utils.Utilities;
//import view.ConsoleLogger;
//import egghead.QuestionList.Question;
//import java.awt.Toolkit;
//
//public class Test {
//
//    /**
//     * <pre>
//     * Workflow: Generate a table for each player
//     * Each table maps the combinations of all others players to the space of
//     * the remaining player
//     * The remaining player knows which row would be the correct one
//     * The other players can filter the rows for the other players, but not
//     * their own, as they don't know their combination
//     * -> The resulting subset describes scenario-based, what one player would
//     * know about the remaining valids of the remainingPlayer.One of those
//     * scenarios is the correct one. Answer the first question and update the tables as follows:
//     * Loop through the rows and hypothetically answer the question again
//     * with the combinations of the row. Calculate the valids based on the row
//     * and the question/answer. Intersection with the value of the row gives the
//     * new valids list = value of that row.
//     * all tables are equal at this point because the questions are "symmetric"
//     * Now incorporate the reactions:
//     * If a player solves, remove all length>1 rows in the table associated to
//     * that player.
//     * If a player doesn't solve, remove all length=1 rows in the table
//     * associated to that player.
//     * Now, for each row in each table, loop through the valids and for each
//     * valid check the following:
//     * choose one player and spare his key from the row. Go into the table of
//     * that player and into the row of (the chosen valid + the rest of the row).
//     * If the spared key is not in the value list the original valid can be
//     * removed, because the built scenario is not possible from this other
//     * players perspective. Since all tables are public, this information may
//     * be used in this way.
//     * Now repeat the reaction process until no more changes happen.
//     * Now ask the next question.
//     * When a player solves, update the tables as follows:
//     * The table assigned to the solving player is completely reset
//     * Optionally, all rows can be removed, which cannot be true based on the
//     * common information of all other tables.
//     * For every other table, do:
//     * For each group of keys formed from the non solving players, do:
//     * Restore all rows for the solving player as the final key, because
//     * this player can now have every combination again.
//     * The value for all those rows in one group is the valids list, which was
//     * the value for (that group + the actual combination of the solving player)
//     * </pre>
//     *
//     * @param args
//     */
//    public static void main(String[] args) {
////        QuestionUtilities.generateTestData();
////        System.out.println(Option.MODE.class.getSimpleName());
////        QuestionUtilities.test();
////        int numPlayers = 3;
////        int variant = 2; //0: NO BEHAVIOUR ANALYSIS, 1:ONE, 2:ALL
////        int numRounds = 1;
////        boolean saveToFile = false;
////        testBots(numPlayers, variant, numRounds, saveToFile);
////        statistic();
////        Utilities.test();
//    }
//
//    public static void statistic() {
//        new File("Statistic.txt").delete();
//        for (int numPlayers = 3; numPlayers < 6; numPlayers++) {
//            for (int cardsSize = 3; cardsSize < 4; cardsSize++) {
//                for (int variant = 0; variant < 1; variant++) {
//                    Stack.CARDSSIZE = cardsSize;
//                    testBots(numPlayers, variant, 500, true);
//                }
//            }
//        }
//    }
//
//    public static ArrayList<PlayerBot> generatePlayers(int numPlayers) {
//        ArrayList<PlayerBot> players = new ArrayList();
//        for (int i = 0; i < numPlayers; i++) {
//            players.add(new PlayerBot(i));
//        }
//        return players;
//    }
//
//    public static HashMap<PlayerBot, Stack> generateAdditionalPlayerView(ArrayList<PlayerBot> players) {
//        HashMap<PlayerBot, Stack> playerView = new HashMap();
//        for (PlayerBot player : players) {
//            playerView.put(player, Stack.generate());
//        }
//        return playerView;
//    }
//
//    public static void setIntersectedViews(ArrayList<PlayerBot> players, HashMap<PlayerBot, Stack> playerView) {
//        for (PlayerBot player : players) {
//            HashMap<PlayerBot, Stack> intersectedView = new HashMap();
//            for (PlayerBot _player : players) {
//                if (!player.equals(_player)) {
//                    intersectedView.put(_player, playerView.get(_player));
//                }
//            }
//            player.setView(intersectedView);
//        }
//    }
//
//    public static void testBots(int numPlayers, int variant, int numRounds, boolean saveToFile) {
////        QuestionUtilities.NUM_QUESTIONS = Stack.CARDSSIZE > 3 ? 20 : 18;
//
//        // initiate player Bots
//        ArrayList<PlayerBot> players = generatePlayers(numPlayers);
//
//        // static playerView from an additional answering player
//        HashMap<PlayerBot, Stack> tableView = generateAdditionalPlayerView(players);
//
//        // intersected views
//        setIntersectedViews(players, tableView);
//
//        //Common Information
//        ProjectionTables commonInfo = null;
//        if (variant > 0) {
//            commonInfo = new ProjectionTables(players);
//            ConsoleLogger.println("CommonInformation:", ConsoleLogger.BOT);
//            ConsoleLogger.println(commonInfo, ConsoleLogger.BOT);
//            //System.exit(4);
//        }
////        int[][] counts = new int[numRounds][numPlayers];
//
//        for (int i = 0; i < numRounds; i++) {
//            ConsoleLogger.println(tableView, ConsoleLogger.MODEL);
//            // first question and answer
//            Question questionID = QuestionUtilities.next();
////            questionID = 8;
//            int answer = QuestionUtilities.invokeQuestion(tableView, questionID);
//            ConsoleLogger.println("QuestionID + Answer:", ConsoleLogger.MODEL);
//            ConsoleLogger.println(questionID + " " + answer, ConsoleLogger.MODEL);
//
//            // 1. interpret only the answer
//            for (PlayerBot player : players) {
//                player.updateValids(questionID, answer);
//                ConsoleLogger.println("Player " + player.getID() + " valids: " + player.getValids(), ConsoleLogger.MODEL);
//                if (player.getValids().isEmpty()) {
//                    System.exit(1);
//                }
//            }
//
//            // 2 update conditional information
//            if (variant > 0) {
//                commonInfo.updateConditionalInformationByAnswer(questionID, answer, tableView);
//                ConsoleLogger.println("CommonInformation:", ConsoleLogger.BOT);
//                ConsoleLogger.println(commonInfo, ConsoleLogger.BOT);
//            }
//            //distribute the reactions until no new information
//            boolean somethingChanged = false;
//            do {
//                HashMap<PlayerBot, Boolean> reactionMap = new HashMap();
//                for (PlayerBot player : players) {
//                    reactionMap.put(player, player.canSolve());
////                    somethingChanged = somethingChanged || player.canSolve();
//                }
//                if (variant > 0) {
//////                SystemView.println(canSolveA + " " + canSolveB + " " + canSolveC, SystemView.BOT);
//                    ConsoleLogger.println("Valids:", ConsoleLogger.BOT);
//                    if (variant == 1) {
//                        players.get(0).updateValids(reactionMap, commonInfo);
//                    } else {
//                        for (PlayerBot player : players) {
//                            player.updateValids(reactionMap, commonInfo);
//                            ConsoleLogger.println(player.validsToString(), ConsoleLogger.BOT);
//                        }
//                        somethingChanged = commonInfo.updateByReaction(reactionMap) || somethingChanged;
//                        ConsoleLogger.println("CommonInformation after reaction update:", ConsoleLogger.BOT);
//                        ConsoleLogger.println(commonInfo, ConsoleLogger.BOT);
//                        ConsoleLogger.println("somethingChanged: " + somethingChanged, ConsoleLogger.BOT);
//                        System.exit(0);
//                    }
//                }
//                for (PlayerBot player : players) {
//                    if (reactionMap.get(player)) {
//                        if (!player.getValids().get(0).equals(tableView.get(player))) {
//                            System.exit(2);
//                        }
////                        counts[i][player.getID()] = 1;
//                        if (variant > 0) {
//                            commonInfo.reset(player, tableView.get(player));
//                        }
//                        Stack stack = Stack.generate();
//                        tableView.put(player, stack);
//                        for (PlayerBot _player : players) {
//                            if (!_player.equals(player)) {
//                                HashMap<PlayerBot, Stack> view = _player.getViewCopy();
//                                view.put(player, stack);
//                                _player.setView(view);
//                            }
//                        }
//                        player.reset();
////                    SystemView.println("CommonInformation after A:", SystemView.BOT);
////                    SystemView.println(commonInfo, SystemView.BOT);
//                    }
//                }
////                somethingChanged = false;
////                for (PlayerBot player : players) {
////                    somethingChanged = somethingChanged ? somethingChanged : player.canSolve();
////                    SystemView.println(player.canSolve(), SystemView.BOT);
////                }
//            } while (somethingChanged);
//            System.out.println(i);
//        }
//        //String variantS = variant == 0 ? "NONE" : variant == 1 ? "ONE" : "ALL";
//        //String fn = String.format("Statistic_C%s_P%s_%s.txt", Stack.CARDSSIZE, numPlayers, variantS);
//        if (saveToFile) {
//            String fn = "Statistic.txt";
//
//            ArrayList<String> s = EggHeadReader.load(fn, false);
//            s.addAll(prepareOutput(counts, variant));
//            EggHeadWriter.write(fn, s);
//        }
//        //EggHeadWriter.write(fn, Utilities.toString(counts));
//    }
//
//    public static ArrayList<String> prepareOutput(int[][] counts, int variant) {
//        ArrayList<String> res = new ArrayList();
//        double[] sum = new double[counts[0].length];
//        for (int i = 0; i < counts.length; i++) {
//            String line = String.format("%d %d %d %d", i, Stack.CARDSSIZE, counts[0].length, variant);
//            for (int j = 0; j < counts[0].length; j++) {
//                line += " " + counts[i][j];
//                sum[j] += counts[i][j];
//            }
//            for (int j = counts[0].length; j < 5; j++) {
//                line += " ";
//            }
//            for (int j = 0; j < counts[0].length; j++) {
//                line += " " + i / sum[j];
//            }
//            line = line.replace(".", ",");
//            res.add(line);
//        }
//        return res;
//    }
//
//    public static void _testBots() {
//        // stacks for each player
//        Stack stackA = Stack.generate();
////        stackA = new Stack(1, 0, 1);
////        stackA = new Stack(1, 1, 2);
//        Stack stackB = Stack.generate();
////        stackB = new Stack(2, 0, 1);
////        stackB = new Stack(0, 2, 2);
//        Stack stackC = Stack.generate();
////        stackC = new Stack(2, 0, 2);
////        stackC = new Stack(0, 1, 1);
//        ConsoleLogger.println(stackA, ConsoleLogger.MODEL);
//        ConsoleLogger.println(stackB, ConsoleLogger.MODEL);
//        ConsoleLogger.println(stackC, ConsoleLogger.MODEL);
//
//        // initiate player Bots
//        PlayerBot A = new PlayerBot(0);
//        PlayerBot B = new PlayerBot(1);
//        PlayerBot C = new PlayerBot(2);
//        PlayerBot D = new PlayerBot(3);
//
//        // static playerView from a D player
//        HashMap<PlayerBot, Stack> playerView = new HashMap();
//        playerView.put(A, stackA);
//        playerView.put(B, stackB);
//        playerView.put(C, stackC);
//        // intersected views
//        HashMap<PlayerBot, Stack> intersectedViewA = new HashMap();
//        intersectedViewA.put(B, stackB);
//        intersectedViewA.put(C, stackC);
//        HashMap<PlayerBot, Stack> intersectedViewB = new HashMap();
//        intersectedViewB.put(A, stackA);
//        intersectedViewB.put(C, stackC);
//        HashMap<PlayerBot, Stack> intersectedViewC = new HashMap();
//        intersectedViewC.put(B, stackB);
//        intersectedViewC.put(A, stackA);
//        A.setView(intersectedViewA);
//        B.setView(intersectedViewB);
//        C.setView(intersectedViewC);
//
//        //Common Information
//        ProjectionTables commonInfo = new ProjectionTables(A, B, C);
//        ConsoleLogger.println("CommonInformation:", ConsoleLogger.BOT);
//        ConsoleLogger.println(commonInfo, ConsoleLogger.BOT);
//        int numRounds = 5000;
//        int[] aCount = new int[numRounds];
//        int[] bCount = new int[numRounds];
//        int[] cCount = new int[numRounds];
//
//        for (int i = 0; i < numRounds; i++) {
//            ConsoleLogger.println(stackA + " " + stackB + " " + stackC, ConsoleLogger.MODEL);
//            // first question and answer
//            int questionID = QuestionUtilities.next();
////            questionID = 8;
//            int answer = QuestionUtilities.invokeQuestion(playerView, questionID);
//            ConsoleLogger.println("QuestionID + Answer:", ConsoleLogger.MODEL);
//            ConsoleLogger.println(questionID + " " + answer, ConsoleLogger.MODEL);
//
//            // 1. interpret only the answer
//            A.updateValids(questionID, answer);
//            B.updateValids(questionID, answer);
//            C.updateValids(questionID, answer);
//            ConsoleLogger.println(A.getValids(), ConsoleLogger.MODEL);
//            ConsoleLogger.println(B.getValids(), ConsoleLogger.MODEL);
//            ConsoleLogger.println(C.getValids(), ConsoleLogger.MODEL);
//            if (A.getValids().isEmpty() || C.getValids().isEmpty() || B.getValids().isEmpty()) {
//                System.exit(0);
//            }
////        C.updateValids(intersectedViewC, questionID, answer);
////            SystemView.println("Valids:", SystemView.BOT);
////            SystemView.println(A.validsToString(), SystemView.BOT);
////            SystemView.println(B.validsToString(), SystemView.BOT);
////            SystemView.println(C.validsToString(), SystemView.BOT);
////        SystemView.println(C.validsToString(), SystemView.BOT);
//
//            // 2 update conditional information
//            commonInfo.updateConditionalInformationByAnswer(questionID, answer, playerView, D);
//            ConsoleLogger.println("CommonInformation:", ConsoleLogger.BOT);
//            ConsoleLogger.println(commonInfo, ConsoleLogger.BOT);
////            // 2 build conditional information
////            A.updateConditionalInformationByAnswer(questionID, answer, stackC, C);
////            B.updateConditionalInformationByAnswer(questionID, answer, stackC, C);
////            SystemView.println("ConditionalInformation:", SystemView.BOT);
////            SystemView.println("If A has, B valids are", SystemView.BOT);
////            SystemView.println(A.conditionalInformationToString(), SystemView.BOT);
////            SystemView.println("If B has, A valids are", SystemView.BOT);
////            SystemView.println(B.conditionalInformationToString(), SystemView.BOT);
//            //distribute the reactions until no new information
//            boolean canSolveA = A.canSolve();
//            boolean canSolveB = B.canSolve();
//            boolean canSolveC = C.canSolve();
//            boolean someoneSolved;
//            do {
//
//                HashMap<PlayerBot, Boolean> reactionMap = new HashMap();
////                reactionMap.put(A, canSolveA);
//                reactionMap.put(B, canSolveB);
//                reactionMap.put(C, canSolveC);
//////                SystemView.println(canSolveA + " " + canSolveB + " " + canSolveC, SystemView.BOT);
//                A.updateValids(reactionMap, commonInfo);
////                B.updateValids(reactionMap, commonInfo);
////                C.updateValids(reactionMap, commonInfo);
//                ConsoleLogger.println("Valids:", ConsoleLogger.BOT);
//                ConsoleLogger.println(A.validsToString(), ConsoleLogger.BOT);
//                ConsoleLogger.println(B.validsToString(), ConsoleLogger.BOT);
//                ConsoleLogger.println(C.validsToString(), ConsoleLogger.BOT);
////                commonInfo.updateConditionalInformationByReaction(reactionMap);
////                SystemView.println("CommonInformation:", SystemView.BOT);
////                SystemView.println(commonInfo, SystemView.BOT);
//                if (canSolveA) {
//                    aCount[i] = 1;
//                    commonInfo.reset(A, stackA);
//                    stackA = Stack.generate();
//                    playerView.put(A, stackA);
//                    intersectedViewB.put(A, stackA);
//                    intersectedViewC.put(A, stackA);
//                    A.reset();
////                    SystemView.println("CommonInformation after A:", SystemView.BOT);
////                    SystemView.println(commonInfo, SystemView.BOT);
//                }
//                if (canSolveB) {
//                    bCount[i] = 1;
//                    commonInfo.reset(B, stackB);
//                    stackB = Stack.generate();
//                    playerView.put(B, stackB);
//                    intersectedViewA.put(B, stackB);
//                    intersectedViewC.put(B, stackB);
//                    B.reset();
////                    SystemView.println("CommonInformation after B:", SystemView.BOT);
////                    SystemView.println(commonInfo, SystemView.BOT);
//                }
//                if (canSolveC) {
//                    cCount[i] = 1;
//                    commonInfo.reset(C, stackC);
//                    stackC = Stack.generate();
//                    playerView.put(C, stackC);
//                    intersectedViewA.put(C, stackC);
//                    intersectedViewB.put(C, stackC);
//                    C.reset();
////                    SystemView.println("CommonInformation after C:", SystemView.BOT);
////                    SystemView.println(commonInfo, SystemView.BOT);
//                }
//                canSolveA = A.canSolve();
//                canSolveB = B.canSolve();
//                canSolveC = C.canSolve();
//                someoneSolved = canSolveA || canSolveB || canSolveC;
//
//            } while (someoneSolved);
//            System.out.println(i);
//        }
//        EggHeadWriter.write("Statistic.txt", Utilities.toString(aCount, bCount, cCount));
//    }
//
//    public ArrayList<Stack> analyseAnswer(ArrayList<Stack> valids, HashMap<PlayerBot, Stack> intersectedView, int questionID, int answer, PlayerBot player) {
//        ArrayList<Stack> remainingValids = new ArrayList();
//        for (Stack proposal : valids) {
//            intersectedView.put(player, proposal);
//            int proposalAnswer = QuestionUtilities.invokeQuestion(intersectedView, questionID);
//            if (proposalAnswer != answer) {
//                ConsoleLogger.println("Removing:" + Utilities.arrayToString(proposal), ConsoleLogger.BOT);
//            } else {
//                remainingValids.add(proposal);
//            }
//        }
//        return remainingValids;
//    }
//
//}
////    public static class Game {
////
////        ArrayList<PlayerBot> players;
////        HashMap<PlayerBot, Stack> playerView;
////        CommonInformation commonInfo;
////
////        public Game(int numPlayers, int variant, int numRounds, boolean saveToFile) {
////            init(numPlayers, variant, numRounds, saveToFile);
////        }
////
////        public final void init(int numPlayers, int variant, int numRounds, boolean saveToFile) {
////            this.players = generatePlayers(numPlayers);
////            this.playerView = generateAdditionalPlayerView(players);
////            setIntersectedViews(players, playerView);
////            this.commonInfo = new CommonInformation(players);
////        }
////
////        public void show() {
////            SystemView.println(playerView, SystemView.MODEL);
////            SystemView.println("CommonInformation:", SystemView.BOT);
////            SystemView.println(commonInfo, SystemView.BOT);
////        }
////
////    }
////
////    public static void turn() {
////        int questionID = Question.next();
////        int answer = Utilities.invokeQuestion(playerView, questionID);
////        SystemView.println("QuestionID + Answer:", SystemView.MODEL);
////        SystemView.println(questionID + " " + answer, SystemView.MODEL);
////
////        // 1. interpret only the answer
////        for (PlayerBot player : players) {
////            player.updateValids(questionID, answer);
////            SystemView.println(player.getValids(), SystemView.MODEL);
////            if (player.getValids().isEmpty()) {
////                System.exit(1);
////            }
////        }
////
////        // 2. update common Information
////        commonInfo.updateConditionalInformationByAnswer(questionID, answer, playerView);
//////            SystemView.println("CommonInformation:", SystemView.BOT);
//////            SystemView.println(commonInfo, SystemView.BOT);
////    }
