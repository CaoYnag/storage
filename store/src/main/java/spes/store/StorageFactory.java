package spes.store;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import spes.store.anno.Read;
import spes.store.anno.StorageType;
import spes.store.anno.Write;
import spes.store.except.StorageException;
import spes.store.except.StoragePermException;
import spes.struct.LinkedList;
import spes.struct.Tuple;
import spes.utils.string.StringUtils;
import spes.utils.util.ConvertUtils;
import spes.utils.util.ReflectUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StorageFactory {
    private static Log log = LogFactory.getLog(StorageFactory.class);

    private ReentrantReadWriteLock lock;
    /* WARN use local file now, maybe sql would be used in future */
    private static final String CONF_FILE = "/storage.json";
    private static StorageFactory _inst;

    private List<Storage> _insts;
    //private List<Class<?>> _clss;
    private List<Class<?>> _drivers;
    private boolean _inited;
    private StoreConfiguration _conf;
    private Map<String, Class<?>> _types;
    /*
    * use for proxy generating
    * init in _sync or _init.
    * so can not support runtime store types adding.
    * */
    private Class<?>[] PROXY_INTFS;

    /*
     * get instance of StorageFactory
     * */
    public static StorageFactory get() {
        synchronized (log){
            if (_inst == null)
                _inst = new StorageFactory();
        }
        return _inst;
    }

    /*
     * release all storage
     * */
    public static void release() {
        synchronized (log){
            if (_inst != null) {
                _inst.destroy();
                _inst = null;
            }
        }
    }

    private class StoreConfiguration {
        public String[] store_scan_pkgs;
        public StoreConf[] stores;
    }

    private StorageFactory() {
        _insts = new LinkedList<>();
        _drivers = new LinkedList<>();
        _types = new HashMap<>();
        lock = new ReentrantReadWriteLock();
        _init();
    }

    /*
    * just load from conf, do not persist to conf, or erase exists insts
    * exp:
    *   mem: a, b, c
    *   conf: b, c, d
    *   after init: a, b, c, d
    * */
    private void _init(){
        lock.writeLock().lock();
        _inited = false;
        try {
            _types.clear();
            _drivers.clear();

            Gson gson = new GsonBuilder().create();
            _conf = null;
            log.info("Using conf file: " + StorageFactory.class.getResource(CONF_FILE).getPath());
            _conf = gson.fromJson(new InputStreamReader(StorageFactory.class.getResourceAsStream(CONF_FILE)), StoreConfiguration.class);

            // try find all types
            List<Class<?>> clss = new LinkedList<>();
            List<Class<?>> tps = new LinkedList<>();
            for (String pkg : _conf.store_scan_pkgs) {
                clss.addAll(ReflectUtils.getClassesByPackage(pkg, true));
            }
            for (Class<?> cls : clss) {
                try {
                    StorageType st = cls.getAnnotation(StorageType.class);
                    if (st != null){
                        _types.put(st.value(), cls);
                        tps.add(cls);
                    }
                } catch (Exception e) {
                }
                if(!cls.isInterface() && Storage.class.isAssignableFrom(cls))
                    _drivers.add(cls);
            }
            tps.add(Storage.class);
            PROXY_INTFS = tps.toArray(new Class[0]);

            // load all configured stores
            for (StoreConf conf : _conf.stores) {
                try {
                    add(conf);
                } catch (StorageException e) {
                    // ignore exists stores.
                }
            }
            log.info("load storage complete, loaded " + _insts.size() + ": " + ConvertUtils.convert(_insts, Storage::name));
        } catch (Exception e) {
            log.error("err while loading storage drivers");
        }
        _inited = true;
        lock.writeLock().unlock();
    }

    /*
    * sync conf file and memory
    * load new item from conf, and write runtime-added item to conf.
    * */
    private void _sync(){
        lock.writeLock().lock();
        _inited = false;
        try {
            Gson gson = new GsonBuilder().create();
            _conf = null;
            String conf_path = StorageFactory.class.getResource(CONF_FILE).getPath();
            log.info("Using conf file: " + conf_path);
            _conf = gson.fromJson(new InputStreamReader(StorageFactory.class.getResourceAsStream(CONF_FILE)), StoreConfiguration.class);

            // do not reload drivers, clss, types, PROXY_INTFS here.
            // just sync stores.

            // load all configured stores
            int cnt = 0;
            for (StoreConf conf : _conf.stores) {
                try {
                    add(conf);
                    ++cnt;
                } catch (StorageException e) {
                    // ignore exists stores.
                }
            }
            _conf.stores = ConvertUtils.convert(_insts, Storage::asStoreConf).toArray(new StoreConf[0]);
            FileOutputStream fos = new FileOutputStream(new File(conf_path));
            fos.write(gson.toJson(_conf).getBytes());
            fos.close();
            log.info("sync storage complete, loaded " + cnt + ", writed " + _insts.size() + ".");
        } catch (Exception e) {
            log.error("err while loading storage drivers");
        }
        _inited = true;
        lock.writeLock().unlock();
    }

    private void destroy() {
        lock.writeLock().lock();
        for (Storage s : _insts) {
            s.destroy();
        }
        _insts.clear();
        lock.writeLock().unlock();
    }

    class StorageProxy implements InvocationHandler{
        private Storage _store;

        public StorageProxy(Storage _store) {
            this._store = _store;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] args) throws Throwable {
            Read r = method.getAnnotation(Read.class);
            Write w = method.getAnnotation(Write.class);
            if(r != null && !_store.perm().readable()) throw new StoragePermException("NO READ PERMISSION: " + _store.name());
            if(w != null && !_store.perm().writable()) throw new StoragePermException("NO WRITE PERMISSION: " + _store.name());
            Object ret = null;
            try {
                ret = method.invoke(_store, args);
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException ite) {
            }
            return ret;
        }
    }

    /*********************************************************************/
    /**************************public intfs*******************************/
    /*********************************************************************/

    /*
     * intf for sync mem & file
     * */
    public void sync(){
        _sync();
    }

    /*
    * intf for _init
    * */
    public void init(){
        _init();
    }

    /*
     * get first valid storage
     * */
    public Storage Get() {
        lock.readLock().lock();
        try{
            if (!_inited) return null;
            for (Storage s : _insts)
                if (s != null && s.valid()) return s;
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    /*
     * get first storage valid, and support specified type
     * */
    public Storage Get(Class<?> cls) {
        lock.readLock().lock();
        try{
            if (!_inited) return null;
            for (Storage s : _insts)
                if (s != null && cls.isAssignableFrom(s.getClass()) && s.valid()) return s;
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }
    /*
     * get a storage with specified name
     * WARN each storage should have a unique name - checked in StorageFactory::add
     * */
    public Storage Get(String name) {
        lock.readLock().lock();
        try{
            if (!_inited) return null;
            for (Storage s : _insts)
                if (s != null && s.valid() && s.name().equals(name)) return s;
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }
    /*
     * get a storage with specified type and name
     * if name not specified, this method has same action with Get(Class)
     * */
    public Storage Get(Class cls, String name) {
        lock.readLock().lock();
        try{
            if (!_inited) return null;
            Storage store = null;
            if (!StringUtils.isEmpty(name)) store = Get(name);
            else store = Get(cls);
            if (store != null && cls.isAssignableFrom(store.getClass()))
                return store;
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }
    public boolean exists(String name) {
        lock.readLock().lock();
        try{
            if (!_inited) return false;
            for (Storage s : _insts) if (s.name().equals(name)) return true;
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }
    public void add(StoreConf conf) throws StorageException {
        if (exists(conf.getName()))
            throw new StorageException("a storage named " + conf.getName() + " already exists.");

        Storage store = create(conf);
        lock.writeLock().lock();
        _insts.add(store);
        lock.writeLock().unlock();
    }
    /*
     * create a storage inst with conf.
     * */
    public Storage create(StoreConf conf) throws StorageException {
        try {
            Class cls = Class.forName(conf.getDriver());
            if (!Storage.class.isAssignableFrom(cls))
                throw new StorageException("illegal storage driver.");

            Storage s = (Storage) cls.getConstructor().newInstance();
            if (s == null) throw new StorageException("Cannot construct storage " + conf.getName());
            s.create(conf);
            Storage proxy = (Storage) Proxy.newProxyInstance(this.getClass().getClassLoader(), PROXY_INTFS, new StorageProxy(s));
            return proxy;
        } catch (StorageException se) {
            throw se;
        } catch (Exception e) {
            log.error("err while add storage.", e);
            throw new StorageException("add storage failed: " + e.getMessage());
        }
    }
    public void remove(String name) {
        lock.writeLock().lock();
        if (_inited) for (Storage s : _insts)
            if (s.name().equals(name)) {
                s.destroy();
                _insts.remove(s);
                break;
            }
        lock.writeLock().unlock();
    }
    public List<Storage> list() {
        lock.readLock().lock();
        try{
            return ConvertUtils.convert(_insts, i->i);
        } finally {
            lock.readLock().unlock();
        }
    }
    public List<Class<?>> drivers(){
        lock.readLock().lock();
        try{
            return ConvertUtils.convert(_drivers, d->d);
        } finally {
            lock.readLock().unlock();
        }
    }
    public void addDriver(Class<?> cls){
        lock.writeLock().lock();
        if(cls != null && !cls.isInterface() && Storage.class.isAssignableFrom(cls))
            _drivers.add(cls);
        lock.writeLock().unlock();
    }
    public List<Tuple<String, Class<?>>> getAllStorageTypes() {
        List<String> list = new LinkedList<>();
        lock.readLock().lock();
        try{
            list.addAll(_types.keySet());
            return new LinkedList<>(ConvertUtils.convert(list, n -> new Tuple<>(n, _types.get(n))));
        } finally {
            lock.readLock().unlock();
        }
    }
    public Class<?> getStorageType(String name){
        lock.readLock().lock();
        try{
            return _types.get(name);
        } finally {
            lock.readLock().unlock();
        }
    }
}
