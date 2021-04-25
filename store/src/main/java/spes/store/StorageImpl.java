package spes.store;

import spes.store.except.StorageException;
import spes.store.pojo.StoreMeta;

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
    public StoreConf asStoreConf(){
        return new StoreConf(name, driver, perm.getPermStr(), conf);
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

    public StoreMeta meta(){
        return new StoreMeta(name(), desc(), getPermStr(), conf());
    }
}
