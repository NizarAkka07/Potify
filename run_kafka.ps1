# Script pour démarrer Zookeeper et Kafka en local
Clear-Host
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "    Démarrage de Zookeeper & Kafka...     " -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# 1. Démarrer Zookeeper
Write-Host "[1/2] Lancement de Zookeeper (Port 2181)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Write-Host 'Démarrage de Zookeeper...'; Set-Location C:\kafka; .\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties" -WindowStyle Normal

# Attendre 5 secondes que Zookeeper démarre
Start-Sleep -Seconds 5

# 2. Démarrer Kafka
Write-Host "[2/2] Lancement de Kafka (Port 9092)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Write-Host 'Démarrage de Kafka...'; Set-Location C:\kafka; .\bin\windows\kafka-server-start.bat .\config\server.properties" -WindowStyle Normal

Write-Host "------------------------------------------" -ForegroundColor Cyan
Write-Host "Zookeeper et Kafka sont en cours de démarrage." -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Cyan
