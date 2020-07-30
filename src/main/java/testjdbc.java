import src.test.demo1;

import java.sql.Connection;
import java.sql.SQLException;

public class testjdbc {
    public Connection test()throws SQLException, ClassNotFoundException {
        String url="jdbc:mysql://localhost:3306/icbc?useSSL=false&serverTimezone=UTC";
        String username="root";
        String password="123456";
        String sql="select skuid from goods ";
        demo1 demo1 =new demo1();
        Connection connection=demo1.connections(url,username,password);
        return connection;
    }
    public void testcon() throws ClassNotFoundException {
        String url="jdbc:mysql://localhost:3306/icbc?useSSL=false&serverTimezone=UTC";
        String username="root";
        String password="123456";
        String sql="select skuid from goods";
        demo1 demo1 =new demo1();
        Connection connection=demo1.connections(url,username,password);
    }
    public void deal() throws SQLException, ClassNotFoundException {
        Connection connection =null;
        String sql="select skuid from goods";
        connection=new testjdbc().test();
        if (connection==null)return;
        new demo1().dealout(connection,sql);
    }
    public void close() throws SQLException, ClassNotFoundException {
        Connection connection=this.test();
        if (null==null)return;
        new demo1().closecon(connection);
    }
}
