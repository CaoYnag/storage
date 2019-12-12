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
import spes.struct.List;
import spes.struct.Tuple;
import spes.utils.string.StringUtils;
import spes.utils.util.ConvertUtils;
import spes.utils.util.ReflectUtils;

import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class StorageFactory {
    private static Log log = LogFactory.getLog(StorageFactory.class);

    /* WARN use local file now, maybe sql would be use in future */
    private static final String CONF_FILE = "/storage.json";
    private static StorageFactory _inst;

    private List<Storage> _insts;
    private List<Class<?>> _clss;
    private boolean _inited;
    private StoreConfiguration _conf;
    private Map<String, Class<?>> _types;
    /* only use for proxy generating */
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
        _inited = false;
        synchronized (log) {
            _insts = new LinkedList<>();
            try {
                Gson gson = new GsonBuilder().create();
                _conf = null;
                log.info("Using conf file: " + StorageFactory.class.getResource(CONF_FILE).getPath());
                _conf = gson.fromJson(new InputStreamReader(StorageFactory.class.getResourceAsStream(CONF_FILE)), StoreConfiguration.class);

                // try find all types
                _types = new HashMap<>();
                List<Class<?>> clss = new LinkedList<>();
                List<Class<?>> tps = new LinkedList<>();
                for (String pkg : _conf.store_scan_pkgs) {
                    clss.addAll(ReflectUtils.getClassesByPackage(pkg));
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
                }
                tps.add(Storage.class);
                PROXY_INTFS = tps.toArray(new Class[0]);

                // load all configured stores
                for (StoreConf conf : _conf.stores) {
                    try {
                        add(conf);
                    } catch (StorageException e) {
                        log.error("err while loading storage " + conf.getName(), e);
                    }
                }
                log.info("load storage complete, loaded " + _insts.size() + ": " + ConvertUtils.convert(_insts, Storage::name));
            } catch (Exception e) {
                log.error("err while loading storage drivers");
            }
            _inited = true;
        }
    }

    private void destroy() {
        synchronized (log) {
            for (Storage s : _insts) {
                s.destroy();
            }
            _insts.clear();
        }
    }

    /*
     * get first valid storage
     * */
    public Storage Get() {
        synchronized (log) {
            if (!_inited) return null;
            for (Storage s : _insts)
                if (s != null && s.valid())
                    return (Storage) s;
            return null;
        }
    }

    /*
     * get first storage valid, and support specified type
     * */
    public Storage Get(Class<?> cls) {
        synchronized (log) {
            if (!_inited) return null;
            for (Storage s : _insts)
                if (s != null && cls.isAssignableFrom(s.getClass()) && s.valid())
                    return (Storage) s;
            return null;
        }
    }

    /*
     * get a storage with specified name
     * WARN each storage should have a unique name - checked in StorageFactory::add
     * */
    public Storage Get(String name) {
        synchronized (log) {
            if (!_inited) return null;
            for (Storage s : _insts){
                if (s != null && s.valid() && s.name().equals(name)) return (Storage) s;
            }
            return null;
        }
    }

    /*
     * get a storage with specified type and name
     * if name not specified, this method has same action with Get(Class)
     * */
    public Storage Get(Class cls, String name) {
        synchronized (log) {
            if (!_inited) return null;
            Storage store = null;
            if (!StringUtils.isEmpty(name)) store = Get(name);
            else store = Get(cls);
            if (store != null && cls.isAssignableFrom(store.getClass()))
                return store;
            return null;
        }
    }

    public boolean exists(String name) {
        synchronized (log) {
            if (!_inited) return false;
            for (Storage s : _insts)
                if (s.name().equals(name))
                    return true;
            return false;
        }
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
    public void add(StoreConf conf) throws StorageException {
        synchronized (log) {
            if (exists(conf.getName()))
                throw new StorageException("a storage named " + conf.getName() + " already exists.");
            try {
                Class cls = Class.forName(conf.getDriver());
                if (Storage.class.isAssignableFrom(cls)) {
                    Storage s = (Storage) cls.getConstructor().newInstance();
                    if (s == null) throw new StorageException("Cannot construct storage " + conf.getName());
                    s.create(conf);
                    Storage proxy = (Storage) Proxy.newProxyInstance(this.getClass().getClassLoader(), PROXY_INTFS, new StorageProxy(s));
                    _insts.add(proxy);
                }
            } catch (StorageException se) {
                throw se;
            } catch (Exception e) {
                log.error("err while add storage.", e);
                throw new StorageException("add storage failed: " + e.getMessage());
            }
        }
    }

    public void remove(String name) {
        synchronized (log) {
            if (!_inited) return;
            for (Storage s : _insts)
                if (s.name().equals(name)) {
                    s.destroy();
                    _insts.remove(s);
                }
        }
    }

    public List<Storage> list() {
        synchronized (log) {
            return _insts;
        }
    }

    public List<Tuple<String, Class<?>>> getAllStorageTypes() {
        List<String> list = new LinkedList<>();
        list.addAll(_types.keySet());
        return new LinkedList<>(ConvertUtils.convert(list, n -> new Tuple<>(n, _types.get(n))));
    }
    public Class<?> getStorageType(String name){
        return _types.get(name);
    }
}
