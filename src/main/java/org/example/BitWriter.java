package org.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitWriter implements AutoCloseable {
    private ByteArrayOutputStream byteArrayOutputStream;
    private byte currentByte;
    private int bitPosition;
    private boolean disposed = false;

    public BitWriter() {
        byteArrayOutputStream = new ByteArrayOutputStream();
        currentByte = 0;
        bitPosition = 7;
    }

    // Write a single bit to the ByteArrayOutputStream
    public void WriteBit(boolean bit) {
        if (bit) {
            currentByte |= (byte)(1 << bitPosition);
        }
        bitPosition--;

        // If we have filled a byte, write it to the ByteArrayOutputStream
        if (bitPosition < 0) {
            byteArrayOutputStream.write(currentByte);
            currentByte = 0;
            bitPosition = 7;
        }
    }

    // Flush any remaining bits and return the ByteArrayOutputStream
    public ByteArrayOutputStream Flush() {
        if (bitPosition < 7) {
            // Write the remaining bits as a byte
            byteArrayOutputStream.write(currentByte);
        }
        return byteArrayOutputStream;
    }

    // Dispose of the ByteArrayOutputStream
    @Override
    public void close() {
        if (!disposed) {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            disposed = true;
        }
    }
}