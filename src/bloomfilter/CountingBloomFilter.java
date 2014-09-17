package bloomfilter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class CountingBloomFilter extends BloomFilter {

    private byte[] _array;

    public CountingBloomFilter(int totalElements, double errorRate, String hashFuncStr) {
        this(totalElements, _MAXIMUM_SIZE, errorRate, hashFuncStr);
    }

    public CountingBloomFilter(int totalElements, int maxSize, double errorRate, String hashFuncStr) {
        super(totalElements, maxSize, errorRate, hashFuncStr);

        findOptimalParameters();

        this._array = new byte[(_size >> 1)];
        Arrays.fill(_array, (byte) 0);
    }

    @Override
    public void add(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Illegal Argument");
        }

        if (_currentElements + 1 > _totalElements) {
            throw new IllegalArgumentException("You have reached the maximum capacity");
        }

        long h;
        ByteBuffer buffer = ByteBuffer.allocate(data.length + (Integer.SIZE >> 3));
        buffer.put(data);

        for (int i = 0; i < _totalHashes; i++) {
            buffer.mark();
            buffer.putInt(i);

            h = (_hashFunc.hash(buffer.array()) & 0x00000000FFFFFFFFL) % _size;

            increaseValue(data, (int) h);
            buffer.reset();
        }

        _currentElements++;
    }

    public void delete(int data) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE >> 3);
        buffer.order(ByteOrder.nativeOrder());
        buffer.putInt(data);
        delete(buffer.array());
    }

    public void delete(byte data) {
        byte[] b = new byte[1];
        b[0] = data;
        delete(b);
    }

    public void delete(long data) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE >> 3);
        buffer.order(ByteOrder.nativeOrder());
        buffer.putLong(data);
        delete(buffer.array());
    }

    public void delete(int[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Illegal Argument");
        }
        ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE >> 3 * data.length);
        buffer.order(ByteOrder.nativeOrder());
        for (int i = 0; i < data.length; i++) {
            buffer.putInt(data[i]);
        }
        delete(buffer.array());
    }

    public void delete(String str) {
        if (str.equals("")) {
            throw new IllegalArgumentException("Illegal Argument");
        }
        delete(str.getBytes());
    }

    public void delete(long[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Illegal Argument");
        }
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE >> 3 * data.length);
        buffer.order(ByteOrder.nativeOrder());
        for (int i = 0; i < data.length; i++) {
            buffer.putLong(data[i]);
        }
        delete(buffer.array());
    }

    public void delete(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Illegal Argument");
        }

        if (_currentElements == 0 || _totalHashes == 0) {
            return;
        }

        long h;
        ByteBuffer buffer = ByteBuffer.allocate(data.length + (Integer.SIZE >> 3));
        buffer.put(data);

        for (int i = 0; i < _totalHashes; i++) {
            buffer.mark();
            buffer.putInt(i);

            h = (_hashFunc.hash(buffer.array()) & 0x00000000FFFFFFFFL) % _size;
            decreaseValue(data, (int) h);
            buffer.reset();
        }

        _currentElements--;

    }

    @Override
    public boolean contains(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Illegal Argument");
        }

        if (_currentElements == 0 || _totalHashes == 0) {
            return false;
        }

        long h;
        byte value;
        ByteBuffer buffer = ByteBuffer.allocate(data.length + (Integer.SIZE >> 3));
        buffer.put(data);

        for (int i = 0; i < _totalHashes; i++) {
            buffer.mark();
            buffer.putInt(i);

            h = (_hashFunc.hash(buffer.array()) & 0x00000000FFFFFFFFL) % _size;
            value = getValue(data, (int) h);

            if (value == (byte) 0) {
                return false;
            }
            buffer.reset();
        }

        return true;
    }

    @Override
    public void clear() {
        Arrays.fill(_array, (byte) 0);
    }

    private byte getValue(byte[] data, int pos) {
        int bytePos = pos >> 1;
        int bitPos = pos & 1;
        byte newByte = _array[bytePos];

        if (bitPos == 1) {
            return (byte) ((byte) ((newByte >> 4)) & 0x0F);
        } else {
            return (byte) ((byte) ((newByte << 4) >> 4) & 0x0F);
        }
    }

    private void increaseValue(byte[] data, int pos) {
        int bytePos = pos >> 1;
        int bitPos = pos & 1;
        byte newByte = _array[bytePos];
        byte v, v1;
        if (bitPos == 1) {
            v = (byte) ((byte) ((newByte >> 4)) & 0x0F);
            v1 = (byte) ((byte) ((newByte << 4) >> 4) & 0x0F);
            if (v < 16) {
                v++;
            }
            v = (byte) ((byte) (v << 4) & 0xF0);
            newByte = (byte) (v1 | v);
        } else {
            v = (byte) ((byte) ((newByte << 4) >> 4) & 0x0F);
            v1 = (byte) ((byte) ((newByte >> 4) << 4) & 0xF0);
            if (v < 16) {
                v++;
            }

            newByte = (byte) (v1 | v);
        }

        _array[bytePos] = newByte;
    }

    private void decreaseValue(byte[] data, int pos) {
        int bytePos = pos >> 1;
        int bitPos = pos & 1;
        byte newByte = _array[bytePos];
        byte v, v1;
        if (bitPos == 1) {
            v = (byte) ((byte) ((newByte >> 4)) & 0x0F);
            v1 = (byte) ((byte) ((newByte << 4) >> 4) & 0x0F);
            if (v > 0) {
                v--;
            }
            v = (byte) ((byte) (v << 4) & 0xF0);
            newByte = (byte) (v1 | v);
        } else {
            v = (byte) ((byte) ((newByte << 4) >> 4) & 0x0F);
            v1 = (byte) ((byte) ((newByte >> 4) << 4) & 0xF0);
            if (v > 0) {
                v--;
            }

            newByte = (byte) (v1 | v);
        }

        _array[bytePos] = newByte;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < _array.length; i++) {
            builder.append(Integer.toBinaryString(_array[i] & 255 | 256).substring(1)).append(" ");
        }
        return builder.toString();
    }
}
