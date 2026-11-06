# Villager Milk Growth

Right-click a baby villager with a milk bucket in your main hand to advance its growth by five minutes. Four buckets grow a newborn into an adult. Returns the empty bucket in survival; creative keeps the milk. Includes green particles and a quiet drinking sound.

Fabric, Java 25. Install on the server, or in your client for singleplayer. Remote clients do not need the mod. No Fabric API dependency at runtime.

Verified with server GameTests and a real client test on 26.1, 26.1.1, 26.1.2, 26.2 and 26.3. Use the file matching your game version.

## Build and tests

With JDK 25 selected in JAVA_HOME:

```powershell
.\gradlew.bat build runClientGameTest '-Pminecraft_version=26.2' --console=plain
```

Run `./verify.ps1` to validate all five versions and collect approved JARs in `dist/`. Per-version build output: `build/<minecraft-version>/libs/`. Tests use isolated worlds under `build/`. Client tests require graphics and audio. The production JAR contains only one mixin; tests and their observers are not distributed.

