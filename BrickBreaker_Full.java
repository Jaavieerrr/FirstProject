import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * BrickBreaker_Full - A complete brick breaker game implementation using Java Swing.
 * 
 * OOP DESIGN OVERVIEW:
 * - Uses INHERITANCE with an abstract GameObject base class (Paddle, Ball, Brick all extend it)
 * - Demonstrates POLYMORPHISM through overridden draw() methods
 * - Uses COMPOSITION to combine game objects (GameBoard contains Paddle, Ball, ArrayList<Brick>)
 * - Implements INTERFACES (KeyListener, MouseListener) for event handling
 * - ENCAPSULATION of game state and logic within GameBoard class
 */
public class BrickBreaker_Full {

    // ---- Constants ----
    // Initial game window dimensions (can be resized at runtime)
    static final int WINDOW_W = 640;
    static final int WINDOW_H = 520;

    // ---- Abstract game object ----
    /**
     * ABSTRACT BASE CLASS - Foundation for all drawable game entities
     * 
     * OOP PRINCIPLE: ABSTRACTION
     * Defines common interface and behavior for all game objects (Paddle, Ball, Brick)
     * Forces subclasses to implement the draw() method (polymorphic behavior)
     * 
     * ENCAPSULATION: Stores position (x, y) and dimensions (width, height)
     */
    abstract static class GameObject {
        // Position and dimensions - used by all game entities
        double x, y;
        int width, height;

        // Constructor initializes position and size
        public GameObject(double x, double y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        // Returns bounding rectangle for collision detection
        public Rectangle2D getBounds2D() {
            return new Rectangle2D.Double(x, y, width, height);
        }

        // Abstract method enforces that all subclasses must implement drawing
        // POLYMORPHISM: Each subclass draws itself differently
        public abstract void draw(Graphics2D g);
    }

    // ---- Paddle ----
    /**
     * INHERITANCE: Extends GameObject abstract class
     * Represents the player-controlled paddle at the bottom of the game
     * 
     * ENCAPSULATION: Contains movement speed and position
     * Implements specialized draw() method (POLYMORPHISM)
     */
    static class Paddle extends GameObject {
        // Movement speed per key press
        final double speed = 8.0;

        // Constructor sets initial position and size (100x16)
        public Paddle(double x, double y) {
            super(x, y, 100, 16);
        }

        // POLYMORPHIC IMPLEMENTATION: Overrides GameObject's abstract draw method
        @Override
        public void draw(Graphics2D g) {
            // Draw blue rounded rectangle paddle
            g.setColor(new Color(40, 130, 200));
            g.fillRoundRect((int)x, (int)y, width, height, 10, 10);
        }

        // Encapsulated movement methods - prevent paddle from going off-screen
        public void moveLeft() { x = Math.max(0, x - speed); }
        public void moveRight(int panelWidth) { x = Math.min(panelWidth - width, x + speed); }
    }

    // ---- Ball ----
    /**
     * INHERITANCE: Extends GameObject abstract class
     * Represents the ball that bounces around the game world
     * 
     * ENCAPSULATION: Stores velocity (dx, dy) and position
     * POLYMORPHIC: Implements its own draw method with different rendering than Paddle/Brick
     */
    static class Ball extends GameObject {
        // Velocity components - direction and speed of movement
        double dx = 3.6;  // horizontal velocity
        double dy = -3.6; // vertical velocity (negative = upward)

        // Constructor sets initial position and size (14x14 circle)
        public Ball(double x, double y) {
            super(x, y, 14, 14);
        }

        // POLYMORPHIC IMPLEMENTATION: Draws ball as red oval
        @Override
        public void draw(Graphics2D g) {
            g.setColor(new Color(200, 50, 50)); // Red color
            g.fillOval((int)x, (int)y, width, height);
        }

        // Update position based on current velocity
        public void update() { x += dx; y += dy; }

        // Direction reversal methods for collision handling
        public void reverseX() { dx = -dx; } // Bounce off left/right walls
        public void reverseY() { dy = -dy; } // Bounce off top/bottom surfaces
    }

    // ---- Brick ----
    /**
     * INHERITANCE: Extends GameObject abstract class
     * Represents destructible bricks in the game grid
     * 
     * ENCAPSULATION: Stores destroyed state and color
     * POLYMORPHIC: Custom draw method that only renders non-destroyed bricks
     */
    static class Brick extends GameObject {
        // State tracking - whether this brick has been hit
        boolean destroyed = false;
        
