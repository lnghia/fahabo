package com.example.demo.Helpers;

import com.dropbox.core.v2.DbxClientV2;
import com.example.demo.DropBox.DropBoxAuthenticator;
import com.example.demo.DropBox.DropBoxUploader;
import com.example.demo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.HashMap;

@Component
public class UserHelper {
    private DropBoxAuthenticator dropBoxAuthenticator;

    public String DEFAULT_AVATAR = "https://thumbs.dreamstime.com/b/default-avatar-profile-trendy-style-social-media-user-icon-187599373.jpg";

    @Autowired
    public UserHelper(DropBoxAuthenticator dropBoxAuthenticator) {
        this.dropBoxAuthenticator = dropBoxAuthenticator;
    }

    public String createShareLink(String uri) {
        DbxClientV2 clientV2 = dropBoxAuthenticator.authenticateDropBoxClient();
        DropBoxUploader uploader = new DropBoxUploader(clientV2);

        return uploader.createSharedLink(uri);
    }

    public HashMap<String, Object> UserToJson(User user){
        HashMap<String, Object> rs = new HashMap<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        String avatarUri = createShareLink(user.getAvatar());

        rs.put("id", user.getId());
        rs.put("username", user.getUsername());
        rs.put("name", user.getName());
        rs.put("phoneNumber", user.getPhoneNumber());
        rs.put("email", user.getEmail());
        rs.put("isValidEmail", user.getValidEmail());
        rs.put("isValidPhoneNumber", user.getValidPhoneNumber());
        rs.put("birthday", (user.getBirthday() != null) ? formatter.format(user.getBirthday()) : "");
        rs.put("contactId", user.getContactId());
        rs.put("languageCode", (user.getLanguageCode() == null) ? null : user.getLanguageCode().trim());
        rs.put("authType", user.getSocialAccountType().getJson());
        rs.put("avatar", (avatarUri != null) ? avatarUri : user.getAvatar());
        rs.put("familyNum", user.getUserInFamilies().size());

        return rs;
    }
}
