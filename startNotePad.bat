cd C:\Code\All-Stocks-Research
c:
cd C:\Code\All-Stocks-Research
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull

cd C:\Code-Base\All-Stocks-Research
c:
cd C:\Code-Base\All-Stocks-Research
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull

xcopy C:\Code-Base\All-Stocks-Research\tokens C:\Code-Base\gf-radar\tokens /s /e /h  /F /R /Y /I
xcopy C:\Code-Base\All-Stocks-Research\tokens C:\Code\gf-radar\tokens /s /e /h  /F /R /Y /I

"C:\Program Files\Notepad++\notepad++.exe"