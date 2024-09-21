package me.nerdoron.himyb.modules.useful.birthdays;

import me.nerdoron.himyb.Global;
import me.nerdoron.himyb.modules.fun.brocoins.BroCoinsSQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BirthdayFunction extends ListenerAdapter {

    final BroCoinsSQL broCoinsSQL = new BroCoinsSQL();
    final BirthdayChecks birthdayChecks = new BirthdayChecks();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Calendar calendar = Calendar.getInstance();
        JDA api = event.getJDA();


        scheduler.scheduleAtFixedRate(() -> {
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            birthdayHandler(api, day, month);
        }, 0, 1, TimeUnit.HOURS);
    }

    private void birthdayHandler(JDA api, int day, int month) {
        Guild guild = api.getGuildById("991446766169903165");
        if (guild == null) {
            System.out.println("Guild not found");
            return;
        }

        Role role = guild.getRoleById("991446766421544977");
        TextChannel channel = guild.getTextChannelById("991446767407218744");

        assert role != null;
        assert channel != null;

        ArrayList<String> birthdaysUserIDs = birthdayChecks.getBirthdays(day, month);

        guild.findMembersWithRoles(role).onSuccess(members -> {
            boolean someoneHasBdayRole = false;
            for (Member member : members) {
                if (!birthdaysUserIDs.contains(member.getId())) {
                    // Removing role from users who do not have a birthday today
                    guild.removeRoleFromMember(member, role).queue();
                } else {
                    someoneHasBdayRole = true;
                }
            }

            if (someoneHasBdayRole) return;
            if (birthdaysUserIDs.isEmpty()) return;

            StringBuilder mentionUsers = new StringBuilder();
            for (String birthdayUserID : birthdaysUserIDs) { // Iterate over the IDs adding them the BDay role
                guild.retrieveMemberById(birthdayUserID).queue(
                        member -> {
                            if (member != null && !member.getRoles().contains(role)) {
                                guild.addRoleToMember(member, role).queue();
                                try {
                                    broCoinsSQL.updateCash(member, 150);
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                        },
                        failure -> System.out.println("Failed to retrieve member: " + birthdayUserID)
                );
                mentionUsers.append("<@").append(birthdayUserID).append("> ");
            }

            channel.sendMessage(mentionUsers).addEmbeds(bdayEmbed(mentionUsers)).queue();
        });
    }

    MessageEmbed bdayEmbed(StringBuilder mentionUsers) {
        return new EmbedBuilder()
                .setTitle("\uD83C\uDF82  Happy Birthday!")
                .setDescription(String.format("Happy Birthday to the following members!\n%s\n\nThey get a birthday gift of 150 %s!", mentionUsers, Global.broCoin.getAsMention()))
                .setColor(Global.embedColor)
                .setFooter(Global.footertext, Global.footerpfp)
                .build();
    }


}
