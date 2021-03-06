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

package space.npstr.wolfia.commands.ingame;

import net.dv8tion.jda.core.entities.ChannelType;
import space.npstr.wolfia.Config;
import space.npstr.wolfia.commands.CommRegistry;
import space.npstr.wolfia.commands.CommandContext;
import space.npstr.wolfia.commands.GameCommand;
import space.npstr.wolfia.game.Game;
import space.npstr.wolfia.game.Player;
import space.npstr.wolfia.game.definitions.Games;
import space.npstr.wolfia.game.exceptions.IllegalGameStateException;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by napster on 14.12.17.
 */
public class ItemsCommand extends GameCommand {

    public ItemsCommand(final String trigger, final String... aliases) {
        super(trigger, aliases);
    }

    @Nonnull
    @Override
    public String help() {
        return invocation() + "\n#Show your items.";
    }

    @SuppressWarnings("Duplicates")
    @Override
    public boolean execute(@Nonnull final CommandContext context) throws IllegalGameStateException {
        //this command is expected to be called by a player in a private channel

        if (context.channel.getType() != ChannelType.PRIVATE) {
            context.replyWithMention("items can only be checked in private messages.");
            return false;
        }

        //todo handle a player being part of multiple games properly
        boolean issued = false;
        for (final Game g : Games.getAll().values()) {
            if (g.isUserPlaying(context.invoker) && g.isLiving(context.invoker)) {
                final Player p = g.getPlayer(context.invoker);
                if (p.items.isEmpty()) {
                    context.reply("You don't possess any items.");
                } else {
                    final List<String> itemsList = p.items.stream().map(i -> i.item.emoji + ": " + i.item.explanation).collect(Collectors.toList());
                    context.reply("You have the following items:\n" + String.join("\n", itemsList));
                }
                issued = true;
            }
        }
        if (!issued) {
            context.replyWithMention(String.format("you aren't alive and in any ongoing game currently. Say `%s` to get started!",
                    Config.PREFIX + CommRegistry.COMM_TRIGGER_HELP));
            return false;
        }
        return true;
    }
}
