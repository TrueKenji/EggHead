package controller;

import utils.Option;
import question.QuestionUtilities;
import bot.BotController;
import bot.PlayerBot;
import bot.stats.Statistics;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import javax.swing.SwingUtilities;
import model.GameModel;
import logger.Logger;

public class EggHeadController {

    private GameModel model;
    private TimerController timerController;
    private ViewController viewController;
    private BotController botController = new BotController();
    private Map<Class<? extends Option>, Option> options = Option.getDefaultOptions();
    private final Logger logger = Logger.getLogger();
    private final int numPlayers = 4;
    private final int numRounds = 1000;
    private Map<PlayerBot, Boolean> reactions; // temp save the reactions while handling all of the reaction analyze process, prevent issue of concurrent changes in reaction
    private Statistics statistics;

    public EggHeadController() {
        setView();
        viewController.show();
    }

    public void newGame() {
        model = new GameModel();
        model.addListener(logger);
        model.newGame(numPlayers);
        botController.init(model);
        applyOptions(options);
        viewController.setModel(model);
        viewController.startGame();
        viewController.updateCommonInfo(botController.commonInfo);
        model.addListener(viewController);
        if (options.get(Option.MODE.class) == Option.MODE.TIMER_CONTROLLED) {
            timerController.start();
        } else if (options.get(Option.MODE.class) == Option.MODE.CONTINUOUS) {
            continuousPlay();
        }
    }

    public void continuousPlay() {
        while (model.getRound() < numRounds) {
            step();
        }
        model.setGameState(GameModel.GameState.END_GAME);
    }

    public void step() {
        switch (model.getGameState()) {
            case DEFAULT:
                drawAndShowQuestion();
                break;
            case QUESTION_SHOWN:
                answerQuestion();
                break;
            case ANSWER_SHOWN:
                showReactions();
                break;
            case LOOP_REACTIONS:
                showReactions();
                break;
            case REACTIONS_SHOWN:
                executeSolve();
                break;
        }
    }

    public void drawAndShowQuestion() {
        model.generateQuestion();
        viewController.showQuestion(model.getQuestion());
        model.setGameState(GameModel.GameState.QUESTION_SHOWN);
    }

    public void answerQuestion() {
        int answer = getAnswer();
        model.setAnswer(answer);
        model.setGameState(GameModel.GameState.ANSWER_SHOWN);
        viewController.displayAnswer(answer, model.getCurrentPlayer().getID());
        viewController.updateValidSizes();
        viewController.updateCommonInfo(botController.commonInfo);
    }

    public void showReactions() {
        reactions = model.getReactions();
        logger.println(reactions, Logger.DEBUG, Logger.EGG_HEAD_CONTROLLER);
        viewController.showReactions(reactions);
        model.setGameState(GameModel.GameState.REACTIONS_SHOWN);
        viewController.updateValidSizes();
        viewController.updateCommonInfo(botController.commonInfo);
    }

    public void executeSolve() {
        for (Map.Entry<PlayerBot, Boolean> entry : reactions.entrySet()) {
            if (entry.getValue()) { // order is not relevant
                if(!entry.getKey().getValids().getFirst().equals(model.getPlayerStack(entry.getKey()))){
                    throw new RuntimeException("Player solved wrongly");
                }
                entry.getKey().reset();
                model.playerSolved(entry.getKey());
                viewController.playerSolved(entry.getKey());
            }
        }
        if (canSomeoneSolve(reactions) || botController.wasUpdatedByReaction()) {
            model.setGameState(GameModel.GameState.LOOP_REACTIONS); // loop if there is a chance that a player reacts differently now
        } else {
            viewController.endTurn();
            model.newRound(); // round can be ended
        }
    }

    public void quit() {
        stopTimer();
        viewController.reset();
    }

    public boolean verifyCommonInfo() {
        if (!botController.verifyCommonInfo()) {
            timerController.stop();
            return false;
        }
        return true;
    }

    public static boolean canSomeoneSolve(Map<PlayerBot, Boolean> reactions) {
        return reactions.values().stream().anyMatch(Boolean::booleanValue);
    }

    public void stopTimer() {
        if (timerController != null && timerController.isRunning()) {
            timerController.stop();
        }
    }

    public void resume() {
        timerController.start();
    }

    public int getAnswer() {
        return QuestionUtilities.invokeQuestion(model.getAnsweringPlayerView(), model.getQuestion());
    }

    public void optionsOpened() {
        stopTimer();
    }

    public void resolveButtonPressed() {
        timerController.stop();
//        viewController.askForGuess();
        logger.println("ResolveButtonPressed", Logger.PROD);
    }

    public void applyOptions(Map<Class<? extends Option>, Option> options) {
        logger.println(options, Logger.PROD);
        this.options = options;
        for (Map.Entry<Class<? extends Option>, Option> entry : options.entrySet()) {
            try {
                String optionName = entry.getKey().getSimpleName();
                Method method = EggHeadController.class.getMethod("set"
                        + optionName.substring(0, 1)
                        + optionName.substring(1).toLowerCase());
                method.invoke(this);
            } catch (IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                System.out.println("Fatal Exception in applyOptions: " + e.getLocalizedMessage() + " " + e.getMessage() + " " + e.toString() + " " + e.getClass() + " " + e.getCause());
            }
        }
    }

    public void setPlayer() {

    }

    public void setMode() {
        Option option = options.get(Option.MODE.class);
        if (option == Option.MODE.CONTINUOUS) {
            //viewController.setMode(option)
            // -> don't update model changes
            // while(not finished)
            //      step()
        } else if (option == Option.MODE.TIMER_CONTROLLED) {
            viewController.setMode(option);
            // -> disable continue button
            // start Timer(delay = option.SPEED)
            // timer.action -> controller.step()
        } else if (option == Option.MODE.USER_CONTROLLED) {
            viewController.setMode(option);
            // -> Enable continue button
        }
    }

    public void setDifficulty() {
        if (model != null) {
            botController.setDifficulty(options.get(Option.DIFFICULTY.class));
        }
    }

    public void setSpeed() {
        Option option = options.get(Option.SPEED.class);
        if (timerController == null) {
            timerController = new TimerController(this);
        }
        timerController.setDelay(option);
    }

    public final void setView() {
        if (this.viewController == null) {
            Option option = options.get(Option.VIEW.class);
            if (option == Option.VIEW.NONE) {
                this.viewController = new EmptyViewController(this);
            } else if (option == Option.VIEW.FRAME) {
                this.viewController = new FrameViewController(this);
            } else if (option == Option.VIEW.CONSOLE) {
                this.viewController = new EmptyViewController(this); //ToDo: new ConsoleViewController(this);
            }
        }
    }

    public void setStats() {
        Option option = options.get(Option.STATS.class);
        if (option == Option.STATS.TRUE && model != null) {
            if (statistics == null) {
                statistics = new Statistics(model.getPlayers().keySet());
                model.addListener(statistics);
                if (botController.commonInfo != null) {
                    botController.commonInfo.addListener(statistics);
                }
            }
        } else if (option == Option.STATS.FALSE && model != null) {
            model.removeListener(statistics);
            statistics = null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EggHeadController());
    }

    public Map<Class<? extends Option>, Option> getOptions() {
        return options;
    }

}
