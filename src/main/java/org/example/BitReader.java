package org.example;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class BitReader implements AutoCloseable{
    private ByteArrayInputStream byteArrayInputStream;
    private byte _currentByte;
    private int _bitPosition;
    private boolean _disposed;

    public BitReader(ByteArrayInputStream memoryStream){
        this.byteArrayInputStream = memoryStream;
        _bitPosition = 8;
        memoryStream.reset();
    }
    public boolean ReadBit(){
        if(_disposed){
            throw new IllegalStateException("BitReader has been disposed");
        }
        if(_bitPosition == 8){
            if(byteArrayInputStream.available() == 0){
                return false;
            }
            _currentByte = (byte) byteArrayInputStream.read();
            _bitPosition = 0;
        }
        boolean bit = (_currentByte & 1 << 7 - _bitPosition) != 0;
        _bitPosition++;
        return bit;
    }
    @Override
    public void close() throws IOException {
        if(!_disposed){
            byteArrayInputStream.close();
            _disposed = true;
        }
    }
}
