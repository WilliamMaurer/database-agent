import src.test.impls.testone;
import src.util.GetClassInterfaceutils;

import java.util.ArrayList;
import java.util.List;

public class test {
    public void test(int k,String s,boolean flag) {
        System.out.println("此处的k值是:  "+k);
    }
   public static void solu(char c){
        String s = "ccc";
        System.out.println(c);
    }
    public static void main(String[] args){
        GetClassInterfaceutils getClassInterfaceutils=new GetClassInterfaceutils();
        String newClassName = "src.test.impls.testone";
        List<String> s = new ArrayList();
//        try {
//            s=getClassInterfaceutils.GetClassInterfaceutilList((Class<String>) Class.forName(newClassName));
//            List<String>  sss=getClassInterfaceutils.GetClassInterfaceutilList((Class<String>) Class.forName(newClassName));
//            for (String q:s
//                 ) {
//                System.out.println(q);
//            }
//            for (String q:sss
//            ) {
//                System.out.println(q);
//            }
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        testone t = new testone();

    }
}
