package spes.store;

import spes.store.except.StorageException;

public interface Storage{
    /*
     * init the storage here
     * using conf string in StoreConf
     * if init failed, throw a StorageException for detail.
     * */
    void create(StoreConf cf) throws StorageException;

    /*
     * stop all actions, and release resources
     * all exception must be processed, there should not throw any exception
     * TODO should this method be async if it would take a lot time to complete
     * */
    void destroy();

    /*
     * check status of storage
     * a simple check, do not wast too many time here
     * exp:
     *   db connections to sql
     *   or fs accessible
     * */
    boolean valid();

    /*
     * a readable string describe this store
     * exp:
     *   "HBase", "MBTiles", "FastDFS"
     * */
    String type();

    /*
    * base info of this store.
    * */
    StoreConf asStoreConf();
    String name();
    String driver();
    StorePerm perm();
    String conf();
    /* description of this store */
    String desc();


    /*
    * for front templates or other places uses getter to access attr of a storage
    * */
    default String getName(){
        return name();
    }
    default String getDriver(){
        return driver();
    }
    default String getPermStr(){
        return perm().getPermStr();
    }
    default String getConf(){
        return conf();
    }
    default String getDesc() {
        return desc();
    }
}
