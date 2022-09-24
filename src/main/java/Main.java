
import java.awt.Desktop;
import java.io.File;
import java.io.PrintWriter;
import javax.swing.JOptionPane;

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

/**
 *
 * @author beamj
 */
public class Main {
    
    public static MainFrame main;
    private static String shortcutParent = "C:\\users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\Microsoft\\Windows\\SendTo";
    private static String lnkName = "Copier";
    private static String lnk;
    private static String[] pastVers = new String[] {};
    private static String exeName = "Copier.exe";
    
    public static void main(String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
        * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
        */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
         
        shortcut();
        main = new MainFrame();
        if(args.length > 0){
            if(args.length >= 1){
                main.setToCopy(new File(args[0]));
            }
            if(args.length >= 2){
                main.setWhere(new File(args[1]));
            }
        }
        main.setVisible(true);
    }
    
    private static void shortcut(){
        delPastVers();
        lnk = lnkName + ".lnk";
        if(!(new File(shortcutParent, lnk)).exists()) {
            File batch = new File("Copier.bat");
            try (PrintWriter writer = new PrintWriter(batch);){
                writer.println("@echo off");
                writer.println("set startupPath=\"" + shortcutParent + "\n"
                        + "set exePath=\"%CD%\n"
                        + "cd %startupPath%\n"
                        + "echo Set oWS = WScript.CreateObject(\"WScript.Shell\") > %startupPath%\\CreateShortcut.vbs\n" +
                        "echo sLinkFile = \"" + lnk + "\" >> %startupPath%\\CreateShortcut.vbs\n" +
                                "echo Set oLink = oWS.CreateShortcut(sLinkFile) >> %startupPath%\\CreateShortcut.vbs\n" +
                                "echo oLink.TargetPath = %exePath%\\" + exeName +"\" >> %startupPath%\\CreateShortcut.vbs\n" +
                                "echo oLink.WorkingDirectory = %startupPath%\" >> %startupPath%\\CreateShortcut.vbs\n" +
                                "echo oLink.Description = \"" + lnkName + "\" >> %startupPath%\\CreateShortcut.vbs\n" +
                                        "echo oLink.Save >> %startupPath%\\CreateShortcut.vbs\n"
                                        + "cd %startupPath%\n" +
                                        "C:\\Windows\\System32\\cscript.exe %startupPath%\\CreateShortcut.vbs\n" +
                                        "del CreateShortcut.vbs\n"
                                        + "echo %CD%\n"
                                        + "start " + lnk +"\n"
                                                + "cd %exePath%\n" +
                                                "(goto) 2>nul & del \"%~f0\" & exit /b");
                Desktop.getDesktop().open(batch);
            } catch (Exception e){
                boolean fail = false;
                if(batch.exists()){
                    try {
                        Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", batch.toString()});
                    } catch(Exception ex){
                        fail = true;
                        JOptionPane.showMessageDialog(null, "Unable to set to open when computer starts!");
                    }
                } else {
                    fail = true;
                    JOptionPane.showMessageDialog(null, "Unable to set to open when computer starts!");
                }
                if(fail){
                    batch.delete();
                }
            }
        }
    }
    
    private static void delPastVers() {
        for(int i = 0; i < pastVers.length; i++) {
            File file = new File(shortcutParent, pastVers[i]);
            if(file.isFile()) {
                file.delete();
            }
        }
    }
}
