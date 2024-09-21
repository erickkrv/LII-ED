package org.example;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class BitReader implements AutoCloseable {
    // Declaramos las variables que vamos a utilizar
    private ByteArrayInputStream byteArrayInputStream;
    private byte currentByte;
    private int bitIndex;
    private boolean disposed;

    // Constructor de la clase BitReader
    public BitReader(ByteArrayInputStream byteArrayInputStream) {
        // Asignamos el valor de byteArrayInputStream a la variable de instancia
        this.byteArrayInputStream = byteArrayInputStream;

        // Si byteArrayInputStream es nulo, lanzamos una excepción
        if (byteArrayInputStream == null) {
            throw new IllegalArgumentException("byteArrayInputStream no puede ser nulo");
        }
        // Inicializamos bitIndex a 8
        bitIndex = 8;
        // Reiniciamos byteArrayInputStream
        byteArrayInputStream.reset();
    }

    // Método para leer un bit
    public boolean ReadBit() {
        // Si BitReader ha sido cerrado, lanzamos una excepción
        if (disposed) {
            throw new IllegalStateException("BitReader ha sido cerrado");
        }

        // Si hemos leído todos los bits del byte actual, cargamos el siguiente byte
        if (bitIndex == 8) {
            // Si no hay más bytes disponibles, devolvemos false
            if (byteArrayInputStream.available() == 0) {
                return false;
            }

            // Leemos el siguiente byte
            currentByte = (byte) byteArrayInputStream.read();
            // Reiniciamos bitIndex a 0
            bitIndex = 0;
        }

        // Calculamos el valor del bit actual y lo devolvemos
        boolean bit = (currentByte & 1 << 7 - bitIndex) != 0;
        bitIndex++;
        return bit;
    }

    // Método para cerrar BitReader
    @Override
    public void close() throws IOException {
        // Si BitReader no ha sido cerrado, cerramos byteArrayInputStream y marcamos BitReader como cerrado
        if (!disposed) {
            byteArrayInputStream.close();
            disposed = true;
        }
    }
}