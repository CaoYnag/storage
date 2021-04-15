package spes.store.pojo;

public class StoreMeta {
    public String name;
    public String desc;
    public String perm;
    public String conf;

    public StoreMeta() {
    }

    public StoreMeta(String name, String desc, String perm, String conf) {
        this.name = name;
        this.desc = desc;
        this.perm = perm;
        this.conf = conf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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
        return "StoreMeta{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", perm='" + perm + '\'' +
                ", conf='" + conf + '\'' +
                '}';
    }
}
