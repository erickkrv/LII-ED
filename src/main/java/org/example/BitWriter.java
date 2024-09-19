package org.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitWriter implements AutoCloseable {
    private ByteArrayOutputStream byteArrayOutputStream;
    private byte _currentByte;
    private int _bitPosition;
    private boolean _disposed = false;

    public BitWriter() {
        byteArrayOutputStream = new ByteArrayOutputStream();
        _currentByte = 0;
        _bitPosition = 7;
    }
    //Escribir solo un bit al ByteArrayOutputStream
    public void WriteBit(boolean bit){
        if(bit){
            _currentByte |= (byte)(1 << _bitPosition);
        }
        _bitPosition--;
        //Si llenamos un byte, escribirlo
        if(_bitPosition < 0){
            byteArrayOutputStream.write(_currentByte);
            _currentByte = 0;
            _bitPosition = 7;
        }
    }
    //Limpiar buffer y regresar al ByteArrayOutputStream
    public byte[] Flush(){
        if(_bitPosition < 7){
           byteArrayOutputStream.write(_currentByte);
        }
        return byteArrayOutputStream.toByteArray();
    }
    @Override
    public void close() throws IOException{
        if(!_disposed){
            byteArrayOutputStream.close();
            _disposed = true;
        }
    }
}
