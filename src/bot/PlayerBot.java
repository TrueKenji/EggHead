package bot;

import question.QuestionUtilities;
import java.util.ArrayList;
import java.util.Iterator;
import model.Stack;
import question.QuestionList.Question;
import java.util.List;
import java.util.Map;
import utils.Combinatorics;

public class PlayerBot implements Comparable<PlayerBot> {

    public static final List<Stack> ALL_STACKS = new ArrayList(generateValids());
    private List<Stack> valids; // initially a shallow copy of @ALL_STACKS; with the first update, the list is copied to prevent side effects
    private int id;
    private boolean isBot = true;
    private boolean usesBehaviourAnalysis;

    public PlayerBot(int id) {
        this.id = id;
        setValids(ALL_STACKS);
    }

    public PlayerBot(int id, boolean usesBehaviourAnalysis) {
        this(id);
        this.usesBehaviourAnalysis = usesBehaviourAnalysis;
    }

    public int getID() {
        return id;
    }

    public boolean isBot() {
        return isBot;
    }

    public void setBot(boolean isBot) {
        this.isBot = isBot;
    }

    public void reset() {
        this.valids = ALL_STACKS;
    }

    public void setValids(List<Stack> valids) {
        this.valids = valids;
    }

    public boolean usesBehaviourAnalysis() {
        return usesBehaviourAnalysis;
    }

    public boolean canSolve() {
        return valids.size() == 1;
    }

    public List<Stack> getValids() {
        return valids;
    }

    public PlayerBot deepcopy() {
        PlayerBot bot = new PlayerBot(id);
        bot.setValids(new ArrayList(this.valids));
        return bot;
    }

    private boolean fitsAnswer(Question question, int answer, Map<PlayerBot, Stack> view, Stack proposal) {
        view.put(this, proposal);
        int proposalAnswer = QuestionUtilities.invokeQuestion(view, question);
        return proposalAnswer == answer;
    }

    /**
     * update the valids list by extracting the information of question and
     * answer. We update the parameter map, despite side effects, for
     * performance. Side effects are pevented by rollback the map to original
     * state. Also, the first update creates a new list, so not to change
     * ALL_STACKS. afterwards, this new list will be modified directly. This
     * way, we don't have to shallow copy ALL_STACKS but only an already reduced
     * list
     *
     * @param view: the common view of the answering player and this player
     * @param question: question id
     * @param answer: answer
     */
    public void updateValids(Question question, int answer, Map<PlayerBot, Stack> view) {
        Iterator<Stack> iterator = valids.iterator();
        if (valids == ALL_STACKS) {
            List<Stack> newValids = new ArrayList();
            while (iterator.hasNext()) {
                Stack proposal = iterator.next();
                if (fitsAnswer(question, answer, view, proposal)) {
                    newValids.add(proposal);
                }
            }
            setValids(newValids);
        } else {
            while (iterator.hasNext()) {
                Stack proposal = iterator.next();
                if (!fitsAnswer(question, answer, view, proposal)) {
                    iterator.remove();
                }
            }
        }
        view.remove(this);
//        if (valids.isEmpty()) {
//            throw new RuntimeException("No valids left.");
//        }
    }

    @Override
    public String toString() {
        return "<" + id + ">";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PlayerBot) {
            return id == ((PlayerBot) o).getID();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    private static ArrayList<Stack> generateValids(int stacksize, int cardssize) {
        ArrayList<Stack> valids = new ArrayList();
        for (ArrayList<Integer> line : Combinatorics.genCombinationsWithRepetition(stacksize, cardssize)) {
            valids.add(new Stack(line));
        }
        return valids;
    }

    private static List<Stack> generateValids() {
        return generateValids(Stack.STACKSIZE, Stack.CARDSSIZE);
    }

    @Override
    public int compareTo(PlayerBot o) {
        return id < o.getID() ? -1 : (equals(o) ? 0 : 1);
    }

}
