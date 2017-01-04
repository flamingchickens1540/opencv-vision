// NavX driver.
// Original Copyright (c) Kauai Labs 2015.
// This rewrite Copyright (c) Cel Skeggs 2016.
// See LICENSE.txt for license details.
package org.team1540.drivers.kauailabs;

import ccre.channel.BooleanInput;
import ccre.channel.DerivedBooleanInput;
import ccre.channel.DerivedFloatInput;
import ccre.channel.EventInput;
import ccre.channel.EventOutput;
import ccre.channel.FloatInput;
import ccre.channel.UpdatingInput;
import ccre.frc.FRC;

public class FRCAHRS {
    private final AHRS ahrs;

    private static final byte NAVX_DEFAULT_UPDATE_RATE_HZ = 60;

    private FRCAHRS(AHRS ahrs) {
        if (ahrs == null) {
            throw new NullPointerException();
        }
        this.ahrs = ahrs;
    }

    public FloatInput getYawAngle() {
        return ahrs.getYawAngle();
    }

    public FloatInput getYawRate() {
        return ahrs.getYawRate();
    }

    public FloatInput getPitchAngle() {
        return ahrs.getFloat(AHRSRegister.PITCH);
    }

    public FloatInput getRollAngle() {
        return ahrs.getFloat(AHRSRegister.ROLL);
    }

    public BooleanInput getConnected() {
        return ahrs.getConnected();
    }

    public void zeroYaw() {
        ahrs.zeroYaw();
    }

    public EventOutput eventZeroYaw() {
        return ahrs::zeroYaw;
    }

    public BooleanInput getBoolean(AHRSRegister reg) {
        return ahrs.getBoolean(reg);
    }

    public FloatInput getFloat(AHRSRegister reg) {
        return ahrs.getFloat(reg);
    }

    public EventInput onUpdate() {
        return ahrs.onUpdate;
    }

    public void onUpdate(EventOutput o) {
        ahrs.onUpdate.send(o);
    }

    public static FRCAHRS newSPI_Onboard(int cs) {
        return new FRCAHRS(new AHRS(FRC.onboardSPI(cs, "navX"), RegisterIO_SPI.DEFAULT_SPI_BITRATE_HZ, NAVX_DEFAULT_UPDATE_RATE_HZ));
    }

    public static FRCAHRS newSPI_MXP() {
        return new FRCAHRS(new AHRS(FRC.mxpSPI("navX"), RegisterIO_SPI.DEFAULT_SPI_BITRATE_HZ, NAVX_DEFAULT_UPDATE_RATE_HZ));
    }
}
