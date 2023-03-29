package dev.arbjerg.ukulele.jda

import dev.arbjerg.ukulele.features.LeaveOnIdleService
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.StatusChangeEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class EventHandler(private val commandManager: CommandManager, private val leaveOnIdleService: LeaveOnIdleService) : ListenerAdapter() {

    private val log: Logger = LoggerFactory.getLogger(EventHandler::class.java)

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.channelType != ChannelType.TEXT) return
        commandManager.onMessage(event.guild, event.channel.asTextChannel(), event.member!!, event.message)
    }

    override fun onStatusChange(event: StatusChangeEvent) {
        log.info("{}: {} -> {}", event.entity.shardInfo, event.oldStatus, event.newStatus)
    }

    override fun onGuildVoiceUpdate(event: GuildVoiceJoinEvent) {
        if (event.getChannelJoined() != null) {
            log.info("Joining voice channel {} in guild {}", event.getChannelJoined().getId(), event.getGuild().getName())
            return
        } else if (event.getChannelLeft() != null) {
            log.info("Leaving voice channel {} in guild {}", event.getChannelLeft().getId(), event.getGuild().getName())
            leaveOnIdleService.destroyTimer(event.getGuild())
            return
        }
    }

    override fun onGuildVoiceUpdate(event: GuildVoiceLeaveEvent) {
        
    }
}