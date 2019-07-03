call gradlew build
if %errorlevel% neq 0 exit /b %errorlevel%
echo Starting Server...
Pushd target
call bin\StjernCraft-ControlPanel.bat
popd