package spes.store;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spes.store.intf.TypedStore;
import test.store.TestStore;

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

    @org.junit.Test
    public void exists() {
        Assert.assertTrue(factory.exists("test_rw"));
        Assert.assertFalse(factory.exists("test"));
    }

    @org.junit.Test
    public void list() {
        Assert.assertTrue(factory.list().size() == 3);
    }

    @org.junit.Test
    public void getAllStorageTypes() {
        Assert.assertTrue(factory.getAllStorageTypes().size() == 1);
    }

    @Test
    public void Get(){
        Assert.assertNotNull(factory.Get("test_rw"));
        Assert.assertNull(factory.Get("test"));

        Assert.assertNotNull(factory.Get(TestStore.class));
        Assert.assertNull(factory.Get(this.getClass()));

        Assert.assertNotNull(factory.Get(TestStore.class, "test_rw"));
        Assert.assertNotNull(factory.Get(TestStore.class, null));
        Assert.assertNull(factory.Get(TestStore.class, "test"));
        Assert.assertNull(factory.Get(this.getClass(), "test_rw"));


        Assert.assertTrue(factory.Get("test_rw").getPerm().readable());
        Assert.assertTrue(factory.Get("test_rw").getPerm().writable());
        Assert.assertTrue(factory.Get("test_rw").valid());
        Assert.assertEquals(factory.Get("test_rw").type(), "test");
    }
}
