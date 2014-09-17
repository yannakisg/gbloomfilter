package bloomfilter;

import bloomfilter.hash.Hash;
import bloomfilter.hash.HashHelper;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class BloomFilter {

    protected static final String _DEFAULT_HASH = "FNV32";
    protected static final int _DEFAULT_SIZE = 128;
    protected static final int DEFAULT_NUM_HASHES = 5;
    protected static final int _MAXIMUM_SIZE = 838860800;
    protected final Hash _hashFunc;
    protected int _totalElements;
    protected double _errorRate;
    protected int _size;
    protected int _totalHashes;
    protected int _maxSize;
    protected int _currentElements;

    protected BloomFilter(int totalElements, int maxSize, double errorRate, String hashFuncStr) {
        this(totalElements, maxSize, _DEFAULT_SIZE, errorRate, hashFuncStr);
    }

    protected BloomFilter(int totalElements, int maxSize, int size, double errorRate, String hashFuncStr) {
        this._hashFunc = HashHelper.getHash(hashFuncStr);
        this._totalElements = totalElements;
        this._errorRate = errorRate;
        this._maxSize = maxSize;
        this._size = size;
        this._currentElements = 0;
        this._totalHashes = DEFAULT_NUM_HASHES;
    }

    protected BloomFilter(int totalElements, int maxSize, int size, String hashFuncStr) {
        this._hashFunc = HashHelper.getHash(hashFuncStr);
        this._size = size;
        this._totalHashes = (int) Math.ceil(((_size * 1.0) / _totalElements) * Math.log(2));
        this._errorRate = Math.pow(1 - Math.pow(Math.E, -1 * _totalHashes * _totalElements / (1.0 * _size)), _totalHashes);
        this._currentElements = totalElements;
    }

    public void add(int data) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE >> 3);
        buffer.order(ByteOrder.nativeOrder());
        buffer.putInt(data);
        add(buffer.array());
    }

    public void add(long data) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE >> 3);
        buffer.order(ByteOrder.nativeOrder());
        buffer.putLong(data);
        add(buffer.array());
    }

    public void add(byte data) {
        byte[] b = new byte[1];
        b[0] = data;
        add(b);
    }

    public void add(int[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Illegal Argument");
        }
        ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE >> 3 * data.length);
        buffer.order(ByteOrder.nativeOrder());
        for (int i = 0; i < data.length; i++) {
            buffer.putInt(data[i]);
        }
        add(buffer.array());
    }

    public void add(long[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Illegal Argument");
        }
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE >> 3 * data.length);
        buffer.order(ByteOrder.nativeOrder());
        for (int i = 0; i < data.length; i++) {
            buffer.putLong(data[i]);
        }
        add(buffer.array());
    }

    public void add(String string) {
        if (string.equals("")) {
            throw new IllegalArgumentException("Illegal Argument");
        }
        add(string.getBytes());
    }

    public boolean contains(int data) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE >> 3);
        buffer.order(ByteOrder.nativeOrder());
        buffer.putInt(data);
        return contains(buffer.array());
    }

    public boolean contains(long data) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE >> 3);
        buffer.order(ByteOrder.nativeOrder());
        buffer.putLong(data);
        return contains(buffer.array());
    }

    public boolean contains(byte data) {
        byte[] b = new byte[1];
        b[0] = data;
        return contains(b);
    }

    public boolean contains(int[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Illegal Argument");
        }
        ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE >> 3 * data.length);
        buffer.order(ByteOrder.nativeOrder());
        for (int i = 0; i < data.length; i++) {
            buffer.putInt(data[i]);
        }

        return contains(buffer.array());
    }

    public boolean contains(long[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Illegal Argument");
        }
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE >> 3 * data.length);
        buffer.order(ByteOrder.nativeOrder());
        for (int i = 0; i < data.length; i++) {
            buffer.putLong(data[i]);
        }

        return contains(buffer.array());
    }

    public boolean contains(String string) {
        return contains(string.getBytes());
    }

    public int getTotalElements() {
        return _totalElements;
    }

    public double getErrorRate() {
        return _errorRate;
    }

    public int getSize() {
        return _size;
    }

    public int getTotalHashes() {
        return _totalHashes;
    }

    protected void findOptimalParameters() {
        while (true) {
            _totalHashes = (int) Math.ceil(((_size * 1.0) / _totalElements) * Math.log(2));

            double curErrorRate = Math.pow(1 - Math.pow(Math.E, -1 * _totalHashes * _totalElements / (1.0 * _size)), _totalHashes);
            if (curErrorRate > _errorRate) {
                _size = _size * 2;
                if (_size > _maxSize) {
                    _size = _size / 2;
                    break;
                }
            } else {
                break;
            }
        }
    }

    public void updateParameters(int totalElements, double errorRate) {
        this._totalElements = totalElements;
        this._errorRate = errorRate;
        clear();

        findOptimalParameters();
    }

    public abstract void clear();

    public abstract void add(byte[] data);

    public abstract boolean contains(byte[] data);
}
