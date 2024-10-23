package bot;

import question.QuestionList.Question;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import listeners.Listener;
import listeners.UpdateEvent;
import logger.Logger;
import model.GameModel;
import model.Stack;

public abstract class CommonInformation {

    protected PlayerBot[] players; // reference model player objects in indexable array
    protected Logger logger = Logger.getLogger();
    protected GameModel model;
    private boolean updatedByReaction; // keep track of reaction based update, so main controller can loop again
    
    private final Collection<Listener> listeners = new HashSet();

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void notifyListeners(UpdateEvent e) {
        for (Listener listener : listeners) {
            listener.notify(e);
        }
    }

    public CommonInformation(GameModel model) {
        this.model = model;
        Collection<PlayerBot> _players = model.getPlayers().keySet();
        this.players = new PlayerBot[_players.size()];
        int i = 0;
        for (PlayerBot player : _players) {
            this.players[i++] = player;
        }
    }

    public abstract ProjectionTables getProjectionTables();

    public void setUpdatedByReaction(boolean updatedByReaction) {
        this.updatedByReaction = updatedByReaction;
    }

    public boolean wasUpdatedByReaction() {
        return updatedByReaction;
    }

    public abstract void init();

    protected abstract void reset(PlayerBot solvingPlayer, Stack solution);

    protected abstract boolean updateByReaction(Map<PlayerBot, Boolean> reactionMap);

    public abstract void updateByAnswer(Question question, int answer);

    public abstract List<Stack> getValids(PlayerBot player);

}
