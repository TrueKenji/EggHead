package bot;

import question.QuestionList.Question;
import question.QuestionUtilities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import model.Stack;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import logger.Logger;
import logger.Logger.Level;
import model.GameModel;
import utils.ArrayComprehension;
import utils.Combinatorics;

public class MasterTable extends CommonInformation {

    // all possible combinations contained in one list of <player,stack> tuples
    private List<StackScenario> table;

    public MasterTable(GameModel model) {
        super(model);
    }

    @Override
    public final void init() {
        table = new ArrayList();
        Level[] loggingLevels = {Logger.DEBUG, Logger.COMMON_INFORMATION, Logger.MASTER_TABLE, Logger.INIT_MASTER_TABLE};
        List<Stack> allStackCombinations = PlayerBot.ALL_STACKS;
        ArrayList<ArrayList<Integer>> stackScenarioIndizes = Combinatorics.genVariationsWithRepetion(players.length, allStackCombinations.size());
        for (ArrayList<Integer> stackScenario : stackScenarioIndizes) {
            StackScenario ss = new StackScenario();
            for (int i = 0; i < stackScenario.size(); i++) {
                ss.put(players[i], allStackCombinations.get(stackScenario.get(i)));
            }
            table.add(ss);
        }
//        logger.println(toString(), loggingLevels);
//        logger.println(table.size(), loggingLevels);
        // filterToVisibleCards(); incorrect, as written in pdf
    }

    private void setTable(List<StackScenario> table) {
        this.table = table;
    }

    @Deprecated
    private void filterToVisibleCards() {
        // remove all those rows, which are initially impossible
        Iterator<StackScenario> it = table.iterator();
        while (it.hasNext()) {
            if (moreThanTwoWrong(it.next())) {
                it.remove();
            }
        }
    }

