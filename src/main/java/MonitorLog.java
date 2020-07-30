import jdk.nashorn.internal.runtime.options.LoggingOption;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.io.*;
/*
* 负责输出日志，尝试用java原生环境的方法
* */
public class MonitorLog {
//    public static Long startTime;
//    public static String processID;
//    public static Long costTime;
//    public static String filePath;
//    public static String sql;
//
//    public MonitorLog(){
//
//    }
//
//    public static Long getCostTime() {
//        return costTime;
//    }
//
//    public static String getFilePath() {
//        return filePath;
//    }
//
//    public static String getProcessID() {
//        return processID;
//    }
//
//    public static Long getStartTime() {
//        return startTime;
//    }
//
//    public static String getSql() {
//        return sql;
//    }
//
//    public static void setCostTime(Long costTime) {
//        MonitorLog.costTime = costTime;
//    }
//
//    public static void setFilePath(String filePath) {
//        MonitorLog.filePath = filePath;
//    }
//
//    public static void setProcessID(String processID) {
//        MonitorLog.processID = processID;
//    }
//
//    public static void setStartTime(Long startTime) {
//        MonitorLog.startTime = startTime;
//    }
//
//    public static void setSql(String sql) {
//        MonitorLog.sql = sql;
//    }
/*
* 输出Log至Log.txt中
* */
    public static void info(Long startTime,String processID,String sql,Long costTime,String filePath){
//        setSql(sql);
//        setCostTime(costTime);
//        setFilePath(filePath);
//        setStartTime(startTime);
//        setProcessID(processID);
        String message = Long.toString(startTime)+","+processID+","+sql+","+Long.toString(costTime);
        FileWriter fw = null;
        PrintWriter pw = null;
        File f = new File(filePath);
        try {
            fw = new FileWriter(f,true);
            pw = new PrintWriter(fw);
            pw.println(message);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("写入txt文件出现异常！");
        }finally {

            try {
                pw.flush();
                fw.flush();
                pw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("在刷新/关闭txt文件出现异常！");
            }

        }
    }

//    public static void main(String[] args) {
//        startTime = System.currentTimeMillis();
//        processID = "10002";
//        sql = "sql:xxx";
//        filePath = "Log.txt";
//        costTime = startTime+100;
//        String message = Long.toString(startTime)+","+processID+","+sql+","+Long.toString(costTime);
//        FileWriter fw = null;
//        PrintWriter pw = null;
//        File f = new File(filePath);
//        try {
//            fw = new FileWriter(f,true);
//            pw = new PrintWriter(fw);
//            pw.println(message);
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("写入txt文件出现异常！");
//        }finally {
//
//            try {
//                pw.flush();
//                fw.flush();
//                pw.close();
//                fw.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.out.println("在刷新/关闭txt文件出现异常！");
//            }
//
//        }
//    }
}
