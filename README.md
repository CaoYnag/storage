# Storage
usually, we design data access with `DAO`, which makes clear, extendable project structure.
if there need several ways to save same data, we may need a management for these ways. this library makes a universal data storage specification, and offer some management interfaces.

# Storage Specification
## DataType
in this library, a datatype is identified by an interface, not an entity class.
some times, a datatype, include not only one entity type.
for example, I need to persist an album, which includes several photos. besides, an album also has some properties need to be persisted.
```java
class Photo
{
    String uuid;
    byte[] data;
}
class Album
{
    String uuid;
    byte[] thumb;
    List<Photo> photos;
}
```
when I store album data, I need album r/w interfaces, and direct photo access interface. such as:
```java
class AlbumOperation {
    void write(Album album);
    void read(Album album); // maybe only read album meta here
    
    void write(Photo photo); // modify a photo directly
    void read(Photo photo); // read a photo directly
}
```
a storage implementation should be able to access both album and photo same time. they are inseparable.

so I mark a datatype on its access interface and call it `StorageType`.
```java
@StorageType(value = "Album Data", desc = "store albums and photos.")
interface AlbumStore {
    // album access intfs here ...
    // photo access intfs here ...
}
```
## Storage Implementation
all storage implementation should implement interface `Storage`.
this interface defined a storage's lifecycle.
```java
interface Storage{
    void create(StoreConf cf) throws StorageException;
    void destroy();
    boolean valid();
    String type();
    // other meta intfs ...
}
```
indeed, there already a default implementation called `StorageImpl` of this interface. it has some default implementation for the interface.
so I usually extend this class, to avoid some duplicated works.
```java
class SampleImpl extends StorageImpl {
    // ...
}
```
## Storage Factory
all storage management interfaces defined in class `StorageFactory`. `StorageFactory` is designed as a singleton, get instance this way:
```java
StorageFactory factory = StorageFactory.get();
```
then, get store inst with name or type, or both.
```java
AlbumStore store = (AlbumStore)factory.Get("sample_store");
AlbumStore store = (AlbumStore)factory.Get(SampleStore.class);
AlbumStore store = (AlbumStore)factory.Get(SampleStore.class, "sample_store");
```

# Usage
first add the jar to project in any way.

create data and intfs like [here](#DataType).

then create a class extends `StorageImpl` and implements the intfs.

```java
import java.lang.annotation.Inherited;

@StoreDriver(value = "TestStore", desc = "storage for test.")
public class TestStorage extends StorageImpl implements AlbumStore {
    public TestStorage() {
    }

    @Override
    public void create(StoreConf cf) throws StorageException {
        super.create(cf);
    }

    @Override
    public void destroy() {
    }

    @Override
    public boolean valid() {
        return true;
    }

    @Override
    public String type() {
        return "test";
    }

    // implement albumstore operations here. and mark them @Read @Write.
    @Read
    void read(Album album);
    @Write
    void write(Album album);
}
```
an implementation could support several data types. so I could put `Read` and `Write` annotation on method who really do R/W action:
```java
class FSImpl extends StorageImpl implements AStore, BStore {
    @Read
    String read_file(String path);
    @Write
    void write_file(String path, String data);
    
    void ReadA(); // call read_file internal
    void WriteA(); // call write_file internal
    
    void ReadB(); // call read_file internal
    void WriteB(); // call write_file internal
}
```

then create a `storage.json` in classpath like this:
```json
{
  "store_scan_pkgs": [
    "test.store"
  ],
  "stores": [
    {
      "name": "test",
      "driver": "test.store.TestStorage",
      "perm": "rw",
      "conf": "{}"
    }
  ]
}
```
`store_scan_pkgs` is some `packages` contains `StorageType` interfaces.
in order to build a clear, beautiful project structure, I usually use a structure like this:
```
xx.xx.someapp
    |-store
    |   |-data
    |   |-intf
    |   |-some impl here
    |-other components...
```
then I just need to scan `xx.xx.someapp.store` to found out all `StorageType` interfaces.

`stores` is an array of `StoreConf`. `StoreConf` can be used to create a store inst.
- `name` used to identify a store inst.

- `driver` shows the class name of implementation.

- `perm` restrict read/write operation on this store inst.

- `conf` used by this implementation, so it can be any type, `json` or `xml`, or just a string.

  for example, a `filesystem` store may only contains a path like `/path/to/store/data`, but a `mysql` store need url, account and psw, and maybe some advanced option, they could be construct as a `json` or other format.
  **NOTICE**: characters in this field need to be escaped. a windows path conf may look like `"conf": "D:\\data\\pictures"`.

  
`StorageFactory` will initialize these stores configured in `stores` when first call `get()`.
If there was too many classes to scan, or store init cost too much time, maybe we could call `get()` once when svc/app startup.

at last, use store in business code:
```java
AlbumStore store = (AlbumStore)StorageFactory.get().Get(AlbumStore.class);
store.read(photo);
```