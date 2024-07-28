del /S /F /Q C:\Code\All-Stocks-Research\genHtml\*
del /S /F /Q C:\Code\All-Stocks-Research\genFiles\*

rmdir /S /Q %temp%

cd C:\Code\All-Stocks-Research
c:

c:\
c:/
cd C:\Code\All-Stocks-Research

rmdir /S /Q C:\Code\All-Stocks-Research\logs

mkdir C:\Users\Vin\AppData\Local\Temp\

cd C:\Code\All-Stocks-Research
c:
cd C:\Code\All-Stocks-Research
git reset --hard HEAD && git pull && git reset --hard HEAD && git pull && git reset --hard HEAD && git pull

mvn clean spring-boot:run
