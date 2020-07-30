package src.test;


import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.MethodInfo;


import java.lang.annotation.Annotation;
import java.sql.*;

public class demo1 implements  cltestinterfaceone{
    public  void connection(String url,String username,String password,String sql) throws ClassNotFoundException, SQLException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");   //com.mysql.jdbc为驱动所在包
//        2.    获得连接对象.
        //jdbc:mysql://localhost:3306/数据库名
        Connection conn = null;
        try{
            conn= DriverManager.getConnection(url, username, password);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (conn==null)return ;
        System.out.println("开始处理sql语句，方法为“dealout”  :");
        dealout(conn,sql);
//        Statement sta=conn.createStatement();
//        ResultSet rs=sta.executeQuery(sql);
//        rs.toString();
//        while(rs.next()){
//            System.out.println(rs.getInt("id")+"..." );
//        }
////        6.    释放资源.   (先开的后关)
////        调用一堆close()方法  关闭对象
//        rs.close();
//        sta.close();
        conn.close();

    }
    public  void dealout(Connection conn,String sql) throws SQLException {
        if (conn==null) return ;
        Statement sta=conn.createStatement();
        ResultSet rs=sta.executeQuery(sql);
        rs.toString();
        System.out.println(rs.toString());
//        while(rs.next()){
//            System.out.println(rs.getInt("skuid")+"..." );
//        }
        rs.close();
        sta.close();
    }
    public Connection connections(String url,String username,String password) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");   //com.mysql.jdbc为驱动所在包
//        2.    获得连接对象.
        //jdbc:mysql://localhost:3306/数据库名
        Connection conn = null;
        try{
            conn=DriverManager.getConnection(url, username, password);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (conn==null)return null;
        return conn;
    }
    public void closecon(Connection conn) throws SQLException {
        if (conn==null) return;
        conn.close();
    }



    //
//
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        demo1 demo1= new demo1();

        System.out.println(demo1.getClass());






        // TODO Auto-generated method stub
//        Class.forName("com.mysql.jdbc.Driver");   //com.mysql.jdbc为驱动所在包
////        2.    获得连接对象.
//        //jdbc:mysql://localhost:3306/数据库名
//        String url="jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC";
//        String username="root";
//        String password="123456";
//        Connection conn = null;
//        try{
//            conn=DriverManager.getConnection(url, username, password);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
////        3.    获得语句执行平台
////        通过连接对象获取对SQL语句的执行者对象
//        if (conn==null) System.out.println("axiba");
//        Statement sta=conn.createStatement();   //接收一定要用java里面的  导包导SQL里的
////        4.    执行sql语句
////        使用执行者对象，向数据库执行SQL语句
////        获取到数据库的执行后的结果
//// 添加
////        String sql="insert into sort(sname,sdesc) value('电子设备','都是骗男人的')";
////        int row=sta.executeUpdate(sql);
////        System.out.println(row);
//        //修改
////        String sql="update sort set sname='玩具' where sid='1'";
////        int row=sta.executeUpdate(sql);
////        System.out.println(row);
//        //查询
//        String sql="select id from tbl ";
//        //删除
////        String sql="delete from sort where sid='1'";
////        5.    处理结果 (仅用于查询)
//        ResultSet rs=sta.executeQuery(sql);
//        rs.toString();
//        while(rs.next()){
//            System.out.println(rs.getInt("id")+"..." );
//        }
////        6.    释放资源.   (先开的后关)
////        调用一堆close()方法  关闭对象
//        rs.close();
//        sta.close();
//        conn.close();
    }
}