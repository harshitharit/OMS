@echo off

REM Start ZooKeeper
start /B %KAFKA_HOME%\bin\windows\zookeeper-server-start.bat %KAFKA_HOME%\config\zookeeper.properties
 
REM Wait for zookeeper to start
timeout /t 5

REM Start Kafka server
start "" cmd /c %KAFKA_HOME%\bin\windows\kafka-server-start.bat %KAFKA_HOME%\config\server.properties

REM Wait for Kafka to start
