package test.store;

import spes.store.Storage;
import spes.store.StorageException;
import spes.store.StoreConf;
import spes.store.anno.Read;
import spes.store.anno.Write;
import spes.store.intf.TypedStore;

import java.util.HashMap;
import java.util.Map;

public class TestStorage extends Storage implements TestStore {
    private Map<Integer, TestData> datas;
    public TestStorage(StoreConf conf) {
        super(conf);
        datas = new HashMap<>();
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
