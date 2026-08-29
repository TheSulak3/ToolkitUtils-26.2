@echo off
setlocal
if not defined JAVA_HOME set JAVA_HOME=C:\jdk25
set PATH=%JAVA_HOME%\bin;%PATH%
cd /d "%~dp0"
echo Building with %JAVA_HOME% > build-log.txt
call gradlew.bat build --no-daemon --console=plain >> build-log.txt 2>&1
echo DONE_EXIT_CODE=%ERRORLEVEL% >> build-log.txt
echo Build finished, exit code %ERRORLEVEL%. See build-log.txt.
