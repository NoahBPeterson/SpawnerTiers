package com.nbpe.spawnertiers;

import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockMobSpawner;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.mob.EntityBlaze;
import cn.nukkit.entity.mob.EntityCreeper;
import cn.nukkit.entity.mob.EntityEnderman;
import cn.nukkit.entity.mob.EntityGhast;
import cn.nukkit.entity.mob.EntityHusk;
import cn.nukkit.entity.mob.EntityMagmaCube;
import cn.nukkit.entity.mob.EntitySkeleton;
import cn.nukkit.entity.mob.EntitySlime;
import cn.nukkit.entity.mob.EntitySpider;
import cn.nukkit.entity.mob.EntityStray;
import cn.nukkit.entity.mob.EntityWitch;
import cn.nukkit.entity.mob.EntityWitherSkeleton;
import cn.nukkit.entity.mob.EntityZombiePigman;
import cn.nukkit.entity.passive.EntityBat;
import cn.nukkit.entity.passive.EntityChicken;
import cn.nukkit.entity.passive.EntityCod;
import cn.nukkit.entity.passive.EntityCow;
import cn.nukkit.entity.passive.EntityDolphin;
import cn.nukkit.entity.passive.EntityDonkey;
import cn.nukkit.entity.passive.EntityHorse;
import cn.nukkit.entity.passive.EntityMooshroom;
import cn.nukkit.entity.passive.EntityOcelot;
import cn.nukkit.entity.passive.EntityParrot;
import cn.nukkit.entity.passive.EntityPig;
import cn.nukkit.entity.passive.EntityPolarBear;
import cn.nukkit.entity.passive.EntityPufferfish;
import cn.nukkit.entity.passive.EntityRabbit;
import cn.nukkit.entity.passive.EntitySalmon;
import cn.nukkit.entity.passive.EntitySheep;
import cn.nukkit.entity.passive.EntitySquid;
import cn.nukkit.entity.passive.EntityTropicalFish;
import cn.nukkit.entity.passive.EntityTurtle;
import cn.nukkit.entity.passive.EntityWolf;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import nukkitcoders.mobplugin.entities.animal.flying.Bee;
import nukkitcoders.mobplugin.entities.animal.walking.Cat;
import nukkitcoders.mobplugin.entities.animal.walking.Fox;
import nukkitcoders.mobplugin.entities.animal.walking.Llama;
import nukkitcoders.mobplugin.entities.animal.walking.Mule;
import nukkitcoders.mobplugin.entities.animal.walking.Panda;
import nukkitcoders.mobplugin.entities.animal.walking.SkeletonHorse;
import nukkitcoders.mobplugin.entities.animal.walking.ZombieHorse;
import nukkitcoders.mobplugin.entities.block.BlockEntitySpawner;
import nukkitcoders.mobplugin.entities.monster.flying.EnderDragon;
import nukkitcoders.mobplugin.entities.monster.flying.Phantom;
import nukkitcoders.mobplugin.entities.monster.flying.Vex;
import nukkitcoders.mobplugin.entities.monster.flying.Wither;
import nukkitcoders.mobplugin.entities.monster.swimming.ElderGuardian;
import nukkitcoders.mobplugin.entities.monster.swimming.Guardian;
import nukkitcoders.mobplugin.entities.monster.walking.CaveSpider;
import nukkitcoders.mobplugin.entities.monster.walking.Drowned;
import nukkitcoders.mobplugin.entities.monster.walking.Endermite;
import nukkitcoders.mobplugin.entities.monster.walking.Evoker;
import nukkitcoders.mobplugin.entities.monster.walking.IronGolem;
import nukkitcoders.mobplugin.entities.monster.walking.Pillager;
import nukkitcoders.mobplugin.entities.monster.walking.Ravager;
import nukkitcoders.mobplugin.entities.monster.walking.Shulker;
import nukkitcoders.mobplugin.entities.monster.walking.Silverfish;
import nukkitcoders.mobplugin.entities.monster.walking.SnowGolem;
import nukkitcoders.mobplugin.entities.monster.walking.Vindicator;
import nukkitcoders.mobplugin.entities.monster.walking.ZombieVillager;

public class SpawnerTiers extends PluginBase {
	
	Config itemsTierUp;
	PluginManager pluginManager;
	Map<String, BlockEntitySpawner> stringToSpawner;
	MobSpawnerInteractListener pIEL;

    @Override
    public void onLoad() {
        this.getLogger().info(TextFormat.WHITE + "SpawnerTiers has been loaded!");
        this.getServer().getCommandMap().register("Spawner", (Command)new SpawnerTiersCommand("spawner", this));

    }

