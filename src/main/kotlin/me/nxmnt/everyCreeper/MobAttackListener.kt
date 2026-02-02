package me.nxmnt.everyCreeper

import org.bukkit.entity.Creeper
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class MobAttackListener : Listener {

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (!EveryCreeper.instance.pluginEnabled) return

        val damager = event.damager
        val victim = event.entity
        if (damager is Mob && damager !is Creeper && victim is Player) {
            if (damager.hasMetadata("everyCreeper")) {
                event.isCancelled = true
            }
        }
    }
}

