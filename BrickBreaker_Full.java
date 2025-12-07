import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class BrickBreaker_Full {

    static final int WINDOW_W = 640;
    static final int WINDOW_H = 520;

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

    static class GameBoard extends JPanel implements KeyListener, MouseListener {
        Paddle paddle;
        Ball ball;
        ArrayList<Brick> bricks = new ArrayList<>();
        Timer timer;

        boolean leftPressed = false;
        boolean rightPressed = false;

        int score = 0;
        int lives = 3;

        boolean gameOver = false;
        boolean gameWin = false;
        boolean gameStarted = false;
        boolean gamePaused = false;

        Image background;
        Rectangle startButtonRect;
        Rectangle playAgainButtonRect;

        final String BACKGROUND_PATH = "file:/mnt/data/3245b020-133f-4650-b36b-e5490edb53b7.png";

        JFrame parentFrame;
        double scaleX = 1.0;
        double scaleY = 1.0;

        public GameBoard(JFrame frame) {
            parentFrame = frame;

            setPreferredSize(new Dimension(WINDOW_W, WINDOW_H));
            setBackground(Color.BLACK);
            setFocusable(true);

            paddle = new Paddle((WINDOW_W - 100) / 2.0, WINDOW_H - 70);
            ball = new Ball(WINDOW_W / 2.0 - 7, WINDOW_H / 2.0);

            generateBricks();

            try {
                background = new ImageIcon(BACKGROUND_PATH).getImage();
            } catch (Exception e) {
                background = null;
            }

            timer = new Timer(16, e -> gameLoop());

            startButtonRect = new Rectangle(WINDOW_W / 2 - 75, WINDOW_H / 2, 150, 50);
            playAgainButtonRect = new Rectangle(WINDOW_W / 2 - 75, WINDOW_H / 2 + 70, 150, 50);

            KeyListener keyListener = this;
            MouseListener mouseListener = this;
            addKeyListener(keyListener);
            addMouseListener(mouseListener);

            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    updateScale();
                }
            });
        }

        private void updateScale() {
            double windowWidth = getWidth();
            double windowHeight = getHeight();

            double scaleFromWidth = windowWidth / WINDOW_W;
            double scaleFromHeight = windowHeight / WINDOW_H;

            double uniformScale = Math.min(scaleFromWidth, scaleFromHeight);

            scaleX = uniformScale;
            scaleY = uniformScale;
        }

        private void generateBricks() {
            bricks.clear();

            Color[] rowColors = {
                new Color(220, 80, 80),
                new Color(220, 160, 60),
                new Color(120, 200, 120),
                new Color(90, 160, 220),
                new Color(180, 120, 220)
            };

            int offsetX = (int)(40 * scaleX);
            int offsetY = (int)(60 * scaleY);
            int brickSpacingX = (int)(60 * scaleX);
            int brickSpacingY = (int)(28 * scaleY);
            int brickWidth = (int)(56 * scaleX);
            int brickHeight = (int)(20 * scaleY);

            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 10; col++) {
                    Brick brick = new Brick(
                        offsetX + col * brickSpacingX,
                        offsetY + row * brickSpacingY,
                        rowColors[row]
                    );
                    brick.width = brickWidth;
                    brick.height = brickHeight;
                    bricks.add(brick);
                }
            }
        }

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

        private void checkWallCollision() {
            if (ball.x <= 0) {
                ball.x = 0;
                ball.reverseX();
                Toolkit.getDefaultToolkit().beep();
            }

            if (ball.x + ball.width >= WINDOW_W) {
                ball.x = WINDOW_W - ball.width;
                ball.reverseX();
                Toolkit.getDefaultToolkit().beep();
            }

            if (ball.y <= 0) {
                ball.y = 0;
                ball.reverseY();
                Toolkit.getDefaultToolkit().beep();
            }

            if (ball.y > WINDOW_H) {
                lives--;
                Toolkit.getDefaultToolkit().beep();
                if (lives <= 0) {
                    gameOver = true;
                    timer.stop();
                } else {
                    resetPositions();
                }
            }
        }

        private void resetPositions() {
            paddle.x = (WINDOW_W - paddle.width) / 2.0;
            paddle.y = WINDOW_H - 70;

            ball.x = WINDOW_W / 2.0 - ball.width / 2.0;
            ball.y = WINDOW_H / 2.0;

            double speed = 3.6;
            ball.dx = speed * (Math.random() > 0.5 ? 1 : -1);
            ball.dy = -speed;
        }

        private void checkPaddleCollision() {
            Rectangle2D ballRect = ball.getBounds2D();
            Rectangle2D paddleRect = paddle.getBounds2D();

            if (ballRect.intersects(paddleRect)) {
                ball.y = paddle.y - ball.height - 1;

                double paddleCenter = paddle.x + paddle.width / 2.0;
                double relativeIntersect = (ball.x + ball.width / 2.0) - paddleCenter;
                double normalized = relativeIntersect / (paddle.width / 2.0);

                double maxAngle = Math.toRadians(75);
                double angle = normalized * maxAngle;
                double speed = Math.hypot(ball.dx, ball.dy);

                ball.dx = speed * Math.sin(angle);
                ball.dy = -Math.abs(speed * Math.cos(angle));

                Toolkit.getDefaultToolkit().beep();
            }
        }

        private void checkBrickCollision() {
            Rectangle2D ballRect = ball.getBounds2D();
            Brick collidedBrick = null;
            double closestDistance = Double.MAX_VALUE;

            for (Brick b : bricks) {
                if (b.destroyed) continue;

                Rectangle2D brickRect = b.getBounds2D();
                if (ballRect.intersects(brickRect)) {
                    double brickCenterX = brickRect.getCenterX();
                    double brickCenterY = brickRect.getCenterY();
                    double ballCenterX = ballRect.getCenterX();
                    double ballCenterY = ballRect.getCenterY();

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
                Rectangle2D brickRect = collidedBrick.getBounds2D();
                Rectangle2D intersection = ballRect.createIntersection(brickRect);

                double overlapX = intersection.getWidth();
                double overlapY = intersection.getHeight();

                if (overlapX < overlapY) {
                    if (ball.dx > 0) {
                        ball.x = brickRect.getMinX() - ball.width - 1;
                    } else {
                        ball.x = brickRect.getMaxX() + 1;
                    }
                    ball.reverseX();
                } else {
                    if (ball.dy > 0) {
                        ball.y = brickRect.getMinY() - ball.height - 1;
                    } else {
                        ball.y = brickRect.getMaxY() + 1;
                    }
                    ball.reverseY();
                }

                collidedBrick.destroyed = true;
                score += 100;
                Toolkit.getDefaultToolkit().beep();
            }
        }

        private void checkGameState() {
            boolean allGone = true;
            for (Brick b : bricks) if (!b.destroyed) { allGone = false; break; }
            if (allGone) {
                gameWin = true;
                timer.stop();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            updateScale();

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int gameWidth = (int)(WINDOW_W * scaleX);
            int gameHeight = (int)(WINDOW_H * scaleY);
            int offsetX = (panelWidth - gameWidth) / 2;
            int offsetY = (panelHeight - gameHeight) / 2;

            g2.translate(offsetX, offsetY);
            g2.scale(scaleX, scaleY);

            if (background != null) {
                g2.drawImage(background, 0, 0, WINDOW_W, WINDOW_H, null);
            }

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));

            for (Brick b : bricks) b.draw(g2);
            paddle.draw(g2);
            ball.draw(g2);

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));

            g2.drawString("Score: " + score, 12, 20);
            g2.drawString("Lives: " + lives, WINDOW_W - 90, 20);

            if (!gameStarted) {
                drawButton(g2, startButtonRect, "START GAME", new Color(40, 180, 60));
            }

            if (gameOver || gameWin) {
                drawButton(g2, playAgainButtonRect, "PLAY AGAIN", new Color(40, 180, 60));
            }

            if (gameOver) {
                g2.setFont(new Font("SansSerif", Font.BOLD, 36));
                g2.setColor(new Color(220, 40, 40));
                g2.drawString("GAME OVER", WINDOW_W / 2 - 120, WINDOW_H / 2 - 80);
            }

            if (gameWin) {
                g2.setFont(new Font("SansSerif", Font.BOLD, 36));
                g2.setColor(new Color(40, 200, 80));
                g2.drawString("YOU WIN!", WINDOW_W / 2 - 90, WINDOW_H / 2 - 80);
            }

            if (gamePaused && gameStarted && !gameOver && !gameWin) {
                g2.setFont(new Font("SansSerif", Font.BOLD, 36));
                g2.setColor(new Color(255, 200, 0));
                g2.drawString("PAUSED", WINDOW_W / 2 - 80, WINDOW_H / 2);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
                g2.drawString("Press ESC to resume", WINDOW_W / 2 - 85, WINDOW_H / 2 + 50);
            }

            if (!gameStarted) {
                g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
                g2.setColor(Color.GRAY);
                g2.drawString("Use LEFT and RIGHT arrow keys to move paddle.", WINDOW_W / 2 - 160, WINDOW_H / 2 - 40);
            }

            g2.dispose();
        }

        private void drawButton(Graphics2D g2, Rectangle rect, String text, Color color) {
            g2.setColor(color);
            g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            FontMetrics fm = g2.getFontMetrics();
            int textX = rect.x + (rect.width - fm.stringWidth(text)) / 2;
            int textY = rect.y + ((rect.height - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(text, textX, textY);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = true;
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = true;

            if (e.getKeyCode() == KeyEvent.VK_ESCAPE && gameStarted && !gameOver && !gameWin) {
                gamePaused = !gamePaused;
            }

            if (e.getKeyCode() == KeyEvent.VK_F && gameStarted) {
                toggleFullscreen();
            }

            if (e.getKeyCode() == KeyEvent.VK_SPACE && (gameOver || gameWin)) restartGame();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = false;
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = false;
        }

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void mouseClicked(MouseEvent e) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int gameWidth = (int)(WINDOW_W * scaleX);
            int gameHeight = (int)(WINDOW_H * scaleY);
            int offsetX = (panelWidth - gameWidth) / 2;
            int offsetY = (panelHeight - gameHeight) / 2;

            double adjustedX = (e.getX() - offsetX) / scaleX;
            double adjustedY = (e.getY() - offsetY) / scaleY;
            Point scaledPoint = new Point((int)adjustedX, (int)adjustedY);

            if (!gameStarted && startButtonRect.contains(scaledPoint)) {
                startGame();
            }

            if ((gameOver || gameWin) && playAgainButtonRect.contains(scaledPoint)) {
                restartGame();
            }
        }

        @Override public void mousePressed(MouseEvent e) {}
        @Override public void mouseReleased(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}

        private void startGame() {
            gameStarted = true;
            gamePaused = false;
            timer.start();
            repaint();
        }

        private void toggleFullscreen() {
            if (parentFrame != null) {
                parentFrame.setResizable(!parentFrame.isResizable());
            }
        }

        private void restartGame() {
            bricks.clear();
            generateBricks();

            score = 0;
            lives = 3;

            gameOver = false;
            gameWin = false;

            resetPositions();

            timer.start();
        }
    }

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
}
