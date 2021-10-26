@echo off
:main
set /p dir1="Enter Current DIR's name: "
set /p dir2="Enter New DIR's name: "

if exist "%dir1%\" (
echo The DIR does exist,
echo please wait...
ren %dir1% %dir2%
cd %dir2%
"C:\Program Files\fart-cmd\fart.exe" -r "settings.gradle" %dir1% %dir2%
) else (
echo The DIR doesn't exists, please try again
echo[
goto :main
)

cmd /k