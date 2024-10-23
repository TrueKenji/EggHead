package listeners;

public class UpdateEvent {

    private final Object source;
    private final String propertyName;
    private final Object oldValue;
    private final Object newValue;

    public UpdateEvent(Object source, String propertyName, Object oldValue, Object newValue) {
        this.source = source;
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Object getSource() {
        return this.source;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    @Override
    public String toString() {
        return "UpdateEvent: " + source + ", " + propertyName + ", " + oldValue + ", " + newValue;
    }

}
