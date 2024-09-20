package org.example;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class BitReader implements AutoCloseable {
    private ByteArrayInputStream byteArrayInputStream;
    private byte currentByte;
    private int bitIndex;
    private boolean disposed;

    public BitReader(ByteArrayInputStream byteArrayInputStream) {
        this.byteArrayInputStream = byteArrayInputStream;
        if (byteArrayInputStream == null) {
            throw new IllegalArgumentException("byteArrayInputStream cannot be null");
        }
        bitIndex = 8; // Start at the beginning of the first byte
        byteArrayInputStream.reset();
    }

    public boolean ReadBit() {
        if (disposed) {
            throw new IllegalStateException("BitReader has been disposed");
        }

        if (bitIndex == 8) { // Load new byte if the current byte is exhausted
            if (byteArrayInputStream.available() == 0) {
                return false;
            }

            currentByte = (byte) byteArrayInputStream.read();
            bitIndex = 0; // Reset bit index for the new byte
        }

        boolean bit = (currentByte & 1 << 7 - bitIndex) != 0;
        bitIndex++;
        return bit;
    }

    @Override
    public void close() throws IOException {
        if (!disposed) {
            byteArrayInputStream.close();
            disposed = true;
        }
    }
}