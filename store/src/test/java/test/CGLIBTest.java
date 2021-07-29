package test;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CGLIBTest {
    interface SampleIntf{
        void hello();
    }
    static class SampleImpl implements SampleIntf {
        @Override
        public void hello() {
            // do nothing.
        }

        public void bye(){
            // do nothing
        }

        @Override
        public boolean equals(Object obj) {
            System.out.println("in SampleImpl::equals.");
            return super.equals(obj);
        }
    }

    static class SampleImpl2 implements SampleIntf {
        public SampleImpl2(int c){}
        @Override
        public void hello() {
            // do nothing.
        }

        public void bye(){
            // do nothing
        }

        @Override
        public boolean equals(Object obj) {
            System.out.println("in SampleImpl2::equals.");
            return super.equals(obj);
        }
    }
    public static SampleIntf cglib_intf(){
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(Object.class.getClassLoader());
        enhancer.setSuperclass(SampleImpl.class);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, methodProxy) -> {
            System.out.println("cglib proxying: " + method.getName() + " : " + obj.getClass() + " : " + methodProxy.getClass());
            return methodProxy.invokeSuper(obj, args);
        });
        return (SampleIntf) enhancer.create();
    }
    public static SampleIntf cglib_intf2(){
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(Object.class.getClassLoader());
        enhancer.setSuperclass(SampleImpl2.class);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, methodProxy) -> {
            System.out.println("cglib proxying: " + method.getName() + " : " + obj.getClass() + " : " + methodProxy.getClass());
            return methodProxy.invokeSuper(obj, args);
        });
        return (SampleIntf) enhancer.create(new Class[]{int.class}, new Object[]{1});
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
    public static SampleIntf cglib_intf3(){
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(Object.class.getClassLoader());
        // enhancer.setInterfaces(new Class[]{SampleIntf.class});
        enhancer.setSuperclass(SampleIntf.class);
        enhancer.setCallback(new MyInterceptor(new SampleImpl()));
        return (SampleIntf) enhancer.create();
    }
    public static void test_usage(){
        SampleIntf intf = cglib_intf();
        intf.hello();
        System.out.println(intf.equals(intf));

        SampleImpl impl = (SampleImpl) intf;
        if(impl != null){
            System.out.println(impl.getClass());
            impl.bye();
        }

        System.out.println("=============intf2");
        SampleIntf intf2 = cglib_intf2();
        intf2.hello();
        System.out.println(intf2.equals(intf2));

        SampleImpl2 impl2 = (SampleImpl2) intf2;
        if(impl2 != null){
            System.out.println(impl2.getClass());
            impl2.bye();
        }

        System.out.println("=============intf3");
        SampleIntf intf3 = cglib_intf3();
        intf3.hello();
        System.out.println(intf3.equals(intf3));
    }
    public static void main(String[] args){
        test_usage();
    }

}
