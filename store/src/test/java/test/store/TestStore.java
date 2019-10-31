package test.store;

import spes.store.anno.StorageType;

@StorageType("test")
public interface TestStore {
    void save(TestData data);
    void read(TestData data);
}
