package org.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitWriter implements AutoCloseable {
    // Declaramos las variables que vamos a utilizar
    private ByteArrayOutputStream byteArrayOutputStream;
    private byte currentByte;
    private int bitPosition;
    private boolean disposed = false;

    // Constructor de la clase BitWriter
    public BitWriter() {
        // Inicializamos byteArrayOutputStream como un nuevo ByteArrayOutputStream
        byteArrayOutputStream = new ByteArrayOutputStream();
        // Inicializamos currentByte a 0
        currentByte = 0;
        // Inicializamos bitPosition a 7
        bitPosition = 7;
    }

    // Método para escribir un bit en byteArrayOutputStream
    public void WriteBit(boolean bit) {
        // Si el bit es 1, lo añadimos a currentByte
        if (bit) {
            currentByte |= (byte)(1 << bitPosition);
        }
        // Decrementamos bitPosition
        bitPosition--;

        // Si hemos llenado un byte, lo escribimos en byteArrayOutputStream
        if (bitPosition < 0) {
            byteArrayOutputStream.write(currentByte);
            // Reiniciamos currentByte a 0
            currentByte = 0;
            // Reiniciamos bitPosition a 7
            bitPosition = 7;
        }
    }

    // Método para vaciar cualquier bit restante y devolver byteArrayOutputStream
    public ByteArrayOutputStream Flush() {
        // Si quedan bits en currentByte, los escribimos como un byte en byteArrayOutputStream
        if (bitPosition < 7) {
            byteArrayOutputStream.write(currentByte);
        }
        // Devolvemos byteArrayOutputStream
        return byteArrayOutputStream;
    }

    // Método para cerrar byteArrayOutputStream
    @Override
    public void close() {
        // Si BitWriter no ha sido cerrado, cerramos byteArrayOutputStream y marcamos BitWriter como cerrado
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