package dev.braian.villagermilkgrowth;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ArtifactVerification implements ModInitializer {
    @Override
    public void onInitialize() {
        String expectedJar = System.getProperty("villagerMilkGrowth.testJar");
        if (expectedJar == null) {
            return; // Normal development tests may still use source classes.
        }
        try {
            var mod = FabricLoader.getInstance().getModContainer("villager-milk-growth").orElseThrow();
            var paths = mod.getOrigin().getPaths();
            Path expected = Path.of(expectedJar).toRealPath();
            if (paths.size() != 1 || !paths.getFirst().toRealPath().equals(expected)) {
                throw new AssertionError("Unexpected production mod origin: " + paths);
            }
            String hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(expected)));
            if (!hash.equalsIgnoreCase(System.getProperty("villagerMilkGrowth.testSha256"))) {
                throw new AssertionError("Production JAR hash mismatch: " + hash);
            }
            System.out.println("VERIFIED_PRODUCTION_JAR sha256=" + hash + " path=" + expected);
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot verify production JAR", exception);
        }
    }
}