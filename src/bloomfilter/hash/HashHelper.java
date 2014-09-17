package bloomfilter.hash;

public class HashHelper {

    private HashHelper() {

    }

    public static Hash getHash(String name) {
        if (name.equals(FNV32.class.getName().substring(FNV32.class.getName().lastIndexOf(".") + 1))) {
            return FNV32.getInstance();
        } else {
            throw new IllegalArgumentException("No available implementation for: " + name);
        }
    }

}
