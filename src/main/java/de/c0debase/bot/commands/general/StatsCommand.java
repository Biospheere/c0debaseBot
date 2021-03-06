package de.c0debase.bot.commands.general;

import de.c0debase.bot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Biosphere
 * @date 23.01.18
 */
public class StatsCommand extends Command {

        public StatsCommand() {
                super("stats", "Zeigt dir einige Informationen über den Bot", Category.GENERAL, "info");
        }

        @Override
        public void execute(final String[] args, final Message message) {
                final JDA jda = message.getJDA();
                final long uptime = ManagementFactory.getRuntimeMXBean().getUptime();

                final EmbedBuilder embedBuilder = getEmbed(message.getGuild(), message.getAuthor());
                embedBuilder.addField("JDA Version", JDAInfo.VERSION, true);
                embedBuilder.addField("Ping", jda.getGatewayPing() + "ms", true);
                embedBuilder.addField("Uptime",
                                String.valueOf(TimeUnit.MILLISECONDS.toDays(uptime) + "d "
                                                + TimeUnit.MILLISECONDS.toHours(uptime) % 24 + "h "
                                                + TimeUnit.MILLISECONDS.toMinutes(uptime) % 60 + "m "
                                                + TimeUnit.MILLISECONDS.toSeconds(uptime) % 60 + "s"),
                                true);
                embedBuilder.addField("Commands", String.valueOf(bot.getCommandManager().getAvailableCommands().size()),
                                true);
                embedBuilder.addField("Mitglieder",
                                String.valueOf(jda.getGuilds().stream().mapToInt(Guild::getMemberCount).sum()), true);
                embedBuilder.addField("Java Version", System.getProperty("java.runtime.version").replace("+", "_"),
                                true);
                embedBuilder.addField("Betriebssystem", ManagementFactory.getOperatingSystemMXBean().getName(), true);
                message.getChannel().sendMessage(embedBuilder.build()).queue();
        }
}
