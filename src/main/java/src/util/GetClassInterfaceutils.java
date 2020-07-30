package src.util;



import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetClassInterfaceutils {
//    static Map<String, List<String>> map = new HashMap<>(); //String为类名，Type为接口名
//    static List<String> Allinterface = new ArrayList<>();//所有接口类
    //将类名作为key，接口作为List
    public   Map<String,  List<String>> GetClassInterfaceutil(String clazz) throws ClassNotFoundException {
        Map<String, List<String>> map = new HashMap<>();
        List<String> list = GetClassInterfaceutilList(clazz);
        map.put(clazz,list);
        if (Class.forName(clazz).getGenericSuperclass()!=null){
          //  Class clazzs=clazz.getSuperclass();
            GetClassInterfaceutil( Class.forName(clazz).getSuperclass().toString());
        }
        return map;
    }
    //将当前类的所有接口保存到list里面
    public   List<String> GetClassInterfaceutilList(String clazz) throws ClassNotFoundException {
        List<String> Allinterface = new ArrayList<>();
        Type[] genericInterfaces = Class.forName(clazz).getGenericInterfaces();
        for (Type type:genericInterfaces
        ) {
            String s=type.toString();
            if (s.contains(" "))Allinterface.add(s.split(" ")[1]);
        }
        return Allinterface;
    }
}
