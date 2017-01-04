// NavX driver.
// Original Copyright (c) Kauai Labs 2015.
// This rewrite Copyright (c) Cel Skeggs 2016.
// See LICENSE.txt for license details.

package org.team1540.drivers.kauailabs;

public enum AHRSRegister {
    WHOAMI(0x00), HW_REV(0x01), FW_VER_MAJOR(0x02), FW_VER_MINOR(0x03), UPDATE_RATE_HZ(0x04, RegisterType.U8, WriteMode.READ_WRITE), ACCEL_FSR_G(0x05), GYRO_FSR_DPS(0x06, RegisterType.U16),

    OP_STATUS(0x08), CAL_STATUS(0x09), SELFTEST_STATUS(0x0A), CAPABILITY_FLAGS(0x0B, RegisterType.U16),

    // flags extracted from CAPABILITY_FLAGS
    CAPABILITY_OMNIMOUNT(0x0B, AHRSProtocol.NAVX_CAPABILITY_FLAG_OMNIMOUNT, RegisterType.FLAG16, WriteMode.READ_ONLY),

    CAPABILITY_YAW_RESET(0x0B, AHRSProtocol.NAVX_CAPABILITY_FLAG_YAW_RESET, RegisterType.FLAG16, WriteMode.READ_ONLY),

    CAPABILITY_VEL_AND_DISP(0x0B, AHRSProtocol.NAVX_CAPABILITY_FLAG_VEL_AND_DISP, RegisterType.FLAG16, WriteMode.READ_ONLY),

    // TODO: do I also need to check the other bit?
    IMU_CAL_COMPLETE(0x09, AHRSProtocol.NAVX_CAL_STATUS_IMU_CAL_COMPLETE, RegisterType.FLAG16, WriteMode.READ_ONLY),

    SENSOR_STATUS(0x10, RegisterType.U16), TIMESTAMP(0x12, RegisterType.U32), YAW(0x16, RegisterType.SF16_100),

    SENSOR_STATUS_MOVING(0x10, AHRSProtocol.NAVX_SENSOR_STATUS_MOVING, RegisterType.FLAG16, WriteMode.READ_ONLY),

    // apparently rotating!
    SENSOR_STATUS_YAW_STABLE(0x10, AHRSProtocol.NAVX_SENSOR_STATUS_YAW_STABLE, RegisterType.FLAG16, WriteMode.READ_ONLY),

    SENSOR_STATUS_ALTITUDE_VALID(0x10, AHRSProtocol.NAVX_SENSOR_STATUS_ALTITUDE_VALID, RegisterType.FLAG16, WriteMode.READ_ONLY),

    SENSOR_STATUS_MAGNETOMETER_CALIBRATED(0x10, AHRSProtocol.NAVX_CAL_STATUS_MAG_CAL_COMPLETE, RegisterType.FLAG16, WriteMode.READ_ONLY),

    SENSOR_STATUS_MAGNETOMETER_DISTURBANCE(0x10, AHRSProtocol.NAVX_SENSOR_STATUS_MAG_DISTURBANCE, RegisterType.FLAG16, WriteMode.READ_ONLY),

    ROLL(0x18, RegisterType.SF16_100), PITCH(0x1A, RegisterType.SF16_100), HEADING(0x1C, RegisterType.UF16_100), FUSED_HEADING(0x1E, RegisterType.UF16_100),

    ALTITUDE_I(0x20, RegisterType.U16), ALTITUDE_D(0x22, RegisterType.SF32), LINEAR_ACC_X(0x24, RegisterType.SF16_1000), LINEAR_ACC_Y(0x26, RegisterType.SF16_1000),

    LINEAR_ACC_Z(0x28, RegisterType.SF16_1000), QUAT_W(0x2A, RegisterType.U16), QUAT_X(0x2C, RegisterType.U16), QUAT_Y(0x2E, RegisterType.U16),

