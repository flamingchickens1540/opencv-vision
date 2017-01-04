// NavX driver.
// Original Copyright (c) Kauai Labs 2015.
// This rewrite Copyright (c) Cel Skeggs 2016.
// See LICENSE.txt for license details.

package org.team1540.drivers.kauailabs;

import java.nio.ByteBuffer;

import ccre.drivers.ByteFiddling;

class AHRSProtocol {

    /* NAVX_CAL_STATUS */

    public static final byte NAVX_CAL_STATUS_IMU_CAL_STATE_MASK = 0x03;
    public static final byte NAVX_CAL_STATUS_IMU_CAL_INPROGRESS = 0x00;
    public static final byte NAVX_CAL_STATUS_IMU_CAL_ACCUMULATE = 0x01;
    public static final byte NAVX_CAL_STATUS_IMU_CAL_COMPLETE = 0x02;

    public static final byte NAVX_CAL_STATUS_MAG_CAL_COMPLETE = 0x04;
    public static final byte NAVX_CAL_STATUS_BARO_CAL_COMPLETE = 0x08;

    /* NAVX_SELFTEST_STATUS */

    public static final byte NAVX_SELFTEST_STATUS_COMPLETE = (byte) 0x80;

    public static final byte NAVX_SELFTEST_RESULT_GYRO_PASSED = 0x01;
    public static final byte NAVX_SELFTEST_RESULT_ACCEL_PASSED = 0x02;
    public static final byte NAVX_SELFTEST_RESULT_MAG_PASSED = 0x04;
    public static final byte NAVX_SELFTEST_RESULT_BARO_PASSED = 0x08;

    /* NAVX_OP_STATUS */

    public static final byte NAVX_OP_STATUS_INITIALIZING = 0x00;
    public static final byte NAVX_OP_STATUS_SELFTEST_IN_PROGRESS = 0x01;
    public static final byte NAVX_OP_STATUS_ERROR = 0x02;
    public static final byte NAVX_OP_STATUS_IMU_AUTOCAL_IN_PROGRESS = 0x03;
    public static final byte NAVX_OP_STATUS_NORMAL = 0x04;

    /* NAVX_SENSOR_STATUS */
    public static final byte NAVX_SENSOR_STATUS_MOVING = 0x01;
    public static final byte NAVX_SENSOR_STATUS_YAW_STABLE = 0x02;
    public static final byte NAVX_SENSOR_STATUS_MAG_DISTURBANCE = 0x04;
    public static final byte NAVX_SENSOR_STATUS_ALTITUDE_VALID = 0x08;
    public static final byte NAVX_SENSOR_STATUS_SEALEVEL_PRESS_SET = 0x10;
    public static final byte NAVX_SENSOR_STATUS_FUSED_HEADING_VALID = 0x20;

    /* NAVX_REG_CAPABILITY_FLAGS (Aligned w/NAV6 Flags, see IMUProtocol.h) */

    public static final short NAVX_CAPABILITY_FLAG_OMNIMOUNT = 0x0004;
    public static final short NAVX_CAPABILITY_FLAG_OMNIMOUNT_CONFIG_MASK = 0x0038;
    public static final short NAVX_CAPABILITY_FLAG_VEL_AND_DISP = 0x0040;
    public static final short NAVX_CAPABILITY_FLAG_YAW_RESET = 0x0080;

    /* NAVX_INTEGRATION_CTL */

    public static final byte NAVX_INTEGRATION_CTL_RESET_VEL_X = 0x01;
    public static final byte NAVX_INTEGRATION_CTL_RESET_VEL_Y = 0x02;
    public static final byte NAVX_INTEGRATION_CTL_RESET_VEL_Z = 0x04;
    public static final byte NAVX_INTEGRATION_CTL_RESET_DISP_X = 0x08;
    public static final byte NAVX_INTEGRATION_CTL_RESET_DISP_Y = 0x10;
    public static final byte NAVX_INTEGRATION_CTL_RESET_DISP_Z = 0x20;
    public static final byte NAVX_INTEGRATION_CTL_RESET_YAW = (byte) 0x80;

    /*
     * protocol data is encoded little endian, convert to Java's big endian
     * format
     */
    public static short decodeBinaryInt16(byte[] buffer, int offset) {
        return (short) (((buffer[offset + 1] & 0xff) << 8) | (buffer[offset] & 0xff));
    }

    public static int decodeBinaryInt32(byte[] buffer, int offset) {
        return ((buffer[offset + 3] & 0xff) << 24) | ((buffer[offset + 2] & 0xff) << 16) | ((buffer[offset + 1] & 0xff) << 8) | (buffer[offset] & 0xff);
    }

    /* -327.68 to +327.68 */
    public static float decodeProtocolSignedHundredthsFloat(byte[] buffer, int offset) {
        float signed_angle = (float) decodeBinaryInt16(buffer, offset);
        signed_angle /= 100;
        return signed_angle;
    }

    /* 0 to 655.35 */
    public static float decodeProtocolUnsignedHundredthsFloat(byte[] buffer, int offset) {
        int uint16 = (int) decodeBinaryInt16(buffer, offset);
        if (uint16 < 0) {
            uint16 += 65536;
        }
        float unsigned_float = (float) uint16;
        unsigned_float /= 100;
        return unsigned_float;
    }

    /* -32.768 to +32.768 */
    public static float decodeProtocolSignedThousandthsFloat(byte[] buffer, int offset) {
        float signed_angle = (float) decodeBinaryInt16(buffer, offset);
        signed_angle /= 1000;
        return signed_angle;
    }

    /* <int16>.<uint16> (-32768.9999 to 32767.9999) */
    public static float decodeProtocol1616Float(byte[] buffer, int offset) {
        float result = (float) decodeBinaryInt32(buffer, offset);
        result /= 65536;
        return result;
    }

    static final int CRC7_POLY = 0x0091;

    public static byte getCRC(ByteBuffer buffer, int length) {
        int i, j, crc = 0;

        for (i = 0; i < length; i++) {
            crc ^= (int) (0x00ff & buffer.get(i));
            for (j = 0; j < 8; j++) {
                if ((crc & 0x0001) != 0) {
                    crc ^= CRC7_POLY;
                }
                crc >>= 1;
            }
        }
        return (byte) crc;
    }
}
