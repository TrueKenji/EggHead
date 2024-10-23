package bot;

import utils.Option;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import listeners.Listener;
import listeners.UpdateEvent;
import model.Stack;
import model.GameModel;
import static model.GameModel.GameState.ANSWER_SHOWN;
import static model.GameModel.GameState.DEFAULT;
import static model.GameModel.GameState.REACTIONS_SHOWN;

public class BotController implements Listener {

    private Collection<PlayerBot> players;
    public CommonInformation commonInfo;
    private GameModel model;

    public void init(GameModel model) {
        this.players = model.getPlayers().keySet();
        this.model = model;
        model.addListener(this);
    }

    @Override
    public void notify(UpdateEvent e) {
        if (e.getPropertyName().equals("GameState")) {
            switch ((GameModel.GameState) e.getNewValue()) {
                case DEFAULT:
                    commonInfo.setUpdatedByReaction(false);
                    break;
                case ANSWER_SHOWN:
                    commonInfo.updateByAnswer(model.getQuestion(), model.getAnswer());
                    updateValids();
//                    for(PlayerBot player : players){
//                        player.updateValids(model.getQuestion(), model.getAnswer(), model.getPlayerView(player));
//                    }
                    break;
                case REACTIONS_SHOWN:
                    boolean updatedByReaction = commonInfo.updateByReaction(model.getReactions());
                    commonInfo.setUpdatedByReaction(updatedByReaction);
                    updateValids();
                    break;
            }
        } else if (e.getPropertyName().equals("PlayerState")) {
            PlayerBot solvingPlayer = (PlayerBot) e.getOldValue();
            commonInfo.reset(solvingPlayer, model.getPlayerStack(solvingPlayer));
        }
    }

    private void updateValids() {
        for (PlayerBot player : players) {
            player.setValids(commonInfo.getValids(player));
            if (player.getValids().isEmpty()) {
//                throw new RuntimeException("No valids left for player" + player);
                System.out.println("No valids left for player" + player);
            }
        }
    }

    public void setDifficulty(Option difficulty) {
        if (difficulty == Option.DIFFICULTY.LEVEL_3) {
            commonInfo = new ProjectionTables(model);
            commonInfo.init();
        }
    }

    public boolean verifyCommonInfo() {
        for (Map.Entry<PlayerBot, Stack> entry : model.getPlayers().entrySet()) {
            List<Stack> remainingValids = commonInfo.getValids(entry.getKey());
            if (!remainingValids.contains(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    public boolean wasUpdatedByReaction() {
        return commonInfo.wasUpdatedByReaction();
    }

}
