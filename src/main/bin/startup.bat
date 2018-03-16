@echo off  
title word-transfer-job
SET CLASSPATH=
SET CURRENT_DIR=%~dp0
SET PROJECT_DIR=%CURRENT_DIR%..

@echo %PROJECT_DIR%

set CLASSPATH=%CLASSPATH%;%PROJECT_DIR%

set CLASSPATH=%CLASSPATH%;%PROJECT_DIR%\lib\*

SET APPNAME=com.zhixue.transfer.application.TransferJobMain
@echo %CLASSPATH%

java -Xms${app.mem} -Xmx${app.mem} -classpath "%CLASSPATH%" %APPNAME% start
pause


