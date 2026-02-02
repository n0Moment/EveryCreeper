package me.nxmnt.everyCreeper

import org.bukkit.entity.Skeleton
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent

class MobShootListener : Listener {

    @EventHandler
    fun onEntityShootBow(event: EntityShootBowEvent) {
        if (!EveryCreeper.instance.pluginEnabled) return

        val entity = event.entity
        if (entity is Skeleton && entity.hasMetadata("everyCreeper")) {
            event.isCancelled = true
        }
    }
}

