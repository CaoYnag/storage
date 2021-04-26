package test.outside_store;

import spes.store.StorageImpl;
import spes.store.anno.StoreDriver;

@StoreDriver(value = "Another", desc = "for test.")
public class AnotherStorage extends StorageImpl {
    public void destroy(){}
    public boolean valid(){return false;}
    public String type(){return "";}
}