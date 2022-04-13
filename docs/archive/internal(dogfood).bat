for /f "tokens=*" %%G in ('dir /b /s /o:d /a:d "Z:\data\bey\releases\"') do cd %%G && forfiles /P %%G /S /M *.jar /C "cmd /c echo @path > Z:\source\lastrun.txt"

set /p jarToRun=<Z:\source\lastrun.txt
z:
cd z:\data\bey
java -jar %jarToRun% --properties config/default.json

