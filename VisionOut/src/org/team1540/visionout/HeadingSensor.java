package robot;

import kauailabs.FRCAHRS;

import ccre.channel.FloatInput;
import ccre.cluck.Cluck;
import ccre.channel.DerivedFloatInput;

public class HeadingSensor {
    private static final FRCAHRS sensor = FRCAHRS.newSPI_MXP();

    public static final FloatInput yawAngle = sensor.getYawAngle();
    public static final FloatInput yawRate = sensor.getYawRate();
    public static final FloatInput pitchAngle = sensor.getRollAngle(); // pitch is actually roll

    public static FloatInput absoluteYaw;

    public static void setup() {
        absoluteYaw = new DerivedFloatInput(yawAngle) {
            int accumulator = 0;
            float oldyaw = yawAngle.get();

            @Override
            protected float apply() {
                float currentYaw = yawAngle.get();
                // Detects that there has been a large jump in yaw angle, meaning the sensor has overflowed from 360 back to 0, or vise versa
                if (Math.abs(currentYaw - oldyaw) > 180) {
                    if (oldyaw > 270) {
                        accumulator += 360;
                    } else if (oldyaw < 90) {
                        accumulator -= 360;
                    }
                }
                oldyaw = currentYaw;
                return accumulator + currentYaw;
            }

        };

        Cluck.publish("Heading Yaw Angle", yawAngle);
        Cluck.publish("Heading Yaw Angle Absolute", absoluteYaw);
        Cluck.publish("Heading Yaw Rate", yawRate);
        Cluck.publish("Heading Yaw Reset", sensor.eventZeroYaw());
        Cluck.publish("Heading Pitch Angle", pitchAngle);
        Cluck.publish("Heading Connected", sensor.getConnected());
        //Instrumentation.recordHeading(sensor.getConnected(), yawAngle, yawRate, pitchAngle);
    }
}