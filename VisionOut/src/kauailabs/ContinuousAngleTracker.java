// NavX driver.
// Original Copyright (c) Kauai Labs 2015.
// This rewrite Copyright (c) Cel Skeggs 2016.
// See LICENSE.txt for license details.
package org.team1540.drivers.kauailabs;

import ccre.channel.FloatCell;
import ccre.channel.FloatInput;

class ContinuousAngleTracker {

    private float last_angle;
    public final FloatCell rate = new FloatCell();
    private int zero_crossing_count;
    public final FloatCell angle = new FloatCell();

    public ContinuousAngleTracker(FloatInput yaw) {
        yaw.send(this::nextAngle);
    }

    private void nextAngle(float newAngle) {
        float adjusted_last_angle = (last_angle < 0.0f) ? last_angle + 360.0f : last_angle;
        float adjusted_curr_angle = (newAngle < 0.0f) ? newAngle + 360.0f : newAngle;
        float delta_angle = adjusted_curr_angle - adjusted_last_angle;
        this.rate.set(delta_angle);

        last_angle = newAngle;

        int angle_last_direction = 0;
        if (adjusted_curr_angle < adjusted_last_angle) {
            if (delta_angle < -180.0f) {
                angle_last_direction = -1;
            } else {
                angle_last_direction = 1;
            }
        } else if (adjusted_curr_angle > adjusted_last_angle) {
            if (delta_angle > 180.0f) {
                angle_last_direction = -1;
            } else {
                angle_last_direction = 1;
            }
        }

        if (angle_last_direction < 0) {
            if ((adjusted_curr_angle < 0.0f) && (adjusted_last_angle >= 0.0f)) {
                zero_crossing_count--;
            }
        } else if (angle_last_direction > 0) {
            if ((adjusted_curr_angle >= 0.0f) && (adjusted_last_angle < 0.0f)) {
                zero_crossing_count++;
            }
        }

        float curr_angle = last_angle;
        if (curr_angle < 0.0f) {
            curr_angle += 360.0f;
        }
        angle.set(zero_crossing_count * 360.0f + curr_angle);
    }
}
