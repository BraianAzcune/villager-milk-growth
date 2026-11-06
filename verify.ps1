$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    $modVersion = (Select-String -Path gradle.properties -Pattern '^mod_version=').Line.Split('=')[1]
    $artifact = "villager-milk-growth-$modVersion.jar"
    $evidenceRoot = 'verification/universal'
    New-Item -ItemType Directory -Force $evidenceRoot, 'build/release' | Out-Null
    & .\gradlew.bat jar '-Pminecraft_version=26.1' --console=plain *> "$evidenceRoot/build.log"
    if ($LASTEXITCODE -ne 0) { throw 'Failed to build production JAR' }
    $candidate = Join-Path $PWD "build/release/$artifact"
    Copy-Item "build/26.1/libs/$artifact" $candidate
    $hash = (Get-FileHash $candidate -Algorithm SHA256).Hash
    foreach ($mc in @('26.1', '26.1.1', '26.1.2', '26.2', '26.3')) {
        $evidence = "$evidenceRoot/$mc"
        New-Item -ItemType Directory -Force "$evidence/screenshots" | Out-Null
        Write-Output "Validating same JAR on Minecraft $mc (server + client)..."
        & .\gradlew.bat runGameTest runClientGameTest "-Pminecraft_version=$mc" "-PtestJar=$candidate" "-PtestSha256=$hash" --console=plain *> "$evidence/build.log"
        if ($LASTEXITCODE -ne 0) { throw "Minecraft $mc failed. See $evidence/build.log" }
        foreach ($side in @('server', 'client')) {
            $run = if ($side -eq 'server') { 'gameTest' } else { 'clientGameTest' }
            Copy-Item "build/run/$run/logs/latest.log" "$evidence/$side.log"
            if (!(Select-String -Path "$evidence/$side.log" -SimpleMatch -Pattern "VERIFIED_PRODUCTION_JAR sha256=$($hash.ToLowerInvariant())" -Quiet)) {
                throw "Missing JAR verification in $mc $side"
            }
        }
        Copy-Item 'build/run/clientGameTest/screenshots/*.png' "$evidence/screenshots/"
        if ((Get-FileHash $candidate -Algorithm SHA256).Hash -ne $hash) { throw 'Candidate changed during testing' }
        @{
            minecraft = $mc
            result = 'passed'
            validation = 'same production JAR; server GameTests and four-click client test'
            artifact = $artifact
            sha256 = $hash
        } | ConvertTo-Json | Set-Content "$evidence/result.json"
        Write-Output "PASS ${mc}: $hash"
    }
    New-Item -ItemType Directory -Force dist | Out-Null
    Copy-Item $candidate "dist/$artifact"
    "$hash  $artifact" | Set-Content dist/SHA256SUMS.txt
    Write-Output "READY: dist/$artifact"
} finally {
    Pop-Location
}