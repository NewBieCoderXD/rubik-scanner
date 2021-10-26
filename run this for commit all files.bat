cd C:\Users\aaa\AndroidStudioProjects\MyApplication\
::tree
del /s /f  /q C:\Users\aaa\AndroidStudioProjects\MyApplication\.git\*
rmdir /S /Q C:\Users\aaa\AndroidStudioProjects\MyApplication\.git\
 git init
 git add -A
 git commit -m "wow"
 git remote add "url" "https://github.com/NewBieCoderXD/Project1.git"
 git push url master --force
cmd /k