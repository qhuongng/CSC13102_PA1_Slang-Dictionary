public class Slang {
    private String key;
    private String value;

    public Slang() {
        key = "defaultKey";
        value = "defaultValue";
    }

    public Slang(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Slang(Slang s) {
        this.key = s.key;
        this.value = s.value;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
