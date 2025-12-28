# Project Troubleshooting Guide

This guide covers common build errors encountered in this project and how to resolve them.

## 1. Build Failure: "Failed to delete ... target/classes" (File Lock)

**Error Message:**
```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-clean-plugin:3.4.0:clean ... Failed to delete ...\target\classes
```

**Cause:**
This occurs on Windows when a file in the `target` directory is locked by another process. Common culprits include:
- A running instance of the application (`java.exe`).
- An open file in an IDE or text editor.
- A terminal window sitting inside the directory.
- `node.exe` processes from the frontend build.

**Solution:**
1.  **Stop running servers:** Check if you have other terminal tabs running the app.
2.  **Kill stuck processes:**
    Open PowerShell and run:
    ```powershell
    taskkill /F /IM java.exe
    taskkill /F /IM node.exe
    ```
    ```
3.  **Manual Clean:**
    If `mvn clean` fails, try manually deleting the `target` folder:
    ```powershell
    Remove-Item -Recurse -Force target
    ```
4.  **Retry the build:** Run your `./mvnw` command again.

---

## 2. Build Failure: "npm run webapp:build failed" (Prettier/Linting)

**Error Message:**
```
[ERROR] ... 'npm run webapp:build' failed.
```
In the logs, you see thousands of errors like:
- `Delete '␍'`
- `Insert ...`

**Cause:**
Inconsistent line endings (CRLF vs LF). This often happens when moving between Windows and Git environments.

**Solution:**
Run the project's formatter to automatically fix line endings:
```powershell
npm run prettier:format
```
Then retry the build.
