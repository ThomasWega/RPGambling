package me.wega.rpgambling.handlers;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import me.wega.rpgambling.data.PlayerData;
import org.bukkit.Bukkit;

public class CasinoTimeHandler extends FlagValueChangeHandler<Boolean> {
    public static final Factory FACTORY = new Factory();

    public static class Factory extends Handler.Factory<CasinoTimeHandler> {
        @Override
        public CasinoTimeHandler create(Session session) {
            return new CasinoTimeHandler(session);
        }
    }

    long enteredTime = -1;

    public CasinoTimeHandler(Session session) {
        super(session, WorldGuardHandler.CASINO_FLAG);
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, Boolean value) {
        if (value == null || !value) return;
        enteredTime = System.currentTimeMillis();
    }

    @Override
    protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Boolean currentValue, Boolean lastValue, MoveType moveType) {
        if (currentValue == lastValue) return true;
        if (currentValue) {
            enteredTime = System.currentTimeMillis();
        } else if (enteredTime != -1) {
            PlayerData.get(Bukkit.getPlayer(player.getUniqueId())).addTimeSpentInCasino(System.currentTimeMillis() - enteredTime);
            enteredTime = -1;
        }
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Boolean lastValue, MoveType moveType) {
        if (lastValue == null || !lastValue) return true;
        if (enteredTime != -1) {
            PlayerData.get(Bukkit.getPlayer(player.getUniqueId())).addTimeSpentInCasino(System.currentTimeMillis() - enteredTime);
            enteredTime = -1;
        }
        return true;
    }
}
