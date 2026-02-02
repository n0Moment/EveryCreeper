package me.nxmnt.everyCreeper

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class ShieldBlockListener : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityDamage(event: EntityDamageEvent) {
        if (!EveryCreeper.instance.pluginEnabled) return
        val entity = event.entity
        if (entity !is Player) return

        if (event.cause != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION &&
            event.cause != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            return
        }

        val mainHand = entity.inventory.itemInMainHand
        val offHand = entity.inventory.itemInOffHand

        val hasShield = mainHand.type == Material.SHIELD || offHand.type == Material.SHIELD
        if (!hasShield) return

        if (entity.isBlocking) {
            event.isCancelled = true

            if (mainHand.type == Material.SHIELD) {
                damageShield(mainHand, entity)
            } else if (offHand.type == Material.SHIELD) {
                damageShield(offHand, entity)
            }

            entity.world.playSound(entity.location, org.bukkit.Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.0f)
            entity.sendMessage("§a당신은 폭발을 막았다!")
        }
    }

    private fun damageShield(shield: org.bukkit.inventory.ItemStack, player: Player) {
        val meta = shield.itemMeta ?: return

        if (meta is org.bukkit.inventory.meta.Damageable) {
            val currentDamage = meta.damage
            val maxDurability = shield.type.maxDurability.toInt()
            meta.damage = currentDamage + 15

            if (meta.damage >= maxDurability) {
                shield.amount = 0
                player.world.playSound(player.location, org.bukkit.Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f)
                player.sendMessage("§c방패가 부서졌다!")
            } else {
                shield.itemMeta = meta
            }
        }
    }
}

