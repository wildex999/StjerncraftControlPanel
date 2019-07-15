@ECHO OFF

ECHO Cleaning ImageResourceGenerator files ...
IF EXIST "%TEMP%\ImageResourceGenerator*" DEL "%TEMP%\ImageResourceGenerator*" /F /Q

ECHO Cleaning uiBinder files ...
IF EXIST "%TEMP%\uiBinder*" DEL "%TEMP%\uiBinder*" /F /Q

ECHO Cleaning gwt files ...
IF EXIST "%TEMP%\gwt*" DEL "%TEMP%\gwt*" /F /Q

ECHO Cleaning gwt directories ...
FOR /D /R %TEMP% %%x IN (gwt*) DO RMDIR /S /Q "%%x"

ECHO.
ECHO Done.
PAUSE