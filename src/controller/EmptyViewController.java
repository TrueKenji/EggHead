package controller;

import bot.CommonInformation;
import utils.Option;
import bot.PlayerBot;
import question.QuestionList.Question;
import java.util.Map;
import listeners.UpdateEvent;

// empty implementation of ViewController
// main controller needs to have something to call
public class EmptyViewController extends ViewController {

    public EmptyViewController(EggHeadController eggHeadController) {
        super(eggHeadController);
    }

    @Override
    public void show() {
        this.eggHeadController.newGame();
    }

    @Override
    public void startGame() {
    }

    @Override
    public void showQuestion(Question questionID) {
    }

    @Override
    public void displayAnswer(int answer, int id) {
    }

    @Override
    public void showReactions(Map<PlayerBot, Boolean> reactions) {
    }

    @Override
    public void setMode(Option mode) {

    }

    @Override
    public void endTurn() {

    }

    @Override
    public void askForInput() {

    }

    @Override
    public void playerSolved(PlayerBot player) {

    }

    @Override
    public void updateValidSizes() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void updateCommonInfo(CommonInformation newCommonInfo) {

    }

    @Override
    public void notify(UpdateEvent e) {

    }

}
