timeout /t 900 /nobreak > NUL

c:
cd C:\Code-Base\All-Stocks-Research
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull
cd C:\Code\All-Stocks-Research
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull

cd C:\Vin\Code-Base\yf-stocks-research
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull
cd C:\Vin\Code-Base\yf-stocks-research
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull

cd C:\Vin\Code-Base\gf-radar
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull
cd C:\Vin\Code-Base\gf-radar
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull

xcopy C:\Code-Base\All-Stocks-Research\tokens C:\Vin\Code-Base\gf-radar\tokens /s /e /h  /F /R /Y /I
xcopy C:\Code\All-Stocks-Research\tokens C:\Vin\Code-Base\gf-radar\tokens /s /e /h  /F /R /Y /I
xcopy C:\Vin\Code-Base\All-Stocks-Research\tokens C:\Vin\Code-Base\gf-radar\tokens /s /e /h  /F /R /Y /I
xcopy C:\Vin\Code\All-Stocks-Research\tokens C:\Vin\Code-Base\gf-radar\tokens /s /e /h  /F /R /Y /I

timeout /t 300 /nobreak > NUL

:enterKey

powershell.exe enterKeyPress.ps1
timeout /t 10 /nobreak > NUL

powershell.exe enterKeyPress.ps1
timeout /t 600 /nobreak > NUL

goto enterKey