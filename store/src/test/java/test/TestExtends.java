package test;

abstract class Base{
    public void info(){
        System.out.println(this.getClass());
    }
}

class Sub extends Base{}

public class TestExtends {
    public static void main(String[] args){
        Sub sub = new Sub();
        sub.info();
    }
}
