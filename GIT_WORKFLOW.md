# Git Workflow Guide - Keeping Your Project Updated

## Quick Reference Commands

### 1. **Check Status**
```bash
git status
```
Shows what files have changed and what branch you're on.

### 2. **Pull Latest Updates from GitHub**
```bash
git pull origin main
```
Downloads latest changes from GitHub and merges them into your local project.

### 3. **Save Your Changes (Stage Files)**
```bash
git add .
```
Stages all modified files for commit.

### 4. **Commit Changes (Save Locally)**
```bash
git commit -m "Your message describing changes"
```
Example:
```bash
git commit -m "Fix ball collision detection and center fullscreen display"
```

### 5. **Push Changes to GitHub**
```bash
git push origin main
```
Uploads your committed changes to GitHub.

---

## Complete Workflow

### **For Each Work Session:**

#### **Step 1: Start Fresh (Pull Latest)**
```bash
git pull origin main
```
Always start by pulling in case someone else made changes or you're on a different machine.

#### **Step 2: Make Your Changes**
- Edit your code files
- Test your changes
- Compile and verify everything works

#### **Step 3: Check What Changed**
```bash
git status
```
Review which files were modified.

#### **Step 4: Stage Your Changes**
```bash
git add .
```
Stages all changes for commit.

#### **Step 5: Commit Your Changes**
```bash
git commit -m "Descriptive message about your changes"
```
Examples of good messages:
- `"Fix ball bouncing off bricks"`
- `"Center game board on fullscreen"`
- `"Add collision detection improvements"`
- `"Update README with new features"`

#### **Step 6: Push to GitHub**
```bash
git push origin main
```
Uploads your changes to GitHub.

---

## Important Notes for This Project

### **Files to Commit (Include)**
- ✅ `BrickBreaker_Full.java` - Source code
- ✅ `README.md` - Documentation
- ✅ `run.bat` - Windows batch script
- ✅ `run.sh` - Unix shell script

### **Files to NOT Commit (Exclude)**
- ❌ `*.class` files (compiled bytecode - generated automatically)
- ❌ `*.project` files (IDE-specific)
- ❌ `.classpath` (IDE-specific)
- ❌ Any temporary files

### **Check .gitignore**
If you want to prevent `.class` files from being tracked:
```bash
# Add to .gitignore file
echo "*.class" >> .gitignore
git add .gitignore
git commit -m "Add .gitignore to exclude class files"
git push origin main
```

---

## Useful Commands

### **View Commit History**
```bash
git log --oneline
```
Shows recent commits with messages.

### **View What Changes You Made**
```bash
git diff
```
Shows exact changes to all modified files.

### **Undo Changes to a File**
```bash
git restore filename.java
```
Reverts file to last committed version.

### **View Remote URL**
```bash
git remote -v
```
Shows where your repository is hosted (GitHub URL).

### **Clone Project to Another Location**
```bash
git clone https://github.com/Jaavieerrr/FirstProject.git
```
Creates a fresh copy of the repository.

---

## Workflow Example

```bash
# 1. Navigate to project
cd "c:\Users\ARIES\OneDrive\Documents\Desktop\Projects\BrickBreaker"

# 2. Pull latest changes
git pull origin main

# 3. Make changes to BrickBreaker_Full.java
# (edit in VS Code or your editor)

# 4. Check status
git status

# 5. Stage changes
git add .

# 6. Commit
git commit -m "Fix fullscreen centering and ball physics"

# 7. Push to GitHub
git push origin main
```

---

## Preventing .class Files

Create a `.gitignore` file to exclude compiled files:

```bash
# Create .gitignore
echo "*.class" > .gitignore
echo "*.project" >> .gitignore
echo ".classpath" >> .gitignore

# Add it to Git
git add .gitignore
git commit -m "Add gitignore to exclude build files"
git push origin main
```

---

## If You Get Conflicts

If someone else modified the same lines:

```bash
# 1. Pull (this will show conflicts)
git pull origin main

# 2. Open the conflicted file and resolve manually
# Look for: <<<<<<, ======, >>>>>>

# 3. Stage the fixed file
git add filename.java

# 4. Commit the merge
git commit -m "Resolve merge conflict"

# 5. Push
git push origin main
```

---

## Quick Daily Commands

**Every time you start working:**
```bash
git pull origin main
```

**Every time you finish:**
```bash
git add .
git commit -m "Your change description"
git push origin main
```

---

## Useful VSCode Integration

In VS Code, you can:
1. Click the **Source Control** icon (left sidebar)
2. Type commit message at the top
3. Click **✓** (checkmark) to commit
4. Click **⋯** (three dots) → **Push** to push to GitHub

---

**Your Repository:**
- Owner: Jaavieerrr
- Repo: FirstProject
- Branch: main
- Project: BrickBreaker

Keep pushing your improvements! 🚀
