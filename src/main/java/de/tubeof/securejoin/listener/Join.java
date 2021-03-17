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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.*;
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

        } else {
            String key = googleAuthenticatorManager.generateAuthKey();
            player.sendMessage(data.getPrefix() + "§aDein Key: §e" + key);

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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
