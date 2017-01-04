// NavX driver.
// Original Copyright (c) Kauai Labs 2015.
// This rewrite Copyright (c) Cel Skeggs 2016.
// See LICENSE.txt for license details.
package org.team1540.drivers.kauailabs;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.locks.LockSupport;

import ccre.bus.SPIBus;
import ccre.bus.SPIIO;
import ccre.time.Time;

class RegisterIO_SPI {

    private final SPIIO port;
    private final ByteBuffer local = ByteBuffer.allocateDirect(256);

    public static final int DEFAULT_SPI_BITRATE_HZ = 100000; // originally 500000

    public RegisterIO_SPI(SPIBus spi_port, int bitrate) {
        port = spi_port.configure(bitrate, true, true, true, true);
    }

    private synchronized void write(byte address, byte value) throws IOException {
        local.clear();
        local.put((byte) (address | 0x80));
        local.put(value);
        local.put(AHRSProtocol.getCRC(local, 2));
        port.writeExact(local, 3);
    }
    
    public synchronized boolean checkAvailable() throws IOException {
        local.clear();
        local.put((byte) 0);
        local.put((byte) 1);
        local.put(AHRSProtocol.getCRC(local, 2));
        if (port.write(local, 3) != 3) {
            return false;
        }
        try {
            // delay 200 us /* TODO: delay more accurately */
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new InterruptedIOException();
        }
        local.clear();
        if (port.readInitiated(local, 2) != 2) {
            return false;
        }
        byte gotten = local.get();
        byte found = local.get();
        byte crc = AHRSProtocol.getCRC(local, 1);
        if (found != crc) {
            throw new IOException("CRC error: " + found + " instead of " + crc + " in " + gotten);
        }
        return true;
    }

    public synchronized void read(byte first_address, byte[] buffer) throws IOException {
        local.clear();
        if (buffer.length > 255) {
            throw new IllegalArgumentException("Buffer is too long: " + buffer.length);
        }
        local.put(first_address);
        local.put((byte) buffer.length);
        local.put(AHRSProtocol.getCRC(local, 2));
        port.writeExact(local, 3);
        // delay 200 us /* TODO: delay more accurately */
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new InterruptedIOException();
        }
        local.clear();
        port.readInitiatedExact(local, buffer.length + 1);
        local.get(buffer);
        byte crc = AHRSProtocol.getCRC(local, buffer.length);
        byte found = local.get();
        if (found != crc) {
            throw new IOException("CRC error: " + found + " instead of " + crc + " in " + Arrays.toString(buffer));
        }
    }

    public void write(AHRSRegister reg, byte value) throws IOException {
        if (!reg.mode.canWrite() || reg.type != AHRSRegister.RegisterType.U8) {
            throw new IllegalArgumentException("Not a writable register, or not a U8 register!");
        }
        this.write(reg.registerID, value);
    }
}
