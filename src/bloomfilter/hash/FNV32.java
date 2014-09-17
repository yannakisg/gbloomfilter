package bloomfilter.hash;

public class FNV32 extends Hash {

    private static FNV32 _instance = null;

    private static final int FNV1_32_INIT = 0x811c9dc5;

    protected FNV32() {
    }

    public static FNV32 getInstance() {
        if (_instance == null) {
            _instance = new FNV32();
        }

        return _instance;
    }

    @Override
    public int hash(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Illegal Argument");
        }

        int hash = (FNV1_32_INIT);

        for (int i = 0; i < data.length; i++) {
            hash ^= (int) data[i];
            hash += (hash << 1) + (hash << 4) + (hash << 7) + (hash << 8) + (hash << 24);
        }

        return hash;
    }
}
