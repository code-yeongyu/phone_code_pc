package exlock.phonecode_pc.Tools;

import java.util.ArrayList;
import java.util.Comparator;

public class StringTools {
    public static ArrayList<Integer> findStringPositions(String source, String target){
        int position;
        final int length = source.length();
        final ArrayList<Integer> positions = new ArrayList<>();

        for(;;){
            position = source.indexOf(target);
            if(position==-1){
                break;
            }
            positions.add(length-source.length()+position);
            source = source.substring(position+1, source.length());
        }
        return positions;
    }
}
class Descending implements Comparator<Integer> {
    @Override
    public int compare(Integer o1, Integer o2) {
        return o2.compareTo(o1);
    }
}