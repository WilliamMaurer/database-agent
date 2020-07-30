package src.test.impls;
import src.test.testinter;
import java.util.ArrayList;
public class testtwo implements testinter {
    public void Main(){
//        this.delete();;
//        this.fly();
        this.remove();
        this.selete();
    }
    @Override
    public void fly(int i,int t) {
        System.out.println(testone.class+"fly"+"测试2");
    }
    @Override
    public void delete(String s,int i) {
        System.out.println(testone.class+"delete"+"测试2");
    }
    @Override
    public void remove() {
        System.out.println(testtwo.class+"remove"+"测试2");
    }
    @Override
    public void selete() {
        System.out.println(testtwo.class+"selete"+"测试2");
    }
    public static void BeforeString(String[] arr){
        ArrayList<String> list = new ArrayList<String>();
        for (int i=0;i<arr.length;i++){
            String s= "$"+i;
            list.add(s);
        }
    }
}
