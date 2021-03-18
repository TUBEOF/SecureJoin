package de.tubeof.securejoin.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import de.tubeof.securejoin.data.Cache;
import de.tubeof.securejoin.data.Data;
import de.tubeof.securejoin.data.MySQL;
import de.tubeof.securejoin.main.SecureJoin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class GoogleAuthenticatorManager {

    private final Data data = SecureJoin.getData();
    private final Cache cache = SecureJoin.getCache();
    private final MySQL mySQL = SecureJoin.getMySQL();

    public GoogleAuthenticatorManager() {}

    private ArrayList<UUID> verifingPlayers = new ArrayList<>();

    public String generateAuthKey() {
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        GoogleAuthenticatorKey googleAuthenticatorKey = googleAuthenticator.createCredentials();
        return googleAuthenticatorKey.getKey();
    }

    public void generateQrCode(String authKey, String url) throws WriterException, IOException {
        File tmpFolder = new File("plugins/SecureJoin/tmp");
        tmpFolder.mkdir();

        BitMatrix matrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 128, 128);
        FileOutputStream fileOutputStream = new FileOutputStream("plugins/SecureJoin/tmp/" + authKey + ".png");
        MatrixToImageWriter.writeToStream(matrix, "png", fileOutputStream);
    }

    public Boolean isCodeValid(String authKey, Integer code) {
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        return googleAuthenticator.authorize(authKey, code);
    }

    public void changePlayerVerifyState(UUID uuid, Boolean state) {
        if(state) verifingPlayers.add(uuid);
        else verifingPlayers.remove(uuid);
    }

    public Boolean isPlayerVerifing(UUID uuid) {
        return verifingPlayers.contains(uuid);
    }

}
