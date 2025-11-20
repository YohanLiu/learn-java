package com.yohan.javabasic.java.crypto;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.*;
import java.util.stream.Collectors;

public class Sm4Utils2 {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String ENCODING = "UTF-8";
    public static final String ALGORITHM_NAME = "SM4";
    // 加密算法/分组加密模式/分组填充方式
    // PKCS5Padding-以8个字节为一组进行分组加密
    // 定义分组加密模式使用：PKCS5Padding
    public static final String ALGORITHM_NAME_ECB_PADDING7 = "SM4/ECB/PKCS5Padding";
    public static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/PKCS7Padding";
    // 64-16位16进制；128-32位16进制；256-64位16进制
    public static final int DEFAULT_KEY_SIZE = 128;

    /**
     * 生成ECB暗号
     *
     * @param algorithmName 算法名称
     * @param mode          模式
     * @param key
     * @return
     * @throws Exception
     * @explain ECB模式（电子密码本模式：Electronic codebook）
     */
    private static Cipher generateEcbCipher(String algorithmName, int mode, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME);
        Key sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        cipher.init(mode, sm4Key);
        return cipher;
    }

    // 产生密钥

    /**
     * 自动生成密钥
     * HEX秘钥 32 位 16进制
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @explain
     */
    public static byte[] generateKey() throws Exception {
        return generateKey(DEFAULT_KEY_SIZE);
    }

    /**
     * @param keySize
     * @return
     * @throws Exception
     * @explain
     */
    public static byte[] generateKey(int keySize) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_NAME, BouncyCastleProvider.PROVIDER_NAME);
        kg.init(keySize, new SecureRandom());
        return kg.generateKey().getEncoded();
    }

    /**
     * sm4加密
     * 秘钥  返回报文HEX
     * @param hexKey   HEX秘钥
     * @param paramStr 待加密字符串
     * @return 返回16进制的加密字符串
     * @throws Exception
     * @explain 加密模式：ECB
     * 密文长度不固定，会随着被加密字符串长度的变化而变化
     */
    public static String encryptHex(String hexKey, String paramStr) throws Exception {
        String cipherText = "";
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] srcData = paramStr.getBytes(ENCODING);
        byte[] cipherArray = encrypt_Ecb_Padding(keyData, srcData);
        cipherText = ByteUtils.toHexString(cipherArray);
        return cipherText;
    }

    /**
     * sm4加密
     * str 秘钥  返回报文HEX
     * @param strKey  str秘钥
     * @param paramStr 待加密字符串
     * @return 返回16进制的加密字符串
     * @throws Exception
     * @explain 加密模式：ECB
     * 密文长度不固定，会随着被加密字符串长度的变化而变化
     */
    public static String encryptStrHex(String strKey, String paramStr) throws Exception {
        String cipherText = "";
        byte[] keyData = strKey.getBytes(ENCODING);
        byte[] srcData = paramStr.getBytes(ENCODING);
        // 加密后的数组
        byte[] cipherArray = encrypt_Ecb_Padding(keyData, srcData);
        cipherText = ByteUtils.toHexString(cipherArray);
        return cipherText;
    }
    /**
     * sm4加密
     * HEX 秘钥  返回报文Base64
     * @param hexKey   HEX秘钥
     * @param paramStr 待加密字符串
     * @return 返回Base64 的加密字符串
     * @throws Exception
     * @explain 加密模式：ECB
     * 密文长度不固定，会随着被加密字符串长度的变化而变化
     */
    public static String encryptHexBase64(String hexKey, String paramStr) throws Exception {
        String cipherText = "";
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] srcData = paramStr.getBytes(ENCODING);
        byte[] cipherArray = encrypt_Ecb_Padding(keyData, srcData);
        cipherText =Base64.encodeBase64String(cipherArray);
        return cipherText;
    }
    /**
     * sm4加密
     * str 秘钥  返回报文Base64
     * @param strKey   str 秘钥
     * @param paramStr 待加密字符串
     * @return 返回Base64 的加密字符串
     * @throws Exception
     * @explain 加密模式：ECB
     * 密文长度不固定，会随着被加密字符串长度的变化而变化
     */
    public static String encryptStrBase64(String strKey, String paramStr) throws Exception {
        String cipherText = "";
        byte[] keyData = strKey.getBytes(ENCODING);
        byte[] srcData = paramStr.getBytes(ENCODING);
        // 加密后的数组
        byte[] cipherArray = encrypt_Ecb_Padding(keyData, srcData);
        cipherText =Base64.encodeBase64String(cipherArray);
        return cipherText;
    }

    /**
     * 加密模式之Ecb
     *
     * @param key
     * @param data
     * @return
     * @throws Exception
     * @explain
     */
    public static byte[] encrypt_Ecb_Padding(byte[] key, byte[] data) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * sm4解密
     *
     * @param hexKey     16进制密钥
     * @param cipherText 16进制的加密字符串
     * @return 解密后的字符串
     * @throws Exception
     * @explain 解密模式：采用ECB
     */
    public static String decryptHex(String hexKey, String cipherText) throws Exception {
        // 用于接收解密后的字符串
        String decryptStr = "";
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] cipherData = ByteUtils.fromHexString(cipherText);
        // 解密
        byte[] srcData = decrypt_Ecb_Padding(keyData, cipherData);
        decryptStr = new String(srcData, ENCODING);
        return decryptStr;
    }


    /**
     * sm4解密
     *
     * @param strKey      密钥
     * @param cipherText 16进制的加密字符串
     * @return 解密后的字符串
     * @throws Exception
     * @explain 解密模式：采用ECB
     */
    public static String decryptStrHex(String strKey, String cipherText) throws Exception {
        // 用于接收解密后的字符串
        String decryptStr = "";
         byte[]  keyData = strKey.getBytes(ENCODING);
        byte[] cipherData = ByteUtils.fromHexString(cipherText);
        // 解密
        byte[] srcData = decrypt_Ecb_Padding(keyData, cipherData);
        decryptStr = new String(srcData, ENCODING);
        return decryptStr;
    }

    /**
     * sm4解密
     *
     * @param hexKey 16进制密钥
     * @param cipherText Base64加密字符串
     * @return 解密后的字符串
     * @throws Exception
     * @explain 解密模式：采用ECB
     */
    public static String decryptHexStr(String hexKey, String cipherText) throws Exception {
        String decryptStr = "";
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        byte[] cipherData =Base64.decodeBase64(cipherText);
        // 解密
        byte[] srcData = decrypt_Ecb_Padding(keyData, cipherData);
        decryptStr = new String(srcData, ENCODING);
        return decryptStr;
    }

    /**
     * sm4解密
     *
     * @param strKey   密钥
     * @param cipherText Base64加密字符串
     * @return 解密后的字符串
     * @throws Exception
     * @explain 解密模式：采用ECB
     */
    public static String decryptStr(String strKey, String cipherText) throws Exception {
        // 用于接收解密后的字符串
        String decryptStr = "";
        byte[]  keyData = strKey.getBytes(ENCODING);
        byte[] cipherData =Base64.decodeBase64(cipherText);
        // 解密
        byte[] srcData = decrypt_Ecb_Padding(keyData, cipherData);
        decryptStr = new String(srcData, ENCODING);
        return decryptStr;
    }
    /**
     * 解密
     *
     * @param key
     * @param cipherText
     * @return
     * @throws Exception
     * @explain
     */
    public static byte[] decrypt_Ecb_Padding(byte[] key, byte[] cipherText) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(cipherText);
    }

    /**
     * HEX key ,加密报文HEX ,参数处理为HEX校验
     * 校验加密前后的字符串是否为同一数据
     *
     * @param hexKey     16进制密钥（忽略大小写）
     * @param cipherText 16进制加密后的字符串
     * @param paramStr   加密前的字符串
     * @return 是否为同一数据
     * @throws Exception
     * @explain
     */
    public static boolean verifyEcb(String hexKey, String cipherText, String paramStr) throws Exception {
        // 用于接收校验结果
        boolean flag = false;
        // hexString-->byte[]
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        //  byte[] keyData = hexKey.getBytes(ENCODING);
        // 将16进制字符串转换成数组
        byte[] cipherData = ByteUtils.fromHexString(cipherText);
        //  byte[] cipherData = Base64.decodeBase64(cipherText);
        // 解密
        byte[] decryptData = decrypt_Ecb_Padding(keyData, cipherData);
        // 将原字符串转换成byte[]
//        byte[] srcData = paramStr.getBytes(ENCODING);
        byte[] hexData = ByteUtils.fromHexString(paramStr);
        // 判断2个数组是否一致
        flag = Arrays.equals(decryptData, hexData);
        return flag;
    }

    public static void main(String[] args)throws Exception {
        CustomerTelRequestDTO d = new CustomerTelRequestDTO();
        d.setAppId("020_1000004_TYCS");
        d.setTimestamp(System.currentTimeMillis() + "");
        d.setRequestId(UUID.randomUUID()+"");
        d.setMethod("append");
        d.setReportType("1");
        d.setPhoneType("1");
        Member m = new Member();
        m.setPhoneNumber("15511421124");
        m.setName("张三测试");
        m.setIdentityCard("100110199909064411");
        m.setIdCardFrontImage(java.util.Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("learn-java-basic/src/main/java/com/yohan/javabasic/java/file/playgirl.jpg"))));
        m.setIdCardBackImage(java.util.Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("learn-java-basic/src/main/java/com/yohan/javabasic/java/file/playgirl.jpg"))));
        m.setLiveFaceImage(java.util.Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get("learn-java-basic/src/main/java/com/yohan/javabasic/java/file/playgirl.jpg"))));

        String jsonString = JSON.toJSONString(d);
        Map<String,String> map = JSON.parseObject(jsonString, Map.class);
        Map<String, String> treeMap = new TreeMap<>(map);
        String appKey = "434cd6345fea94ccfe89537279288fc7";
        String collect = treeMap.entrySet().stream().filter(e -> org.springframework.util.StringUtils.hasText(e.getValue()))
                .map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
        String signStr = SecureUtil.md5((collect+"&appKey="+appKey)).toUpperCase(Locale.ROOT);

        d.setSign(signStr);
        try {
            d.setMember(Sm4Utils2.encryptHex(appKey, JSON.toJSONString(m)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(d);

    }
}
