package net.aeronetwork.core.command.impl.player;

import net.aeronetwork.core.command.Command;
import net.aeronetwork.core.command.info.CommandDetails;
import net.aeronetwork.core.player.AeroPlayer;
import net.aeronetwork.core.util.FM;

public class MyCoinsCommand extends Command {

    public MyCoinsCommand() {
        super("mycoins", "View your coin balance.", "/mycoins", null);
    }

    @Override
    public void execute(AeroPlayer player, CommandDetails details) {
        if(player != null) {
            player.sendMessage(FM.mainFormat("Coins", "You have §c" + player.getCoins() + "§e Coin(s)."));
        }
    }
}
