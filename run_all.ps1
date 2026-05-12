# Script de lancement pour Potify
# Ce script rebuild data-jpa, libere les ports, et lance les services.

# Couleurs
$Green = "Green"
$Cyan = "Cyan"
$Yellow = "Yellow"
$Red = "Red"

function Stop-ProcessOnPort($port) {
    $connections = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    if ($connections) {
        Write-Host "Liberation du port $port..." -ForegroundColor Gray
        foreach ($conn in $connections) {
            try {
                Stop-Process -Id $conn.OwningProcess -Force -ErrorAction SilentlyContinue
            } catch {}
        }
    }
}

Clear-Host
Write-Host "==========================================" -ForegroundColor $Cyan
Write-Host "   Potify - Lancement des Microservices   " -ForegroundColor $Cyan
Write-Host "==========================================" -ForegroundColor $Cyan

# Verification du repertoire
if (!(Test-Path "user") -or !(Test-Path "pool") -or !(Test-Path "frontend")) {
    Write-Host "Erreur : Veuillez lancer ce script depuis la racine du projet 'potify'." -ForegroundColor $Red
    Write-Host "Chemin actuel : $(Get-Location)"
    Pause
    exit
}

# 0a. Rebuild du module data-jpa (necessaire si des queries ont ete modifiees)
Write-Host "[0/3] Rebuild du module data-jpa..." -ForegroundColor $Yellow
.\mvnw install -DskipTests -pl data-jpa -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERREUR : Le build de data-jpa a echoue. Verifiez les logs." -ForegroundColor $Red
    Pause
    exit
}
Write-Host "       data-jpa compile avec succes !" -ForegroundColor $Green

# 0b. Nettoyage des ports
Write-Host "Nettoyage des ports..." -ForegroundColor $Cyan
Stop-ProcessOnPort 8081 # User
Stop-ProcessOnPort 8082 # Pool
Stop-ProcessOnPort 9000 # Frontend (Quasar)

# 1. Microservice User
Write-Host "[1/3] Lancement du Microservice User (Port 8081)..." -ForegroundColor $Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Write-Host 'Demarrage du service User...'; Set-Location user; ..\mvnw spring-boot:run" -WindowStyle Normal

# 2. Microservice Pool
Write-Host "[2/3] Lancement du Microservice Pool (Port 8082)..." -ForegroundColor $Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Write-Host 'Demarrage du service Pool...'; Set-Location pool; ..\mvnw spring-boot:run" -WindowStyle Normal

# 3. Frontend (Quasar)
Write-Host "[3/3] Lancement du Frontend (Quasar Port 9000)..." -ForegroundColor $Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Write-Host 'Demarrage du Frontend...'; Set-Location frontend; npm run dev" -WindowStyle Normal

Write-Host "------------------------------------------" -ForegroundColor $Cyan
Write-Host "Tous les services sont en cours de demarrage." -ForegroundColor $Green
Write-Host "Consultez les nouvelles fenetres pour voir les logs." -ForegroundColor $Green
Write-Host "==========================================" -ForegroundColor $Cyan
