@echo off
cd /d "c:\JavaFx-Web-Browser\src"
echo Compiling JavaFX application...
javac --module-path "C:\Program Files\Java\javafx-sdk-24.0.2\lib" --add-modules javafx.controls,javafx.fxml,javafx.web *.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)
echo Starting JavaFX Web Browser...
java --module-path "C:\Program Files\Java\javafx-sdk-24.0.2\lib" --add-modules javafx.controls,javafx.fxml,javafx.web App
pause
