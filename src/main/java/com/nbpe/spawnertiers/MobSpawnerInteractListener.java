package com.nbpe.spawnertiers;

import java.util.HashMap;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockMobSpawner;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import me.onebone.economyapi.EconomyAPI;
import nukkitcoders.mobplugin.entities.block.BlockEntitySpawner;

public class MobSpawnerInteractListener implements Listener {
	
	Config itemTierUp;
	boolean EconomyAdded;
	Map<Player, Integer> playerCooldown = new HashMap<Player, Integer>();
	
	MobSpawnerInteractListener(Config items)
	{
		itemTierUp=items;
    	Plugin plugin = Server.getInstance().getPluginManager().getPlugin("EconomyAPI");
    	if(plugin==null) {
    		EconomyAdded=false;
    	}
    	EconomyAdded=true;
	}

    @EventHandler(ignoreCancelled = true)
	public void onBlockRightClick(PlayerInteractEvent e) { //Listen for players right-clicking mob spawners
		Action rightClick = e.getAction();
		if(rightClick!=Action.RIGHT_CLICK_BLOCK && rightClick!=Action.RIGHT_CLICK_AIR) return;

		Block spawner = e.getBlock();
		BlockEntity mobSpawner = e.getBlock().getLevel().getBlockEntity(e.getBlock());
		if(spawner instanceof BlockMobSpawner) {
			BlockEntitySpawner tieredSpawner = (BlockEntitySpawner) mobSpawner;
			tieredSpawner.getBlock();
			int maxDelay = ((BlockEntitySpawner) mobSpawner).namedTag.getShort("TAG_MAX_SPAWN_DELAY");
			BlockEntitySpawner bes = (BlockEntitySpawner) mobSpawner;
			if(maxDelay==0) {
				bes.setMaxSpawnDelay(1000);
				bes.setMinSpawnDelay(200);
				bes.saveNBT();
				maxDelay=1000;
			}
			
			
			switch(maxDelay)
			{
				case 1000: //Tier 1
					if(isUpgradeItem(e.getItem(), 2))
					{
						upgradeTierItem( tieredSpawner, e.getPlayer(), 2);
						sendTier(e.getPlayer(), 2);
					}else if(EconomyAdded && isEnoughCredits(e.getPlayer(), 1)) {
						upgradeTierCredits( tieredSpawner, e.getPlayer(), 2);
					}else {
						sendTier(e.getPlayer(), 1);
					}
					break;
				case 500://Tier 2;
					if(isUpgradeItem(e.getItem(), 3))
					{
						upgradeTierItem( tieredSpawner, e.getPlayer(), 3);
						sendTier(e.getPlayer(), 3);
					}else if(EconomyAdded && isEnoughCredits(e.getPlayer(), 2)) {
						upgradeTierCredits( tieredSpawner, e.getPlayer(), 3);
					}
					
					break;
				case 333://Tier 3
					sendTier(e.getPlayer(), 3);
					break;
				default:
					return;
			}
			playerCooldown.put(e.getPlayer(), 1);
			Plugin thisPlugin = Server.getInstance().getPluginManager().getPlugin("SpawnerTiers");
			Server.getInstance().getScheduler().scheduleDelayedTask(thisPlugin, this.removeFromMap(e.getPlayer()), 25);
		}
	}
    
    public Runnable removeFromMap(final Player player) {
        Runnable runnable = new Runnable(){
            public void run(){
            	playerCooldown.remove(player);
            }
        };
		return runnable;
    }

	
	private void upgradeTierItem(BlockEntitySpawner mobSpawner, Player player, int upgradeTo) { //Upgrade tier, remove player item/credits
		boolean cooldown = playerCooldown.containsKey(player);

		if(!cooldown) {
			PlayerInventory playerInv = player.getInventory();
			Item itemToRemove = new Item((int) itemTierUp.get("ItemUpgradeTier"+upgradeTo));
			int remove = playerInv.first(itemToRemove, false);
			playerInv.decreaseCount(remove);
			mobSpawner.setSpawnDelay(200/upgradeTo, 1000/upgradeTo);
			mobSpawner.namedTag.putShort("TAG_MAX_SPAWN_DELAY", 1000/upgradeTo);
			mobSpawner.namedTag.putShort("TAG_MIN_SPAWN_DELAY", 200/upgradeTo);
			mobSpawner.saveNBT();
		}
	}
	
	private void upgradeTierCredits(BlockEntitySpawner mobSpawner, Player player, int upgradeTo) { //Upgrade tier, remove player item/credits
		boolean cooldown = playerCooldown.containsKey(player);

		if(!cooldown) {
			double playerCredits = EconomyAPI.getInstance().myMoney(player);
			double amtToUpgrade = itemTierUp.getDouble("CreditsTier"+upgradeTo);
			if(playerCredits < amtToUpgrade) {
				return;
			}
			EconomyAPI.getInstance().reduceMoney(player, amtToUpgrade);
			mobSpawner.setSpawnDelay(200/upgradeTo, 1000/upgradeTo);
			mobSpawner.namedTag.putShort("TAG_MAX_SPAWN_DELAY", 1000/upgradeTo);
			mobSpawner.namedTag.putShort("TAG_MIN_SPAWN_DELAY", 200/upgradeTo);
			mobSpawner.saveNBT();
		}
	}
	
	private void sendTier(Player player, int Tier) {
		boolean cooldown = playerCooldown.containsKey(player);

		if(!cooldown) {
			player.sendMessage("This mob spawner is Tier "+Tier);
		}
	}
	
	private boolean isUpgradeItem(Item item, int upgradeTier) {
		if(itemTierUp.getInt("ItemUpgradeTier"+upgradeTier)==item.getId())
		{
			return true;
		}
		return false;
	}
	
	private boolean isEnoughCredits(Player player, int upgradeTo) {
		double playerCredits = EconomyAPI.getInstance().myMoney(player);
		double amtToUpgrade = itemTierUp.getDouble("CreditsTier"+upgradeTo);
		if(playerCredits < amtToUpgrade) {
			return false;
		}
		return true;
	}
}
