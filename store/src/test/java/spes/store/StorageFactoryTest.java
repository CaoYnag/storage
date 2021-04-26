package spes.store;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spes.store.anno.StoreDriver;
import test.outside_store.AnotherStorage;
import test.store.TestStore;
import test.store.TestStorage;

public class StorageFactoryTest {
    private StorageFactory factory;
    @Before
    public void setUp(){
        factory = StorageFactory.get();
    }
    @After
    public void tearDown(){
        StorageFactory.release();
    }

    @Test
    public void exists() {
        Assert.assertTrue(factory.exists("test_rw"));
        Assert.assertFalse(factory.exists("test"));
    }

    @Test
    public void list() {
        Assert.assertTrue(factory.list().size() == 3);
    }

    @Test
    public void getAllStorageTypes() {
        Assert.assertTrue(factory.getAllStorageTypes().size() == 1);
    }

    @Test
    public void Get(){
        Assert.assertNotNull(factory.Get("test_rw"));
        Assert.assertNull(factory.Get("test"));

        TestStore store = (TestStore) factory.Get(TestStore.class);
        Assert.assertNotNull(store);
        Assert.assertNull(factory.Get(this.getClass()));

        store = (TestStore) factory.Get(TestStore.class, "test_rw");
        Assert.assertNotNull(store);
        store = (TestStore) factory.Get(TestStore.class, null);
        Assert.assertNotNull(store);
        Assert.assertNull(factory.Get(TestStore.class, "test"));
        store = (TestStore) factory.Get(this.getClass(), "test_rw");
        Assert.assertNull(store);


        Assert.assertTrue(factory.Get("test_rw").perm().readable());
        Assert.assertTrue(factory.Get("test_rw").perm().writable());
        Assert.assertTrue(factory.Get("test_rw").valid());
        Assert.assertEquals(factory.Get("test_rw").type(), "test");
    }

    @Test
    public void drivers() {
        Assert.assertNotNull(factory.drivers());
        Assert.assertEquals(factory.drivers().size(), 1);
        Assert.assertEquals(factory.drivers().get(0).getName(), "TestStore");
        factory.addDriver(this.getClass());
        factory.addDriver(AnotherStorage.class);
        Assert.assertEquals(factory.drivers().size(), 2);
        Assert.assertEquals(factory.drivers().get(1).getName(), "Another");
    }

    @Test
    public void test_remove(){
        Assert.assertEquals(factory.list().size(), 3);
        Assert.assertTrue(factory.remove("test_r", 1));
        Assert.assertEquals(factory.list().size(), 2);
        Assert.assertTrue(factory.remove("test_rw", 1));
        Assert.assertEquals(factory.list().size(), 1);
        Assert.assertTrue(factory.remove("test_w", 1));
        Assert.assertEquals(factory.list().size(), 0);
    }
}
