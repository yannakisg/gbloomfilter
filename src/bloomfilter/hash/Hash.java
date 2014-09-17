package bloomfilter.hash;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class Hash {

    protected Hash() {

    }

    public abstract int hash(byte[] data);

    public int hash(String data) {
        return hash(data.getBytes());
    }

    public int hash(byte data) {
        byte[] b = new byte[1];
        b[0] = data;
        return hash(b);
    }

    public int hash(int data) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE >> 3);
        buffer.order(ByteOrder.nativeOrder());
        buffer.putInt(data);
        return hash(buffer.array());
    }

    public int hash(long data) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE >> 3);
        buffer.order(ByteOrder.nativeOrder());
        buffer.putLong(data);
        return hash(buffer.array());
    }

    public int hash(int[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Illegal Argument");
        }
        ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE >> 3 * data.length);
        buffer.order(ByteOrder.nativeOrder());
        for (int i = 0; i < data.length; i++) {
            buffer.putInt(data[i]);
        }

        return hash(buffer.array());
    }

    public int hash(long[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Illegal Argument");
        }
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE >> 3 * data.length);
        buffer.order(ByteOrder.nativeOrder());
        for (int i = 0; i < data.length; i++) {
            buffer.putLong(data[i]);
        }

        return hash(buffer.array());
    }
}
