package exlock.phonecode_pc.EditFeatures.CustomDialogs;

public class SearchFunctionLists {
    private String category;
    private String function;

    public SearchFunctionLists newInstance(String category, String function) {
        this.category = category;
        this.function = function;
        return SearchFunctionLists.this;
    }
}
