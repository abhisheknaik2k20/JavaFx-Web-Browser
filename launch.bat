@echo off
cd /d "c:\JavaFx-Web-Browser\src"
echo Compiling JavaFX application...

REM Compile with proper module settings
javac --module-path "C:\Program Files\Java\javafx-sdk-24.0.2\lib" ^
      --add-modules javafx.controls,javafx.fxml,javafx.web ^
      *.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Starting JavaFX Web Browser with optimized settings...

REM Run with native access enabled and warnings suppressed
java --module-path "C:\Program Files\Java\javafx-sdk-24.0.2\lib" ^
     --add-modules javafx.controls,javafx.fxml,javafx.web ^
     --enable-native-access=javafx.web ^
     --add-opens javafx.web/com.sun.webkit=ALL-UNNAMED ^
     --add-opens javafx.base/com.sun.javafx.reflect=ALL-UNNAMED ^
     --add-opens javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED ^
     --add-opens javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED ^
     --add-opens javafx.fxml/javafx.fxml=ALL-UNNAMED ^
     -Djava.awt.headless=false ^
     App

pause