        // Visual appearance - each brick has its own color
        Color color;

        // Constructor sets position, size, and color
        public Brick(int x, int y, Color color) {
            super(x, y, 56, 20);  // 56x20 pixel bricks
            this.color = color;
        }

        // POLYMORPHIC IMPLEMENTATION: Only draws if brick hasn't been destroyed
        @Override
        public void draw(Graphics2D g) {
            if (!destroyed) {
                // Fill brick with assigned color
                g.setColor(color);
                g.fillRect((int)x, (int)y, width, height);
                
                // Draw dark border for contrast
                g.setColor(Color.DARK_GRAY);
                g.drawRect((int)x, (int)y, width, height);
            }
        }
    }

    // ---- Game board ----
    /**
     * COMPOSITION: The main game controller that contains instances of Paddle, Ball, and Bricks
     * INTERFACE IMPLEMENTATION: Implements KeyListener and MouseListener for event handling
     * 
     * This class demonstrates:
     * - ENCAPSULATION: Contains all game state (score, lives, game flags)
     * - POLYMORPHISM: Uses abstract GameObject references (Paddle, Ball are GameObjects)
     * - COMPOSITION: Aggregates multiple game objects into single Game instance
     * - EVENT-DRIVEN PROGRAMMING: Responds to keyboard and mouse input
     */
    static class GameBoard extends JPanel implements KeyListener, MouseListener {
        // Game objects - COMPOSITION of different classes
        Paddle paddle;        // Player-controlled paddle
        Ball ball;            // Game ball
        ArrayList<Brick> bricks = new ArrayList<>();  // Collection of destructible bricks
        
        Timer timer;          // Game loop timer - runs at 60 FPS (16ms per frame)

        // Input state tracking - which keys are currently pressed
        boolean leftPressed = false;
        boolean rightPressed = false;

        // Game state variables - ENCAPSULATED game state
        int score = 0;        // Current player score
        int lives = 3;        // Remaining lives

        // Game phase flags
        boolean gameOver = false;     // Player lost all lives
        boolean gameWin = false;      // Player destroyed all bricks
        boolean gameStarted = false;  // Game has begun
        boolean gamePaused = false;   // Game is temporarily paused

        Image background;  // Optional background image

        // Button hitboxes for UI interaction
        Rectangle startButtonRect;
        Rectangle playAgainButtonRect;

        // Replace the path below with the file URL of your uploaded image if you want a background.
        // Example local path usage: new ImageIcon("file:/mnt/data/3245b020-133f-4650-b36b-e5490edb53b7.png").getImage();
        final String BACKGROUND_PATH = "file:/mnt/data/3245b020-133f-4650-b36b-e5490edb53b7.png";

        JFrame parentFrame;  // Reference to parent window
        
        // Scaling factors - allow responsive rendering for window resizing
        double scaleX = 1.0;
        double scaleY = 1.0;

        // Constructor initializes the game board
        public GameBoard(JFrame frame) {
            parentFrame = frame;
            
            // Setup panel properties
            setPreferredSize(new Dimension(WINDOW_W, WINDOW_H));
            setBackground(Color.BLACK);
            setFocusable(true);
            
            // Register event listeners - INTERFACE IMPLEMENTATION
            addKeyListener(this);
            addMouseListener(this);
            
            // Listen for window resize events to update scaling
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    updateScale();
                }
            });

            // Initialize game objects
            paddle = new Paddle( (WINDOW_W - 100)/2.0, WINDOW_H - 70 );
            ball = new Ball(WINDOW_W/2.0 - 7, WINDOW_H/2.0);

            generateBricks();

            // Try to load background image (optional)
            try {
                background = new ImageIcon(BACKGROUND_PATH).getImage();
            } catch (Exception e) {
                background = null;
            }

            // Setup game loop - 16ms interval ≈ 60 FPS
            timer = new Timer(16, e -> gameLoop());

