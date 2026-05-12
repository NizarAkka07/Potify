@echo off
REM Lance le script PowerShell run_all.ps1 en contournant la politique d'exécution si nécessaire.
powershell -ExecutionPolicy Bypass -File ".\run_all.ps1"
pause
