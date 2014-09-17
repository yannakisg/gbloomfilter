package bloomfilter;

import java.util.BitSet;

public class BloomFilterHelper {

    public static SimpleBloomFilter OR(SimpleBloomFilter b0, SimpleBloomFilter b1) {
        int totalElements = b0.getTotalElements() + b1.getTotalElements();
        BitSet bb0 = b0.getBitSet();
        BitSet bb1 = b1.getBitSet();
        BitSet bb = new BitSet(b0.getSize() > b1.getSize() ? b0.getSize() : b1.getSize());
        bb.or(bb0);
        bb.or(bb1);

        return new SimpleBloomFilter(totalElements, bb);
    }

    public static SimpleBloomFilter AND(SimpleBloomFilter b0, SimpleBloomFilter b1) {
        BitSet bb0 = b0.getBitSet();
        BitSet bb1 = b1.getBitSet();
        BitSet bb = new BitSet(b0.getSize() > b1.getSize() ? b0.getSize() : b1.getSize());
        bb.set(0, bb.size());

        bb.and(bb0);
        bb.and(bb1);

        return new SimpleBloomFilter(bb.cardinality(), bb);
    }

    public static SimpleBloomFilter XOR(SimpleBloomFilter b0, SimpleBloomFilter b1) {
        BitSet bb0 = b0.getBitSet();
        BitSet bb1 = b1.getBitSet();
        BitSet bb = new BitSet(b0.getSize() > b1.getSize() ? b0.getSize() : b1.getSize());
        bb.xor(bb0);
        bb.xor(bb1);

        return new SimpleBloomFilter(bb.cardinality(), bb);
    }
}
