package spes.store;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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

        Assert.assertNotNull(factory.Get(TestStore.class));
        Assert.assertNull(factory.Get(this.getClass()));

        Assert.assertNotNull(factory.Get(TestStore.class, "test_rw"));
        Assert.assertNotNull(factory.Get(TestStore.class, null));
        Assert.assertNull(factory.Get(TestStore.class, "test"));
        Assert.assertNull(factory.Get(this.getClass(), "test_rw"));


        Assert.assertTrue(factory.Get("test_rw").perm().readable());
        Assert.assertTrue(factory.Get("test_rw").perm().writable());
        Assert.assertTrue(factory.Get("test_rw").valid());
        Assert.assertEquals(factory.Get("test_rw").type(), "test");
    }
}
