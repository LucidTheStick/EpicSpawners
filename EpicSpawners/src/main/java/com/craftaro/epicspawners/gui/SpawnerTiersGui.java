package com.craftaro.epicspawners.gui;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.gui.CustomizableGui;
import com.craftaro.core.utils.NumberUtils;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.epicspawners.EpicSpawners;
import com.craftaro.epicspawners.settings.Settings;
import com.craftaro.epicspawners.spawners.spawner.PlacedSpawnerImpl;
import com.craftaro.epicspawners.spawners.spawner.SpawnerStackImpl;
import com.craftaro.epicspawners.spawners.spawner.SpawnerTier;
import com.craftaro.epicspawners.utils.GuiUtils;
import com.craftaro.epicspawners.api.utils.HeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SpawnerTiersGui extends CustomizableGui {

    private final EpicSpawners plugin;
    private final Player player;
    private final PlacedSpawnerImpl spawner;

    private int task;

    public SpawnerTiersGui(EpicSpawners plugin, Player player, PlacedSpawnerImpl spawner) {
        super(plugin, "tiers");
        setRows(6);
        this.plugin = plugin;
        this.player = player;
        this.spawner = spawner;

        setTitle(plugin.getLocale().getMessage("interface.tiers.title").getMessage());
        setOnClose(event -> {
            Bukkit.getScheduler().cancelTask(task);
            plugin.getSpawnerManager().saveSpawnerDataToFile();
        });
        setDefaultItem(null);

        runTask();
        paint();
    }

    public void paint() {
        reset();

        // decorate the edges
        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial(CompatibleMaterial.BLUE_STAINED_GLASS_PANE));
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial(CompatibleMaterial.LIGHT_BLUE_STAINED_GLASS_PANE));

        // edges will be type 3
        mirrorFill("mirrorfill_1", 0, 2, true, true, glass3);
        mirrorFill("mirrorfill_2", 1, 1, true, true, glass3);

        // decorate corners with type 2
        mirrorFill("mirrorfill_3", 0, 0, true, true, glass2);
        mirrorFill("mirrorfill_4", 1, 0, true, true, glass2);
        mirrorFill("mirrorfill_5", 0, 1, true, true, glass2);

        List<SpawnerStackImpl> stacks = spawner.getSpawnerStacks();

        int num = 10;
        for (int i = 0; i < 28; i++) {
            num++;
            SpawnerTier tier = i < stacks.size() ? stacks.get(i).getCurrentTier() : null;
            if (num == 16 || num == 36)
                num = num + 2;

            if (tier == null)
                continue;

            if (acceptsItems) {
                setItem(num, GuiUtils.createButtonItem(tier.getDisplayItem() == CompatibleMaterial.AIR ? CompatibleMaterial.DIRT : tier.getDisplayItem(),
                        tier.getIdentifyingName()));
            } else {
                SpawnerStackImpl stack = stacks.get(i);
                CompatibleMaterial material = tier.getDisplayItem();
                setButton(num, GuiUtils.createButtonItem(material == null || material.isAir() ? HeadUtils.getTexturedSkull(tier) : tier.getDisplayItem().getItem(),
                        TextUtils.formatText(tier.getCompiledDisplayName(false, stack.getStackSize()))),
                        (event) -> plugin.getGuiManager().showGUI(player, new SpawnerOverviewGui(plugin, stack, player)));
            }
        }

        GuiUtils.applyBoosted(5, this, plugin, player, spawner);

        setItem("stats", 3, GuiUtils.createButtonItem(CompatibleMaterial.PAPER, plugin.getLocale().getMessage("interface.spawner.statstitle").getMessage(),
                plugin.getLocale().getMessage("interface.spawner.stats")
                        .processPlaceholder("amount", NumberUtils.formatNumber(spawner.getSpawnCount())).getMessage()));
    }

    private void runTask() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (inventory != null && inventory.getViewers().size() != 0)
                paint();
        }, 5L, 5L);
    }

    public static void openTiers(EpicSpawners plugin, Player player, PlacedSpawnerImpl spawner) {
        if (spawner.getSpawnerStacks().size() == 1)
            plugin.getGuiManager().showGUI(player, new SpawnerOverviewGui(plugin, spawner.getFirstStack(), player));
        else
            plugin.getGuiManager().showGUI(player, new SpawnerTiersGui(plugin, player, spawner));
    }

}