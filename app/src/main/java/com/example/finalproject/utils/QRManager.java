package com.example.finalproject.utils;

import android.graphics.Bitmap;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.EnumMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class QRManager {
    private static final int WIDTH = 512;
    private static final int HEIGHT = 512;
    private static final String AES_MODE = "AES/ECB/PKCS5Padding";

    private static QRManager instance;

    private QRManager() {
    }

    public static QRManager getInstance() {
        if (instance == null) {
            instance = new QRManager();
        }
        return instance;
    }

    public Bitmap generateQRCode(String text) {
        return createQRcode(AES256encryption(text));
    }

    private String AES256encryption(String input) {
        try {
            // Obtener la clave base (UID)
            String rawKey = SessionDataManager.getInstance().getCurrentUser().getUid();

            // Hash SHA-256 para asegurar longitud de 32 bytes (AES-256)
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = sha.digest(rawKey.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            // Inicializar cifrado
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));

            // Retornar como Base64 (string seguro para QR)
            return Base64.encodeToString(encrypted, Base64.NO_WRAP);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap createQRcode(String input) {
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = new MultiFormatWriter().encode(input, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);

            Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.RGB_565);
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF); // negro/blanco
                }
            }
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}