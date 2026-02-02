package me.nxmnt.everyCreeper

import org.bukkit.plugin.java.JavaPlugin

class EveryCreeper : JavaPlugin() {

    companion object {
        lateinit var instance: EveryCreeper
            private set
    }

    var pluginEnabled = true
    var mobSpeedMultiplier = 1.0

    override fun onEnable() {
        instance = this

        getCommand("everycreeper")?.setExecutor(EveryCreeperCommand())
        getCommand("everycreeper")?.tabCompleter = EveryCreeperCommand()

        server.pluginManager.registerEvents(MobSpawnListener(), this)
//        server.pluginManager.registerEvents(MobTargetListener(), this)
//        server.pluginManager.registerEvents(PlayerDeathListener(), this)
        server.pluginManager.registerEvents(MobShootListener(), this)
//        server.pluginManager.registerEvents(PlayerAdvancementListener(), this)
//        server.pluginManager.registerEvents(PlayerJoinQuitListener(), this)
        server.pluginManager.registerEvents(ShieldBlockListener(), this)

        MobBehaviorTask().runTaskTimer(this, 0L, 10L)

        logger.info("EveryCreeper Enabled.")
    }

    override fun onDisable() {
        logger.info("EveryCreeper Disabled.")
    }
}
