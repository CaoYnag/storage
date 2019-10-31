package test.store;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spes.store.Storage;
import spes.store.StorageFactory;
import spes.store.StoreConf;
import spes.store.anno.Read;
import spes.store.anno.Write;
import spes.struct.LinkedList;
import spes.struct.List;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
    public void testNM(){
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
    public void testPerm(){
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
    public void testProxy(){
        class TestStorageProxy implements InvocationHandler {
            private Storage _store;

            public TestStorageProxy(TestStorage _store) {
                this._store = _store;
            }

            @Override
            public Object invoke(Object o, Method method, Object[] args) throws Throwable {
                Read r = method.getAnnotation(Read.class);
                Write w = method.getAnnotation(Write.class);
                if(r != null && !_store.perm().readable()) throw new Exception("PERM EXCEPT: NO READ PERMISSION.");
                if(w != null && !_store.perm().writable()) throw new Exception("PERM EXCEPT: NO WRITE PERMISSION");
                return method.invoke(_store, args);
            }
        }
        TestStorage real = (TestStorage) factory.Get("test_w");
        TestStorageProxy proxy = new TestStorageProxy(real);
        TestStore store = (TestStore) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{TestStore.class}, proxy);
        store.save(datas[1]);
        try{
            store.read(new TestData(1, null));
            Assert.assertFalse(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
