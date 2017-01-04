// NavX driver.
// Original Copyright (c) Kauai Labs 2015.
// This rewrite Copyright (c) Cel Skeggs 2016.
// See LICENSE.txt for license details.
package org.team1540.drivers.kauailabs;

import ccre.channel.DerivedFloatInput;
import ccre.channel.EventInput;
import ccre.channel.FloatCell;
import ccre.channel.FloatInput;

class OffsetTracker {
    private final float[] history = new float[10];
    private int next_index = 0;
    private final FloatCell offset = new FloatCell();

    private OffsetTracker() {
    }

    public void putNext(float curr_value) {
        history[next_index] = curr_value;
        next_index = (next_index + 1) % history.length;
    }

    public void zero() {
        float value_history_sum = 0.0f;
        for (float elem : history) {
            value_history_sum += elem;
        }
        offset.set(value_history_sum / history.length);
    }

    public void resetOffset() {
        offset.set(0);
    }

    public FloatInput addOffset(FloatInput original) {
        return new DerivedFloatInput(original, offset) {
            @Override
            protected float apply() {
                float out = original.get() - offset.get();
                if (out < -180) {
                    out += 360;
                } else if (out > 180) {
                    out -= 360;
                }
                return out;
            }
        };
    }

    public static FloatInput track(FloatInput original, EventInput zero, EventInput resetOffset) {
        OffsetTracker oft = new OffsetTracker();
        original.send(oft::putNext);
        zero.send(oft::zero);
        resetOffset.send(oft::resetOffset);
        return oft.addOffset(original);
    }
}
