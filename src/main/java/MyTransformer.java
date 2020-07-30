
import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import src.Monitor;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.*;

/*
* MAP内容： 实例号， 服务名称号，目标数据库，目标表，sql语句，时间戳等
* */

public class MyTransformer implements ClassFileTransformer {
 //   static  Logger logger = Logger.getLogger(" ");//logs


    static Map<String,CtMethod[]> map = new HashMap<>();//k为实列号，v为方法名
    static List<String> list = new ArrayList<String>();//要监控的实现类
    public MyTransformer() throws Exception {
        findall(new File(System.getProperty("user.dir")));
        ClassPool pool = ClassPool.getDefault();//不能删，不知道为啥
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        // 会加载所有类，进行剔除，对应list监控List里面的所有类

        String newClassName = className.replace("/", ".");//类名的转换
        if (list.contains(so(newClassName))){
        try {
//            获取进程ID

            System.out.println("当前进程ID："+getProcessID());

            System.out.println("transform: [" + newClassName + "]");
            CtClass ctClass = ClassPool.getDefault().get(newClassName);
            CtBehavior[] methods = ctClass.getDeclaredBehaviors();
//            for (CtBehavior method : methods) {
//                enhanceMethod(method);//增强
//            }
            CtMethod[] ctMethod = ctClass.getDeclaredMethods();//取参数
            for (CtMethod ct:ctMethod) {
                getClassParameter(ct);

            }
            return ctClass.toBytecode();
        }catch (Exception e){
        }
        //<.....................>//如果不是自写类，则监控底层jdbc类
        }else {
            try {
                CtClass cl = null;
                ClassPool classPool = ClassPool.getDefault();

                cl = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));

                if ((Arrays.toString(cl.getInterfaces()).contains("java.sql.Statement") || Arrays.toString(cl.getInterfaces()).contains(" java.sql.Connection")) && !cl.isInterface()) {
                    CtMethod[] ctMethod = cl.getDeclaredMethods();
                    System.out.println("类名： " + cl.getName());

                    for (CtMethod ct : ctMethod) {
                        String name = ct.getName();
                        MethodInfo methodInfo = ct.getMethodInfo();//MethodInfo 中包括了方法的信息；名称、类型等内容。 //内容为字节码内容
                        //System.out.println("获取需要监控的方法名:" + methodInfo);
                        boolean isStatic = (methodInfo.getAccessFlags() & AccessFlag.STATIC) != 0;//判断是否为静态
                        /** 2种实现 */
                        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
                        //获得入参的名称
                        CtClass[] parameterTypes = ct.getParameterTypes();//获得入参类型
                        String returnTypeName = ct.getReturnType().getName();//获得出参信息



//                    System.out.println("方法名：   " + ct.getName());
//                    System.out.println("类型：" + (isStatic ? "静态方法" : "非静态方法"));
//                    System.out.println("描述：" + methodInfo.getDescriptor());
//                        int pos = Modifier.isStatic(ct.getModifiers()) ? 0 : 1;
//                        int para_index = -1;
//                        System.out.println("入参[名称]：");
//                        for (int i = 0; i < ct.getParameterTypes().length; i++) {
//                            System.out.print(attr.variableName(i) + " ");//入参名称
//                            if("sql".equals(attr.variableName(i))){
//                                para_index = i;
//                                CtClass cm = classPool.makeClass("MonitorLog");
//                                CtField ctField = new CtField(CtClass.longType, "porcessID", new CtClass[]{CtClass.longType})
//                            }
//                        }

//                    System.out.println();
//                    System.out.println("入参[类型]：");
//                    for (int i = 0; i < ct.getParameterTypes().length; i++) {
//                        System.out.print(parameterTypes[i].getName() + "  ");
//                    }
//                    System.out.println();
//                    System.out.println("出参[类型]：" + returnTypeName);

//                        添加一个输出方法
//                        String out = "";
//                        输入 startTime, processID ,Sql, costTime
//                        CtMethod outToTxt = new CtMethod(CtClass.voidType,"outPut",new CtClass[]{CtClass.longType,CtClass.})

//                        cl.addMethod(m);
                        addTimeCountMethod2(ct, classPool, newClassName, ct.getParameterTypes().length, Modifier.isStatic(ct.getModifiers()));


                    }
                    byte[] transformed = cl.toBytecode();
                    return transformed;
                }//监控底层接口算法
            } catch (Exception e) {
                //e.printStackTrace();
            }//都不是则过滤
        }
        return classfileBuffer;
    }
    //<....................................>
    //对底层代码增强，拿到底层运行的sql语句

    private void addTimeCountMethod2(CtMethod method,ClassPool classPool,String newClassName,int n,boolean flag) throws Exception {
        method.addLocalVariable("start", CtClass.longType);
        method.insertBefore("start = System.currentTimeMillis();");
        method.insertBefore("System.out.print(\"开始时间戳:  \");");
        method.insertBefore("System.out.println(System.currentTimeMillis());");
    //  method.insertBefore("System.out.println("+getProcessID()+");");//得到进程ID
        //打印时间戳
        ArrayList<String> list1 = BeforeString(n,flag);
        for (String s:list1
             ) {
            method.insertBefore(s);//传入的sql语句和数据库名
//            System.out.println("addTimeCountMethod():"+s);
        }
        //结束时间戳
        method.insertAfter("System.out.println(\"" +  method.getName() + " cost: \" + (System" +
                       ".currentTimeMillis() - start)+\"ms\");");
    }
    public static void post(){
        System.out.println("测试方法类");
    }
    //<......................................>
    //对方法名的转换函数
    public static String[] MethodName(String ClassName) throws ClassNotFoundException {
        Class de=Class.forName(ClassName);
        Method[] methods = de.getDeclaredMethods();
        System.out.println(methods.length);// 返回的是地址值，证明得到的是个数组对象
        // 遍历这个数组
        String[] arr = new String[methods.length];
        int index=0;
        for (Method ct : methods) {
            //arr[index++]= (ct.toString().indexOf('.')>0)?ct.toString().substring(0,ct.toString().indexOf('(')).substring(ct.toString().indexOf('.')+1,ct.toString().substring(0,ct.toString().indexOf('(')).length()):null;
            arr[index++]= so(ct.toString());
        }
        return arr;
    }

    //<................................>

    //解析传的类名和自己写的类名相匹配
    public static String so(String s){
        if(s.indexOf('.')<0)return null;
       // s=s.substring(0,s.indexOf('('));
       // System.out.println(s.lastIndexOf('.'));
        return s.substring(s.lastIndexOf('.')+1,s.length());//找到
    }

    //<................................>



    public static ArrayList BeforeString(int n,boolean flag){
        //bug未修改,如果是静态方法，需要剔除第一个，没测试
        ArrayList<String> list = new ArrayList<String>();
        if (flag){
            for (int i=1;i<n+1;i++){
                String s= "System.out.print($"+i+" +\"  \"  );";
                list.add(s);
            }
        }else for (int i=1;i<n+1;i++){
            String s= "System.out.print($"+i+"  +\"  \" );";
            list.add(s);
        }
        return list;
    }

    //得到进程ID函数
    public static final String getProcessID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
     //   System.out.println(runtimeMXBean.getName());//得到进程ID
        return (runtimeMXBean.getName().split("@")[0]);
    }

    //<................................>

    //得到当前工程下的所有自编写java文件

    public static void findall(File file ) {
        if(!file.exists())System.out.print("文件不存在！");
        if(file.isFile()) {
            if (file.getName().contains(".java")) list.add(file.getName().substring(0,file.getName().indexOf(".")));
        }else {
            File[] files=file.listFiles();
            if(files.length==0) {
                System.out.println("no file");
            }else {
                for(File f:files) {
                    findall(f);
                }
            }
        }
    }

    //<................................>
    //对自编写的java文件代码增强,仅仅监控函数使用时间

    private void enhanceMethod(CtBehavior method) throws Exception {
        if (method.isEmpty()) {
            return;
        }
        String methodName = method.getName();
        if ("main".equalsIgnoreCase(methodName)) {
            return;
        }
        final StringBuilder source = new StringBuilder();
        // 前置增强: 打入时间戳
        // 保留原有的代码处理逻辑
//        System.out.println("开始代码增强===========");
        source.append("{")
                .append("long start = System.nanoTime();\n") //前置增强: 打入时间戳
                .append("System.out.print(\"实例类的开始时间戳:   \");")
                .append("System.out.println(start);") //实例类的开始时间戳
                .append("$_ = $proceed($$);\n")              //调用原有代码，类似于method();($$)表示所有的参数
                .append("System.out.print(\"method:[")
                .append(methodName).append("]\");").append("\n")
                .append("System.out.println(\" cost:[\" +(System.nanoTime() - start)+ \"ns]\");") // 后置增强，计算输出方法执行耗时
                .append("}");
        ExprEditor editor = new ExprEditor() {
            @Override
            public void edit(MethodCall methodCall) throws CannotCompileException {
                methodCall.replace(source.toString());
            }
        };
        method.instrument(editor);
//        System.out.println("===========代码增强");
    }
    public void getClassParameter(CtMethod ctMethod)throws Exception{
        ctMethod.addLocalVariable("start", CtClass.longType);
        ctMethod.insertBefore("start = System.currentTimeMillis();");

        ArrayList<String> list1 = BeforeString(ctMethod.getParameterTypes().length,true);
        for (String s:list1
        ) {
            ctMethod.insertBefore(s);//传入的sql语句和数据库名

        }
    }

}