    @Override
    public void onEnable() {
        this.getLogger().info(TextFormat.DARK_GREEN + "SpawnerTiers has been enabled!");
        
    	setupConfig();
    	
    	pIEL = new MobSpawnerInteractListener(itemsTierUp);
        this.getServer().getPluginManager().registerEvents(pIEL, this);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
    {
    	switch(cmd.getName().toLowerCase())
    	{
	    	case "spawner":
	    		if(args.length < 3) {
	    			return false;
	    		}
	    		if(args[1].charAt(0)-'1'<0) {
	    			args[1]="1";
	    		}else if(args[1].charAt(0)-'3'>0) {
	    			args[1]="3";
	    		}
	    		if(mobType(args[2])==-1) {
	    			sender.sendMessage("Not a valid mob type!");
	    			return false;
	    		}
	    		Player playerToGiveTo = Server.getInstance().getPlayer(args[0]);
	    		pIEL.addSpawner(playerToGiveTo, mobType(args[2]), Integer.parseInt(args[1]));
	    		ItemBlock monsterSpawner = new ItemBlock(new BlockMobSpawner());
	    		playerToGiveTo.getInventory().addItem(monsterSpawner);
	    		return true;
	    	default:
	    		return false;
    	} 
    }
    
    //Check if config.yml has any keys. If not, add the defaults and save.
    private void setupConfig() {
    	itemsTierUp = new Config(this.getDataFolder() + "/config.yml", Config.YAML);
    	if(!itemsTierUp.isString("ItemUpgradeTier2")) {
    		itemsTierUp.set("ItemUpgradeTier2", Item.NETHER_STAR);
    		itemsTierUp.set("ItemUpgradeTier3", Item.NETHER_STAR);
    		itemsTierUp.set("CreditsTier2", 10000.0);
    		itemsTierUp.set("CreditsTier3", 20000.0);
    		itemsTierUp.save();
    		itemsTierUp.reload();
    	}
    }

    @Override
    public void onDisable() {
        this.getLogger().info(TextFormat.DARK_RED + "SpawnerTiers has been disabled!");
    }
    
	public void upgradeTierItem(BlockEntitySpawner mobSpawner, int upgradeTo) {
		mobSpawner.setMaxSpawnDelay(1000/upgradeTo);
		mobSpawner.saveNBT();
	}
    
    public int mobType(String mobType) {
    	switch(mobType.toLowerCase()) {
    	case "bat":
    		return EntityBat.NETWORK_ID;
    	case "bee":
    		return Bee.NETWORK_ID;
    	case "blaze":
    		return EntityBlaze.NETWORK_ID;
    	case "cat":
    		return Cat.NETWORK_ID;
    	case "cavespider":
    		return  CaveSpider.NETWORK_ID;
    	case "chicken":
    		return EntityChicken.NETWORK_ID;
    	case "cod":
    		return EntityCod.NETWORK_ID;
    	case "cow":
    		return EntityCow.NETWORK_ID;
    	case "creeper":
    		return EntityCreeper.NETWORK_ID;
    	case "dolphin":
    		return EntityDolphin.NETWORK_ID;
    	case "donkey":
    		return EntityDonkey.NETWORK_ID;
    	case "drowned":
    		return Drowned.NETWORK_ID;
    	case "elderguardian":
    		return ElderGuardian.NETWORK_ID;
    	case "enderdragon":
    		return EnderDragon.NETWORK_ID;
    	case "enderman":
    		return EntityEnderman.NETWORK_ID;
    	case "endermite":
    		return Endermite.NETWORK_ID;
    	case "evoker":
    		return Evoker.NETWORK_ID;
    	case "fox":
    		return Fox.NETWORK_ID;
    	case "ghast":
    		return EntityGhast.NETWORK_ID;
    	case "guardian":
    		return Guardian.NETWORK_ID;
    	case "horse":
    		return EntityHorse.NETWORK_ID;
    	case "husk":
    		return EntityHusk.NETWORK_ID;
    	case "irongolem":
    	case "golem":
    		return IronGolem.NETWORK_ID;
    	case "llama":
    		return Llama.NETWORK_ID;
    	case "magma":
    	case "magmacube":
    		return EntityMagmaCube.NETWORK_ID;
    	case "mooshroom":
    		return EntityMooshroom.NETWORK_ID;
    	case "mule":
    		return Mule.NETWORK_ID;
    	case "ocelot":
    		return EntityOcelot.NETWORK_ID;
    	case "panda":
    		return Panda.NETWORK_ID;
    	case "parrot":
    		return EntityParrot.NETWORK_ID;
    	case "phantom":
    		return Phantom.NETWORK_ID;
    	case "pig":
    		return EntityPig.NETWORK_ID;
    	case "pillager":
    		return Pillager.NETWORK_ID;
    	case "polarbear":
    		return EntityPolarBear.NETWORK_ID;
    	case "pufferfish":
    		return EntityPufferfish.NETWORK_ID;
    	case "rabbit":
    		return EntityRabbit.NETWORK_ID;
    	case "ravager":
    		return Ravager.NETWORK_ID;
    	case "salmon":
    		return EntitySalmon.NETWORK_ID;
    	case "sheep":
    		return EntitySheep.NETWORK_ID;
    	case "shulker":
    		return Shulker.NETWORK_ID;
    	case "silverfish":
    		return Silverfish.NETWORK_ID;
    	case "skeleton":
    		return EntitySkeleton.NETWORK_ID;
    	case "skeletonhorse":
    		return SkeletonHorse.NETWORK_ID;
    	case "slime":
    		return EntitySlime.NETWORK_ID;
    	case "snowgolem":
    		return SnowGolem.NETWORK_ID;
    	case "spider":
    		return EntitySpider.NETWORK_ID;
    	case "squid":
    		return EntitySquid.NETWORK_ID;
    	case "stray":
    		return EntityStray.NETWORK_ID;
    	case "tropicalfish":
    		return EntityTropicalFish.NETWORK_ID;
    	case "turtle":
    		return EntityTurtle.NETWORK_ID;
    	case "vex":
    		return Vex.NETWORK_ID;
    	case "vindicator":
    		return Vindicator.NETWORK_ID;
    	case "witch":
    		return EntityWitch.NETWORK_ID;
    	case "wither":
    		return Wither.NETWORK_ID;
    	case "witherskeleton":
    		return EntityWitherSkeleton.NETWORK_ID;
    	case "wolf":
    		return EntityWolf.NETWORK_ID;
    	case "zombiehorse":
    		return ZombieHorse.NETWORK_ID;
    	case "zombiepigman":
    		return EntityZombiePigman.NETWORK_ID;
    	case "zombievillager":
    		return ZombieVillager.NETWORK_ID;
    	default:
    		return -1;
    	}
    }
}
