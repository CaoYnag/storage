package spes.store.impl;

import spes.store.Storage;
import spes.store.StorageException;
import spes.store.StoreConf;
import spes.store.intf.TypedStore;

/*
* a sample storage
* */
public class SampleStore extends Storage implements TypedStore {
    public SampleStore(StoreConf conf) {
        super(conf);
    }

    @Override
    public void create(String json) throws StorageException {
        // init use json here
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
