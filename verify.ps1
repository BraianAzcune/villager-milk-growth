param(
    [ValidateSet('26.1', '26.1.1', '26.1.2', '26.2', '26.3')]
    [string[]] $Versions = @('26.1', '26.1.1', '26.1.2', '26.2', '26.3')
)
$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    $modVersion = (Select-String -Path gradle.properties -Pattern '^mod_version=').Line.Split('=')[1]
    New-Item -ItemType Directory -Force dist | Out-Null
    foreach ($mc in $Versions) {
        $evidence = "verification/$mc"
        New-Item -ItemType Directory -Force "$evidence/screenshots" | Out-Null
        Write-Output "Validating Minecraft $mc (server + client)..."
        & .\gradlew.bat build runClientGameTest "-Pminecraft_version=$mc" --console=plain *> "$evidence/build.log"
        if ($LASTEXITCODE -ne 0) {
            throw "Minecraft $mc failed. See $evidence/build.log"
        }
        Copy-Item "build/run/gameTest/logs/latest.log" "$evidence/server.log"
        Copy-Item "build/run/clientGameTest/logs/latest.log" "$evidence/client.log"
        Copy-Item "build/run/clientGameTest/screenshots/*.png" "$evidence/screenshots/"
        $artifact = "villager-milk-growth-$modVersion+mc$mc.jar"
        Copy-Item "build/$mc/libs/$artifact" "dist/$artifact"
        @{
            minecraft = $mc
            result = 'passed'
            validation = 'server GameTests and four-click client test'
            artifact = $artifact
            sha256 = (Get-FileHash "dist/$artifact" -Algorithm SHA256).Hash
        } | ConvertTo-Json | Set-Content "$evidence/result.json"
        Write-Output "PASS $mc -> dist/$artifact"
    }
    Get-ChildItem dist -Filter '*.jar' | Sort-Object Name | ForEach-Object {
        '{0}  {1}' -f (Get-FileHash $_.FullName -Algorithm SHA256).Hash, $_.Name
    } | Set-Content dist/SHA256SUMS.txt
} finally {
    Pop-Location
}

