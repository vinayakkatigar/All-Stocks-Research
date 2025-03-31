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
wt -w 0 nt  C:\Code\yf-radar\yfRadarAlive.bat

timeout /t 20 /nobreak > NUL
wt -w 0 nt C:\Code\gf-radar\yfYearLowAlive.bat

timeout /t 5 /nobreak > NUL
wt -w 0 nt  C:\Code\All-Stocks-Research\enterKeyPresses.bat