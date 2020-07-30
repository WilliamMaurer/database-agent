package src.util;
import src.test.impls.testone;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class GetClassInterface {
    private static void getAllClazz(Class<?> clazz, List<Class<?>> superClazzList) {
        if (clazz == null) {
            return;
        }
        if (!superClazzList.contains(clazz)) {
            superClazzList.add(clazz);
            Class<?> superclass = clazz.getSuperclass();
            getAllClazz(superclass, superClazzList);
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> interfaceCls : interfaces) {
                getAllClazz(interfaceCls, superClazzList);
            }
        }
    }
    public static void main(String[] args) {
        Class<?> clazz = testone.class;
        // 接口泛型类
        System.out.println(clazz.getName());

        Type[] genericInterfaces = clazz.getGenericInterfaces();
       // Arrays.stream(genericInterfaces).forEach(System.out::println);
        for (Type t:genericInterfaces
             ) {System.out.println(t);

        }
        Type genericInterface = genericInterfaces[0];
   //     System.out.println(genericInterface.getTypeName());
        if (genericInterface instanceof ParameterizedType) {
            print((ParameterizedType) genericInterface);
        }
        System.out.println("\n----------------------------------------");
        // 父类泛型类
        Type genericSuperclass = clazz.getGenericSuperclass();
        System.out.println(genericSuperclass.getTypeName());
        if (genericSuperclass instanceof ParameterizedType) {
            print((ParameterizedType) genericSuperclass);
        }
    }
    static  Map<String,Type[]> map = new HashMap<>();
    static List<String> Allinterface = new ArrayList<>();//所有接口类
    public static void AllClassmethod(Class<?> clazz){
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        for (Type type:genericInterfaces
             ) {
            String s=type.toString();
            Allinterface.add(s.split(" ")[1]);
        }
        map.put(clazz.getName(),genericInterfaces);//类名+接口名
        boolean flag=true;
        while (flag)
        {
            Type genericSuperclass = clazz.getGenericSuperclass();
            if (genericSuperclass==null){
                break;
            }
            map.put(clazz.getGenericSuperclass().getTypeName().getClass().getName(),genericSuperclass.getTypeName().getClass().getGenericInterfaces());
            clazz = genericSuperclass.getTypeName().getClass();
            Type[] tempinterface = clazz.getGenericInterfaces();
            for (Type type:tempinterface
            ) {
                String s=type.toString();
                Allinterface.add(s.split(" ")[1]);
            }
        }
        return ;
    }
    private static void print(ParameterizedType parameterizedType) {
        System.out.println(parameterizedType.getOwnerType());
        System.out.println(parameterizedType.getRawType());
        System.out.print("泛型参数：");
        Arrays.stream(parameterizedType.getActualTypeArguments()).forEach(arg -> System.out.print(arg + "、"));
    }


}
