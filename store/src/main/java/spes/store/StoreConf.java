package spes.store;

public class StoreConf {
    private String name;
    private String driver;
    private String perm;
    private String conf;

    public StoreConf() {
    }

    public StoreConf(String name, String driver, String perm, String conf) {
        this.name = name;
        this.driver = driver;
        this.perm = perm;
        this.conf = conf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getPerm() {
        return perm;
    }

    public void setPerm(String perm) {
        this.perm = perm;
    }

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }

    @Override
    public String toString() {
        return "StoreConf{" +
                "name='" + name + '\'' +
                ", driver='" + driver + '\'' +
                ", perm='" + perm + '\'' +
                ", conf='" + conf + '\'' +
                '}';
    }
}
