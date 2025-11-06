@echo off
set JOGL=C:\Users\HomePC\Downloads\jogl-main\jar
mkdir out 2>nul
javac -d out -cp "%JOGL%\jogl-all.jar;%JOGL%\gluegen-rt.jar" src\*.java gmaths\*.java || exit /b
java  -cp "out;%JOGL%\jogl-all.jar;%JOGL%\gluegen-rt.jar" Buzz