    @Deprecated
    private boolean moreThanTwoWrong(StackScenario scenario) {
        // scenarios with more than two stacks wrong are publicly false
        int ctr = 0;
        for (Map.Entry<PlayerBot, Stack> entry : scenario.entrySet()) {
            if (!model.getPlayerStack(entry.getKey()).equals(entry.getValue())) {
                if (++ctr == 3) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean _updateByReaction(Map<PlayerBot, Boolean> reactionMap) {
        Level[] loggingLevels = {Logger.DEBUG, Logger.COMMON_INFORMATION, Logger.MASTER_TABLE, Logger.UPDATE_BY_REACTION};
        boolean somethingChanged = false;
        List<StackScenario> newList = deepcopy().table;
        for (Map.Entry<PlayerBot, Boolean> entry : reactionMap.entrySet()) { // ToDo: switch loops and keep only those rows where all reactions fit
            Iterator<StackScenario> it = newList.iterator();
            while (it.hasNext()) {
                StackScenario baseScenario = it.next().deepcopy();
                baseScenario.remove(entry.getKey());
                Set<StackScenario> projection = getRowsContainingScenario(baseScenario);
                if((projection.size() == 1) != entry.getValue()){
                    
                }
//                int validsSize = getValidsSizeForScenario(baseScenario);
//                if ((validsSize == 1) != entry.getValue()) {
//                    it.remove();
//                    somethingChanged = true;
//                }
            }
        }
        table = newList;
        return somethingChanged;
    }

    @Override
    public List<Stack> getValids(PlayerBot player) {
        List<Stack> valids = new ArrayList();
        StackScenario playerView = new StackScenario(model.getPlayerView(player));
        for (StackScenario row : table) {
            StackScenario copy = row.deepcopy();
            copy.remove(player);
            if (copy.equals(playerView)) {
                valids.add(row.get(player));
            }
        }
        return valids;
    }

    public List<Stack> getValids(StackScenario scenario) {
        List<Stack> valids = new ArrayList();
        for (StackScenario row : table) {
            if (row.contains(scenario)) {
                valids.add(row.getObserved(scenario.keySet()));
            }
        }
        Collections.sort(valids);
        return valids;
    }

    @Override
    public void updateByAnswer(Question questionID, int answer) {
        Level[] loggingLevels = {Logger.DEBUG, Logger.COMMON_INFORMATION, Logger.MASTER_TABLE, Logger.UPDATE_BY_ANSWER};
//        ArrayList<StackScenario> targetedForDeletion = new ArrayList();
        Iterator<StackScenario> it = table.iterator();
        while (it.hasNext()) {
            if (answer != QuestionUtilities.invokeQuestion(it.next(), questionID)) {
                it.remove();
            }
        }
//        for (StackScenario row : table) {
//            if (answer != QuestionUtilities.invokeQuestion(row, questionID)) {
//                targetedForDeletion.add(row);
//            }
//        }
//        logger.println(toString(), loggingLevels);
//        table.removeAll(targetedForDeletion);
//        logger.println(stackScenarioListToString(targetedForDeletion), loggingLevels);
//        logger.println(toString(), loggingLevels);
    }

    @Override
    protected void reset(PlayerBot solvingPlayer, Stack solution) {
        ArrayList<StackScenario> targetedForAdding = new ArrayList();
        ArrayList<StackScenario> targetedForDeletion = new ArrayList();
        for (StackScenario row : table) {
            if (row.contains(solvingPlayer, solution)) {
                ArrayList<Stack> allStackCombinations = PlayerBot.ALL_STACKS;
                for (Stack s : allStackCombinations) {
                    if (!s.equals(solution)) {
                        StackScenario duplicateRow = row.deepcopy();
                        duplicateRow.put(solvingPlayer, s);
                        targetedForAdding.add(duplicateRow);
                    }
                }
            } else {
                targetedForDeletion.add(row);
            }
        }
        table.removeAll(targetedForDeletion);
        table.addAll(targetedForAdding);
    }

    @Override
    public String toString() {
        return stackScenarioListToString(table);
    }

    public String stackScenarioListToString(List<StackScenario> scenarios) {
        String s = new String();
        for (StackScenario scenario : scenarios) {
            s += scenario.toString() + " " + scenario.bigHashCode() + "\n";
        }
        return s;
        //return scenarios.stream().map(StackScenario::toString)
        //        .collect(Collectors.joining(System.lineSeparator()));
    }

    private Set<StackScenario> getRowsContainingScenario(StackScenario scenario) {
        Set<StackScenario> result = new HashSet();
        for (StackScenario row : table) {
            if (row.contains(scenario)) {
                result.add(row);
            }
        }
        return result;
    }

    private int getValidsSizeForScenario(StackScenario scenario) {
        // scenario is n-1 stacks per player as a filter for the table
        int result = 0;
        for (StackScenario row : table) {
            result += row.contains(scenario) ? 1 : 0;
        }
        return result;
    }

    public ProjectionTables _project() {
        Level[] loggingLevels = {Logger.DEBUG, Logger.COMMON_INFORMATION, Logger.PROJECT_MASTER_TABLE};
        ProjectionTables projection = new ProjectionTables(model);
        Map<PlayerBotTuple, ConditionalInformation> conditionalInformationMap = new TreeMap();
        ArrayList<ArrayList<Integer>> playerCombinations = Combinatorics.genCombinationsWithoutRepetition(players.length - 1, players.length);
        logger.println(playerCombinations, loggingLevels);
        ArrayList<Stack> allStackCombinations = PlayerBot.ALL_STACKS;
        logger.println(allStackCombinations, loggingLevels);
        ArrayList<ArrayList<Integer>> stackScenarioIndizes = Combinatorics.genVariationsWithRepetion(players.length - 1, allStackCombinations.size());
        int j = 0;
        for (ArrayList<Integer> playerCombination : playerCombinations) {
            logger.println(stackScenarioIndizes, loggingLevels);
            ConditionalInformation conditionalInformation = new ConditionalInformation();
            for (ArrayList<Integer> stackScenario : stackScenarioIndizes) {
                StackScenario ss = new StackScenario();
                for (int i = 0; i < stackScenario.size(); i++) {
                    ss.put(players[playerCombination.get(i)], allStackCombinations.get(stackScenario.get(i)));
                }
                logger.println(ss, loggingLevels);
                logger.println(ss.hashCode(), loggingLevels);
                List<Stack> remainingValids = getValids(ss);
                if (!remainingValids.isEmpty()) {
                    conditionalInformation.put(ss, remainingValids);
                    logger.println(remainingValids + " " + ss + " " + ss.bigHashCode(), loggingLevels);
                }
            }
            logger.println(conditionalInformation, loggingLevels);
            conditionalInformationMap.put(new PlayerBotTuple(ArrayComprehension.subList(players, playerCombination)), conditionalInformation);
            j++;
        }
        projection.set(conditionalInformationMap);
        return projection;
    }

    private MasterTable deepcopy() {
        MasterTable copy = new MasterTable(model);
        List<StackScenario> table = new ArrayList();
        for (StackScenario s : this.table) {
            table.add(s.deepcopy());
        }
        copy.setTable(table);
        return copy;
    }

}
