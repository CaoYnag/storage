package spes.store;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spes.store.intf.TypedStore;

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
        Assert.assertTrue(factory.exists("sample"));
        Assert.assertFalse(factory.exists("sample2"));
    }

    @org.junit.Test
    public void list() {
        Assert.assertTrue(factory.list().size() == 1);
    }

    @org.junit.Test
    public void getAllStorageTypes() {
        Assert.assertTrue(factory.getAllStorageTypes().size() == 1);
    }

    @Test
    public void Get(){
        Assert.assertNotNull(factory.Get("sample"));
        Assert.assertNull(factory.Get("sample2"));

        Assert.assertNotNull(factory.Get(TypedStore.class));
        Assert.assertNull(factory.Get(this.getClass()));

        Assert.assertNotNull(factory.Get(TypedStore.class, "sample"));
        Assert.assertNotNull(factory.Get(TypedStore.class, null));
        Assert.assertNull(factory.Get(TypedStore.class, "sample2"));
        Assert.assertNull(factory.Get(this.getClass(), "sample"));


        Assert.assertTrue(factory.Get("sample").getPerm().readable());
        Assert.assertTrue(factory.Get("sample").getPerm().writable());
        Assert.assertTrue(factory.Get("sample").valid());
        Assert.assertEquals(factory.Get("sample").type(), "Sample");
    }
}
