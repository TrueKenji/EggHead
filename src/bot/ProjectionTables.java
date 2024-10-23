package bot;

import java.util.ArrayList;
import java.util.Map;
import model.Stack;
import question.QuestionList.Question;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import listeners.UpdateEvent;
import logger.Logger;
import logger.Logger.Level;
import model.GameModel;
import utils.ArrayComprehension;
import utils.CollectionFactory;
import utils.Combinatorics;
import utils.ExecutionTimer;

/**
 * ProjectionTables encapsulates the entire {@link ConditionalInformation} about
 * the current player information levels. There is one conditionalInformation
 * object per player.
 *
 * Within the related classes, we make mainly use of shallow or even reference
 * copies of collections for performance
 *
 */
public class ProjectionTables extends CommonInformation {

    public Map<PlayerBotTuple, ConditionalInformation> conditionalInformationMap = CollectionFactory.factory.createMap();
    // stackMapping is used in @fitsReaction
    private Map<PlayerBot, Map<PlayerBotTuple, Map<StackScenario, Map<Stack, StackScenario>>>> stackMapping = CollectionFactory.factory.createMap();
    private Map<PlayerBotTuple, PlayerBot> missingPlayerMap = CollectionFactory.factory.createMap();

    public ProjectionTables(GameModel model) {
        super(model);
    }

    public PlayerBot getMissingPlayer(PlayerBotTuple tuple) {
        for (PlayerBot player : players) {
            if (!tuple.contains(player)) {
                return player;
            }
        }
        return null;
    }

    public PlayerBotTuple getTuple(PlayerBot player) {
        for (PlayerBotTuple tuple : conditionalInformationMap.keySet()) {
            if (!tuple.contains(player)) {
                return tuple;
            }
        }
        return null;
    }

    public void set(Map<PlayerBotTuple, ConditionalInformation> conditionalInformationMap) {
        this.conditionalInformationMap = conditionalInformationMap;
    }

    /**
     * updateValids is performed by using a dummy for each row of the tables the
     * dummys valids list is a reference copy of the valids list in
     * conditionalInformation, the update will have a side effect. This is
     * intentional, to improve performance
     *
     * @param question
     * @param answer
     */
    @Override
    public void updateByAnswer(Question question, int answer) {
        conditionalInformationMap.entrySet().parallelStream().forEach(entry -> {
            Iterator<StackScenario> iterator = entry.getValue().iterator();
            while (iterator.hasNext()) {
                StackScenario scenario = iterator.next();
                scenario.getDummy().updateValids(question, answer, scenario);
            }
        });
    }

