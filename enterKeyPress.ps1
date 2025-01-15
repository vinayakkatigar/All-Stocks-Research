$wshell = New-Object -ComObject wscript.shell;
$wshell.AppActivate('title of the application window')
Sleep 1
$wshell.SendKeys('~')
Sleep 5
$wshell.SendKeys("{ESC}")