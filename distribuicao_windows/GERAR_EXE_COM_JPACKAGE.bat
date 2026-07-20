@echo off
setlocal
chcp 65001 >nul
set "HERE=%~dp0"
set "INPUT=%HERE%app"
set "OUT=%HERE%saida"
set "ICON=%HERE%Gerard.ico"
if not exist "%INPUT%\Gerard.jar" (
  echo Gerard.jar nao encontrado em %INPUT%
  pause
  exit /b 1
)
where jpackage.exe >nul 2>&1 || (
  echo jpackage.exe nao encontrado. Instale o JDK 21 para gerar o EXE.
  pause
  exit /b 1
)
if not exist "%OUT%" mkdir "%OUT%"
jpackage.exe ^
  --type exe ^
  --name Gerard ^
  --app-version 1.0.0 ^
  --vendor "Projeto Gerard" ^
  --description "Ambiente Gerard para situacoes-problema aditivas" ^
  --input "%INPUT%" ^
  --main-jar Gerard.jar ^
  --main-class Main ^
  --icon "%ICON%" ^
  --dest "%OUT%" ^
  --win-per-user-install ^
  --win-shortcut ^
  --win-menu ^
  --win-menu-group Gerard ^
  --java-options "-Dfile.encoding=UTF-8" ^
  --java-options "-Xmx1024m"
if errorlevel 1 (
  echo.
  echo Nao foi possivel gerar o EXE. No Windows, o jpackage pode solicitar o WiX Toolset.
  pause
  exit /b 1
)
echo.
echo Instalador criado em: %OUT%
pause