            // Initialize button positions
            startButtonRect = new Rectangle(WINDOW_W/2 - 75, WINDOW_H/2, 150, 50);
            playAgainButtonRect = new Rectangle(WINDOW_W/2 - 75, WINDOW_H/2 + 70, 150, 50);
        }

        // Update scaling factors when window is resized
        private void updateScale() {
            scaleX = (double) getWidth() / WINDOW_W;
            scaleY = (double) getHeight() / WINDOW_H;
        }

        // Generate the grid of bricks (5 rows x 10 columns = 50 bricks)
        private void generateBricks() {
            bricks.clear();
            
            // Define colors for each row - from hot to cool
            Color[] rowColors = { 
                new Color(220,80,80),    // Red row
                new Color(220,160,60),   // Orange row
                new Color(120,200,120),  // Green row
                new Color(90,160,220),   // Blue row
                new Color(180,120,220)   // Purple row
            };
            
            // Calculate positions based on current window scale
            int offsetX = (int)(40 * scaleX);
            int offsetY = (int)(60 * scaleY);
            int brickSpacingX = (int)(60 * scaleX);
            int brickSpacingY = (int)(28 * scaleY);
            int brickWidth = (int)(56 * scaleX);
            int brickHeight = (int)(20 * scaleY);
            
            // Create 5 rows, 10 columns of bricks
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 10; col++) {
                    Brick brick = new Brick(
                        offsetX + col * brickSpacingX, 
                        offsetY + row * brickSpacingY, 
                        rowColors[row]
                    );
                    // Scale brick dimensions
                    brick.width = brickWidth;
                    brick.height = brickHeight;
                    bricks.add(brick);
                }
            }
        }

        /**
         * Main game loop - runs every 16ms (60 FPS)
         * Updates game state only when game is actively playing
         */
        private void gameLoop() {
            // Skip updates if game is not in play state
            if (gameOver || gameWin || !gameStarted || gamePaused) return;

            // Handle paddle movement based on pressed keys
            if (leftPressed) paddle.moveLeft();
            if (rightPressed) paddle.moveRight(getWidth());

            // Update ball position
            ball.update();

            // Check for all collision types
            checkWallCollision();
            checkPaddleCollision();
            checkBrickCollision();
            checkGameState();

            // Redraw the screen with updated positions
            repaint();
        }

        /**
         * Collision detection: Ball vs walls and game boundaries
         * Bounces ball off walls and handles losing a life
         */
        private void checkWallCollision() {
            // Left wall collision
            if (ball.x <= 0) { 
                ball.x = 0; 
                ball.reverseX(); 
                Toolkit.getDefaultToolkit().beep(); 
            }
            
            // Right wall collision
            if (ball.x + ball.width >= getWidth()) { 
                ball.x = getWidth() - ball.width; 
                ball.reverseX(); 
                Toolkit.getDefaultToolkit().beep(); 
            }
            
            // Top wall collision
            if (ball.y <= 0) { 
                ball.y = 0; 
                ball.reverseY(); 
                Toolkit.getDefaultToolkit().beep(); 
            }
            
            // Bottom boundary - player loses a life
            if (ball.y > getHeight()) {
                lives--;
                Toolkit.getDefaultToolkit().beep();
                if (lives <= 0) {
                    gameOver = true;
                    timer.stop();
                } else {
                    // Reset ball and paddle for next life
                    resetPositions();
                }
            }
        }

        /**
         * Reset ball and paddle to center after losing a life
         * Ball velocity is randomized slightly for variety
         */
        private void resetPositions() {
            // Center paddle horizontally
            paddle.x = (getWidth() - (int)(paddle.width * scaleX)) / 2.0;
            // Position paddle near bottom
            paddle.y = getHeight() - (int)(70 * scaleY);
            
            // Center ball
            ball.x = getWidth() / 2.0 - (ball.width * scaleX)/2.0;
            ball.y = getHeight() / 2.0;
            
            // Set ball speed and direction with random horizontal component
            double speed = 3.6 * Math.sqrt((scaleX + scaleY) / 2.0);
            ball.dx = speed * (Math.random() > 0.5 ? 1 : -1);  // Random left/right
            ball.dy = -speed;  // Always upward
        }

        /**
         * Collision detection: Ball vs Paddle
         * Adjusts ball velocity based on where it hits the paddle
         */
        private void checkPaddleCollision() {
            Rectangle2D ballRect = ball.getBounds2D();
            Rectangle2D paddleRect = paddle.getBounds2D();
            
            if (ballRect.intersects(paddleRect)) {
                // Move ball above paddle to prevent re-collision
                ball.y = paddle.y - ball.height - 1;

                // Calculate where on the paddle the ball hit (relative to center)
                double paddleCenter = paddle.x + paddle.width / 2.0;
                double relativeIntersect = (ball.x + ball.width/2.0) - paddleCenter;
                double normalized = relativeIntersect / (paddle.width / 2.0); // Range: -1 to 1

                // Convert hit position to bounce angle (up to ±75 degrees)
                double maxAngle = Math.toRadians(75);
                double angle = normalized * maxAngle;
                double speed = Math.hypot(ball.dx, ball.dy);

                // Apply new velocity based on angle
                ball.dx = speed * Math.sin(angle);
                ball.dy = -Math.abs(speed * Math.cos(angle));

                Toolkit.getDefaultToolkit().beep();
            }
        }

        /**
         * Collision detection: Ball vs Bricks
         * Detects which side was hit and bounces accordingly
         */
        private void checkBrickCollision() {
            Rectangle2D ballRect = ball.getBounds2D();
            
            for (Brick b : bricks) {
                // Skip already destroyed bricks
                if (b.destroyed) continue;
                
                Rectangle2D brickRect = b.getBounds2D();
                if (ballRect.intersects(brickRect)) {
                    // Calculate intersection area to determine collision side
                    Rectangle2D intersection = ballRect.createIntersection(brickRect);

                    // If width < height, hit from left/right; otherwise hit from top/bottom
                    if (intersection.getWidth() < intersection.getHeight()) {
                        ball.reverseX();  // Bounce horizontally
                    } else {
                        ball.reverseY();  // Bounce vertically
                    }

                    // Mark brick as destroyed and update score
                    b.destroyed = true;
                    score += 100;
                    Toolkit.getDefaultToolkit().beep();

                    // Small nudge prevents re-collision with same brick
                    ball.x += ball.dx * 0.5;
                    ball.y += ball.dy * 0.5;
                    
                    break; // Only collide with one brick per frame for stability
                }
            }
        }

        /**
         * Check if game has been won (all bricks destroyed)
         */
        private void checkGameState() {
            boolean allGone = true;
            for (Brick b : bricks) if (!b.destroyed) { allGone = false; break; }
            if (allGone) {
                gameWin = true;
                timer.stop();
            }
        }

        /**
         * Render the entire game screen
         * Called whenever repaint() is invoked
         * ENCAPSULATION: All drawing logic is contained within this method
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            updateScale();
            
            // Use Graphics2D for advanced rendering (anti-aliasing, transformations)
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Apply scaling transformations for responsive design
            g2.scale(scaleX, scaleY);

            // Draw optional background image
            if (background != null) {
                g2.drawImage(background, 0, 0, WINDOW_W, WINDOW_H, null);
            }

            // Set transparency for UI rendering
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));

            // Draw all game objects using POLYMORPHISM - each calls its own draw() method
            for (Brick b : bricks) b.draw(g2);   // Bricks (if not destroyed)
            paddle.draw(g2);                      // Paddle (blue rectangle)
            ball.draw(g2);                        // Ball (red oval)

            // Return to full opacity for HUD text
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            
            // Draw score and lives HUD
            g2.drawString("Score: " + score, 12, 20);
            g2.drawString("Lives: " + lives, WINDOW_W - 90, 20);

            // Draw Start Button if game hasn't started
            if (!gameStarted) {
                drawButton(g2, startButtonRect, "START GAME", new Color(40, 180, 60));
            }

            // Draw Play Again Button if game is over or won
            if (gameOver || gameWin) {
                drawButton(g2, playAgainButtonRect, "PLAY AGAIN", new Color(40, 180, 60));
            }

            // Draw Game Over message (red)
            if (gameOver) {
                g2.setFont(new Font("SansSerif", Font.BOLD, 36));
                g2.setColor(new Color(220, 40, 40));
                g2.drawString("GAME OVER", WINDOW_W/2 - 120, WINDOW_H/2 - 80);
            }

            // Draw Win message (green)
            if (gameWin) {
                g2.setFont(new Font("SansSerif", Font.BOLD, 36));
                g2.setColor(new Color(40, 200, 80));
                g2.drawString("YOU WIN!", WINDOW_W/2 - 90, WINDOW_H/2 - 80);
            }

            // Draw Pause overlay (yellow) when game is paused
            if (gamePaused && gameStarted && !gameOver && !gameWin) {
                g2.setFont(new Font("SansSerif", Font.BOLD, 36));
                g2.setColor(new Color(255, 200, 0));
                g2.drawString("PAUSED", WINDOW_W/2 - 80, WINDOW_H/2);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
                g2.drawString("Press ESC to resume", WINDOW_W/2 - 85, WINDOW_H/2 + 50);
            }

            // Draw on-screen instructions for new players
            if (!gameStarted) {
                g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
                g2.setColor(Color.GRAY);
                g2.drawString("Use LEFT and RIGHT arrow keys to move paddle.", WINDOW_W/2 - 160, WINDOW_H/2 - 40);

            }

            g2.dispose();  // Clean up Graphics2D resources
        }

        /**
         * Helper method to draw interactive buttons with styling
         */
        private void drawButton(Graphics2D g2, Rectangle rect, String text, Color color) {
            // Draw button background with rounded corners
            g2.setColor(color);
            g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

            // Draw button border (white outline)
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

            // Draw centered button text
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            FontMetrics fm = g2.getFontMetrics();
            int textX = rect.x + (rect.width - fm.stringWidth(text)) / 2;
            int textY = rect.y + ((rect.height - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(text, textX, textY);
        }

        // =====================================================
        // EVENT HANDLERS - INTERFACE IMPLEMENTATION
        // =====================================================
        
        /**
         * INTERFACE: KeyListener implementation
         * Handles keyboard input for game controls
         */
        @Override 
        public void keyPressed(KeyEvent e) { 
            // Arrow key controls for paddle movement
            if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = true; 
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = true; 
            
            // ESC key to toggle pause
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE && gameStarted && !gameOver && !gameWin) {
                gamePaused = !gamePaused;
            }
            
            // F key to toggle fullscreen mode (resize ability)
            if (e.getKeyCode() == KeyEvent.VK_F && gameStarted) {
                toggleFullscreen();
            }
            
            // SPACE key to restart from game over/win screen
            if (e.getKeyCode() == KeyEvent.VK_SPACE && (gameOver || gameWin)) restartGame(); 
        }
        
        @Override 
        public void keyReleased(KeyEvent e) { 
            // Stop paddle movement when key is released
            if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = false; 
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = false; 
        }
        
        @Override 
        public void keyTyped(KeyEvent e) {
            // Not used in this game
        }

        /**
         * INTERFACE: MouseListener implementation
         * Handles mouse clicks on interactive buttons
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            // Scale mouse coordinates to match game coordinate system
            Point scaledPoint = new Point((int)(e.getX() / scaleX), (int)(e.getY() / scaleY));
            
            // Check if "START GAME" button was clicked
            if (!gameStarted && startButtonRect.contains(scaledPoint)) {
                startGame();
            }
            
            // Check if "PLAY AGAIN" button was clicked
            if ((gameOver || gameWin) && playAgainButtonRect.contains(scaledPoint)) {
                restartGame();
            }
        }

        @Override public void mousePressed(MouseEvent e) {}   // Not used
        @Override public void mouseReleased(MouseEvent e) {}  // Not used
        @Override public void mouseEntered(MouseEvent e) {}   // Not used
        @Override public void mouseExited(MouseEvent e) {}    // Not used

        /**
         * Start the game - initialize timer and set game state
         */
        private void startGame() {
            gameStarted = true;
            gamePaused = false;
            timer.start();  // Begin game loop
            repaint();
        }

        /**
         * Toggle window resizing capability (fullscreen-like behavior)
         */
        private void toggleFullscreen() {
            if (parentFrame != null) {
                parentFrame.setResizable(!parentFrame.isResizable());
            }
        }

        /**
         * Reset game to initial state for replay
         * Clears all game state and regenerates bricks
         */
        private void restartGame() {
            // Reset game objects
            bricks.clear();
            generateBricks();
            
            // Reset game statistics
            score = 0;
            lives = 3;
            
            // Reset game state flags
            gameOver = false;
            gameWin = false;
            
            // Reset object positions
            resetPositions();
            
            // Restart game loop timer
            timer.start();
        }
    }

    // =====================================================
    // APPLICATION ENTRY POINT
    // =====================================================
    
    /**
     * Main method - Entry point for the Java application
     * 
     * Uses SwingUtilities.invokeLater() to ensure GUI operations run on the Event Dispatch Thread (EDT)
     * This is THREAD-SAFE and best practice for Swing applications
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create the main window frame
            JFrame frame = new JFrame("Brick Breaker");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // Create and add the game board panel
            GameBoard board = new GameBoard(frame);
            frame.add(board);
            
            // Configure window properties
            frame.pack();              // Size window to preferred size of components
            frame.setResizable(true);  // Allow player to resize window
            frame.setLocationRelativeTo(null);  // Center on screen
            frame.setVisible(true);    // Display the window
        });
    }
}
