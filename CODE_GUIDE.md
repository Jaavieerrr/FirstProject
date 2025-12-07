# Brick Breaker Game - Code Guide (Student Explanation)

Hey! So here's what the code does, broken down in a simple way. I'll show you each part and explain what's happening.

---

## 1. The Game Object (Lines 12-27)

```java
abstract static class GameObject {
    double x, y;
    int width, height;

    public GameObject(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle2D getBounds2D() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    public abstract void draw(Graphics2D g);
}
```

**What's this?** This is like a template for everything in the game that we can see. We made it `abstract` so we can't create it directly, but we can use it as a base for other things.

**What does it do?**
- Stores position (`x, y`) and size (`width, height`)
- Has a `getBounds2D()` method that creates a rectangle around the object (we use this to check collisions)
- Has an `abstract` `draw()` method that says "every object that uses this template MUST know how to draw itself"

---

## 2. The Paddle (Lines 29-44)

```java
static class Paddle extends GameObject {
    final double speed = 8.0;

    public Paddle(double x, double y) {
        super(x, y, 100, 16);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(new Color(40, 130, 200));
        g.fillRoundRect((int)x, (int)y, width, height, 10, 10);
    }

    public void moveLeft() { x = Math.max(0, x - speed); }
    public void moveRight(int panelWidth) { x = Math.min(panelWidth - width, x + speed); }
}
```

**What's this?** The blue paddle that you move at the bottom of the screen. It's a `Paddle` and it `extends GameObject` (meaning it inherits from it).

**What does it do?**
- `speed = 8.0` - each time you press the arrow key, it moves 8 pixels
- In the constructor, we set it to be 100 pixels wide and 16 pixels tall
- The `draw()` method draws it as a blue rounded rectangle
- `moveLeft()` and `moveRight()` move it left/right, but they stop it from going off-screen using `Math.max()` and `Math.min()`

---

## 3. The Ball (Lines 46-66)

```java
static class Ball extends GameObject {
    double dx = 3.6;
    double dy = -3.6;

    public Ball(double x, double y) {
        super(x, y, 14, 14);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(new Color(200, 50, 50));
        g.fillOval((int)x, (int)y, width, height);
    }

    public void update() { x += dx; y += dy; }

    public void reverseX() { dx = -dx; }
    public void reverseY() { dy = -dy; }
}
```

**What's this?** The red ball that bounces around. It also extends `GameObject`.

