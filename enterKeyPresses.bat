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

cd C:\Vin\Code-Base\yf-radar
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull
cd C:\Vin\Code\yf-radar
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull

cd C:\Vin\Code-Base\yf-radar-apac
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull
cd C:\Vin\Code\yf-radar-apac
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull

timeout /t 300 /nobreak > NUL

REM :enterKey

REM powershell.exe  C:\Code-Base\enterKeyPress.ps1
REM timeout /t 10 /nobreak > NUL

REM powershell.exe  C:\Code-Base\enterKeyPress.ps1
REM timeout /t 600 /nobreak > NUL

REM goto enterKey