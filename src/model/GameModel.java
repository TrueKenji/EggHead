package model;

import bot.PlayerBot;
import question.QuestionList.Question;
import question.QuestionUtilities;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import listeners.UpdateEvent;
import logger.Logger;

public class GameModel extends Model {

    public static final int NUM_LEVEL_3 = 3;

    private TreeMap<PlayerBot, Stack> players;
    private Iterator<PlayerBot> playersIterator; // only used to easier keep track of the currentPlayer
    private PlayerBot currentPlayer;
    private boolean answersByDummyBot = true; //whether answers are given by an additional hypothetic bot
    private Question question;
    private int answer = -1;
    private GameState gameState;
    private int round;
    private TreeMap<PlayerBot, Integer> points;

    public enum GameState {
        DEFAULT,
        QUESTION_SHOWN,
        ANSWER_SHOWN,
        LOOP_REACTIONS,
        REACTIONS_SHOWN,
        END_GAME
    }

    public void setGameState(GameState gameState) {
        notifyListeners(new UpdateEvent(this, "GameState", this.gameState, gameState));
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setAnswerByDummyBot(boolean answersByDummyBot) {
        this.answersByDummyBot = answersByDummyBot;
    }

    public void setAnswer(int answer) {
        notifyListeners(new UpdateEvent(this, "Answer", this.answer, answer));
        this.answer = answer;
    }

    public int getAnswer() {
        return answer;
    }

    public int getRound() {
        return round;
    }

    public Map<PlayerBot, Boolean> getReactions() {
        Map<PlayerBot, Boolean> reactions = new TreeMap();
        for (PlayerBot player : players.keySet()) {
            reactions.put(player, player.canSolve());
        }
        return reactions;
    }

    public void playerSolved(PlayerBot player) {
        this.notifyListeners(new UpdateEvent(this, "PlayerState", player, player));
        players.put(player, Stack.generate());
        points.put(player, points.get(player) + 1);
    }

    public void setCurrentPlayer(PlayerBot currentPlayer) {
        notifyListeners(new UpdateEvent(this, "CurrentPlayer", this.currentPlayer, currentPlayer));
        this.currentPlayer = currentPlayer;
    }

    public PlayerBot getCurrentPlayer() {
        return currentPlayer;
    }

    public void setQuestion(Question question) {
        notifyListeners(new UpdateEvent(this, "QuestionID", this.question, question));
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }

    public void newGame(int numPlayers) {
        players = new TreeMap();
        points = new TreeMap();
        for (int i = 0; i < numPlayers; i++) {
            PlayerBot player = new PlayerBot(i, i < NUM_LEVEL_3);
            Stack stack = Stack.generate(Stack.STACKSIZE);
            players.put(player, stack);
            points.put(player, 0);
            logger.println("Model initialised Player " + player + " with " + stack, Logger.MODEL);
        }
        setGameState(GameState.DEFAULT);
        incrementPlayer();
    }

    public void newRound() {
        setGameState(GameModel.GameState.DEFAULT);
        incrementPlayer();
        notifyListeners(new UpdateEvent(this, "Round", round, ++round));
    }

    public void incrementPlayer() {
        if (playersIterator == null || !playersIterator.hasNext()) {
            initPlayersIterator();
        }
        setCurrentPlayer(playersIterator.next());
    }

    public void generateQuestion() {
        setQuestion(QuestionUtilities.next());
    }

    private void initPlayersIterator() {
        playersIterator = players.keySet().iterator();
    }

    public Map<PlayerBot, Stack> getAnsweringPlayerView() {
        if (answersByDummyBot) {
            return getPlayersDeepCopy();
        }
        return getPlayerView(currentPlayer);
    }

    /**
     * generate a list of player ids containg all players the current player see
     *
     * @param numPlayers
     * @param playerID: the id of the current player
     * @return List, a list of player ids the player answering a question sees
     * TODO: possibly move to a bot controller
     */
    public List<Integer> generateView(int numPlayers, int playerID) {
        List playerIDs = utils.ArrayComprehension.generateStream(0, numPlayers - 1);
        playerIDs.remove(playerID);
        return playerIDs;
    }

    public Map<PlayerBot, Stack> getPlayerView(PlayerBot player) {
        Map<PlayerBot, Stack> copy = getPlayersDeepCopy();
        Stack s = copy.remove(player);
//        logger.println("Removing interpreter for playerView:" + s, Logger.DEBUG, Logger.MODEL, Logger.GET_PLAYER_VIEW);
        return copy;
    }

    public Map<PlayerBot, Stack> getIntersectedPlayerView(PlayerBot interpreter, Map<PlayerBot, Stack> answeringPlayerView) {
        answeringPlayerView.remove(interpreter);
        return answeringPlayerView;
    }

    public Map<PlayerBot, Stack> getPlayers() {
        return players;
    }

    public Map<PlayerBot, Stack> getPlayersDeepCopy() {
        return utils.ArrayComprehension.deepcopyPlayersMap(players);
    }

    public Stack getPlayerStack(PlayerBot player) {
        return players.get(player);
    }

    public int getPlayerPoints(PlayerBot player) {
        return points.get(player);
    }

}
