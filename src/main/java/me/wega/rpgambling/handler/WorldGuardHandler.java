package me.wega.rpgambling.handler;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

public class WorldGuardHandler {
    public static BooleanFlag CASINO_FLAG;
    private final WorldGuard worldGuard = WorldGuard.getInstance();

    public WorldGuardHandler() {
        registerCasinoFlag();
    }

    private void registerCasinoFlag() {
        FlagRegistry registry = worldGuard.getFlagRegistry();
        BooleanFlag flag = new BooleanFlag("casino");
        registry.register(flag);
        CASINO_FLAG = flag;
    }
}
