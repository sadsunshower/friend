/* code by nick */
package com.sadsunshower.friend;

// crudely syncs animations to ~66ms per frame, which is ~15fps
// or ~22ms per frame for behaviour 2, to capture cursor movements faster.
public class Synchroniser {
    private static long startTime = -1L;
    private static int syncTime = -1;
    
    public static void startSync(int sync) {
        startTime = System.currentTimeMillis();
        syncTime = sync;
    }
    
    public static void doSync() {
        long frameTime = System.currentTimeMillis() - startTime;
        if (frameTime < syncTime) {
            // sleep for however much time is left over
            try { Thread.sleep(syncTime - frameTime); } catch (Exception e) {}
        }
    }
}
