
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

/**
 *
 * @author beamj
 */
public class Copier {
    
    private static long bytes = 0;
    
    public static void copy(File file, File dir, boolean dateChecker, CopyPanel pan){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CopyPanel.pan.setSpeed(bytes * 4);
                bytes = 0;
            }
        }, 250, 250);
        if(file.isDirectory()){
            dir = new File(dir.getAbsolutePath() + "\\" + file.getName());
            copyDir(file, dir, dateChecker, pan);
        } else {
            copyFile(file, dir, pan);
        }
        timer.cancel();
        timer.purge();
    }
    
    private static void copyFile(File file, File dir, CopyPanel pan){
        File dest = new File(dir.getAbsolutePath() + "\\" + file.getName());
        copyOp(file, dest, pan);
    }
    
    public static void cut(File file, File dir, boolean dateChecker, CopyPanel pan){
        copy(file, dir, dateChecker, pan);
        file.delete();
    }
    
    public static void copyOp(File file, File dest, CopyPanel pan){
        InputStream is = null;
        OutputStream os = null;
        try {
            int bufferSize = 100000;
            pan.setCopying(file.getName());
            is = new FileInputStream(file);
            os = new BufferedOutputStream(new FileOutputStream(dest), bufferSize);
            byte[] buffer = new byte[bufferSize];
            int length;
            while ((length = is.read(buffer)) > 0) {
                bytes += length;
                os.write(buffer, 0, length);
            }
            System.out.println("File Copied: " + dest.getAbsolutePath());
            is.close();
            os.close();
        } catch(Exception e){
            e.printStackTrace();
            try {
                is.close();
                os.close();
            } catch(Exception f) {
                f.printStackTrace();
            }
        }
    }
    
    private static void copyDir(File dir1, File dir2, boolean smart, CopyPanel pan){
        dir2.mkdirs();
        for(File file : dir1.listFiles()){
            if(file.isDirectory()){
                File newDir = new File(dir2.getAbsolutePath() + "\\" + file.getName());
                copyDir(file, newDir, smart, pan);
            } else {
                File possDup = new File(dir2 + "\\" + file.getName());
                if(possDup.isFile()){
                    long toCopyTime = file.lastModified();
                    long dupTime = possDup.lastModified();
                    if(toCopyTime > dupTime){
                        copyFile(file, dir2, pan);
                    }
                } else {
                    copyFile(file, dir2, pan);
                }
            }
        }
    }
}
