package cz.cvut.fel.pm2;

import jakarta.persistence.Embeddable;

@Embeddable
public class UnlockMethodState {

    private boolean enabled;
    private boolean complete;

    // Constructor

    public UnlockMethodState() {
        this.enabled = false;
        this.complete = false;
    }

    public UnlockMethodState(boolean enabled, boolean complete) {
        this.enabled = enabled;
        this.complete = complete;
    }

    // Getters and Setters
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
