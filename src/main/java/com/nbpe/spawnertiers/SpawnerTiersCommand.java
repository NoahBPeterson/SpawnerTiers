package com.nbpe.spawnertiers;

import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.Plugin;

public class SpawnerTiersCommand extends PluginCommand<SpawnerTiers>
{

	public SpawnerTiersCommand(String name, Plugin owner) {
		super(name, (SpawnerTiers) owner);
        this.setPermission(Permission.DEFAULT_OP);
        this.commandParameters.clear();
        this.commandParameters.put(name, new CommandParameter[]
        		{
                    new CommandParameter("player", CommandParamType.TARGET, false),
                    new CommandParameter("tier", CommandParamType.INT, false),
                    new CommandParameter("mobType", false, CommandParameter.ENUM_TYPE_ENTITY_LIST)

        });
        this.setUsage("/spawner [player] [tier] [type]");
	}
}