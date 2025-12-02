# Brick Breaker Game - System Documentation

A fun and interactive brick breaker game built with **Java Swing**! This project demonstrates professional Object-Oriented Programming (OOP) principles and clean code architecture.

---

## 📋 Table of Contents

- [System Overview](#system-overview)
- [Architecture & OOP Design](#architecture--oop-design)
- [Requirements](#requirements)
- [How to Compile](#how-to-compile)
- [How to Run](#how-to-run)
- [Game Features](#game-features)
- [Game Controls](#game-controls)
- [Game Mechanics](#game-mechanics)
- [Code Structure](#code-structure)
- [Technical Details](#technical-details)

---

## 🎮 System Overview

**Brick Breaker_Full** is a complete Swing-based game featuring:
- A resizable game window with responsive scaling
- Physics-based ball bouncing and collision detection
- Interactive UI with buttons and game state management
- Score tracking and lives system
- Color-coded brick grid with 50 destructible bricks

The game demonstrates enterprise-level Java practices with clear separation of concerns and maintainable code architecture.

---

## 🏗️ Architecture & OOP Design

This project showcases **five core OOP principles**:

### 1. **ABSTRACTION**
```java
abstract static class GameObject {
    double x, y;
    int width, height;
    public abstract void draw(Graphics2D g);
}
```
- `GameObject` defines a common interface for all drawable entities
- Forces subclasses to implement their own `draw()` method

### 2. **INHERITANCE**
- `Paddle extends GameObject`
- `Ball extends GameObject`
- `Brick extends GameObject`

All three classes inherit position, dimensions, and collision detection from the abstract parent.

### 3. **POLYMORPHISM**
Each subclass implements `draw()` differently:
- **Paddle**: Blue rounded rectangle
- **Ball**: Red circle (oval)
- **Brick**: Colored rectangles with borders

Same method name, different behavior!

### 4. **ENCAPSULATION**
- Game state (score, lives, flags) is private to `GameBoard`
- Each class encapsulates its own data and methods
- Movement logic is contained within `Paddle` and `Ball`

### 5. **COMPOSITION**
```java
class GameBoard {
    Paddle paddle;           // Contains a Paddle instance
    Ball ball;               // Contains a Ball instance
    ArrayList<Brick> bricks; // Contains multiple Bricks
}
```
`GameBoard` composes multiple objects into a cohesive game system.

### **INTERFACE IMPLEMENTATION**
```java
class GameBoard extends JPanel implements KeyListener, MouseListener
```
Responds to keyboard and mouse events through interface contracts.

---

## 💻 Requirements

- **Java 8 or higher** (tested with Java 8)
- **No external dependencies** - uses only Java standard library
- **Windows/Mac/Linux** - cross-platform compatible

### System Requirements
- Minimum 512 MB RAM
- Any processor (no GPU required)
- Display with 800x600+ resolution

---

## 🔧 How to Compile

### Quick Compile (Java 8 compatible)
```bash
javac -target 8 -source 8 BrickBreaker_Full.java
```

### Standard Compile
```bash
javac BrickBreaker_Full.java
```

### Windows (PowerShell)
```powershell
cd "C:\Users\YourUsername\Path\To\BrickBreaker"
javac -target 8 -source 8 BrickBreaker_Full.java
```

### Successful Compilation
You should see a new `BrickBreaker_Full.class` file appear in the directory.

---

## 🚀 How to Run

### Option 1: Run Directly from Terminal
```bash
java BrickBreaker_Full
```

### Option 2: Use Batch File (Windows)
```bash
run.bat
```

### Option 3: Use Shell Script (Mac/Linux)
```bash
./run.sh
```

### Troubleshooting

**Error: "UnsupportedClassVersionError"**
- Problem: Compiled with newer Java, running on older Java
- Solution: Recompile with `javac -target 8 -source 8 BrickBreaker_Full.java`

**Error: "NoClassDefFoundError"**
- Problem: Missing class file
- Solution: Ensure you're in the correct directory and compiled successfully

**Game Window Won't Appear**
- Ensure Java can access your display
- Try running from a terminal instead of double-clicking

---

## 🎯 Game Features

### Core Features
- ✅ **Resizable Window** - Stretch and resize dynamically; all elements scale proportionally
- ✅ **Start Screen** - Click "START GAME" button to begin
- ✅ **Pause/Resume** - Press **ESC** during gameplay to pause and resume
- ✅ **Responsive Controls** - Use **Left/Right Arrow Keys** to move the paddle
- ✅ **Score System** - Earn 100 points for each brick destroyed
- ✅ **Lives System** - Start with 3 lives
- ✅ **Win Condition** - Destroy all 50 bricks to win
- ✅ **Play Again** - Click "PLAY AGAIN" button after game ends to restart
- ✅ **Physics Engine** - Realistic ball bouncing with angle variation

### Visual Elements
- **Paddle**: Blue rounded rectangle at the bottom
- **Ball**: Red circle that bounces around
- **Bricks**: 50 colorful bricks in 5 rows:
  - Row 1: Red
  - Row 2: Orange
  - Row 3: Green
  - Row 4: Blue
  - Row 5: Purple

---

## 🎮 Game Controls

| Key | Action |
|-----|--------|
| **Left Arrow** | Move paddle left |
| **Right Arrow** | Move paddle right |
| **ESC** | Pause/Resume during gameplay |
| **F** | Toggle window resize mode |
| **SPACE** | Restart game (at game over/win screen) |
| **Mouse Click** | Click buttons (START GAME, PLAY AGAIN) |

---

## ⚙️ Game Mechanics

### Ball Physics
- **Velocity**: Ball moves at constant speed with direction vector (dx, dy)
- **Bouncing**: Ball reverses direction when hitting walls, paddle, or bricks
- **Paddle Angle**: Ball's bounce angle depends on where it hits the paddle
  - Hit center → Bounces straight up
  - Hit edge → Bounces at angle up to ±75°

### Collision Detection
1. **Wall Collisions**: Ball bounces off top, left, right walls
2. **Paddle Collision**: Ball angle adjusts based on hit position
3. **Brick Collision**: Ball bounces; brick disappears; score increases
4. **Floor Collision**: Ball falls below paddle → lose a life

### Win/Lose Conditions
- **Lose**: All 3 lives depleted
- **Win**: All 50 bricks destroyed
- **Restart**: Click "PLAY AGAIN" to reset and try again

### Scoring
- 100 points per brick destroyed
- Total possible score: 5,000 points (50 bricks × 100)

---

## 📁 Code Structure

```
BrickBreaker_Full.java
├── Imports (Swing, AWT, Utilities)
│
├── Main Class: BrickBreaker_Full
│   └── main() - Entry point
│
├── Inner Class: GameObject (Abstract)
│   ├── Fields: x, y, width, height
│   ├── getBounds2D() - Collision detection
│   └── abstract draw() - Polymorphic rendering
│
├── Inner Class: Paddle (extends GameObject)
│   ├── Fields: speed
│   ├── draw() - Blue rounded rectangle
│   ├── moveLeft()
│   └── moveRight()
│
├── Inner Class: Ball (extends GameObject)
│   ├── Fields: dx, dy (velocity)
│   ├── draw() - Red oval
│   ├── update() - Position movement
│   ├── reverseX() - Bounce horizontally
│   └── reverseY() - Bounce vertically
│
├── Inner Class: Brick (extends GameObject)
│   ├── Fields: destroyed, color
│   └── draw() - Colored rectangle (if not destroyed)
│
└── Inner Class: GameBoard (extends JPanel)
    ├── Game Objects: paddle, ball, bricks[]
    ├── Game State: score, lives, gameOver, gameWin, gamePaused
    ├── Game Loop: gameLoop() - Runs at 60 FPS
    ├── Collision Detection:
    │   ├── checkWallCollision()
    │   ├── checkPaddleCollision()
    │   ├── checkBrickCollision()
    │   └── checkGameState()
    ├── Rendering: paintComponent()
    ├── Event Handlers:
    │   ├── KeyListener implementation
    │   └── MouseListener implementation
    └── Game Control:
        ├── startGame()
        ├── restartGame()
        └── resetPositions()
```

---

## 🔬 Technical Details

### Game Loop
```
Timer triggers every 16ms (≈60 FPS)
│
├─ Update paddle position (based on key input)
├─ Update ball position (x += dx, y += dy)
├─ Check all collisions
├─ Check win/lose conditions
└─ Repaint the screen
```

### Coordinate System
- **Origin (0,0)**: Top-left corner
- **X-axis**: Left (0) to Right (window width)
- **Y-axis**: Top (0) to Bottom (window height)

### Scaling System
- Dynamic scaling factors: `scaleX` and `scaleY`
- Applied to all game objects when window is resized
- Maintains aspect ratio and responsiveness

### Collision Detection Algorithm
1. Get bounding rectangles for both objects
2. Check if rectangles intersect using `Rectangle2D.intersects()`
3. If collision detected, get intersection rectangle
4. Compare width vs height of intersection:
   - Width < Height → Side hit → Reverse X velocity
   - Width ≥ Height → Top/Bottom hit → Reverse Y velocity

### Rendering Pipeline
```
paintComponent()
│
├─ Enable anti-aliasing
├─ Apply scaling transformation
├─ Draw background (if available)
├─ Draw all bricks (polymorphic draw calls)
├─ Draw paddle (polymorphic draw call)
├─ Draw ball (polymorphic draw call)
├─ Draw HUD (score, lives)
├─ Draw buttons (start, play again)
├─ Draw game state messages (paused, game over, win)
└─ Cleanup graphics resources
```

### Input Handling
- **Keyboard**: Tracked with boolean flags (leftPressed, rightPressed)
- **Mouse**: Click detection with scaled coordinates

---

## 📊 Performance Metrics

| Metric | Value |
|--------|-------|
| Frame Rate | 60 FPS (16ms/frame) |
| Game Objects | ~55 (1 paddle + 1 ball + 50 bricks + ui) |
| Memory Usage | ~5-10 MB |
| Startup Time | <1 second |
| Compile Time | ~1-2 seconds |

---

## 🎓 Learning Points

This project demonstrates:
- ✅ Object-Oriented Programming (OOP) principles
- ✅ Java Swing GUI development
- ✅ Event-driven programming
- ✅ Game loop implementation
- ✅ Collision detection algorithms
- ✅ Physics simulation (bouncing ball)
- ✅ State management
- ✅ Responsive/scalable UI design
- ✅ Code organization and comments
- ✅ Java best practices

---

## 🔄 Game Development Workflow

### Development Cycle
1. **Design Phase**: Plan classes and responsibilities
2. **Implementation**: Code GameObject hierarchy
3. **Testing**: Verify collision detection
4. **Polish**: Add UI, sounds, animations
5. **Optimization**: Improve performance

### Extending the Game
You can easily extend this project:
- Add power-ups (slow ball, paddle expansion)
- Add different brick types (unbreakable, bonus bricks)
- Add sound effects (using Java Audio API)
- Add levels with different brick patterns
- Add score persistence (file I/O)
- Add multiplayer support

---

## 📝 File Manifest

| File | Purpose |
|------|---------|
| `BrickBreaker_Full.java` | Main source code (fully commented) |
| `BrickBreaker_Full.class` | Compiled bytecode (generated after compilation) |
| `README.md` | This documentation |
| `run.bat` | Windows batch script to compile and run |
| `run.sh` | Unix/Linux shell script to compile and run |

---

## ✨ Credits & Notes

- **Language**: Java 8+
- **GUI Framework**: Java Swing (AWT)
- **Architecture**: Object-Oriented with Design Patterns
- **Code Comments**: Comprehensive inline documentation
- **No External Dependencies**: Pure Java implementation

---

## 🤝 Contributing

Feel free to enhance this project:
- Add new features
- Improve graphics
- Optimize performance
- Fix bugs
- Add new game modes

---

## 📞 Support & Questions

If you encounter issues:
1. Ensure Java 8+ is installed
2. Verify compilation succeeds
3. Check console output for error messages
4. Review the code comments for implementation details
5. Test in a different terminal or IDE

---

**Happy Gaming! 🎮🎯**

Last Updated: December 2, 2025
