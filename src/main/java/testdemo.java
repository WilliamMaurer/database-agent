import jdk.nashorn.internal.runtime.options.LoggingOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import src.test.demo1;
import src.test.impls.testone;
import src.test.impls.testtwo;
import src.util.GetClassInterfaceutils;
//import src.test.impls.testtwo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class testdemo {
//        private static final Logger logger2 = LoggerFactory.getLogger(testdemo.class);

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
//        Logger logger = Logger.getLogger(" ") ;
//        logger.info("fff");

        System.out.println("please enter number 1 continue");
        Long Start = null;
        Long End = null;

        MonitorLog ml = new MonitorLog();
        while (true) {
            Scanner reader = new Scanner(System.in);
            int number = reader.nextInt();
            if (number == 1) {
                demo1 demo1 = new demo1();
                String url="jdbc:mysql://localhost:3306/icbc?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
                String username="root";
                String password="123456";
                String sql="select skuid from goods";
                Start = System.currentTimeMillis();
//                logger2.info(String.valueOf(Start));
                demo1.connection(url,username,password,sql);
//                testjdbc testjdbc = new testjdbc();
//                testjdbc.testcon();;
//                testjdbc.deal();;
//                testjdbc.close();
                End = System.currentTimeMillis();
//                ml.info(Start,"10001","sql:xxxx",End,"Log.txt");
            }
        }




    }
}
