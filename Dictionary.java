import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

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

    // to display keys on JList
    public String[] getKeyArray() {
        ArrayList<String> keys = new ArrayList<>();
        Set<String> keySet = dictionary.keySet();

        for (String key : keySet) {
            keys.add(key);
        }

        return keys.toArray(new String[keys.size()]);
    }

    public String toString(String key, ArrayList<String> values) {
        String output = key + "`";

        for (int i = 0; i < values.size() - 1; i++) {
            output = output + values.get(i) + "|";
        }

        output = output + values.get(values.size() - 1);

        return output;
    }

    public boolean exportToFile(String fname) {
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

    public boolean importFromFile(String fname) {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(fname));
            String line = "";

            while ((line = buffer.readLine()) != null) {
                String[] data = line.split("\\`");

                if (data.length < 2)
                    continue;

                String key = data[0];

                String[] valueLine = data[1].split("\\|");
                ArrayList<String> values = new ArrayList<>();

                for (int i = 0; i < valueLine.length; i++) {
                    values.add(valueLine[i]);
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

    public static void main(String args[]) {
        // Dictionary d = new Dictionary();

        // d.importFromFile("data/user_slang.txt");

        // System.out.println(d.get("YMMV"));

        String test = "HK`Hong Kong";
        String[] data = test.split("\\`");
        String[] a = data[1].split("\\|");
        System.out.println(a.length);
    }
}