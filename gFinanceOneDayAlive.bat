:kickForOneDay

start chrome --new-window https://docs.google.com/spreadsheets/d/18-BpblNjElxTNZm12Q_PEo-Ru-4hYKQgiI1-8Yk4CaU/edit?gid=0#gid=0
timeout /t 60 /nobreak > NUL
start chrome https://docs.google.com/spreadsheets/d/1Sr8VyB61Lu5GYMOrceNbwM7WTyDuaUPOD3wTCwXBnNk/edit?gid=0#gid=0
timeout /t 60 /nobreak > NUL
start chrome https://docs.google.com/spreadsheets/d/1_5CTJjqt6AEbzyE2EjvXNoshnie3dxGtX09Pspxto3A/edit?gid=0#gid=0
timeout /t 60 /nobreak > NUL
start chrome https://docs.google.com/spreadsheets/d/1tLDyMvoIR8tUmMjKSGWAGKSTYyQfHG50xZQNG1gVXGU/edit?gid=0#gid=0
timeout /t 60 /nobreak > NUL
start chrome https://docs.google.com/spreadsheets/d/1p1wCt1zbz8b6tVEImuhpmooDiiPLsIDLxUdJZljeq3A/edit?gid=0#gid=0
timeout /t 60 /nobreak > NUL
start chrome https://docs.google.com/spreadsheets/d/1FRliqkfwDvF5969jW0tybnn6QIsWmC70jlS7oNPDh7c/edit?gid=0#gid=0
timeout /t 60 /nobreak > NUL
start chrome https://docs.google.com/spreadsheets/d/1HsF63N9SZd8ByxGOvidy4XCOkRRpmD5QaEs49x0T2Ro/edit?gid=0#gid=0
timeout /t 60 /nobreak > NUL

timeout /t 86400 /nobreak > NUL
TASKKILL /IM chrome.exe /F

goto kickForOneDay