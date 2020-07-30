package src.test.impls;

import src.test.cltestinterfaceone;
import src.test.demo1;
import src.test.testinter;

public class testone extends demo1 implements testinter , cltestinterfaceone
{

    @Override
    public void fly(int i,int t) {
        System.out.println(testone.class+"fly"+"测试1");
    }

    @Override
    public void delete(String s,int i) {
        System.out.println(testone.class+"delete"+"测试1");
    }

    @Override
    public void remove() {
        System.out.println(testone.class+"remove"+"测试1");
    }

    @Override
    public void selete() {
        System.out.println(testone.class+"selete"+"测试1");
    }
    public void sssain(){
        this.delete("zhangleilei ",23);;
        this.fly(1,2);
        this.remove();
        this.selete();
    }
}
