# Villager Milk Growth

Right-click a baby villager with a milk bucket in your main hand to advance its growth by five minutes. Four buckets grow a newborn into an adult. Returns the empty bucket in survival; creative keeps the milk. Includes green particles and a quiet drinking sound.

Fabric, Java 25. Install on the server, or in your client for singleplayer. Remote clients do not need the mod. No Fabric API dependency at runtime.

Verified with server GameTests and a real client test on 26.1, 26.1.1, 26.1.2, 26.2 and 26.3. One JAR supports these five versions.

## Build and tests

With JDK 25 selected in JAVA_HOME:

```powershell
.\verify.ps1
```

The script builds the distributable once against 26.1, then runs server and client tests on all five versions using that exact JAR instead of production source classes. Both sides verify its origin and SHA-256. Only after all versions pass, the script copies `villager-milk-growth-1.0.0.jar` to `dist/`. Results and screenshots are saved in `verification/universal/<minecraft-version>/`. Tests use isolated worlds under `build/`. Client tests require graphics and audio. The production JAR contains only one mixin; tests and their observers are not distributed.

