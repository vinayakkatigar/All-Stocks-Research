cd C:\Vin\Code-Base\yf-radar-apac
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull
cd C:\Vin\Code\yf-radar-apac
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull
cd C:\Code\All-Stocks-Research
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull

timeout /t 2 /nobreak > NUL
wt -w 0 nt  C:\Code\All-Stocks-Research\startNotePad.bat

timeout /t 5 /nobreak > NUL
wt -w 0 nt  C:\Code\All-Stocks-Research\gFinanceAlive.bat

timeout /t 10 /nobreak > NUL
wt -w 0 nt C:\Vin\KeepAlive.bat

timeout /t 10 /nobreak > NUL
wt -w 0 nt  C:\Code\All-Stocks-Research\startStocks.bat

timeout /t 2 /nobreak > NUL
wt -w 0 nt  C:\Code\All-Stocks-Research\startOutlook.bat

timeout /t 2 /nobreak > NUL
wt -w 0 nt  C:\Code\All-Stocks-Research\gFinanceOneDayAlive.bat

timeout /t 10 /nobreak > NUL
wt -w 0 nt C:\Code\yf-radar-apac\yfRadarAPACStartStocks.bat

timeout /t 20 /nobreak > NUL
wt -w 0 nt C:\Code\yf-radar-apac\yfAPACAlive.bat

timeout /t 20 /nobreak > NUL
REM wt -w 0 nt C:\Code\gf-radar\yfYearLowAlive.bat

REM timeout /t 5 /nobreak > NUL
REM wt -w 0 nt  C:\Code\All-Stocks-Research\enterKeyPresses.bat