    QUAT_Z(0x30, RegisterType.U16), MPU_TEMP_C(0x32, RegisterType.SF16_100), GYRO_X(0x34, RegisterType.U16), GYRO_Y(0x36, RegisterType.U16),

    // TODO: GYRO and ACCEL need to be divided by (DEV_UNITS_MAX / (float) gyro_fsr_dps)
    GYRO_Z(0x38, RegisterType.U16), ACC_X(0x3A, RegisterType.U16), ACC_Y(0x3C, RegisterType.U16), ACC_Z(0x3E, RegisterType.U16),

    MAG_X(0x40, 1 / 0.15f, RegisterType.U16_MUL, WriteMode.READ_ONLY), MAG_Y(0x42, 1 / 0.15f, RegisterType.U16_MUL, WriteMode.READ_ONLY), MAG_Z(0x44, 1 / 0.15f, RegisterType.U16_MUL, WriteMode.READ_ONLY), PRESSURE_I(0x46, RegisterType.U16),

    PRESSURE_D(0x48, RegisterType.SF32), PRESSURE_TEMP(0x4A, RegisterType.U16), YAW_OFFSET(0x4C, RegisterType.U16), QUAT_OFFSET_W(0x4E, RegisterType.U16),

    QUAT_OFFSET_X(0x50, RegisterType.U16), QUAT_OFFSET_Y(0x52, RegisterType.U16), QUAT_OFFSET_Z(0x54, RegisterType.U16), INTEGRATION_CTL(0x56, RegisterType.U8, WriteMode.WRITE_ONLY), PAD_UNUSED(0x57, RegisterType.U8, WriteMode.WRITE_ONLY),

    VEL_X_I(0x58, RegisterType.U16), VEL_X_D(0x5A, RegisterType.U16), VEL_Y_I(0x5C, RegisterType.U16), VEL_Y_D(0x5E, RegisterType.U16),

    VEL_Z_I(0x60, RegisterType.U16), VEL_Z_D(0x62, RegisterType.U16), DISP_X_I(0x64, RegisterType.U16), DISP_X_D(0x66, RegisterType.U16),

    DISP_Y_I(0x68, RegisterType.U16), DISP_Y_D(0x6A, RegisterType.U16), DISP_Z_I(0x6C, RegisterType.U16), DISP_Z_D(0x6E, RegisterType.U16);

    static enum RegisterType {
        // L is followed by H
        U8(1), U16(2), U16_MUL(2), U32(4), SF16_100(2), UF16_100(2), SF16_1000(2), SF32(4), FLAG16(2);

        public final int bytes;

        private RegisterType(int bytes) {
            this.bytes = bytes;
        }
    }

    static enum WriteMode {
        READ_ONLY, WRITE_ONLY, READ_WRITE;

        public boolean canRead() {
            return this != WRITE_ONLY;
        }

        public boolean canWrite() {
            return this != READ_ONLY;
        }
    }

    final byte registerID;
    final RegisterType type;
    final WriteMode mode;
    final float multiplier;
    final int flag;

    private AHRSRegister(int registerID, float multiplier, RegisterType rt, WriteMode mode) {
        if (registerID < 0 || registerID >= 128 || rt == null || mode == null) {
            throw new IllegalArgumentException();
        }
        if (multiplier != 0 && rt != RegisterType.U16_MUL) {
            throw new IllegalArgumentException(); // must not pass multiplier
        }
        if (rt == RegisterType.FLAG16) {
            // must use other constructor
            throw new IllegalArgumentException();
        }
        this.multiplier = multiplier;
        this.flag = 0;
        this.registerID = (byte) registerID;
        this.type = rt;
        this.mode = mode;
    }

    private AHRSRegister(int registerID, int flag, RegisterType rt, WriteMode mode) {
        if (registerID < 0 || registerID >= 128 || rt == null || mode == null) {
            throw new IllegalArgumentException();
        }
        if (flag != 0 && rt != RegisterType.FLAG16) {
            throw new IllegalArgumentException(); // must not pass flag
        }
        if (rt == RegisterType.U16_MUL) {
            // must use other constructor
            throw new IllegalArgumentException();
        }
        this.multiplier = 0;
        this.flag = flag;
        this.registerID = (byte) registerID;
        this.type = rt;
        this.mode = mode;
    }

