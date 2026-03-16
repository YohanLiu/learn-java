package com.yohan.javabasic.java.hlht;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AesDataUtil {

  // 编码方式
  public static final String bm = "UTF-8";

  /**
   * 加密
   *
   * @param data         被加密数据
   * @param dataSecretIV 初始向量 AES 为16bytes. DES 为8bytes
   * @param dataSecret   私钥  AES固定格式为128/192/256 bits.即：16/24/32bytes。DES固定格式为128bits，即8bytes。
   * @return
   */
  public static String encrypt (String data, String dataSecretIV, String dataSecret) {
    // 加密方式： AES128(CBC/PKCS5Padding) + Base64
    try {
      if (data == null || data.isEmpty()) {
        return "";
      }
      IvParameterSpec zeroIv = new IvParameterSpec(dataSecretIV.getBytes());
      // 两个参数，第一个为私钥字节数组， 第二个为加密方式 AES或者DES
      SecretKeySpec key = new SecretKeySpec(dataSecret.getBytes(), "AES");
      // 实例化加密类，参数为加密方式，要写全
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); //PKCS5Padding比PKCS7Padding效率高，PKCS7Padding可支持IOS加解密
      //初始化，此方法可以采用三种方式，按加密算法要求来添加。
      // 1）无第三个参数
      // 2）第三个参数为SecureRandom random = new SecureRandom();中random对象，随机数。(AES不可采用这种方法)（
      // 3）采用此代码中的IVParameterSpec
      cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
      // 加密操作,返回加密后的字节数组，然后需要编码。主要编解码方式有Base64, HEX, UUE,7bit等等。此处看服务器需要什么编码方式
      byte[] encryptedData = cipher.doFinal(data.getBytes(bm));

      return Base64Encoder.encode(encryptedData).replace("\n", "").replace("\r", "");
    } catch (Exception e) {
      log.error("异常:Data数据加密失败[AES/CBC模式]", e);
    }
    return null;
  }

  /**
   * CBC模式解密
   *
   * @param encrypted
   * @return
   */
  public static String decrypt (String encrypted, String dataSecretIV, String dataSecret) {
    try {
      if (encrypted == null || encrypted.isEmpty()) {
        return "";
      }
      byte[] byteMi = Base64Decoder.decode(encrypted);
      IvParameterSpec zeroIv = new IvParameterSpec(dataSecretIV.getBytes());
      SecretKeySpec key = new SecretKeySpec(dataSecret.getBytes(), "AES");
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      // 与加密时不同 Cipher.DECRYPT_MODE
      cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
      byte[] decryptedData = cipher.doFinal(byteMi);

      return new String(decryptedData, bm);
    } catch (Exception e) {
      log.error("异常:Data数据解密失败[AES/CBC模式]", e);
    }
    return null;
  }

  /**
   * ECB模式加密
   *
   * @param data
   * @return
   */
  public static String ecbEncrypt (String data, String dataSecret) {
    try {
      if (StrUtil.isBlank(data)) {
        return "";
      }

      // 创建AES秘钥
      SecretKeySpec key = new SecretKeySpec(dataSecret.getBytes(CharsetUtil.CHARSET_UTF_8), "AES");
      // 创建密码器
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      // 初始化加密器
      cipher.init(Cipher.ENCRYPT_MODE, key);

      // 加密操作,返回加密后的字节数组，然后需要编码。主要编解码方式有Base64, HEX, UUE,7bit等等。此处看服务器需要什么编码方式
      byte[] encryptedData = cipher.doFinal(data.getBytes(CharsetUtil.CHARSET_UTF_8));

      return Base64Encoder.encode(encryptedData).replace("\n", "").replace("\r", "");
    } catch (Exception e) {
      log.error("异常:Data数据加密失败[AES/ECB模式]", e);
    }
    return "";
  }

  /**
   * ECB模式解密
   *
   * @param data
   * @return
   */
  public static String ecbDecrypt (String data, String dataSecret) {
    try {
      if (StrUtil.isBlank(data)) {
        return "";
      }

      byte[] byteMi = Base64Decoder.decode(data);

      SecretKeySpec key = new SecretKeySpec(dataSecret.getBytes(), "AES");
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      // 与加密时不同 Cipher.DECRYPT_MODE
      cipher.init(Cipher.DECRYPT_MODE, key);
      byte[] decodeByte = cipher.doFinal(byteMi);
      return new String(decodeByte, CharsetUtil.CHARSET_UTF_8);
    } catch (Exception e) {
      log.error("异常:Data数据解密失败[AES/ECB模式]", e);
    }
    return "";
  }

  /**
   * AES-CBC加密
   *
   * @param content
   * @param dataSecret
   * @param dataSecretIv
   * @return
   * @throws Exception
   */
  public static String cbcEncrypt (String content, String dataSecret, String dataSecretIv) {
    try {
      if (StrUtil.isBlank(content)) {
        return "";
      }

      SecretKeySpec keySpec = new SecretKeySpec(dataSecret.getBytes(CharsetUtil.CHARSET_UTF_8), "AES");
      // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
      IvParameterSpec iv = new IvParameterSpec(dataSecretIv.getBytes(CharsetUtil.CHARSET_UTF_8));

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
      byte[] encodeByte = cipher.doFinal(content.getBytes(CharsetUtil.CHARSET_UTF_8));
      return Base64Encoder.encode(encodeByte).replaceAll("\r\n", "").replaceAll("\n", "");
    } catch (Exception e) {
      log.error("异常:Data数据加密失败[AES/CBC模式]", e);
    }
    return "";
  }

  /**
   * AES-CBC解密
   *
   * @param content
   * @param dataSecret
   * @param dataSecretIv
   * @return
   * @throws Exception
   */
  public static String cbcDecrypt (String content, String dataSecret, String dataSecretIv) {
    try {
      if (StrUtil.isBlank(content)) {
        return "";
      }
      byte[] byteMi = Base64Decoder.decode(content);

      SecretKeySpec keySpec = new SecretKeySpec(dataSecret.getBytes(CharsetUtil.CHARSET_UTF_8), "AES");
      // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
      IvParameterSpec iv = new IvParameterSpec(dataSecretIv.getBytes(CharsetUtil.CHARSET_UTF_8));

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
      byte[] decodeByte = cipher.doFinal(byteMi);
      return new String(decodeByte, CharsetUtil.CHARSET_UTF_8);
    } catch (Exception e) {
      log.error("异常:Data数据解密失败[AES/CBC模式]", e);
    }
    return "";
  }

  // ... existing code ...
  public static void main(String[] args) {
    try {
      // 互联互通协议解密
      String rawResponse = new String(java.nio.file.Files.readAllBytes(
          java.nio.file.Paths.get("learn-java-basic/src/main/java/com/yohan/javabasic/java/hlht/密文.txt")),
          java.nio.charset.StandardCharsets.UTF_8);

      String s = cbcDecrypt(rawResponse, "oejdwEgw5RjoaxF4", "P70QesjSuwLZY3jS");
      System.out.println(s);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
// ... existing code ...


}
