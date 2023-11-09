import java.io.*;
import java.util.*;

public class Dictionary {
    private TreeMap<String, ArrayList<String>> dictionary;

    public Dictionary() {
        dictionary = new TreeMap<>();
    }

    // since TreeMap does not support duplicates, updating a slang is essentially
    // adding it again, but with an updated value list
    public void add(String key, ArrayList<String> values) {
        dictionary.put(key, values);
    }

    public ArrayList<String> get(String key) {
        return dictionary.get(key);
    }

    public void delete(String key) {
        dictionary.remove(key);
    }

    public boolean contains(String key) {
        return dictionary.containsKey(key);
    }

    public void clear() {
        dictionary.clear();
    }

    public void replace(String key, ArrayList<String> values) {
        dictionary.replace(key, values);
    }

    // to display keys on JList
    public ArrayList<String> getKeyList() {
        ArrayList<String> keys = new ArrayList<>();
        Set<String> keySet = dictionary.keySet();

        for (String key : keySet) {
            keys.add(key);
        }

        return keys;
    }

    public String toString(String key, ArrayList<String> values) {
        String output = key + "`";

        for (int i = 0; i < values.size() - 1; i++) {
            output = output + values.get(i) + "|";
        }

        output = output + values.get(values.size() - 1);

        return output;
    }

    public boolean exportSlangList(String fname) {
        try {
            BufferedWriter buffer = new BufferedWriter(new FileWriter(fname));

            for (Map.Entry<String, ArrayList<String>> e : dictionary.entrySet()) {
                String key = e.getKey();
                ArrayList<String> values = e.getValue();
                buffer.write(toString(key, values));
                buffer.newLine();
            }

            buffer.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean importSlangList(String fname) {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(fname));
            String line = "";

            while ((line = buffer.readLine()) != null) {
                String[] data = line.split("\\`");

                // lines that have no slang, or no definition are not imported
                if (data.length < 2)
                    continue;

                // first item in data is the slang
                String key = data[0].trim();

                // second item in data is a string of all definitions, separated by |
                String[] valueLine = data[1].split("\\|");
                ArrayList<String> values = new ArrayList<>();

                for (int i = 0; i < valueLine.length; i++) {
                    values.add(valueLine[i].trim());
                }

                this.add(key, values);
            }

            buffer.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<String> searchSubstringByKey(String subString) {
        ArrayList<String> keys = new ArrayList<>();
        Set<String> keySet = dictionary.keySet();

        for (String key : keySet) {
            if (key.toLowerCase().contains(subString.toLowerCase())) {
                keys.add(key);
            }
        }

        return keys;
    }

    public ArrayList<String> searchSubstringByDefinition(String subString) {
        ArrayList<String> keys = new ArrayList<>();
        Set<String> keySet = dictionary.keySet();

        for (String key : keySet) {
            ArrayList<String> values = dictionary.get(key);

            for (int i = 0; i < values.size(); i++) {
                if (values.get(i).toLowerCase().contains(subString.toLowerCase())) {
                    keys.add(key);
                }
            }
        }

        return keys;
    }

    public static void main(String args[]) {
        // put test method calls here
    }
}