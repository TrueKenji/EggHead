package controller;

import utils.Option;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.Timer;

public class TimerController implements ActionListener {

    private final EggHeadController controller;
    private final HashMap<Option, Integer> speedToDelayMap = getSpeedToDelayMap();
    private final Timer timer;

    public TimerController(EggHeadController controller) {
        this.controller = controller;
        this.timer = new Timer(1000, this);
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    public void setDelay(int delay) {
        timer.setDelay(delay);
    }

    public boolean isRunning() {
        return timer.isRunning();
    }

    public void setDelay(Option option) {
        setDelay(speedToDelayMap.get(option));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        controller.step();
    }

    public static HashMap<Option, Integer> getSpeedToDelayMap() {
        HashMap<Option, Integer> speedToDelayMap = new HashMap();
        speedToDelayMap.put(Option.SPEED.SLOW, 1000);
        speedToDelayMap.put(Option.SPEED.NORMAL, 100);
        speedToDelayMap.put(Option.SPEED.FAST, 1);
        return speedToDelayMap;
    }

}
