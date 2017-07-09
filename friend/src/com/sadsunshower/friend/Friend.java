/* code by nick */
package com.sadsunshower.friend;

// main class
public class Friend {
    public static boolean lwinpressed = false;
    public static boolean rwinpressed = false;
    
    // main method
    public static void main(String[] args) {
        FriendChar friend = new FriendChar();
        
        javax.swing.JFrame.setDefaultLookAndFeelDecorated(true);
        
        // create the tray icon
        TrayManager.initialise(friend);
        
        // start the main loop
        while(true) {
            // start the sync process
            Synchroniser.startSync();
            
            // use awt to find the cursor location
            java.awt.Point pos = java.awt.MouseInfo.getPointerInfo().getLocation();
            friend.moveTo(pos.x, pos.y);
            friend.loop();
            
            Synchroniser.doSync();
        }
    }
}
