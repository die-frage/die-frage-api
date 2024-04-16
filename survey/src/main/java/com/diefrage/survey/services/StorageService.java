package com.diefrage.survey.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.diefrage.exceptions.TypicalServerException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StorageService {
    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    @Value("${application.bucket.url}")
    private String bucketUrl;

    @Value("${application.telegram.url}")
    private String telegramLink;

    public String getTelegramLink(String code) {
        return this.telegramLink + "?start=" + code;
    }

    public String uploadFile(String code) {
        try {
            String fileName = String.valueOf(System.currentTimeMillis());
            BufferedImage image = generateImage(code);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            byte[] byteArray = os.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(byteArray.length);
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, bis, metadata));
            return bucketUrl + fileName;
        } catch (IOException e) {
            TypicalServerException.INTERNAL_EXCEPTION.throwException();
        }
        return null;
    }

    private BufferedImage generateImage(String code) {
        String activeLink = this.telegramLink + "?start=" + code;
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = qrCodeWriter.encode(activeLink, BarcodeFormat.QR_CODE, 200, 200, hints);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            TypicalServerException.INTERNAL_EXCEPTION.throwException();
        }
        return null;
    }

    public void deleteImage(String fileName) {
        s3Client.deleteObject(bucketName, fileName.replace(bucketUrl, ""));
    }
}
