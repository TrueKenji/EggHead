package bot;

import java.util.ArrayList;
import logger.Logger;

public class PlayerBotTuple implements Comparable<PlayerBotTuple> {

    private ArrayList<PlayerBot> players;
    private int hashCode;
    protected Logger logger = Logger.getLogger();

    public PlayerBotTuple(ArrayList<PlayerBot> players) {
        this.players = players;
    }

    public boolean contains(PlayerBot player) {
        return players.contains(player);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof PlayerBotTuple)) {
            return false;
        }
        return other.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            for (int i = 0; i < players.size(); i++) {
                hashCode += Math.pow(10, players.size() - i - 1) * players.get(i).getID();
            }
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return "BotTuple: " + hashCode();
    }

    @Override
    public int compareTo(PlayerBotTuple o) {
        return hashCode() - o.hashCode();
    }

//    public PlayerBotTuple deepcopy() {
//        ArrayList<PlayerBot> playersCopy = new ArrayList();
//        for (PlayerBot player : players) {
//            playersCopy.add(player.deepcopy());
//        }
//        return new PlayerBotTuple(playersCopy);
//    }
}
