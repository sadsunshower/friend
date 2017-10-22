/* code by nick */
package com.sadsunshower.friend;

// allows us to create the various awt utilities we need
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

// for loading images
import javax.imageio.ImageIO;

// for behaviour 2
import java.util.ArrayDeque;

// code for the actual friend following the cursor
public class FriendChar implements TrayListener {
    // position we want to move to
    int goalX;
    int goalY;
    
    // current cursor position
    int curX;
    int curY;
    
    // current friend position
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
    
    // if we're on linux, our only choice is to draw a white bg
    // seems linux cannot into clearing transparent frames
    boolean linux;
    
    // application properties
    java.util.Properties props;
    
    // tracks cursor positions for behaviour 2
    ArrayDeque<int[]> points = new ArrayDeque<>();
    
    public FriendChar(boolean linux, java.util.Properties props) {
        this.linux = linux;
        this.props = props;
        
        mainWin = new JFrame();
        
        // create our 'window' with no borders
        mainWin.setUndecorated(true);
        mainWin.setSize(18, 18);
        mainWin.setAlwaysOnTop(true);
        mainWin.setType(javax.swing.JFrame.Type.UTILITY);
        
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
                super.paintComponent(g);
                if (g instanceof Graphics2D) {
                    render((Graphics2D) g);
                }
            }
        };
        
        if (!linux) {
            pane.setOpaque(false);
            com.sun.awt.AWTUtilities.setWindowOpaque(mainWin, false);
        }
        
        mainWin.setVisible(true);
        
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
        curX = x;
        curY = y;
        
        // for behaviour 2, add this to the points queue
        if (props.getProperty("behaviour").equals("2")) {
            // don't fetch the last point if there isn't one.
            if (!points.isEmpty()) {
                int[] top = points.peekLast();
                // if the updated point is close enough to the last, no point adding it
                if ((int) Math.sqrt(((top[0] - curX) * (top[0] - curX)) + ((top[1] - curY) * (top[1] - curY))) > 15) {
                    points.addLast(new int[] {curX, curY});
                }
            } else {
                points.addLast(new int[] {curX, curY});
            }
        // for behaviours 0 and 1, set this as the goal
        } else {
            goalX = curX;
            goalY = curY;
        }
    }
    
    // rendering method
    public void render(Graphics2D g2d) {
        // clear old frame
        if (linux) {
            g2d.setColor(java.awt.Color.WHITE);
        } else {
            g2d.setColor(new java.awt.Color(0, 0, 0, 0));
        }
        g2d.fillRect(0, 0, 18, 18);
        
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
        
        // behaviour 0 = follow cursor roughly
        if (props.getProperty("behaviour").equals("0")) {
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
        // behaviour 1 = move away from cursor if close
        } else if (props.getProperty("behaviour").equals("1")) {
            // if we are close enough, move away
            if (Math.sqrt(Math.pow(x - goalX, 2) + Math.pow(y - goalY, 2)) <= 25.0) {
                angle = -angle;
                
                if (inverse) {
                    x += (int) (15.0 * Math.cos(angle));
                    y += (int) (15.0 * Math.sin(angle));
                } else {
                    x -= (int) (15.0 * Math.cos(angle));
                    y -= (int) (15.0 * Math.sin(angle));
                }

                moved = true;
            }
        // behaviour 2 = trace cursor's path
        } else if (props.getProperty("behaviour").equals("2")) {
            // are we moving to a point currently?
            if (x != goalX && y != goalY) {
                // this is (basically) the same movement code for behaviour 1
                // if the distance is under 15px, just teleport to the cursor
                if (Math.sqrt(Math.pow(x - goalX, 2) + Math.pow(y - goalY, 2)) <= 15.0) {
                    moved = true;

                    x = goalX;
                    y = goalY;
                    
                    // set the goal as the next point if there is one
                    if (!points.isEmpty()) {
                        int[] pt = points.removeFirst();

                        goalX = pt[0];
                        goalY = pt[1];
                    }
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
            // if not, then check if theres another point to move to
            } else {
                // if there is, set that as the goal
                if (!points.isEmpty()) {
                    int[] pt = points.removeFirst();
                    
                    goalX = pt[0];
                    goalY = pt[1];
                }
                // if not, do nothing
            }
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
