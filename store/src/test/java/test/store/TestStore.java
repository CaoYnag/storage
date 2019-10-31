package test.store;

import spes.store.anno.Read;
import spes.store.anno.StorageType;
import spes.store.anno.Write;

@StorageType("test")
public interface TestStore {
    @Write
    void save(TestData data);
    @Read
    void read(TestData data);
}
