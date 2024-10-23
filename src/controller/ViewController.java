package controller;

import bot.CommonInformation;
import utils.Option;
import bot.PlayerBot;
import question.QuestionList.Question;
import java.util.Map;
import listeners.Listener;
import model.GameModel;

public abstract class ViewController implements Listener{

    protected GameModel model;
    protected EggHeadController eggHeadController;

    public ViewController(EggHeadController eggHeadController) {
        this.eggHeadController = eggHeadController;
    }

    public final void newGameTriggered() {
        eggHeadController.newGame();
    }

    public void setModel(GameModel model) {
        this.model = model;
    }

    public abstract void setMode(Option mode);

    public abstract void show();

    public abstract void endTurn();

    public abstract void startGame();

    public abstract void reset();

    public abstract void showQuestion(Question questionID);

    public abstract void displayAnswer(int answer, int id);

    public abstract void showReactions(Map<PlayerBot, Boolean> reactions);

    public abstract void askForInput();

    public abstract void playerSolved(PlayerBot player);

    public abstract void updateValidSizes();

    public abstract void updateCommonInfo(CommonInformation newCommonInfo);
}
