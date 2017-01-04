// NavX driver.
// Original Copyright (c) Kauai Labs 2015.
// This rewrite Copyright (c) Cel Skeggs 2016.
// See LICENSE.txt for license details.
package org.team1540.drivers.kauailabs;

import java.io.IOException;

import ccre.channel.EventOutput;
import ccre.concurrency.ReporterThread;
import ccre.log.Logger;

final class RegisterIO {
    private final RegisterIO_SPI io_provider;
    private final EventOutput notify;
    private byte[] current_data = new byte[0];

    private static final AHRSRegister FIRST_REGISTER = AHRSRegister.WHOAMI;
    private static final AHRSRegister LAST_REGISTER = AHRSRegister.QUAT_OFFSET_Z;
    private static final int REGISTER_READ_SIZE = LAST_REGISTER.getLastIndex() - FIRST_REGISTER.registerID + 1;

    public RegisterIO(RegisterIO_SPI io_provider, byte update_rate_hz, EventOutput notify) {
        this.io_provider = io_provider;
        this.notify = notify;
        new ReporterThread("AHRS-RegisterIO") {
            protected void threadBody() throws InterruptedException {
                while (true) {
                    try {
                        if (!io_provider.checkAvailable()) {
                            Thread.sleep(100);
                            continue;
                        }
                        if (!RegisterIO.this.hasData() || AHRSRegister.UPDATE_RATE_HZ.decodeByte(RegisterIO.this) != update_rate_hz) {
                            setUpdateRateHz(update_rate_hz);
                        }
                        getCurrentData();
                        Thread.sleep(1000 / update_rate_hz);
                    } catch (Throwable thr) {
                        try {
                            Logger.severe("Error in AHRS loop! " + io_provider.checkAvailable(), thr);
                        } catch (IOException e) {
                            Logger.severe("uhm", e);
                        }
                        //Thread.sleep(100);
                    }
                }
            }
        }.start();
    }

    private final AHRSRegister[] registers = new AHRSRegister[] {

            AHRSRegister.TIMESTAMP, AHRSRegister.HW_REV, AHRSRegister.FW_VER_MAJOR, AHRSRegister.FW_VER_MINOR, AHRSRegister.WHOAMI,

            AHRSRegister.OP_STATUS, AHRSRegister.SELFTEST_STATUS, AHRSRegister.CAL_STATUS, AHRSRegister.SENSOR_STATUS, AHRSRegister.YAW, AHRSRegister.PITCH, AHRSRegister.ROLL, AHRSRegister.HEADING, AHRSRegister.MPU_TEMP_C, AHRSRegister.LINEAR_ACC_X, AHRSRegister.LINEAR_ACC_Y, AHRSRegister.LINEAR_ACC_Z, AHRSRegister.ALTITUDE_D, AHRSRegister.PRESSURE_D, AHRSRegister.FUSED_HEADING,

            AHRSRegister.UPDATE_RATE_HZ, AHRSRegister.GYRO_FSR_DPS, AHRSRegister.ACCEL_FSR_G, AHRSRegister.CAPABILITY_FLAGS,
            
            // these three are actually flags
            AHRSRegister.CAPABILITY_OMNIMOUNT, AHRSRegister.CAPABILITY_VEL_AND_DISP, AHRSRegister.CAPABILITY_YAW_RESET,

            AHRSRegister.GYRO_X, AHRSRegister.GYRO_Y, AHRSRegister.GYRO_Z, AHRSRegister.ACC_X, AHRSRegister.ACC_Y, AHRSRegister.ACC_Z, AHRSRegister.MAG_X, AHRSRegister.MAG_Y, AHRSRegister.MAG_Z,

    };

    public synchronized boolean hasData() {
        return current_data.length != 0;
    }

    synchronized byte[] getRaw() {
        return current_data;
    }

    int getOrigin() {
        return FIRST_REGISTER.registerID;
    }

    private void getCurrentData() throws InterruptedException {
        byte[] curr_data = new byte[REGISTER_READ_SIZE];
        try {
            io_provider.read(FIRST_REGISTER.registerID, curr_data);
        } catch (IOException e) {
            Logger.warning("navX read failure. Sleeping...", e);
            Thread.sleep(1000);
            return;
        }

        synchronized (this) {
            this.current_data = curr_data;
        }

        notify.event();
    }

    private void setUpdateRateHz(byte update_rate) throws InterruptedException {
        try {
            io_provider.write(AHRSRegister.UPDATE_RATE_HZ, update_rate);
        } catch (IOException e) {
            Logger.warning("navX write failure on setUpdateRateHz(). Sleeping...", e);
            Thread.sleep(1000);
        }
    }

    public void zeroYaw() {
        try {
            io_provider.write(AHRSRegister.INTEGRATION_CTL, AHRSProtocol.NAVX_INTEGRATION_CTL_RESET_YAW);
        } catch (IOException e) {
            Logger.warning("navX write failure on zeroYaw()", e);
        }
    }
}
