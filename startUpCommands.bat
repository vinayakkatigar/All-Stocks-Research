cd C:\Code\yf-radar
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull
cd C:\Code-Base\yf-radar
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull
cd C:\Code-Base\All-Stocks-Research
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull

timeout /t 2 /nobreak > NUL
wt -w 0 nt  C:\Code-Base\All-Stocks-Research\startNotePad.bat

timeout /t 5 /nobreak > NUL
wt -w 0 nt  C:\Code-Base\All-Stocks-Research\gFinanceAlive.bat

timeout /t 10 /nobreak > NUL
wt -w 0 nt C:\Vin\KeepAlive.bat

timeout /t 10 /nobreak > NUL
wt -w 0 nt  C:\Code-Base\All-Stocks-Research\startStocks.bat

timeout /t 2 /nobreak > NUL
wt -w 0 nt  C:\Code-Base\All-Stocks-Research\startOutlook.bat

timeout /t 2 /nobreak > NUL
wt -w 0 nt  C:\Code-Base\All-Stocks-Research\gFinanceOneDayAlive.bat

timeout /t 10 /nobreak > NUL
wt -w 0 nt C:\Code-Base\yf-radar-apac\yfRadarAPACStartStocks.bat

timeout /t 20 /nobreak > NUL
wt -w 0 nt C:\Code-Base\yf-radar-apac\yfAPACAlive.bat

timeout /t 20 /nobreak > NUL
REM wt -w 0 nt C:\Code-Base\gf-radar\yfYearLowAlive.bat

REM timeout /t 5 /nobreak > NUL
REM wt -w 0 nt  C:\Code-Base\All-Stocks-Research\enterKeyPresses.bat