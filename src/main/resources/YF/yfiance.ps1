param (
    [string]$Stock = 'AAPL'
)
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
#$Stock = 'MSFT' 

try { Invoke-WebRequest -UseBasicParsing -Uri 'https://fc.yahoo.com/' -SessionVariable session } catch {$null}        
        

$crumb = Invoke-WebRequest -UseBasicParsing -Uri 'https://query2.finance.yahoo.com/v1/test/getcrumb' -WebSession $session


#$URL = $('https://query2.finance.yahoo.com/v10/finance/quoteSummary/' + $Stock + '?modules=price&crumb=' + $crumb)
$URL = $('https://query2.finance.yahoo.com/v7/finance/quote?symbols=' + $Stock + '&crumb=' + $crumb)
#$URL = $('https://query2.finance.yahoo.com/v7/finance/quote?symbols=' + $Stock + '&crumb=' + $crumb)
$ResponseText = Invoke-WebRequest -UseBasicParsing -Uri $URL -WebSession $session

    
$ResponseText = $ResponseText.ToString()
$ResponseText 