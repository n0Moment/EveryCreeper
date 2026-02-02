package me.nxmnt.everyCreeper

import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.metadata.FixedMetadataValue

class MobSpawnListener : Listener {

    @EventHandler
    fun onMobSpawn(event: CreatureSpawnEvent) {
        if (!EveryCreeper.instance.pluginEnabled) return

        val entity = event.entity
        if (entity is Creeper || entity is EnderDragon) return

        if (entity is LivingEntity && entity !is Player && entity !is ArmorStand) {
            entity.setMetadata("everyCreeper", FixedMetadataValue(EveryCreeper.instance, true))

            if (entity is Mob) {
                entity.getAttribute(Attribute.MOVEMENT_SPEED)?.let { attr ->
                    attr.baseValue = attr.baseValue * EveryCreeper.instance.mobSpeedMultiplier
                }

                entity.getAttribute(Attribute.FOLLOW_RANGE)?.let { attr ->
                    attr.baseValue = 64.0
                }

                entity.isAware = true
                entity.removeWhenFarAway = true
            }
        }
    }
}

