package exlock.phonecode_pc.EditFeatures;

public class SearchFunctionLists {
    String category;
    String function;

    public SearchFunctionLists newInstance(String category, String function) {
        this.category = category;
        this.function = function;
        return SearchFunctionLists.this;
    }
}
