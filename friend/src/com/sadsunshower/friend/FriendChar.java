/* code by nick */
package com.sadsunshower.friend;

// allows us to create the various awt utilities we need
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

// for loading images
import javax.imageio.ImageIO;

// code for the actual friend following the cursor
public class FriendChar implements TrayListener {
    int goalX;
    int goalY;
    
    int x;
    int y;
    
    JFrame mainWin;
    
    Animation idle;
    Animation moveUp;
    Animation moveDown;
    Animation moveLeft;
    Animation moveRight;
    Animation sleep;
    
    // timer for sleep animation
    int sleepTimer = 0;
    
    boolean hidden = false;
    
    boolean moved = false;
    
    // we need this because of java's arctan
    boolean inverse = false;
    
    float angle = 0.0F;
    
    public FriendChar() {
        mainWin = new JFrame();
        
        // create our 'window' with no borders
        mainWin.setUndecorated(true);
        mainWin.setSize(18, 18);
        mainWin.setVisible(true);
        mainWin.setAlwaysOnTop(true);
        
        // place our friend in the middle of the screen
        java.awt.Dimension ss = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        goalX = (int) ss.getWidth() / 2;
        goalY = (int) ss.getHeight() / 2;
        x = goalX;
        y = goalY;
        mainWin.setLocation(goalX, goalY);
        
        // make the window transparent
        mainWin.setBackground(new java.awt.Color(0, 0, 0, 0));
        
        // override the window's rendering
        JPanel pane = new JPanel() {
            protected void paintComponent(java.awt.Graphics g) {
                if (g instanceof Graphics2D) {
                    render((Graphics2D) g);
                }
            }
        };
        
        // i hate swing
        pane.setSize(18, 18);
        
        mainWin.setContentPane(pane);
        
        // create all the animations
        try {
            BufferedImage spritesheet = ImageIO.read(Friend.class.getResourceAsStream("char1.png"));
            idle = new Animation(5, new BufferedImage[] {
                spritesheet.getSubimage(0, 0, 18, 18),
                spritesheet.getSubimage(18, 0, 18, 18),
                spritesheet.getSubimage(0, 0, 18, 18),
                spritesheet.getSubimage(36, 0, 18, 18)
            });
            moveUp = new Animation(1, new BufferedImage[] {
                spritesheet.getSubimage(0, 36, 18, 18),
                spritesheet.getSubimage(18, 36, 18, 18)
            });
            moveDown = new Animation(1, new BufferedImage[] {
                spritesheet.getSubimage(0, 18, 18, 18),
                spritesheet.getSubimage(18, 18, 18, 18)
            });
            moveLeft = new Animation(1, new BufferedImage[] {
                spritesheet.getSubimage(36, 18, 18, 18),
                spritesheet.getSubimage(54, 18, 18, 18),
                spritesheet.getSubimage(72, 18, 18, 18)
            });
            moveRight = new Animation(1, new BufferedImage[] {
                spritesheet.getSubimage(36, 36, 18, 18),
                spritesheet.getSubimage(54, 36, 18, 18),
                spritesheet.getSubimage(72, 36, 18, 18)
            });
            sleep = new Animation(5, new BufferedImage[] {
                spritesheet.getSubimage(54, 0, 18, 18),
                spritesheet.getSubimage(72, 0, 18, 18)
            });
        } catch (Exception e) {
            System.out.println("Error loading sprites: " + e.toString());
            System.exit(-1);
        }
    }
    
    public void moveTo(int x, int y) {
        goalX = x;
        goalY = y;
    }
    
    // rendering method
    public void render(Graphics2D g2d) {
        // clear old frame
        g2d.clearRect(0, 0, 18, 18);
        
        if (moved) {
            // reset sleep timer
            sleepTimer = 0;
            
            // draw correct movement animation
            if (angle >= 0.0F && angle < (float) (Math.PI / 4)) {
                if (inverse) {
                    g2d.drawImage(moveRight.getFrame(), 0, 0, null);
                } else {
                    g2d.drawImage(moveLeft.getFrame(), 0, 0, null);
                }
            } else if (angle >= (float) (Math.PI / 4) && angle <= (float) (Math.PI / 2)) {
                if (inverse) {
                    g2d.drawImage(moveDown.getFrame(), 0, 0, null);
                } else {
                    g2d.drawImage(moveUp.getFrame(), 0, 0, null);
                }
            } else if (angle < 0.0F && angle > (float) (-Math.PI / 4)) {
                if (inverse) {
                    g2d.drawImage(moveRight.getFrame(), 0, 0, null);
                } else {
                    g2d.drawImage(moveLeft.getFrame(), 0, 0, null);
                }
            } else {
                if (inverse) {
                    g2d.drawImage(moveUp.getFrame(), 0, 0, null);
                } else {
                    g2d.drawImage(moveDown.getFrame(), 0, 0, null);
                }
            }
        } else {
            // if we haven't moved for 200 frames, start sleeping
            if (sleepTimer < 200) {
                sleepTimer++;
                g2d.drawImage(idle.getFrame(), 0, 0, null);
            } else {
                g2d.drawImage(sleep.getFrame(), 0, 0, null);
            }
        }
    }
    
    public void loop() {
        // move just near the cursor
        mainWin.setLocation(x - 20, y - 20);
        
        moved = false;
        
        if (x == goalX) {
            if (y > goalY) {
                angle = (float) (-Math.PI / 2.0);
            } else {
                angle = (float) (Math.PI / 2.0);
            }
        } else {
            angle = (float) Math.atan((float) (y - goalY) / (float) (x - goalX));
        }
        
        // because of arctan
        inverse = x < goalX;
        
        // if the distance is under 15px, just teleport to the cursor
        if (Math.sqrt(Math.pow(x - goalX, 2) + Math.pow(y - goalY, 2)) <= 15.0) {
            if (x != goalX && y != goalY) {
                moved = true;
            }
            
            x = goalX;
            y = goalY;
        // otherwise, move 15px in the direction of the cursor
        } else {
            if (inverse) {
                x += (int) (15.0 * Math.cos(angle));
                y += (int) (15.0 * Math.sin(angle));
            } else {
                x -= (int) (15.0 * Math.cos(angle));
                y -= (int) (15.0 * Math.sin(angle));
            }
            
            moved = true;
        }
        
        // progress all our animations
        idle.step();
        sleep.step();
        moveUp.step();
        moveDown.step();
        moveLeft.step();
        moveRight.step();
        
        mainWin.repaint();
    }
    
    public void hide() {
        if (hidden) {
            x = goalX;
            y = goalY;
            hidden = false;
            mainWin.setVisible(true);
        } else {
            hidden = true;
            mainWin.setVisible(false);
        }
    }

    public void trayEvent(int button) {
        switch (button) {
            case TrayManager.MENU_BTN_HIDE:
                hide();
                break;
            case TrayManager.MENU_BTN_EXIT:
                System.exit(0);
                break;
        }
    }
}
