package spes.store.pojo;

public class DriverMeta {
    public String name;
    public String driver;
    public String desc;

    public DriverMeta() {
    }

    public DriverMeta(String name, String driver, String desc) {
        this.name = name;
        this.driver = driver;
        this.desc = desc;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "DriverMeta{" +
                "name='" + name + '\'' +
                ", driver='" + driver + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
