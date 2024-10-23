package model;

import java.util.Collection;
import java.util.HashSet;
import listeners.Listener;
import listeners.UpdateEvent;
import logger.Logger;

public abstract class Model {

    private final Collection<Listener> listeners = new HashSet();
    protected Logger logger = Logger.getLogger();

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

}