    /**
     * Exchange one player in the given scenario with the given player. Assign s
     * for the given player in the scenario. Lookup this newly created row in
     * the exchanged player's table. Check the valids length with the exchanged
     * player's reaction. To improve performance, @stackMapping contains a
     * mapping for every lookup required here
     *
     * @param player
     * @param scenario
     * @param s
     * @param reactionMap
     * @return
     */
    public boolean fitsReaction(PlayerBot player, StackScenario scenario, Stack s, Map<PlayerBot, Boolean> reactionMap) {
        for (PlayerBotTuple tuple : conditionalInformationMap.keySet()) {
            if (tuple.contains(player)) {
//                StackScenario lookupScenario = stackMapping.get(player).get(tuple).get(scenario).get(s);
//                int validSize = lookupScenario.getDummy().getValids().size();
                StackScenario lookupScenario = scenario.shallowCopy();
                lookupScenario.remove(missingPlayerMap.get(tuple));
                lookupScenario.put(player, s);
                int validSize = conditionalInformationMap.get(tuple).get(lookupScenario).getDummy().getValids().size();
                if (validSize == 0 || (reactionMap.get(missingPlayerMap.get(tuple)) != (validSize == 1))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * perform the update by Reaction step. First, clear the stack lists, where
     * their length don't fit the reaction Second, cross reference the other
     * tables and check those reactions, too
     *
     * @param reactionMap
     * @return boolean: whether at least one modification was performed
     */
    @Override
    public boolean updateByReaction(Map<PlayerBot, Boolean> reactionMap) {
        ExecutionTimer.get().start();
        AtomicBoolean somethingChanged = new AtomicBoolean(false); // modify within parallelStream
        conditionalInformationMap.entrySet().parallelStream().forEach(entry -> {
            PlayerBot player = getMissingPlayer(entry.getKey());
//            if (player.usesBehaviourAnalysis()) {
                ConditionalInformation condInfo = entry.getValue();
                condInfo.initTransaction();
                Iterator<StackScenario> iterator = condInfo.iterator();
                while (iterator.hasNext()) {
                    StackScenario scenario = iterator.next();
                    List<Stack> currentValids = scenario.getDummy().getValids();
                    if ((currentValids.size() == 1) != reactionMap.get(player)) {
                        if (!scenario.getDummy().getValids().isEmpty()) {
                            somethingChanged.set(true);
                            scenario.transactionClear();
                        }
                    }
                }
//            }
        });
        transaction();
        conditionalInformationMap.entrySet().parallelStream().forEach(entry -> {
            PlayerBot player = getMissingPlayer(entry.getKey());
            if (player.usesBehaviourAnalysis()) {
                ConditionalInformation condInfo = entry.getValue();
                condInfo.initTransaction();
                Iterator<StackScenario> iterator = condInfo.iterator();
                while (iterator.hasNext()) {
                    StackScenario scenario = iterator.next();
                    if(model.getPlayerView(player).equals(scenario)){ // unique correct row
                        notifyListeners(new UpdateEvent(this, "ValidsRemovedByBA", null, scenario.getDummy().getValids().size()));
                    }
                    Iterator<Stack> validsIterator = scenario.transactionIterator();
                    while (validsIterator.hasNext()) {
                        Stack validToTest = validsIterator.next();
                        if (!fitsReaction(player, scenario, validToTest, reactionMap)) {
                            validsIterator.remove();
                            somethingChanged.set(true);
                        }
                    }
                    if(model.getPlayerView(player).equals(scenario)){ // unique correct row
                        notifyListeners(new UpdateEvent(this, "ValidsRemovedByBA", null, scenario.getDummy().getValids().size()));
                    }
                }
            }
        });
        transaction();
        ExecutionTimer.get().end();
        return somethingChanged.get();
    }

    /**
     * We need to perform step 1 and step 2 of @updateByReaction based on the
     * original tables, respectively. Therefore, we save the updates in a
     * separate dummy and apply them all at once after the loops
     */
    private void transaction() {
        conditionalInformationMap.entrySet().parallelStream().forEach(entry -> {
            entry.getValue().transaction();
        });
    }

    /**
     * Perform the reset step. The solving player simply receives new ALL_STACKS
     * for all rows. The other players duplicate the valids set corresponding to
     * the correct rows with regard to the solving player as described in the
     * pdf
     *
     * @param solvingPlayer
     * @param solution
     */
    @Override
    public void reset(PlayerBot solvingPlayer, Stack solution) {
        for (Map.Entry<PlayerBotTuple, ConditionalInformation> mapEntry : conditionalInformationMap.entrySet()) {
            if (mapEntry.getKey().contains(solvingPlayer)) {
                Iterator<StackScenario> iterator = mapEntry.getValue().iterator();
                while (iterator.hasNext()) {
                    StackScenario next = iterator.next();
                    StackScenario copy = next.shallowCopy();
                    copy.put(solvingPlayer, solution);
                    StackScenario lookup = mapEntry.getValue().get(copy);
                    next.getDummy().setValids(new ArrayList(lookup.getDummy().getValids()));
                }
            } else {
                Iterator<StackScenario> iterator = mapEntry.getValue().iterator();
                while (iterator.hasNext()) {
                    iterator.next().getDummy().setValids(PlayerBot.ALL_STACKS);
                }
            }
        }
    }

    public void initStackMapping() {
        // statically map all combinations of player/tuple/scenario/valid to the
        // lookup scenario into another table, used to speed up fitsReaction, 
        // where we need to construct the correct key to lookup the valids size 
        // of different tables
        for (PlayerBot player : players) {
            Map<PlayerBotTuple, Map<StackScenario, Map<Stack, StackScenario>>> _stackMapping = CollectionFactory.factory.createMap();
            ConditionalInformation condInfo = conditionalInformationMap.get(getTuple(player));
            for (Map.Entry<PlayerBotTuple, ConditionalInformation> entry : conditionalInformationMap.entrySet()) {
                PlayerBotTuple tuple = entry.getKey();
                ExecutionTimer.get().start();
                if (tuple.contains(player)) {
                    Map<StackScenario, Map<Stack, StackScenario>> __stackMapping = CollectionFactory.factory.createMap();
                    Iterator<StackScenario> iterator = condInfo.iterator();
                    while (iterator.hasNext()) {
                        StackScenario scenario = iterator.next();
                        Map<Stack, StackScenario> ___stackMapping = CollectionFactory.factory.createMap();
                        for (Stack s : PlayerBot.ALL_STACKS) {
                            StackScenario lookupScenario = scenario.shallowCopy();
                            lookupScenario.remove(missingPlayerMap.get(tuple));
                            lookupScenario.put(player, s);
                            ___stackMapping.put(s, conditionalInformationMap.get(tuple).get(lookupScenario));
                        }
                        __stackMapping.put(scenario, ___stackMapping);
                    }
                    _stackMapping.put(tuple, __stackMapping);
                }
                ExecutionTimer.get().end();
            }
            stackMapping.put(player, _stackMapping);
        }
    }

    public void initMissingPlayerMapping() {
        for (PlayerBotTuple tuple : conditionalInformationMap.keySet()) {
            missingPlayerMap.put(tuple, getMissingPlayer(tuple));
        }
    }

    @Override
    public void init() {
        Level[] loggingLevels = {Logger.DEBUG, Logger.COMMON_INFORMATION, Logger.INIT_CONDITIONAL_INFORMATION};
        ArrayList<ArrayList<Integer>> playerCombinations = Combinatorics.genCombinationsWithoutRepetition(players.length - 1, players.length);
        logger.println(playerCombinations, loggingLevels);
        List<Stack> allStackCombinations = PlayerBot.ALL_STACKS;
        logger.println(allStackCombinations, loggingLevels);
        ArrayList<ArrayList<Integer>> stackScenarioIndizes = Combinatorics.genVariationsWithRepetion(players.length - 1, allStackCombinations.size());
        playerCombinations.stream().forEach(playerCombination -> {
            logger.println(stackScenarioIndizes, loggingLevels);
            ConditionalInformation conditionalInformation = new ConditionalInformation();
            int missingPlayerId = (players.length * (players.length - 1) / 2) - ArrayComprehension.add(playerCombination);
            for (ArrayList<Integer> stackScenario : stackScenarioIndizes) {
                StackScenario ss = new StackScenario(missingPlayerId);
                for (int i = 0; i < stackScenario.size(); i++) {
                    ss.put(players[playerCombination.get(i)], allStackCombinations.get(stackScenario.get(i)));
                }
                logger.println(ss, loggingLevels);
                logger.println(ss.hashCode(), loggingLevels);
                conditionalInformation.add(ss);
            }
            logger.println(conditionalInformation, loggingLevels);
            conditionalInformationMap.put(new PlayerBotTuple(ArrayComprehension.subList(players, playerCombination)), conditionalInformation);
//            j++;
        });
        initMissingPlayerMapping();
//        initStackMapping();
    }

    /**
     * returns the conditionalInformation value of the map, which corresponds to
     * the given player. I.e. the key for the returned value is exactly the one
     * tuple, which does not contain the given player
     *
     * @param player: the player assigned to the desired cond. Info
     * @return List<Stack> the correct valids of the given player
     */
    @Override
    public List<Stack> getValids(PlayerBot player) {
        for (PlayerBotTuple tuple : conditionalInformationMap.keySet()) {
            if (!tuple.contains(player)) {
                StackScenario lookupScenario = new StackScenario(model.getPlayerView(player));
                return conditionalInformationMap.get(tuple).get(lookupScenario).getDummy().getValids();
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ProjectionTables)) {
            return false;
        }
        ProjectionTables other = (ProjectionTables) obj;
        return conditionalInformationMap.equals(other.conditionalInformationMap);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.conditionalInformationMap);
        return hash;
    }

    @Override
    public ProjectionTables getProjectionTables() {
        return this;
    }

    public static String toString(Map<PlayerBotTuple, ConditionalInformation> conditionalInformationMap) {
        String s = new String();
        for (PlayerBotTuple tuple : conditionalInformationMap.keySet()) {
            s += tuple.toString() + System.lineSeparator()
                    + conditionalInformationMap.get(tuple).toString();
        }
        return s;
    }

}
