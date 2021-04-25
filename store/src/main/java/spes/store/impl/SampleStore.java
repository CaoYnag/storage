package spes.store.impl;

import spes.store.Storage;
import spes.store.StorageImpl;
import spes.store.StoreConf;
import spes.store.anno.StoreDriver;
import spes.store.except.StorageException;
import spes.store.intf.TypedStore;

/*
* a sample storage
* */
@StoreDriver(value = "SampleStore", desc = "store sample data in memory.")
public class SampleStore extends StorageImpl implements TypedStore {
    public SampleStore() {
    }

    @Override
    public void create(StoreConf cf) throws StorageException {
        super.create(cf);
    }

    @Override
    public void destroy() {
        // release resources here
    }

    @Override
    public boolean valid() {
        return true;
    }

    @Override
    public String type() {
        return "Sample";
    }
}
