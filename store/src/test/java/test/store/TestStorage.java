package test.store;

import spes.store.Storage;
import spes.store.StorageImpl;
import spes.store.StoreConf;
import spes.store.anno.Read;
import spes.store.anno.Write;
import spes.store.except.StorageException;

import java.util.HashMap;
import java.util.Map;

public class TestStorage extends StorageImpl implements TestStore {
    private Map<Integer, TestData> datas;
    public TestStorage() {
        datas = new HashMap<>();
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
        return "test";
    }

    @Override
    @Write
    public void save(TestData data) {
        datas.put(data.id, data);
    }

    @Override
    @Read
    public void read(TestData data) {
        data.msg = datas.get(data.id) == null ? "empty" : datas.get(data.id).msg;
    }
}
