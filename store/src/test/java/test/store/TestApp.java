package test.store;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spes.store.Storage;
import spes.store.StorageFactory;
import spes.store.except.StorageException;

public class TestApp {
    private TestData[] datas;
    private StorageFactory factory;
    @Before
    public void setUp(){
        factory = StorageFactory.get();
        datas = new TestData[]{
                new TestData(0, "aaa"),
                new TestData(1, "bbb"),
                new TestData(2, "ccc"),
        };
    }
    @Test
    public void testNM() throws StorageException {
        TestStore store = (TestStore) factory.Get("test_rw"); //rw
        Assert.assertEquals(((Storage)store).perm().getPermStr(), "rw");
        store.save(datas[0]);
        TestData test = new TestData(0, null);
        store.read(test);
        Assert.assertEquals(test.msg, datas[0].msg);

        test.id = 4;
        store.read(test);
        Assert.assertEquals(test.msg, "empty");
    }
    @Test
    public void testPerm() throws StorageException {
        TestStore store = (TestStore) factory.Get("test_w"); //rw
        Assert.assertEquals(((Storage)store).perm().getPermStr(), "w");
        store.save(datas[0]);
        TestData test = new TestData(0, null);
        try{
            store.read(test);
            Assert.assertFalse(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void testMethod(){
    }
}
