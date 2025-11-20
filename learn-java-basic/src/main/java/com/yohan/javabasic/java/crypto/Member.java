package com.yohan.javabasic.java.crypto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    private String phoneNumber;

    private String name;

    private String identityCard;

    private String idCardFrontImage;

    private String idCardBackImage;

    private String liveFaceImage;
}