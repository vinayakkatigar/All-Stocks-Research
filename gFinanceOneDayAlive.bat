echo %date% - %time%
timeout /t 28800 /nobreak > NUL

:kickForOneDay

echo %date% - %time%

start chrome --new-window https://docs.google.com/spreadsheets/d/18-BpblNjElxTNZm12Q_PEo-Ru-4hYKQgiI1-8Yk4CaU/edit?gid=0#gid=0
timeout /t 180 /nobreak > NUL
start chrome https://docs.google.com/spreadsheets/d/1Sr8VyB61Lu5GYMOrceNbwM7WTyDuaUPOD3wTCwXBnNk/edit?gid=0#gid=0
timeout /t 30 /nobreak > NUL
start chrome https://docs.google.com/spreadsheets/d/1_5CTJjqt6AEbzyE2EjvXNoshnie3dxGtX09Pspxto3A/edit?gid=0#gid=0
timeout /t 30 /nobreak > NUL
start chrome https://docs.google.com/spreadsheets/d/1tLDyMvoIR8tUmMjKSGWAGKSTYyQfHG50xZQNG1gVXGU/edit?gid=0#gid=0
timeout /t 30 /nobreak > NUL
start chrome https://docs.google.com/spreadsheets/d/1p1wCt1zbz8b6tVEImuhpmooDiiPLsIDLxUdJZljeq3A/edit?gid=0#gid=0
timeout /t 30 /nobreak > NUL
start chrome https://docs.google.com/spreadsheets/d/1FRliqkfwDvF5969jW0tybnn6QIsWmC70jlS7oNPDh7c/edit?gid=0#gid=0
timeout /t 30 /nobreak > NUL
start chrome https://docs.google.com/spreadsheets/d/1HsF63N9SZd8ByxGOvidy4XCOkRRpmD5QaEs49x0T2Ro/edit?gid=0#gid=0
timeout /t 30 /nobreak > NUL
start chrome https://docs.google.com/spreadsheets/d/1PhG_yA2Jxs8n5d2e-5DsZuQyNjZEvjhJxztZhDbcO7Q/edit?gid=1551880706#gid=1551880706
timeout /t 30 /nobreak > NUL
start chrome https://docs.google.com/spreadsheets/d/1Tk-JnVzGTtmP-tn7y0uG64avg6u8-teQVqUu5kvADzs/edit?gid=1551880706#gid=1551880706
timeout /t 30 /nobreak > NUL
start chrome https://docs.google.com/spreadsheets/d/1jqnnYdy5VOZEgC4aYPHVYrxZI4Pfo-0yrfHZcPJ-2K0/edit?gid=1551880706#gid=1551880706
timeout /t 30 /nobreak > NUL

timeout /t 120 /nobreak > NUL
TASKKILL /IM chrome.exe /F

echo %date% - %time%

timeout /t 10 /nobreak > NUL
REM powershell.exe  C:\Code-Base\enterKeyPress.ps1
timeout /t 10 /nobreak > NUL

timeout /t 43200 /nobreak > NUL

goto kickForOneDay