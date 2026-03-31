$backendHealthUrl = if ($env:BACKEND_HEALTH_URL) { $env:BACKEND_HEALTH_URL } else { 'http://127.0.0.1:8081/actuator/health' }

function Wait-ForBackendHealth {
  param (
    [string]$Url
  )

  Write-Host "Checking backend health at $Url"

  for ($attempt = 1; $attempt -le 15; $attempt++) {
    try {
      $response = Invoke-RestMethod -Uri $Url -TimeoutSec 10
      if ($response.status -eq 'UP') {
        Write-Host 'Backend health check passed.'
        return
      }
    } catch {
    }

    Write-Host "Backend not ready yet (attempt $attempt/15). Retrying..."
    Start-Sleep -Seconds 2
  }

  throw "Backend health check failed for $Url. Start the Spring Boot backend before running verify:local."
}

function Get-FreeTcpPort {
  $listener = [System.Net.Sockets.TcpListener]::new([System.Net.IPAddress]::Loopback, 0)
  $listener.Start()
  $port = $listener.LocalEndpoint.Port
  $listener.Stop()

  return [string]$port
}

function Invoke-Step {
  param (
    [string]$Command,
    [string]$FailureMessage
  )

  Write-Host "Running: $Command"
  Invoke-Expression $Command
  if ($LASTEXITCODE -ne 0) {
    throw $FailureMessage
  }
}

Wait-ForBackendHealth -Url $backendHealthUrl

Invoke-Step -Command 'npm.cmd run build' -FailureMessage 'Frontend build failed.'

$env:PLAYWRIGHT_REUSE_SERVER = '0'

$env:PLAYWRIGHT_PORT = Get-FreeTcpPort
Write-Host "Running login Playwright flow on port $env:PLAYWRIGHT_PORT"
Invoke-Step -Command 'npm.cmd run e2e:login' -FailureMessage 'Login Playwright e2e failed.'

$env:PLAYWRIGHT_PORT = Get-FreeTcpPort
Write-Host "Running intake Playwright flow on port $env:PLAYWRIGHT_PORT"
Invoke-Step -Command 'npm.cmd run e2e:intake' -FailureMessage 'Intake Playwright e2e failed.'

$env:PLAYWRIGHT_PORT = Get-FreeTcpPort
Write-Host "Running nurse Playwright flow on port $env:PLAYWRIGHT_PORT"
Invoke-Step -Command 'npm.cmd run e2e:nurse' -FailureMessage 'Nurse Playwright e2e failed.'

$env:PLAYWRIGHT_PORT = Get-FreeTcpPort
Write-Host "Running nurse vitals Playwright flow on port $env:PLAYWRIGHT_PORT"
Invoke-Step -Command 'npm.cmd run e2e:nurse-vitals' -FailureMessage 'Nurse vitals Playwright e2e failed.'

$env:PLAYWRIGHT_PORT = Get-FreeTcpPort
Write-Host "Running nurse medications Playwright flow on port $env:PLAYWRIGHT_PORT"
Invoke-Step -Command 'npm.cmd run e2e:nurse-medications' -FailureMessage 'Nurse medications Playwright e2e failed.'

$env:PLAYWRIGHT_PORT = Get-FreeTcpPort
Write-Host "Running doctor Playwright flow on port $env:PLAYWRIGHT_PORT"
Invoke-Step -Command 'npm.cmd run e2e:doctor' -FailureMessage 'Doctor Playwright e2e failed.'

$env:PLAYWRIGHT_PORT = Get-FreeTcpPort
Write-Host "Running history Playwright flow on port $env:PLAYWRIGHT_PORT"
Invoke-Step -Command 'npm.cmd run e2e:history' -FailureMessage 'History Playwright e2e failed.'

Write-Host 'Local verification passed.'
