/*
 * Copyright (C) 2017 Dennis Neufeld
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package space.npstr.wolfia.commands.util;

import net.dv8tion.jda.core.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.npstr.wolfia.App;
import space.npstr.wolfia.Config;
import space.npstr.wolfia.commands.BaseCommand;
import space.npstr.wolfia.commands.CommRegistry;
import space.npstr.wolfia.commands.CommandContext;
import space.npstr.wolfia.commands.GuildCommandContext;
import space.npstr.wolfia.events.WolfiaGuildListener;
import space.npstr.wolfia.utils.discord.TextchatUtils;

import javax.annotation.Nonnull;

/**
 * Created by napster on 07.01.18.
 * <p>
 * Allows users to add / remove special roles in the Wolfia Lounge
 */
public class RankCommand extends BaseCommand {

    private static final Logger log = LoggerFactory.getLogger(RankCommand.class);

    public RankCommand(@Nonnull final String name, @Nonnull final String... aliases) {
        super(name, aliases);
    }

    @Override
    protected boolean execute(@Nonnull final CommandContext commandContext) {
        final GuildCommandContext context = commandContext.requireGuild();
        if (context == null) {
            return false;
        }

        if (context.guild.getIdLong() != App.WOLFIA_LOUNGE_ID) {
            context.reply(String.format("This command is restricted to the official Wolfia Lounge. Say `%s` to get invited.",
                    Config.PREFIX + CommRegistry.COMM_TRIGGER_INVITE));
            return false;
        }

        if (!context.hasArguments()) {
            context.help();
            return false;
        }

        final Role role;
        if (TextchatUtils.isSimilarLower("Announcements", context.rawArgs)) {
            role = context.guild.getRoleById(WolfiaGuildListener.ANNOUNCEMENTS_ROLE_ID);
            if (role == null) {
                log.warn("Did the Announcements role disappear in the Wolfia Lounge?");
                return false;
            }
        } else if (TextchatUtils.isSimilarLower("AlphaWolves", context.rawArgs)) {
            role = context.guild.getRoleById(WolfiaGuildListener.ALPHAWOLVES_ROLE_ID);
            if (role == null) {
                log.warn("Did the AlphaWolves role disappear in the Wolfia Lounge?");
                return false;
            }
        } else {
            context.help();
            return false;
        }

        if (context.member.getRoles().contains(role)) {
            context.guild.getController().removeSingleRoleFromMember(context.member, role).queue();
            context.replyWithName(String.format("removed role `%s` from you.", role.getName()));
        } else {
            context.guild.getController().addSingleRoleToMember(context.member, role).queue();
            context.replyWithName(String.format("added role `%s` to you.", role.getName()));
        }
        return true;
    }

    @Nonnull
    @Override
    protected String help() {
        return invocation() + " AlphaWolves OR Announcements"
                + "\n#Add or remove special roles of the official Wolfia Lounge for you.";
    }
}
