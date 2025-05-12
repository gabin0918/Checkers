@echo off
echo Kompilowanie...
del /q *.class
javac -cp ".;lib/mysql-connector-j-9.3.0.jar" *.java

echo Kompilacja zakończona
echo Uruchamianie programu...
java -cp ".;lib/mysql-connector-j-9.3.0.jar" LoginGUI

pause
