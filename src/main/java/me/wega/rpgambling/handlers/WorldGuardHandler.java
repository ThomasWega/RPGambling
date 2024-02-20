package me.wega.rpgambling.handlers;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

public class WorldGuardHandler {
    public static StateFlag CASINO_FLAG;
    private final WorldGuard worldGuard = WorldGuard.getInstance();

    public WorldGuardHandler() {
        registerCasinoFlag();
    }

    private void registerCasinoFlag() {
        FlagRegistry registry = worldGuard.getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("casino", false);
            registry.register(flag);
            CASINO_FLAG = flag;
        } catch (FlagConflictException e) {
            CASINO_FLAG = (StateFlag) registry.get("casino");
        }
    }
}
