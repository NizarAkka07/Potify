# Script de configuration automatique pour Kafka en local
# Télécharge et extrait Apache Kafka dans le dossier du projet

$Url = "https://archive.apache.org/dist/kafka/3.7.0/kafka_2.13-3.7.0.tgz"
$DestFile = "kafka_2.13-3.7.0.tgz"

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "      Installation Locale de Kafka        " -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

Write-Host "[1/3] Téléchargement de Apache Kafka (version 3.7.0)..." -ForegroundColor Yellow
Invoke-WebRequest -Uri $Url -OutFile $DestFile

Write-Host "[2/3] Extraction de l'archive avec tar..." -ForegroundColor Yellow
tar -xzf $DestFile

Write-Host "[3/3] Nettoyage et finalisation..." -ForegroundColor Yellow
Remove-Item $DestFile

if (Test-Path "kafka_2.13-3.7.0") {
    if (Test-Path "kafka") {
        Remove-Item "kafka" -Recurse -Force
    }
    Rename-Item "kafka_2.13-3.7.0" "kafka"
}

Write-Host "------------------------------------------" -ForegroundColor Cyan
Write-Host "Apache Kafka a été installé avec succès dans le dossier 'kafka' !" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Cyan
