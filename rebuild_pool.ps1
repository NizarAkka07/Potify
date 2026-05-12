# Rebuild data-jpa puis relance pool
Write-Host "=== Rebuild data-jpa ===" -ForegroundColor Cyan
Set-Location "$env:USERPROFILE\Desktop\Project\potify"

.\mvnw install -DskipTests -pl data-jpa

if ($LASTEXITCODE -eq 0) {
    Write-Host "data-jpa compile avec succes !" -ForegroundColor Green
    Write-Host "=== Lancement du service Pool ===" -ForegroundColor Cyan
    Set-Location "pool"
    ..\mvnw spring-boot:run
} else {
    Write-Host "ERREUR lors du build de data-jpa" -ForegroundColor Red
    Pause
}
