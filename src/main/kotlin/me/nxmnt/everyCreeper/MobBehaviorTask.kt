package me.nxmnt.everyCreeper

import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitRunnable

class MobBehaviorTask : BukkitRunnable() {

    private val explodingMobs = mutableMapOf<LivingEntity, Int>()

    private var tickCounter = 0

    override fun run() {
        if (!EveryCreeper.instance.pluginEnabled) return

        tickCounter++
        EveryCreeper.instance.server.worlds.forEach { world ->

            val activePlayers = world.players.filter {
                !it.isDead && it.gameMode != org.bukkit.GameMode.SPECTATOR
            }

            if (activePlayers.isEmpty()) return@forEach

            for (entity in world.livingEntities) {
                if (entity is Creeper || entity is Player || entity is ArmorStand || entity is EnderDragon) continue
                if (entity !is LivingEntity || entity.isDead) continue

                if (!entity.hasMetadata("everyCreeper")) {
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

                // 64 blocks
                var nearestPlayer: Player? = null
                var minDistanceSq = Double.MAX_VALUE

                for (player in activePlayers) {
                    val distSq = entity.location.distanceSquared(player.location)
                    if (distSq < minDistanceSq && distSq < 4096.0) {
                        minDistanceSq = distSq
                        nearestPlayer = player
                    }
                }

                if (nearestPlayer != null) {
                    val distanceSq = minDistanceSq

                    if (entity is Mob && tickCounter % 5 == 0) {
                        entity.target = nearestPlayer
                    }

                    if (distanceSq <= 9.0) {
                        if (!explodingMobs.containsKey(entity)) {
                            explodingMobs[entity] = 30
                            entity.world.playSound(entity.location, Sound.ENTITY_CREEPER_PRIMED, 1.0f, 1.0f)
                        }
                    } else if (distanceSq > 25.0) {
                        explodingMobs.remove(entity)
                    }
                }
            }
        }

        explodingMobs.entries.removeIf { (entity, remainingTicks) ->
            if (entity.isDead) {
                return@removeIf true
            }

            val newTicks = remainingTicks - 10
            if (newTicks <= 0) {
                explode(entity)
                true
            } else {
                explodingMobs[entity] = newTicks
                false
            }
        }
    }

    private fun explode(entity: LivingEntity) {
        val location = entity.location
        val world = entity.world
        val mobName = getMobDisplayName(entity)

        val explosionRadiusSq = 36.0
        world.players.forEach { player ->
            if (player.location.distanceSquared(location) <= explosionRadiusSq) {
                player.setMetadata("killedByMob", FixedMetadataValue(EveryCreeper.instance, mobName))
                EveryCreeper.instance.server.scheduler.runTaskLater(EveryCreeper.instance, Runnable {
                    player.removeMetadata("killedByMob", EveryCreeper.instance)
                }, 2L)
            }
        }

        world.createExplosion(
            location,
            3.0f,
            false,
            true
        )

        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f)
        entity.remove()
    }

    private fun getMobDisplayName(entity: LivingEntity): String {
        if (entity.customName != null) {
            return entity.customName!!
        }

        //TODO
        return when (entity) {
            is Zombie -> "좀비"
            is Skeleton -> "스켈레톤"
            is Spider -> "거미"
            is Enderman -> "엔더맨"
            is Witch -> "마녀"
            is Slime -> "슬라임"
            is Blaze -> "블레이즈"
            is Ghast -> "가스트"
            is PigZombie -> "좀비 피글린"
            is Silverfish -> "좀벌레"
            is CaveSpider -> "동굴 거미"
            is Vindicator -> "변명자"
            is Evoker -> "소환사"
            is Vex -> "벡스"
            is Pillager -> "약탈자"
            is Ravager -> "파괴수"
            is Drowned -> "드라운드"
            is Husk -> "허스크"
            is Stray -> "스트레이"
            is Phantom -> "팬텀"
            is Cow -> "소"
            is Pig -> "돼지"
            is Sheep -> "양"
            is Chicken -> "닭"
            is Rabbit -> "토끼"
            is Horse -> "말"
            is Villager -> "주민"
            else -> entity.type.name.lowercase().replaceFirstChar { it.uppercase() }
        }
    }
}

