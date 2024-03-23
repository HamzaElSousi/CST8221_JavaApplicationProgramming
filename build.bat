@echo off
REM Set the project base directories
set SRC_DIR=src
set BIN_DIR=bin
set RES_DIR=src/resources

REM Create the bin directory if it does not exist
if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"

REM Compile the Java source files
echo Compiling Java source files...
javac -d "%BIN_DIR%" -sourcepath "%SRC_DIR%" %SRC_DIR%/*.java

REM Copy resources
echo Copying resources...
xcopy "%RES_DIR%" "%BIN_DIR%/resources" /E /I /Q

REM Package everything into a JAR file
echo Packaging into a JAR file...
jar cvfm Connect4MVC.jar Manifest.txt -C "%BIN_DIR%" .

echo Build completed.
