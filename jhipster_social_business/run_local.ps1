# PowerShell script to run the app with .env variables
$envFile = ".env"

if (Test-Path $envFile) {
    Write-Host "Loading environment variables from $envFile..." -ForegroundColor Cyan
    Get-Content $envFile | ForEach-Object {
        $line = $_.Trim()
        if ($line -notmatch "^#" -and $line -ne "") {
            $parts = $line -split "=", 2
            if ($parts.Count -eq 2) {
                $name = $parts[0].Trim()
                $value = $parts[1].Trim()
                # Remove surrounding quotes if present
                if ($value -match '^"(.*)"$') { $value = $matches[1] }
                elseif ($value -match "^'(.*)'$") { $value = $matches[1] }
                
                [Environment]::SetEnvironmentVariable($name, $value, "Process")
            }
        }
    }
} else {
    Write-Host ".env file not found!" -ForegroundColor Red
    exit 1
}

Write-Host "Starting Application..." -ForegroundColor Green
java -jar target/whatsapp-product-service-pro-0.0.1-SNAPSHOT.jar
