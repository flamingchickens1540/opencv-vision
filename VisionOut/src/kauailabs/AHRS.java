// NavX driver.
// Original Copyright (c) Kauai Labs 2015.
// This rewrite Copyright (c) Cel Skeggs 2016.
// See LICENSE.txt for license details.

package org.team1540.drivers.kauailabs;

import ccre.bus.I2CBus;
import ccre.bus.RS232Bus;
import ccre.bus.SPIBus;
import ccre.channel.BooleanCell;
import ccre.channel.BooleanInput;
import ccre.channel.DerivedBooleanInput;
import ccre.channel.DerivedFloatInput;
import ccre.channel.EventCell;
import ccre.channel.EventOutput;
import ccre.channel.FloatCell;
import ccre.channel.FloatInput;
import ccre.timers.PauseTimer;

class AHRS {
    private final PauseTimer connectionActive = new PauseTimer(100);
    final RegisterIO io;
    final EventCell onUpdate = new EventCell(connectionActive);
    private final EventCell resetOffset = new EventCell(), zeroOffset = new EventCell();
    private final ContinuousAngleTracker cat;

    public AHRS(SPIBus spi_port, int spi_bitrate, byte update_rate_hz) {
        io = new RegisterIO(new RegisterIO_SPI(spi_port, spi_bitrate), update_rate_hz, onUpdate);
        this.cat = new ContinuousAngleTracker(OffsetTracker.track(getFloat(AHRSRegister.YAW), zeroOffset, resetOffset));
    }

    public BooleanInput getBoolean(AHRSRegister register) {
        if (register.type != AHRSRegister.RegisterType.FLAG16) {
            throw new IllegalArgumentException("Cannot get a Boolean from a non-flag register, such as " + register);
        }
        return new DerivedBooleanInput(onUpdate) {
            @Override
            protected boolean apply() {
                return io.hasData() && register.decodeFlag(io);
            }
        };
    }

    public FloatInput getFloat(AHRSRegister register) {
        if (register == null) {
            throw new NullPointerException();
        }
        return new DerivedFloatInput(onUpdate) {
            @Override
            protected float apply() {
                if (io.hasData()) {
                    return register.decodeAnyFloat(io);
                } else {
                    return Float.NaN;
                }
            }
        };
    }

    public FloatInput getYawAngle() {
        return this.cat.angle;
    }

    public FloatInput getYawRate() {
        return this.cat.rate;
    }
    
    public BooleanInput getConnected() {
        return connectionActive;
    }

    public void zeroYaw() {
        // if we don't have any data, hope that we can reset the yaw through the
        // interface - in any case, nothing we can do if we assume the opposite.
        if (!io.hasData() || AHRSRegister.CAPABILITY_YAW_RESET.decodeFlag(io)) {
            io.zeroYaw();
            resetOffset.event();
        } else {
            zeroOffset.event();
        }
    }

    private final float DEV_UNITS_MAX = 32768.0f, UTESLA_PER_DEV_UNIT = 0.15f;

    public String getFirmwareVersion() {
        if (io.hasData()) {
            byte major = AHRSRegister.FW_VER_MAJOR.decodeByte(io);
            byte minor = AHRSRegister.FW_VER_MINOR.decodeByte(io);
            return Double.toString((major) + (minor / 10.0));
        } else {
            return "unknown";
        }
    }
}
