:again
start chrome https://docs.google.com/spreadsheets/d/1r0ZqMeOPIfkoakhcW3dGHE2YsKgJJO4M7InwgcP2-Ao/edit#gid=0
start chrome https://docs.google.com/spreadsheets/d/1DdkJYnXIR0UCLeB7cjB8G4LGzZMXHKQ_dGpXGO8CU8I/edit#gid=0
start chrome https://docs.google.com/spreadsheets/d/1YmgSZuLMPJqgPLUuaQCTq2TXjts4aPD0w19zYpV0WpE/edit#gid=0
start chrome https://docs.google.com/spreadsheets/d/1TGdtwdz_6O9wTO3xFzUwo4wlbsyGsQWeHolLVJzxLyQ/edit#gid=0
start chrome https://docs.google.com/spreadsheets/d/1M7swFwopiNGRZn052yCc1YM3cYVQvLiBytJNehncSPI/edit#gid=0
timeout /t 600 /nobreak > NUL
TASKKILL /IM chrome.exe /F
goto again