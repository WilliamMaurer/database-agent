
import javassist.*;
import javassist.bytecode.*;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.objectweb.asm.ClassReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/*
* MyTransformerTest 尝试收集信息进行日主输出
* */

public class MyTransformerTest implements ClassFileTransformer {
    //   static  Logger logger = Logger.getLogger(" ");//logs
    static final Logger logger = LoggerFactory.getLogger(MyTransformerTest.class);
    static Map<String,CtMethod[]> map = new HashMap<>();//k为实列号，v为方法名
    static List<String> list = new ArrayList<String>();//要监控的实现类
    public MyTransformerTest() throws Exception {
        findall(new File(System.getProperty("user.dir")));
        ClassPool pool = ClassPool.getDefault();//不能删，不知道为啥
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        // 会加载所有类，进行剔除，对应list监控List里面的所有类

        String newClassName = className.replace("/", ".");//类名的转换
//        System.out.println(newClassName.getClass());
        if (list.contains(so(newClassName))){
            try {
//            获取进程ID

//                System.out.println("当前进程ID："+getProcessID());
                logger.info(getProcessID());

                System.out.println("transform: [" + newClassName + "]");
                CtClass ctClass = ClassPool.getDefault().get(newClassName);
                CtBehavior[] methods = ctClass.getDeclaredBehaviors();
//            for (CtBehavior method : methods) {
//                enhanceMethod(method);//增强
//            }
                CtMethod[] ctMethod = ctClass.getDeclaredMethods();//取参数
                for (CtMethod ct:ctMethod) {
                    getClassParameter(ct);
//                    addTimeout(ct,ctClass,ClassPool.getDefault());
                }
                return ctClass.toBytecode();
            }catch (Exception e){
            }
            //<.....................>//如果不是自写类，则监控底层jdbc类
        }else {
            try {
                CtClass cl = null;
                ClassPool classPool = ClassPool.getDefault();
//                classPool.appendClassPath("org.slf4j");
//                CtClass logClass = classPool.get("org.slf4j.Logger");

                cl = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));

//                使用slf4j进行日志收集
//                CtField log = new CtField(classPool.get("org.slf4j.Logger"),"logger",cl);
//                log.setModifiers(Modifier.PUBLIC+Modifier.STATIC);
//                cl.addField(log,cl.getName());

//                在待测方法中创建几个全局变量进行探针信息的收集
                CtField o_sql = new CtField(classPool.get("java.lang.String"),"o_sql",cl);
                o_sql.setModifiers(Modifier.PUBLIC+Modifier.STATIC);
                cl.addField(o_sql);

                CtField o_path = new CtField(classPool.get("java.lang.String"),"o_path",cl);
                o_path.setModifiers(Modifier.PUBLIC+Modifier.STATIC);
                cl.addField(o_path,CtField.Initializer.constant("Log.txt"));

                CtField o_processID = new CtField(classPool.get("java.lang.String"),"o_processID",cl);
                o_processID.setModifiers(Modifier.PUBLIC+Modifier.STATIC);
                cl.addField(o_processID);

                CtField o_log = new CtField(classPool.get("java.lang.String"),"o_log",cl);
                o_log.setModifiers(Modifier.PUBLIC+Modifier.STATIC);
                cl.addField(o_log);

                CtField o_cost = new CtField(CtClass.longType,"o_cost",cl);
                o_cost.setModifiers(Modifier.PUBLIC+Modifier.STATIC);
                cl.addField(o_cost);

                CtField o_start = new CtField(CtClass.longType,"o_start",cl);
                o_start.setModifiers(Modifier.PUBLIC+Modifier.STATIC);
                cl.addField(o_start);

                if ((Arrays.toString(cl.getInterfaces()).contains("java.sql.Statement") || Arrays.toString(cl.getInterfaces()).contains(" java.sql.Connection")||Arrays.toString(cl.getInterfaces()).contains(" java.sql.Connection")) && !cl.isInterface()) {
                    CtMethod[] ctMethod = cl.getDeclaredMethods();
                    System.out.println("类名： " + cl.getName());
                    System.out.println("another place:"+getProcessID());
//                    logger.info(getProcessID());

                    String pd = getProcessID();
                    for (CtMethod ct : ctMethod) {
                        String name = ct.getName();
//                        classPool.importPackage("org.slf4j.LoggerFactory");
//                        System.out.println("___________-"+cl.getName());
//                        ct.insertBefore("Logger logger = LoggerFactory.getLogger(MyTransformerTest.class);");
                        MethodInfo methodInfo = ct.getMethodInfo();//MethodInfo 中包括了方法的信息；名称、类型等内容。 //内容为字节码内容
                        //System.out.println("获取需要监控的方法名:" + methodInfo);
                        boolean isStatic = (methodInfo.getAccessFlags() & AccessFlag.STATIC) != 0;//判断是否为静态
                        /** 2种实现 */
                        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
//                        System.out.println("attr"+attr);
                        //获得入参的名称
                        CtClass[] parameterTypes = ct.getParameterTypes();//获得入参类型
                        String returnTypeName = ct.getReturnType().getName();//获得出参信息

//                        ct.addLocalVariable("processID",classPool.get("java.lang.String"));
//                        ct.insertBefore("processID = "+pd+";");
//                        ct.insertBefore("System.out.println(\"o_processid:\"+processID);");
//                    System.out.println("方法名：   " + ct.getName());
//                    System.out.println("类型：" + (isStatic ? "静态方法" : "非静态方法"));
//                    System.out.println("描述：" + methodInfo.getDescriptor());
//                        int pos = Modifier.isStatic(ct.getModifiers()) ? 0 : 1;
//                        System.out.println("入参[名称]：");
//                        for (int i = 0; i < ct.getParameterTypes().length; i++) {
//                            System.out.print(attr.variableName(i) + " ");//入参名称
//
//                        }

//                    System.out.println();
//                    System.out.println("入参[类型]：");
//                    for (int i = 0; i < ct.getParameterTypes().length; i++) {
//                        System.out.print(parameterTypes[i].getName() + "  ");
//                    }
//                    System.out.println();
//                    System.out.println("出参[类型]：" + returnTypeName);

                        addTimeCountMethod2(ct,cl, classPool, newClassName, ct.getParameterTypes().length, Modifier.isStatic(ct.getModifiers()));



                    }
                    output(cl,classPool);

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

    private void addTimeCountMethod2(CtMethod method,CtClass ctClass,ClassPool classPool,String newClassName,int n,boolean flag) throws Exception {

//        将start时间戳传给全局变量o_start
        method.addLocalVariable("start", CtClass.longType);
        method.insertBefore("start = System.currentTimeMillis();");

//        method.insertBefore("o_start = System.currentTimeMillis();");
//        method.insertBefore("System.out.print(\"开始时间戳:  \");");
//        method.insertBefore("System.out.println(System.currentTimeMillis());");

//        method.insertAfter("System.out.println(\"start:\"+start);");
        //打印时间戳
        ArrayList<String> list1 = BeforeString(n,flag);
        for (String s:list1
        ) {
            method.insertBefore(s);//传入的sql语句和数据库名
//            System.out.println("addTimeCountMethod():"+s);

        }
        //结束时间戳
//        method.insertAfter("System.out.println(\"" +  method.getName() + " cost: \" + (System" +
//                ".currentTimeMillis() - start)+\"ms\");");

        method.addLocalVariable("cost",CtClass.longType);
        method.insertAfter("cost = System.currentTimeMillis() - start;");

        method.insertAfter("o_start = start;");

//        method.insertBefore("logger.info(o_start);");


//        给全局变量赋值，得到要输出日志的信息
        method.insertAfter("o_cost = cost;");
//        method.insertAfter("o_processID = "+getProcessID()+";");
        method.insertAfter("o_log = o_start+\",\"+o_cost;");
        method.insertAfter("System.out.println(\"mes:\"+o_log);");
        method.insertAfter("System.out.println(\"o_path:\"+o_path);");

        /*
        * 通过insertAfter/insertBefore方法将MonitorLog中的日志输出方法写入到method中
        * */
        method.addLocalVariable("file",classPool.get("java.io.File"));
        method.addLocalVariable("fw",classPool.get("java.io.FileWriter"));
        method.addLocalVariable("pw",classPool.get("java.io.PrintWriter"));


//        method.insertAfter("file = java.io.File(o_path);");
//        method.insertAfter("fw = java.io.FileWriter(file,true);");
//        method.insertAfter("pw = java.io.PrintWriter(fw);");
//        method.insertAfter("pw.println(o_log);");
//        method.insertAfter("pw.flush();");
//        method.insertAfter("fw.flush();");
//        method.insertAfter("pw.close();");
//        method.insertAfter("fw.close();");
//        CtClass eIO =classPool.getDefault().get("java.io.IOException");
//        method.addCatch("{System.out.println($e);pw.flush();fw.flush();pw.close(); fw.close();throw $e;}",eIO);
//        method.insertAfter("info(o_start,o_cost);");

    }

    //使用代码块的形式新建一个方法，进行日志的输出
    public static void output(CtClass ctClass,ClassPool classPool){
        classPool.importPackage("java.io");

        CtMethod ou = new CtMethod(CtClass.voidType,"info",new CtClass[]{CtClass.longType,CtClass.longType},ctClass);
        ou.setModifiers(Modifier.PUBLIC);
//
        try {
            ou.setBody("{" +
//                    "java.io.File file = new java.io.File(\"Log.txt\");"+
//                    "java.io.FileWriter fw = null;"+
//                    "java.io.PrintWriter pw = null;"+
//                    "fw = new FileWriter(file,true);"+
//                    "pw = new PrintWriter(fw);"+
//                    "pw.println(\"1000xxx\");"+
//                    "pw.flush();"+
//                    "fw.flush();"+
//                    "pw.close();"+
//                    "fw.close();"+
                    "System.out.println(\"===========》\");"+
                    "}");
//            ou.insertAfter("pw.flush();");
//            ou.insertAfter("fw.flush();");
//            ou.insertAfter("pw.close();");
//            ou.insertAfter("fw.close();");

            CtClass eIO =classPool.getDefault().get("java.io.IOException");
            ou.addCatch("{System.out.println($e+\"写入异常！\"); throw $e;}",eIO);

//            CtClass eIO2 =classPool.getDefault().get("java.io.IOException");
//            ou.addCatch("{System.out.println($e+\"关闭异常！\"); throw $e;}",eIO2);
            ctClass.addMethod(ou);

        } catch (CannotCompileException | NotFoundException e) {
            e.printStackTrace();
        }

//

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