**What does it do?**
- `dx` and `dy` are the velocities (how fast it moves left/right and up/down)
- It's 14x14 pixels (a small circle)
- The `draw()` method draws it as a red oval
- `update()` moves the ball each frame by adding `dx` and `dy` to its position
- `reverseX()` and `reverseY()` flip the direction when it hits something (that's how bouncing works!)

---

## 4. The Brick (Lines 68-84)

```java
static class Brick extends GameObject {
    boolean destroyed = false;
    Color color;

    public Brick(int x, int y, Color color) {
        super(x, y, 56, 20);
        this.color = color;
    }

    @Override
    public void draw(Graphics2D g) {
        if (!destroyed) {
            g.setColor(color);
            g.fillRect((int)x, (int)y, width, height);
            g.setColor(Color.DARK_GRAY);
            g.drawRect((int)x, (int)y, width, height);
        }
    }
}
```

**What's this?** The colorful bricks you break. Also extends `GameObject`.

**What does it do?**
- Has a `destroyed` flag to track if it's been hit
- Has a `color` so each row of bricks can be different
- It's 56x20 pixels
- The `draw()` method only draws it if it's NOT destroyed
- It draws the brick as a filled rectangle with a dark border

---

## 5. The Game Board (Lines 86-529)

This is the big one! It's where all the game logic lives.

### 5.1 Setting Up (Constructor - Lines 111-148)

```java
public GameBoard(JFrame frame) {
    parentFrame = frame;
    
    setPreferredSize(new Dimension(WINDOW_W, WINDOW_H));
    setBackground(Color.BLACK);
    setFocusable(true);

    paddle = new Paddle((WINDOW_W - 100) / 2.0, WINDOW_H - 70);
    ball = new Ball(WINDOW_W / 2.0 - 7, WINDOW_H / 2.0);

    generateBricks();
    
    timer = new Timer(16, e -> gameLoop());
    
    // ... rest of setup
}
```

**What does it do?**
- Creates the paddle in the middle-bottom
- Creates the ball in the center
- Generates all 50 bricks
- Sets up a timer that runs `gameLoop()` every 16 milliseconds (that's ~60 times per second, which is the frame rate)

### 5.2 Game Loop (Lines 177-196)

```java
private void gameLoop() {
    if (gameOver || gameWin || !gameStarted || gamePaused) return;

    if (leftPressed) {
        paddle.x = Math.max(0, paddle.x - paddle.speed);
    }
    if (rightPressed) {
        paddle.x = Math.min(WINDOW_W - paddle.width, paddle.x + paddle.speed);
    }

    ball.update();

    checkWallCollision();
    checkPaddleCollision();
    checkBrickCollision();
    checkGameState();

    repaint();
}
```

**What does it do?** This runs 60 times per second. It:
1. Checks if the game should be running
2. Moves the paddle if arrow keys are pressed
3. Updates the ball's position
4. Checks for collisions with walls, paddle, and bricks
5. Checks if the game is won/lost
6. Redraws everything on screen

### 5.3 Wall Collision (Lines 198-225)

```java
private void checkWallCollision() {
    // Left wall
    if (ball.x <= 0) { 
        ball.x = 0; 
        ball.reverseX(); 
        Toolkit.getDefaultToolkit().beep(); 
    }
    
    // Right wall
    if (ball.x + ball.width >= WINDOW_W) { 
        ball.x = WINDOW_W - ball.width; 
        ball.reverseX(); 
        Toolkit.getDefaultToolkit().beep(); 
    }
    
    // Top wall
    if (ball.y <= 0) { 
        ball.y = 0; 
        ball.reverseY(); 
        Toolkit.getDefaultToolkit().beep(); 
    }
    
    // Bottom (you lose a life)
    if (ball.y > WINDOW_H) {
        lives--;
        if (lives <= 0) {
            gameOver = true;
            timer.stop();
        } else {
            resetPositions();
        }
    }
}
```

**What does it do?** Checks if the ball hit any wall:
- Left/right walls: bounce the ball back (`reverseX()`)
- Top wall: bounce down (`reverseY()`)
- Bottom: you lose a life! If you're out of lives, game over.

### 5.4 Brick Collision (Lines 279-359)

```java
private void checkBrickCollision() {
    Rectangle2D ballRect = ball.getBounds2D();
    Brick collidedBrick = null;
    double closestDistance = Double.MAX_VALUE;

    // Find the closest brick
    for (Brick b : bricks) {
        if (b.destroyed) continue;

        Rectangle2D brickRect = b.getBounds2D();
        if (ballRect.intersects(brickRect)) {
            // Calculate distance...
            double distance = Math.sqrt(
                Math.pow(ballCenterX - brickCenterX, 2) + 
                Math.pow(ballCenterY - brickCenterY, 2)
            );

            if (distance < closestDistance) {
                closestDistance = distance;
                collidedBrick = b;
            }
        }
    }

    if (collidedBrick != null) {
        // Bounce the ball
        // Mark brick as destroyed
        collidedBrick.destroyed = true;
        score += 100;
    }
}
```

**What does it do?**
- Loops through all bricks and finds which ones the ball is hitting
- Finds the CLOSEST brick (so the ball doesn't go through multiple bricks at once)
- Bounces the ball off that brick
- Marks the brick as destroyed
- Adds 100 points to your score

### 5.5 Drawing (Lines 361-420)

```java
@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    updateScale();

    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Calculate centered position
    int offsetX = (panelWidth - gameWidth) / 2;
    int offsetY = (panelHeight - gameHeight) / 2;

    g2.translate(offsetX, offsetY);
    g2.scale(scaleX, scaleY);

    // Draw all bricks
    for (Brick b : bricks) b.draw(g2);
    paddle.draw(g2);
    ball.draw(g2);

    // Draw score and lives
    g2.drawString("Score: " + score, 12, 20);
    g2.drawString("Lives: " + lives, WINDOW_W - 90, 20);

    // Draw buttons and messages
    if (!gameStarted) {
        drawButton(g2, startButtonRect, "START GAME", new Color(40, 180, 60));
    }
    // ... more drawing code
}
```

**What does it do?**
- Called every time the screen needs to redraw
- Centers and scales the game to fit the window
- Draws all the bricks, paddle, and ball (by calling their `draw()` methods)
- Draws the score and lives on screen
- Draws buttons and game state messages

### 5.6 Button Clicks (Lines 431-452)

```java
@Override
public void mouseClicked(MouseEvent e) {
    // Calculate adjusted coordinates
    double adjustedX = (e.getX() - offsetX) / scaleX;
    double adjustedY = (e.getY() - offsetY) / scaleY;
    Point scaledPoint = new Point((int)adjustedX, (int)adjustedY);

    // Check if START button was clicked
    if (!gameStarted && startButtonRect.contains(scaledPoint)) {
        startGame();
    }

    // Check if PLAY AGAIN button was clicked
    if ((gameOver || gameWin) && playAgainButtonRect.contains(scaledPoint)) {
        restartGame();
    }
}
```

**What does it do?** When you click the mouse:
- Adjusts the click position to account for window scaling
- Checks if you clicked the START GAME button
- Checks if you clicked the PLAY AGAIN button

### 5.7 Keyboard Input (Lines 455-471)

```java
@Override
public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = true;
    if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = true;

    if (e.getKeyCode() == KeyEvent.VK_ESCAPE && gameStarted && !gameOver && !gameWin) {
        gamePaused = !gamePaused;  // Toggle pause
    }

    if (e.getKeyCode() == KeyEvent.VK_SPACE && (gameOver || gameWin)) 
        restartGame();
}

@Override
public void keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = false;
    if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = false;
}
```

**What does it do?** Handles keyboard input:
- LEFT/RIGHT arrow keys move the paddle
- ESC key pauses/unpauses the game
- SPACE restarts the game

---

## 6. Main Method (Lines 505-529)

```java
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        JFrame frame = new JFrame("Brick Breaker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GameBoard board = new GameBoard(frame);
        frame.add(board);

        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    });
}
```

**What does it do?** This is where the program starts:
- Creates a window (JFrame)
- Creates the game board and puts it in the window
- Shows the window on screen

---

## Quick Summary

The game works like this:

1. **GameObject** - Base class for everything you see (Paddle, Ball, Brick)
2. **Paddle** - Your blue controllable paddle at the bottom
3. **Ball** - Red ball that bounces around
4. **Brick** - Colorful bricks you break
5. **GameBoard** - The main controller that:
   - Runs the game loop 60 times per second
   - Handles all collisions
   - Draws everything on screen
   - Responds to keyboard and mouse input

That's it! Pretty straightforward once you break it down. 🎮

