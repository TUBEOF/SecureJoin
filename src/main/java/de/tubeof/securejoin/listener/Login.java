package de.tubeof.securejoin.listener;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Login implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

    }
}
