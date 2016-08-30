package org.tudelft.ewi.ceng.examples.pairHMM;

import org.apache.spark.util.SerializableBuffer;
import org.tudelft.ewi.ceng.Bean;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by root on 8/17/16.
 */
public class RawBufferBean extends Bean implements Serializable {
    public int capacity = 0;
    public int alignment = 0;
    public long address = 0L;
    public SerializableBuffer buffer;
    public byte[] bytes;

    private RawBufferBean(){}

    public byte[] getBytes(){
        return bytes;
    }

    public RawBufferBean(byte[] bytes){
        this.bytes = bytes;
    }

    public RawBufferBean(int capacity, int alignment) {
        this.capacity = capacity;
        this.alignment = alignment;

        ByteBuffer buffer = ByteBuffer.allocateDirect(capacity + alignment);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        address = PairHmmMain.getUnsafe().getLong(buffer, PairHmmMain.addressOffset);
        while(address % 128 != 0){
            buffer = ByteBuffer.allocateDirect(capacity + alignment);
            address = PairHmmMain.getUnsafe().getLong(buffer, PairHmmMain.addressOffset);
        }

        this.buffer = new SerializableBuffer(buffer);
        System.out.println(String.format("Buffer bean address is %s", Long.toHexString(address)));
    }

    public ByteBuffer getByteBuffer(){
        return buffer.buffer();
    }

    public byte[] getByteArray(){
        byte[] ret = new byte[buffer.buffer().limit()];
        buffer.buffer().get(ret);
//        buffer.buffer().rewind();
        buffer.buffer().flip();
        return ret;
    }

    public void fixBuffer(){
        RawBufferBean buff = new RawBufferBean(capacity, alignment);
        buffer = new SerializableBuffer(buff.getByteBuffer().put(getByteBuffer()));
        System.out.println("TEST");
    }

    public RawBufferBean(RawBufferBean indirect){
        ByteBuffer badBuff = indirect.getByteBuffer();
        RawBufferBean buff = new RawBufferBean(indirect.capacity, indirect.alignment);
        buff.getByteBuffer().put(badBuff);
        System.out.println("TEST");
    }
}
