/* code by nick */
package com.sadsunshower.friend;

// crudely syncs animations to ~66ms per frame, which is ~15fps
public class Synchroniser {
    private static long startTime = -1L;
    
    public static void startSync() {
        startTime = System.currentTimeMillis();
    }
    
    public static void doSync() {
        long frameTime = System.currentTimeMillis() - startTime;
        if (frameTime < 66) {
            // sleep for however much time is left over
            try { Thread.sleep(66 - frameTime); } catch (Exception e) {}
        }
    }
}
