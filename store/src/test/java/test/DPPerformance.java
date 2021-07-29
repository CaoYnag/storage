package test;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;

public class DPPerformance {
    interface SampleIntf{
        void hello();
    }
    static class SampleImpl implements SampleIntf{
        @Override
        public void hello() {
            // do nothing.
        }
    }

    static class SampleProxy implements InvocationHandler {
        SampleIntf s;
        public SampleProxy(SampleIntf s) {
            this.s = s;
        }
        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            if(method.getName().equals("equals")) return equals(objects[0]);
            return method.invoke(s, objects);
        }

        @Override
        public boolean equals(Object obj) {
            if(this == obj) return true;
            if(obj instanceof SampleProxy) return s.equals(((SampleProxy) obj).s);
            return obj.equals(this);
        }
    }
    public static long benchmark(Runnable pro, int batch){
        long start = System.currentTimeMillis();
        for(int i = 0; i < batch; ++i) pro.run();
        long end = System.currentTimeMillis();
        return end - start;
    }
    public static long benchmark(SampleIntf intf, int batch){
        return benchmark((Runnable) () -> intf.hello(), batch);
    }
    public static SampleIntf cglib_intf(){
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(DPPerformance.class.getClassLoader());
        enhancer.setSuperclass(SampleImpl.class);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, methodProxy) -> methodProxy.invokeSuper(obj, args));
        return (SampleIntf) enhancer.create();
    }

    static class MyInterceptor implements MethodInterceptor {
        SampleIntf s;
        public MyInterceptor(SampleIntf s){
            this.s = s;
        }
        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            return method.invoke(s, objects);
        }
    }
    public static SampleIntf cglib_intf2(){
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(Object.class.getClassLoader());
        enhancer.setSuperclass(SampleIntf.class);
        enhancer.setCallback(new MyInterceptor(new SampleImpl()));
        return (SampleIntf) enhancer.create();
    }
    public static SampleIntf jdk_intf(){
        return (SampleIntf) Proxy.newProxyInstance(DynamicProxyTest.class.getClassLoader(), new Class[]{SampleIntf.class}, new SampleProxy(new SampleImpl()));
    }
    public static SampleIntf origin_intf(){
        return new SampleImpl();
    }
    public static void test_cmp() {
        SampleIntf jdk = jdk_intf();
        SampleIntf cglib1 = cglib_intf();
        SampleIntf cglib2 = cglib_intf2();
        System.out.println(jdk.equals(jdk));
        System.out.println(jdk.equals(new String()));
        System.out.println(cglib1.equals(cglib1));
        System.out.println(cglib2.equals(cglib2));

        LinkedList<SampleIntf> intfs = new LinkedList<>();
        intfs.add(jdk);
        intfs.add(cglib1);
        intfs.add(cglib2);
        intfs.remove(jdk);
        intfs.remove(cglib1);
        intfs.remove(cglib2);
        System.out.println(intfs.size());

    }
    public static void test_foo_call() {
        System.out.println("==================test call");
        for(int b = 100000; b <= 100000000; b *= 10){
            SampleIntf origin = origin_intf();
            SampleIntf jdk = jdk_intf();
            SampleIntf cg1 = cglib_intf();
            SampleIntf cg2 = cglib_intf2();

            System.out.println("batch: " + b);
            System.out.printf("origin: %d\n", benchmark(origin, b));
            System.out.printf("jdk   : %d\n", benchmark(jdk, b));
            System.out.printf("cg1   : %d\n", benchmark(cg1, b));
            System.out.printf("cg2   : %d\n", benchmark(cg2, b));
        }
    }
    public static void test_create() {
        System.out.println("==================test create");
        for(int b = 100000; b <= 100000000; b *= 10){
            System.out.println("batch: " + b);
            System.out.printf("origin: %d\n", benchmark((Runnable)()->{origin_intf();}, b));
            System.out.printf("jdk   : %d\n", benchmark((Runnable)()->{jdk_intf();}, b));
            System.out.printf("cg1   : %d\n", benchmark((Runnable)()->{cglib_intf();}, b));
            System.out.printf("cg2   : %d\n", benchmark((Runnable)()->{cglib_intf2();}, b));
        }
    }


    public static void main(String[] args){
        test_foo_call();
        test_create();
    }
}
