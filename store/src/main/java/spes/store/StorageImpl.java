package spes.store;

import spes.store.except.StorageException;
import spes.store.pojo.StoreMeta;

import java.util.Objects;

public abstract class StorageImpl implements Storage {
    protected StorePerm perm;
    protected String name;
    protected String driver;
    protected String conf;
    protected String desc;

    public void create(StoreConf cf) throws StorageException {
        name = cf.getName();
        driver = cf.getDriver();
        perm = new StorePerm(cf.getPerm());
        conf = cf.getConf();
        desc = cf.getDesc();
    }
    public String name(){
        return name;
    }
    public String driver(){
        return driver;
    }
    public StorePerm perm(){
        return perm;
    }
    public String conf(){
        return conf;
    }
    public String desc() {
        return desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StorageImpl)) return false;
        StorageImpl storage = (StorageImpl) o;
        return Objects.equals(perm, storage.perm) && Objects.equals(name, storage.name) && Objects.equals(driver, storage.driver) && Objects.equals(conf, storage.conf) && Objects.equals(desc, storage.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(perm, name, driver, conf, desc);
    }
}
