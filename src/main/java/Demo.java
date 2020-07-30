import java.lang.instrument.Instrumentation;

public class Demo {
    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        System.out.println("premain!");
     //   System.out.println(inst+"ceshi");
        inst.addTransformer(new MyTransformerTest() ,true);

    }
}