    private AHRSRegister(int registerID, RegisterType rt, WriteMode mode) {
        this(registerID, 0, rt, mode);
        if (rt == RegisterType.FLAG16 || rt == RegisterType.U16_MUL) {
            // must use other constructor
            throw new IllegalArgumentException();
        }
    }

    private AHRSRegister(int registerID, RegisterType rt) {
        this(registerID, rt, WriteMode.READ_ONLY);
    }

    private AHRSRegister(int registerID) {
        this(registerID, RegisterType.U8, WriteMode.READ_ONLY);
    }

    public byte decodeByte(RegisterIO io) {
        if (type != RegisterType.U8) {
            throw new RuntimeException("Wrong type!");
        }
        return io.getRaw()[registerID - io.getOrigin()];
    }

    public short decodeShort(RegisterIO io) {
        if (type != RegisterType.U16) {
            throw new RuntimeException("Wrong type!");
        }
        return AHRSProtocol.decodeBinaryInt16(io.getRaw(), registerID - io.getOrigin());
    }

    public boolean decodeFlag(RegisterIO io) {
        if (type != RegisterType.FLAG16) {
            throw new RuntimeException("Wrong type!");
        }
        return (AHRSProtocol.decodeBinaryInt16(io.getRaw(), registerID - io.getOrigin()) & flag) != 0;
    }

    public int decodeInt(RegisterIO io) {
        if (type != RegisterType.U32) {
            throw new RuntimeException("Wrong type!");
        }
        return AHRSProtocol.decodeBinaryInt32(io.getRaw(), registerID - io.getOrigin());
    }

    public int decodeAnyInt(RegisterIO io) {
        switch (type) {
        case U8:
            return decodeByte(io);
        case U16:
            return decodeShort(io);
        case U32:
            return decodeInt(io);
        case FLAG16:
            return decodeFlag(io) ? 1 : 0;
        default:
            throw new RuntimeException("Wrong type!");
        }
    }

    public long decodeAnyUint(RegisterIO io) {
        switch (type) {
        case U8:
            return decodeByte(io) & 0xFF;
        case U16:
            return decodeShort(io) & 0xFFFF;
        case U32:
            return decodeInt(io) & 0xFFFFFFFFL;
        case FLAG16:
            return decodeFlag(io) ? 1 : 0;
        default:
            throw new RuntimeException("Wrong type!");
        }
    }

    public float decodeAnyFloat(RegisterIO io) {
        switch (type) {
        case U8:
            return decodeByte(io) & 0xFF;
        case U16:
            return decodeShort(io) & 0xFFFF;
        case U32:
            return decodeInt(io) & 0xFFFFFFFFL;
        case FLAG16:
            return decodeFlag(io) ? 1f : 0f;
        case SF16_100:
            return AHRSProtocol.decodeProtocolSignedHundredthsFloat(io.getRaw(), registerID - io.getOrigin());
        case UF16_100:
            return AHRSProtocol.decodeProtocolUnsignedHundredthsFloat(io.getRaw(), registerID - io.getOrigin());
        case SF16_1000:
            return AHRSProtocol.decodeProtocolSignedThousandthsFloat(io.getRaw(), registerID - io.getOrigin());
        case SF32:
            return AHRSProtocol.decodeProtocol1616Float(io.getRaw(), registerID - io.getOrigin());
        case U16_MUL:
            return AHRSProtocol.decodeBinaryInt16(io.getRaw(), registerID - io.getOrigin()) * multiplier;
        default:
            throw new RuntimeException("Invalid type!");
        }
    }

    int getLastIndex() {
        return registerID + type.bytes - 1;
    }
}
