package exlock.phonecode_pc.EditFeatures;

public class BlockLists {
    String func1;
    String func2;
    String arg;

    public BlockLists newInstance(String func1, String arg, String func2) {
        this.func1 = func1;
        this.func2 = func2;
        this.arg = arg;
        return BlockLists.this;
    }
}
