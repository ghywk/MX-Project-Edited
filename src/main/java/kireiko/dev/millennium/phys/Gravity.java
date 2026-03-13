package kireiko.dev.millennium.phys;

import lombok.Data;

@Data
public final class Gravity {
    private double gravity;
    private double motion;

    public Gravity(double gravity) {
        this.gravity = gravity;
    }

    public Gravity(Number gravity) {
        this.gravity = gravity.doubleValue();
    }

    public void run() {
        this.motion -= this.gravity;
    }

    public void addMotion(double motion) {
        this.motion += motion;
    }

    public void addMotion(Number motion) {
        this.motion += motion.doubleValue();
    }

    public void reset() {
        this.motion = 0.0;
    }
}
