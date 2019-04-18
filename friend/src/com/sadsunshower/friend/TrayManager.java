/* code by nick */
package com.sadsunshower.friend;

// to access the system tray
import java.awt.SystemTray;
import java.awt.TrayIcon;

// the menu
import java.awt.PopupMenu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;

// for images
import javax.imageio.ImageIO;

// manages the tray icon
public class TrayManager {
    private static SystemTray tray;
    private static TrayIcon icon = null;
    
    private static TrayListener listener;
    
    public static void initialise(TrayListener listener) {
        // stop if the system tray isnt supported
        if (!SystemTray.isSupported()) {
            System.out.println("Warning! System tray is not supported");
            return;
        }

        TrayManager.tray = SystemTray.getSystemTray();
        TrayManager.listener = listener;
        
        try {
            icon = new TrayIcon(ImageIO.read(Friend.class.getResourceAsStream("trayicn.png")), "friend");
            
            PopupMenu menu = new PopupMenu();
            
            MenuItem itm1 = new MenuItem("Hide / Unhide");
            itm1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    listener.trayEvent(MENU_BTN_HIDE);
                }
            });
            
            
            MenuItem itm2 = new MenuItem("About");
            itm2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new AboutDialog(null, false).setVisible(true);
                }
            });
            
            MenuItem itm3 = new MenuItem("Exit");
            itm3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    listener.trayEvent(MENU_BTN_EXIT);
                }
            });
            
            menu.add(itm1);
            menu.add(itm2);
            menu.add(itm3);
            
            icon.setPopupMenu(menu);
            
            tray.add(icon);
        } catch (Exception e) {
            System.out.println("error creating tray icon: " + e.toString());
        }
    }
    
    public static final int MENU_BTN_HIDE = 0x0;
    public static final int MENU_BTN_EXIT = 0x1;
}
