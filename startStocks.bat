del /S /F /Q C:\Code\All-Stocks-Research\genHtml\*
del /S /F /Q C:\Code\All-Stocks-Research\genHtml\*
del /S /F /Q C:\Code\All-Stocks-Research\.git\index.lock
del C:\Code\All-Stocks-Research\.git\index.lock

rmdir /S /Q %temp%

cd C:\Code\All-Stocks-Research
c:
cd C:\Code\All-Stocks-Research

rmdir /S /Q C:\Code\All-Stocks-Research\logs

mkdir C:\Users\Vin\AppData\Local\Temp\

cd C:\Code\All-Stocks-Research
c:
cd C:\Code\All-Stocks-Research
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull
cd C:\Code\All-Stocks-Research
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull

xcopy C:\Code\All-Stocks-Research\tokens C:\Code-Base\gf-radar\tokens /s /e /h  /F /R /Y /I
xcopy C:\Code\All-Stocks-Research\tokens C:\Code\gf-radar\tokens /s /e /h  /F /R /Y /I
xcopy C:\Code-Base\All-Stocks-Research\tokens C:\Code-Base\gf-radar\tokens /s /e /h  /F /R /Y /I
xcopy C:\Code-Base\All-Stocks-Research\tokens C:\Code\gf-radar\tokens /s /e /h  /F /R /Y /I

mvn clean spring-boot:run
