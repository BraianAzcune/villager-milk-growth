package dev.braian.villagermilkgrowth.client;

import java.util.ArrayList;
import java.util.List;

// Test-only state. Read and write exclusively on the Minecraft client thread.
public final class EfectosObservados {
    public static final List<Integer> particulas = new ArrayList<>();
    public static int sonidosIniciados;

    public static void reset() {
        particulas.clear();
        sonidosIniciados = 0;
    }

    private EfectosObservados() {}
}
