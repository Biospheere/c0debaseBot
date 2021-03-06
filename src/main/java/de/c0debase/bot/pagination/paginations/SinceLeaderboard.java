package de.c0debase.bot.pagination.paginations;

import de.c0debase.bot.pagination.Pagination;
import de.c0debase.bot.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SinceLeaderboard extends Pagination {

    public SinceLeaderboard() {
        super("Since Leaderboard:");
    }

    @Override
    public void buildList(EmbedBuilder embedBuilder, int page, boolean descending, Guild guild) {
        final List<Member> users = getSortedMembers(guild);
        if (!descending)
            Collections.reverse(users);
        for (Map.Entry<Integer, Member> entry : getPage(page, users, descending).entrySet()) {
            Member member = entry.getValue();
            int count = entry.getKey();
            if (member != null) {
                long days = ChronoUnit.DAYS.between(member.getTimeJoined(),
                        LocalDateTime.now().atOffset(ZoneOffset.UTC));
                embedBuilder
                        .appendDescription("`" + count + ")` " + StringUtils.replaceCharacter(member.getEffectiveName())
                                + "#" + member.getUser().getDiscriminator() + " (Beitritt am "
                                + member.getTimeJoined().toInstant().atOffset(ZoneOffset.UTC)
                                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                                + " / Seit " + days + " Tag" + (days == 1 ? "" : "en") + ")\n");
            } else {
                embedBuilder.appendDescription("`" + count + ")` undefined#0000\n");
            }
            count++;
        }
    }

    private List<Member> getSortedMembers(final Guild guild) {
        final List<Member> members = new LinkedList<>();
        guild.getMembers().forEach(member -> {
            if (!member.hasTimeJoined()) {
                members.add(member.getGuild().retrieveMemberById(member.getId(), true).complete());
            } else {
                members.add(member);
            }
        });
        members.sort((m1, m2) -> Long.compare(m2.getTimeJoined().toInstant().toEpochMilli(),
                m1.getTimeJoined().toInstant().toEpochMilli()));
        return members;
    }
}