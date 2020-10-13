package test;

import spes.store.Storage;
import spes.store.StorageFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxyTest {
    interface SampleIntf{
        String hello(String w);
    }
    static class Sample implements SampleIntf{
        public String hello(String w){
            return w;
        }
    }
    static class SampleProxy implements InvocationHandler{
        Sample s;
        public SampleProxy(Sample s){
            this.s = s;
        }
        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            Object ret = method.invoke(s, objects);
            if(ret.getClass().equals(String.class)) ret = "proxy: " + ret;
            return ret;
        }
    }
    public static void main(String[] args){
        SampleIntf proxy = (SampleIntf) Proxy.newProxyInstance(DynamicProxyTest.class.getClassLoader(), new Class[]{SampleIntf.class}, new SampleProxy(new Sample()));
        System.out.println(proxy.hello("hello,world"));
        System.out.println(proxy.getClass());
        System.out.println(SampleIntf.class.isAssignableFrom(proxy.getClass()));
    }
}
