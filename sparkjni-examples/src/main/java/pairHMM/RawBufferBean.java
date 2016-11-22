package pairHMM;

import org.apache.spark.util.SerializableBuffer;
import sparkjni.dataLink.JavaBean;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Created by Tudor on 8/17/16.
 */
public class RawBufferBean extends JavaBean implements Serializable {
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

    public ByteBuffer getByteBuffer(){
        return buffer.buffer();
    }

    public byte[] getByteArray(){
        byte[] ret = new byte[buffer.buffer().limit()];
        buffer.buffer().get(ret);
        buffer.buffer().flip();
        return ret;
    }
}
