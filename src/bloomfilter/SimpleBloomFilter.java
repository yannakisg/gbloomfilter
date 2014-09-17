package bloomfilter;

import java.nio.ByteBuffer;
import java.util.BitSet;

public class SimpleBloomFilter extends BloomFilter {

    private final BitSet _bitSet;

    public SimpleBloomFilter(int totalElements, double errorRate, String hashFuncStr) {
        this(totalElements, _MAXIMUM_SIZE, errorRate, hashFuncStr);
    }

    public SimpleBloomFilter(int totalElements, int maxSize, double errorRate, String hashFuncStr) {
        super(totalElements, maxSize, errorRate, hashFuncStr);

        this._bitSet = new BitSet(_DEFAULT_SIZE);
        findOptimalParameters();
    }

    public SimpleBloomFilter(int totalElements, BitSet bitSet) {
        super(totalElements, _MAXIMUM_SIZE, bitSet.size(), _DEFAULT_HASH);

        this._bitSet = bitSet;

    }

    public final BitSet getBitSet() {
        return _bitSet;
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
            _bitSet.set((int) h);
            buffer.reset();
        }
        _currentElements++;
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
        ByteBuffer buffer = ByteBuffer.allocate(data.length + (Integer.SIZE >> 3));
        buffer.put(data);

        for (int i = 0; i < _totalHashes; i++) {
            buffer.mark();
            buffer.putInt(i);

            h = (_hashFunc.hash(buffer.array()) & 0x00000000FFFFFFFFL) % _size;
            if (!_bitSet.get((int) h)) {
                return false;
            }
            buffer.reset();
        }

        return true;
    }

    @Override
    public void clear() {
        this._bitSet.clear();
    }

}
