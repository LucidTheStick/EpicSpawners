package com.craftaro.epicspawners.api.events;


import com.craftaro.epicspawners.api.spawners.spawner.PlacedSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when a spawner has been dropped in the world after being broken
 */
public class SpawnerDropEvent extends SpawnerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean canceled = false;

    public SpawnerDropEvent(Player player, PlacedSpawner spawner) {
        super(player, spawner);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean canceled) {
        this.canceled = canceled;
    }

}