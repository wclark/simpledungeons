$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$blockbench = Join-Path $env:LOCALAPPDATA "Programs\Blockbench\Blockbench.exe"

if (-not (Test-Path -LiteralPath $blockbench)) {
    throw "Blockbench was not found at $blockbench"
}

Start-Process -FilePath $blockbench
Start-Process -FilePath "explorer.exe" -ArgumentList @((Join-Path $repoRoot "blockbench\projects"))
Start-Process -FilePath "explorer.exe" -ArgumentList @((Join-Path $repoRoot "src\main\resources\assets\simpledungeons"))

