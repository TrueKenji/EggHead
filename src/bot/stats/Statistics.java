package bot.stats;

import bot.PlayerBot;
import utils.Option;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import listeners.Listener;
import listeners.UpdateEvent;
import logger.Logger;
import model.GameModel;
import model.Stack;

public class Statistics implements Listener {

    private final Logger logger = Logger.getLogger();
    private final Map<PlayerBot, List<Integer>> stats;
//    private final Map<PlayerBot, Integer> stats;
    /**
     * Count rounds of game. Could be extracted from EggHeadController, but want
     * to prevent the dependency
     */
    private int roundCtr = 0;

    public Statistics(Set<PlayerBot> players) {
        this.stats = new HashMap();
        for (PlayerBot player : players) {
            stats.put(player, new ArrayList());
        }
    }

    @Override
    public void notify(UpdateEvent e) {
        switch (e.getPropertyName()) {
            case "Round":
                roundCtr++;
                break;
            case "PlayerState":
                List<Integer> currentStats = stats.get((PlayerBot) e.getOldValue());
                if (currentStats.isEmpty()) { // first solve
                    stats.get((PlayerBot) e.getOldValue()).add(roundCtr);
                } else { // calculate the deltas
                    currentStats.add(roundCtr - currentStats.stream().mapToInt(Integer::intValue).sum());
                }
//                stats.merge((PlayerBot) e.getOldValue(), 1, Integer::sum);
                System.out.println("Player solved");
                break;

            case "GameState":
                if (e.getNewValue().equals(GameModel.GameState.END_GAME)) {
                    endGame();
                }
            case "ValidsRemovedByBA":
                
        }
    }

    public void endGame() {
        logger.println(stats, Logger.PROD, Logger.EGG_HEAD_CONTROLLER, Logger.STATISTIC);
//        System.out.println(StackScenario.biggestExp);
        ArrayList<String> out = convertToCsv(stats, ";");
//        for (Map.Entry<PlayerBot, List<Integer>> entry : stats.entrySet()) {
//            out.add(entry.getKey() + ": " + (roundCtr / entry.getValue()));
//        }
        db.EggHeadWriter.write(getOutFileName(), out);
    }

    public String getOutFileName() {
        return String.format("Statistic_%s_%s_%s_%s.txt",
                GameModel.NUM_LEVEL_3, // how many level3 bots
                Stack.CARDSSIZE, // card range
                stats.keySet().size(), // num players 
                Option.getDefaultOptions().get(Option.PLAYER.class)); // bot mode
    }

    @Override
    public String toString() {
        return stats.toString();
    }

    public static ArrayList<String> convertToCsv(Map<PlayerBot, List<Integer>> map, String delimiter) {
        // Find the maximum length of the lists
        int maxRows = 0;
        for (List<Integer> list : map.values()) {
            maxRows = Math.max(maxRows, list.size());
        }

        Collection<PlayerBot> players = map.keySet(); // ensure consistent ordering

        ArrayList<String> result = new ArrayList<>();

        // For each row (up to maxRows), create a CSV line
        for (int row = 0; row < maxRows; row++) {
            StringBuilder rowString = new StringBuilder();

            for (PlayerBot key : players) {
                List<Integer> list = map.get(key);

                // Append value if it exists, else append empty
                if (row < list.size()) {
                    rowString.append(list.get(row));
                } else {
                    rowString.append(""); // If no value, append empty string
                }

                // Add delimiter between values
                rowString.append(delimiter);
            }

            // Remove the trailing delimiter
            if (rowString.length() > 0) {
                rowString.setLength(rowString.length() - 1);
            }

            // Add the row string to the result ArrayList
            result.add(rowString.toString());
        }

        return result;
    }

}
