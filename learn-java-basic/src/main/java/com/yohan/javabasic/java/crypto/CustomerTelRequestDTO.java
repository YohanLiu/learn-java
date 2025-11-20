package com.yohan.javabasic.java.crypto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerTelRequestDTO {
    private String appId;

    private String timestamp;

	private String sign;

    private String requestId;

    private String method;

    private String reportType;

    private String phoneType;

    private String member;
}