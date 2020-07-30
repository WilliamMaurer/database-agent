package src.util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;

public class FileList {
    public static void findall(File file ) {
        if(!file.exists())System.out.print("文件不存在！");
        if(file.isFile()) {
            System.out.println(file.getPath()+" "+file.getName());
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
}