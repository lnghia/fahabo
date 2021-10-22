package com.example.demo.Helpers;

import com.example.demo.DropBox.ItemToUpload;
import com.example.demo.domain.Image;
import com.example.demo.domain.Photo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.mail.imap.protocol.Item;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Slf4j
public class Helper {
    private static Helper instance;

    ArrayList<Integer> dateNumInMonths = new ArrayList<>(List.of(
            0,
            31,
            28,
            31,
            30,
            31,
            30,
            31,
            31,
            30,
            31,
            30,
            31
    ));

    public final String DEFAULT_FAMILY_THUMBNAIL = "https://media.istockphoto.com/vectors/large-happy-family-is-standing-and-hugging-several-generations-with-vector-id1221390042?b=1&k=20&m=1221390042&s=612x612&w=0&h=DIY35-8Q2jZe8LMLdwXETf54sKwQL3_F6OCtXdTnB44=";

    public static Helper getInstance() {
        if (instance == null) instance = new Helper();
        return instance;
    }

    public HashSet<String> getUnauthenticatedEndpoints() {
        return new HashSet<>(
                List.of("/api/v1/login",
                        "/api/v1/users",
                        "/api/v1/token",
                        "/api/v1/register_with_email",
                        "/api/v1/register_with_phone",
                        "/api/v1/register_with_phone",
                        "/api/v1/getOTP",
                        "/api/v1/verify",
                        "/api/v1/lang_code",
                        "/api/v1/get_reset_password_otp",
                        "/api/v1/verify_reset_password",
                        "/api/v1/reset_password",
                        "/api/v1/country_code_list",
                        "/api/v1/users/temp"));
    }

    public String mapToJsonString(HashMap<Object, Object> map) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(map);
    }

    public boolean isLeapYear(int y) {
        if (y % 4 != 0) {
            return false;
        }
        if (y % 100 != 0) {
            return true;
        }
        if (y % 400 != 0) {
            return false;
        }

        return true;
    }

    public boolean isValidDate(int dd, int mm, int yyyy) {
        if (dd < 0 || mm < 0 || yyyy < 0) return false;

        dateNumInMonths.set(2, ((isLeapYear(yyyy) ? 29 : 28)));

        return (mm <= 12 && dateNumInMonths.get(mm) >= dd);
    }

    public ItemToUpload[] listOfImagesToArrOfItemToUpload(List<Image> images) {
        ItemToUpload[] rs = new ItemToUpload[images.size()];

        for (int i = 0; i < images.size(); ++i) {
            rs[i] = new ItemToUpload(images.get(i).getName(), images.get(i).getBase64Data());
        }

        return rs;
    }

    public String generatePhotoNameToUploadToAlbum(int familyId, int albumId, int photoId){
        return String.format("%d_%d_%d_%d.jpg", familyId, albumId, photoId, new Date().getTime());
    }

    public ItemToUpload[] listOfImagesToArrOfItemToUploadWithGeneratedName(List<Image> images, List<Photo> photos, int albumId, int familyId){
        ItemToUpload[] rs = new ItemToUpload[images.size()];

        for (int i = 0; i < images.size(); ++i) {
            int photoId = photos.get(i).getId();
            String name = generatePhotoNameToUploadToAlbum(familyId, albumId, photoId);

            photos.get(i).setName(name);
            images.get(i).setName(name);
            rs[i] = new ItemToUpload(images.get(i).getName(), images.get(i).getBase64Data());
        }

        return rs;
    }

    public ItemToUpload[] convertAImgToParaForUploadImgs(Image img){
        ItemToUpload[] rs = new ItemToUpload[1];

        rs[0] = new ItemToUpload(img.getName(), img.getBase64Data());

        return rs;
    }

    public byte[] base64ToBytes(String base64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));

            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String createSharedLink(String uri){
        if(uri == null || uri.isEmpty() || uri.isBlank()){
            log.info("No item to create.");
            return null;
        }

        URLConnection con = null;
        try {
            con = new URL(uri.replace("dl=0", "raw=1")).openConnection();
            con.connect();
            InputStream in = con.getInputStream();
            in.close();

            return con.getURL().toString();
        } catch (IOException e) {
            e.printStackTrace();
            log.info("Error while trying to get redirected uri for: " + uri);
        }

        return uri;
    }
}
