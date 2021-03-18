package de.tubeof.securejoin.listener;

import com.github.johnnyjayjay.spigotmaps.RenderedMap;
import com.github.johnnyjayjay.spigotmaps.rendering.ImageRenderer;
import com.google.zxing.WriterException;
import de.tubeof.securejoin.data.Cache;
import de.tubeof.securejoin.data.Data;
import de.tubeof.securejoin.data.MySQL;
import de.tubeof.securejoin.main.SecureJoin;
import de.tubeof.securejoin.utils.GoogleAuthenticatorManager;
import de.tubeof.tubetils.api.itembuilder.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Join implements Listener {

    private final Data data = SecureJoin.getData();
    private final Cache cache = SecureJoin.getCache();
    private final MySQL mySQL = SecureJoin.getMySQL();
    private final ItemBuilder itemBuilder = new ItemBuilder("SecureJoinItemBuilder");
    private final GoogleAuthenticatorManager googleAuthenticatorManager = SecureJoin.getGoogleAuthenticatorManager();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(mySQL.hasPlayerAuthEnabled(player.getUniqueId())) {
            String authKey = mySQL.getAuthKey(player.getUniqueId());
            if(!mySQL.isAuthVerified(authKey)) {
                mySQL.deleteAuthKey(authKey);
                player.sendMessage(data.getPrefix() + "§cDein Auth-Key wurde bisher nicht verifiziert! Es wird ein neuer Key erstellt ...");

                String key = googleAuthenticatorManager.generateAuthKey();
                try {
                    googleAuthenticatorManager.generateQrCode(key, "otpauth://totp/Example:alice@google.com?secret=" + key + "&issuer=ExampleIssues");
                } catch (WriterException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    File image = new File("plugins/SecureJoin/tmp/" + key + ".png");
                    BufferedImage bufferedImage = ImageIO.read(image);
                    ImageRenderer renderer = ImageRenderer.create(bufferedImage);
                    RenderedMap map = RenderedMap.create(renderer);
                    player.getInventory().addItem(map.createItemStack());
                    player.updateInventory();
                    image.delete();

                    mySQL.createAuthKey(player.getUniqueId(), key);
                    googleAuthenticatorManager.changePlayerVerifyState(player.getUniqueId(), true);
                    player.sendMessage(data.getPrefix() + "§aBitte gebe den Code zur Verifizierung ein!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage(data.getPrefix() + "§aBitte geben den Code ein!");
                googleAuthenticatorManager.changePlayerVerifyState(player.getUniqueId(), true);
            }
        } else {
            String key = googleAuthenticatorManager.generateAuthKey();

            try {
                googleAuthenticatorManager.generateQrCode(key, "otpauth://totp/Example:alice@google.com?secret=" + key + "&issuer=ExampleIssues");
            } catch (WriterException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                File image = new File("plugins/SecureJoin/tmp/" + key + ".png");
                BufferedImage bufferedImage = ImageIO.read(image);
                ImageRenderer renderer = ImageRenderer.create(bufferedImage);
                RenderedMap map = RenderedMap.create(renderer);
                player.getInventory().addItem(map.createItemStack());
                player.updateInventory();
                image.delete();

                mySQL.createAuthKey(player.getUniqueId(), key);
                googleAuthenticatorManager.changePlayerVerifyState(player.getUniqueId(), true);
                player.sendMessage(data.getPrefix() + "§aBitte gebe den Code zur Verifizierung ein!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
