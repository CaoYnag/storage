package test;

import spes.store.StorageFactory;

import java.util.List;

public class TestModify {
    public static void main(String[] args){
        StorageFactory factory = StorageFactory.get();
        List<Class<?>> drivers = factory.drivers();
        System.out.println("begin: " + drivers.size());
        drivers.add(TestModify.class);
        drivers = factory.drivers();
        System.out.println("last: " + drivers.size());
    }
}
