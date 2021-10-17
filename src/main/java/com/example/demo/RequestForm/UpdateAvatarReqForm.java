package com.example.demo.RequestForm;

import com.example.demo.Validators.ImageName.ValidImageName;
import com.example.demo.domain.Image;
import lombok.Data;

@Data
public class UpdateAvatarReqForm {
    Image avatar;
}
