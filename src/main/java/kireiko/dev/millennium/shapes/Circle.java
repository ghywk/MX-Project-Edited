package kireiko.dev.millennium.shapes;

import kireiko.dev.millennium.math.BuildSpeed;
import kireiko.dev.millennium.math.GeneralMath;
import kireiko.dev.millennium.vectors.Vec2;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/*
Draw a circle to ArrayList
 */

@Data
public final class Circle {
    private final List<Vec2> vectors;

    public Circle() {
        this.vectors = new ArrayList<>();
    }

    // round
    // 1 - circle
    // 0 - square

    // res
    // 1 - 360
    // 2 - 180
    // 4 - 90
    public void build(BuildSpeed s, double size, double round, int res) {
        vectors.clear();
        Vec2 firstVector = null;
        for (int k = 0; k <= 720; k += res) {
            double x = GeneralMath.sin((float) Math.toRadians(k), s) * res;
            double y = GeneralMath.cos((float) Math.toRadians(k), s) * res;
            double xj = x / Math.max(Math.abs(x), 1) * round;
            double yj = y / Math.max(Math.abs(y), 1) * round;
            Vec2 v2 = new Vec2(xj * size, yj * size);
            if (k == 0) {
                firstVector = v2;
            } else if (v2.compare(firstVector)) {
                break;
            }
            vectors.add(v2);
        }
    }

}
