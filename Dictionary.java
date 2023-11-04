import java.util.LinkedHashMap;
import java.util.Set;

public class Dictionary {
    LinkedHashMap<String, String> dictionary;

    public Dictionary() {
        dictionary = new LinkedHashMap<>();
    }

    public void add(Slang s) {
        dictionary.put(s.getKey(), s.getValue());
    }

    public void delete(Slang s) {
        dictionary.remove(s.getKey());
    }

    public void printAll() {
        Set<String> keySet = dictionary.keySet();
        for (String key : keySet) {
            // handle displaying slangs
        }
    }

    public static void main(String args[]) {

    }
}