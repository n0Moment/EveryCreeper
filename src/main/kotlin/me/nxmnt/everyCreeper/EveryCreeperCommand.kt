package me.nxmnt.everyCreeper

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class EveryCreeperCommand : CommandExecutor, TabCompleter {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("/everycreeper <on|off|toggle|status|speed>")
            return true
        }

        when (args[0].lowercase()) {
            "on" -> {
                if (EveryCreeper.instance.pluginEnabled) {
                    sender.sendMessage("§e이미 활성화되어 있습니다!")
                } else {
                    EveryCreeper.instance.pluginEnabled = true
                    sender.sendMessage("§a활성화 완료.")
                }
            }
            "off" -> {
                if (!EveryCreeper.instance.pluginEnabled) {
                    sender.sendMessage("§e이미 비활성화되어 있습니다!")
                } else {
                    EveryCreeper.instance.pluginEnabled = false
                    sender.sendMessage("§c비활성화 완료.")
                }
            }
            "toggle" -> {
                EveryCreeper.instance.pluginEnabled = !EveryCreeper.instance.pluginEnabled
                if (EveryCreeper.instance.pluginEnabled) {
                    sender.sendMessage("§a활성화 완료.")
                } else {
                    sender.sendMessage("§c비활성화 완료.")
                }
            }
            "status" -> {
                val status = if (EveryCreeper.instance.pluginEnabled) "§a활성화" else "§c비활성화"
                sender.sendMessage("§e상태: $status")
                sender.sendMessage("§e이동 속도 배수: §a${EveryCreeper.instance.mobSpeedMultiplier}x")
            }
            "speed" -> {
                if (args.size < 2) {
                    sender.sendMessage("이동 속도 배수: §a${EveryCreeper.instance.mobSpeedMultiplier}x")
                    return true
                }

                val speed = args[1].toDoubleOrNull()
                if (speed == null || speed <= 0) {
                    sender.sendMessage("§c올바른 숫자를 입력하세요!")
                    return true
                }

                EveryCreeper.instance.mobSpeedMultiplier = speed
                sender.sendMessage("§a이동 속도 배수가 §e${speed}x§a로 설정되었습니다!")
            }
            else -> {
                sender.sendMessage("§c알 수 없는 명령어입니다!")
                sender.sendMessage("§e사용법: /everycreeper <on|off|toggle|status|speed>")
            }
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): List<String> {
        if (args.size == 1) {
            return listOf("on", "off", "toggle", "status", "speed")
                .filter { it.startsWith(args[0].lowercase()) }
        }

        if (args.size == 2 && args[0].lowercase() == "speed") {
            return listOf("1.0", "1.5", "2.0", "2.5", "3.0")
        }

        return emptyList()
    }
}

