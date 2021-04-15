package test.store;

import spes.store.anno.Read;
import spes.store.anno.StorageType;
import spes.store.anno.Write;
import spes.store.except.StorageException;

@StorageType(value = "test", desc = "store test datas.")
public interface TestStore {
    @Write
    void save(TestData data) throws StorageException;
    @Read
    void read(TestData data) throws StorageException;
}
