package spes.store;

public abstract class Storage {
    private String name;
    private String driver;
    private StorePerm perm;
    private String conf;

    public Storage(StoreConf conf) {
        name = conf.getName();
        driver = conf.getDriver();
        perm = new StorePerm(conf.getPerm());
        this.conf = conf.getConf();
    }

    /*
    * init the storage here
    * using conf string in StoreConf
    * if init failed, throw a StorageException for detail.
    * */
    public abstract void create(String json) throws StorageException;

    /*
    * stop all actions, and release resources
    * all exception must be processed, there should not throw any exception
    * TODO should this method be async if it would take a lot time to complete
    * */
    public abstract void destroy();

    /*
    * check status of storage
    * a simple check, do not wast too many time here
    * exp:
    *   db connections to sql
    *   or fs accessible
    * */
    public abstract boolean valid();

    /*
    * a readable string describe this store
    * exp:
    *   "HBase", "MBTiles", "FastDFS"
    * */
    public abstract String type();

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

    public StorePerm getPerm() {
        return perm;
    }

    public void setPerm(StorePerm perm) {
        this.perm = perm;
    }

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }

    public StoreConf asStoreConf(){
        StoreConf cf = new StoreConf(name, driver, perm.getPermStr(), conf);
        return cf;
    }
    @Override
    public String toString() {
        return "Storage{" +
                "name='" + name + '\'' +
                ", driver='" + driver + '\'' +
                ", perm=" + perm +
                ", conf='" + conf + '\'' +
                '}';
    }
}
