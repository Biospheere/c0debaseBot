package de.c0debase.bot.utils;

import de.c0debase.bot.Codebase;
import de.c0debase.bot.database.model.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Invite;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class InviteTracker {

    private final Codebase bot;
    private final ConcurrentHashMap<String, Invite> inviteHashMap;

    public InviteTracker(final Codebase bot) {
        this.bot = bot;
        inviteHashMap = new ConcurrentHashMap<>();
    }

    /**
     * See which invite has been used by a new member of a guild
     */
    public void start() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> bot.getJDA().getGuilds().get(0).retrieveInvites().queue(inviteList -> inviteList.forEach(invite -> {
            if (inviteHashMap.containsKey(invite.getCode()) && invite.getUses() > inviteHashMap.get(invite.getCode()).getUses()) {
                final User user = bot.getDatabase().getUserDao().getOrCreateUser(invite.getGuild().getId(), invite.getInviter().getId());

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setDescription(invite.getInviter().getAsMention() + " vielen Dank das du jemand neues auf c0debase gebracht hast [" + invite.getCode() + "]");
                
                bot.getJDA().getTextChannelById(System.getenv("BOTCHANNEL")).sendMessage(embedBuilder.build()).queue();

                if (user.addXP(100)) {
                    EmbedBuilder levelUpEmbed = new EmbedBuilder();
                    levelUpEmbed.setDescription(invite.getInviter().getAsMention() + " ist nun Level " + user.getLevel());
                    bot.getJDA().getTextChannelById(System.getenv("BOTCHANNEL")).sendMessage(levelUpEmbed.build()).queue();
                }
                bot.getDatabase().getUserDao().updateUser(user);
            }
            inviteHashMap.put(invite.getCode(), invite);
        })), 5, 5, TimeUnit.SECONDS);
    }
}
