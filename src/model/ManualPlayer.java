package model;

public class ManualPlayer extends Player {
    
    private GameModel model;

    public ManualPlayer() {
    }

    public ManualPlayer(Stack stack) {
        setStack(stack);
    }

    /**
     * When manual players are asked to answer, the main controller needs to be
     * informed. This happens via UpdateEvent and gameState in model.
     */
    @Override
    public void produceAnswer() {
        //model.setGameState(GameModel.WAITINGFORANSWER);
    }
}
