/* code by nick */
package com.sadsunshower.friend;

// for the images
import java.awt.image.BufferedImage;

// manages displaying an animation
public class Animation {
    int timeInt;
    BufferedImage[] frames;
    
    int frameCounter = 0;
    int intervalCounter = 0;
    
    public Animation(int timeInt, BufferedImage[] frames) {
        this.timeInt = timeInt;
        this.frames = frames;
    }
    
    public BufferedImage getFrame() {
        return frames[frameCounter];
    }
    
    public void step() {
        intervalCounter = (intervalCounter == timeInt ? 0 : intervalCounter + 1);
        
        if (intervalCounter == 0) {
            frameCounter = (frameCounter == frames.length - 1 ? 0 : frameCounter + 1);
        }
    }
}
