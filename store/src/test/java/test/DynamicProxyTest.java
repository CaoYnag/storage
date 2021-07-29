package test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

public class DynamicProxyTest {
    interface SampleIntf{
        String hello(String w);
    }
    static class Sample implements SampleIntf{
        public String hello(String w){
            return w;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            else return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }
    static class SampleProxy implements InvocationHandler{
        Sample s;
        private final Method EQUAL_METHOD = Sample.class.getMethod("equals", Object.class);
        public SampleProxy(Sample s) throws NoSuchMethodException {
            this.s = s;
        }
        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            if(method.getName().equals("equals")){
                return equals(objects[0]);
            }
            Object ret = method.invoke(s, objects);
            if(ret.getClass().equals(String.class)) ret = "proxy: " + ret;
            return ret;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof SampleProxy) {
                SampleProxy that = (SampleProxy) o;
                return Objects.equals(s, that.s);
            }
            return o.equals(this);
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }
    static void test_proxy() throws NoSuchMethodException {
        SampleIntf proxy = (SampleIntf) Proxy.newProxyInstance(DynamicProxyTest.class.getClassLoader(), new Class[]{SampleIntf.class}, new SampleProxy(new Sample()));
        System.out.println(proxy.hello("hello,world"));
        System.out.println(proxy.getClass());
        System.out.println(SampleIntf.class.isAssignableFrom(proxy.getClass()));
    }
    static void test_compare() throws NoSuchMethodException {
        System.out.println("test compare:");
        SampleIntf proxy = (SampleIntf) Proxy.newProxyInstance(DynamicProxyTest.class.getClassLoader(), new Class[]{SampleIntf.class}, new SampleProxy(new Sample()));
        System.out.println(proxy.equals(proxy));
        System.out.println(proxy.hashCode());
    }
    public static void main(String[] args) throws NoSuchMethodException {
        test_proxy();
        test_compare();
    }
}
