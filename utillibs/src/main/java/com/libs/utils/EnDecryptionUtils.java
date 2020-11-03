package com.libs.utils;

import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Created by qlang on 2017/7/21.
 */

public class EnDecryptionUtils {
    private final static String KEY_RES = "RSA";
    private final static String KEY_ALGORTHM = "RSA/ECB/PKCS1Padding";
    private final static String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /*-----------------对称 加解密---------start---------*/

    /**
     * 加密
     *
     * @param key  密钥
     * @param data 加密的数据
     * @return
     */
    public static String encode(String key, @NonNull String data) {
        return encode(key, data.getBytes());
    }

    /**
     * 加密
     *
     * @param data       待加密字符串
     * @param encryptKey 加密私钥，长度不能够小于8位
     * @return 加密后的字节数组，一般结合Base64编码使用
     */
    public static String encode(String encryptKey, @NonNull byte[] data) {
        String val = "";
        try {
            byte[] iv = {1, 2, 3, 4, 5, 6, 7, 8};
            IvParameterSpec zeroIv = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            byte[] encryptedData = cipher.doFinal(data);
            val = Base64.encodeToString(encryptedData, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }

    /**
     * 获取编码后的值
     *
     * @param key
     * @param data
     * @return
     * @throws Exception
     */
    public static String decodeValue(String key, @NonNull String data) {
        byte[] datas;
        String value = "";
        if (TextUtils.isEmpty(data)) return "";

        try {
            datas = decode(key, data);
            value = new String(datas);
        } catch (Exception e) {
            value = "";
        }
        return value;
    }

    /**
     * 解密
     *
     * @param decryptKey    待解密字符串
     * @param decryptString 解密私钥，长度不能够小于8位
     * @return 解密后的字节数组
     * @throws Exception 异常
     */
    public static byte[] decode(String decryptKey, String decryptString) {
        byte[] result = new byte[0];
        try {
            byte[] iv = {1, 2, 3, 4, 5, 6, 7, 8};
            byte[] byteMi = Base64.decode(decryptString, Base64.DEFAULT);
            IvParameterSpec zeroIv = new IvParameterSpec(iv);
//			      IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
            SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            result = cipher.doFinal(byteMi);
            //return new String(decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /*-----------------对称 加解密---------end---------*/

    /*-----------------非对称 加解密---------end---------*/

    /**
     * 初始化密钥
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> initKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(512);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        Map<String, Object> keyMap = new HashMap<>(2);
        keyMap.put("publicKey", publicKey);
        keyMap.put("privateKey", privateKey);

        return keyMap;
    }

    /**
     * 取得公钥，并转化为String类型
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPublicKey(@NonNull Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get("publicKey");
        return Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
    }

    /**
     * 取得私钥，并转化为String类型
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(@NonNull Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get("privateKey");
        return Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
    }

    /**
     * 用私钥加密
     *
     * @param data 加密数据
     * @param key  密钥
     * @return
     * @throws Exception
     */
    public static String encryptByPrivateKey(@NonNull String data, String key) throws Exception {
        // 解密密钥
        byte[] keyBytes = Base64.decode(key.getBytes(), Base64.DEFAULT);
        // 取私钥
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_RES);
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        // 对数据加密
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        Cipher cipher = Cipher.getInstance(KEY_ALGORTHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] resultBytes = cipher.doFinal(data.getBytes("UTF-8"));
        return Base64.encodeToString(resultBytes, Base64.DEFAULT);
    }

    /**
     * 用私钥解密
     *
     * @param data 加密数据
     * @param key  密钥
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(@NonNull String data, String key) throws Exception {
        // 对私钥解密
        byte[] keyBytes = Base64.decode(key.getBytes(), Base64.DEFAULT);

        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_RES);
        Key privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        // 对数据解密
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        Cipher cipher = Cipher.getInstance(KEY_ALGORTHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] dataBytes = Base64.decode(data.getBytes(), Base64.DEFAULT);
        byte[] resultBytes = cipher.doFinal(dataBytes);
        return new String(resultBytes, "UTF-8");
    }

    /**
     * 用公钥加密
     *
     * @param data 加密数据
     * @param key  密钥
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(@NonNull String data, String key) throws Exception {
        //对公钥解密
        byte[] keyBytes = Base64.decode(key.getBytes(), Base64.DEFAULT);
        //取公钥
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_RES);
        Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        //对数据解密
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        Cipher cipher = Cipher.getInstance(KEY_ALGORTHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] resultBytes = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(resultBytes, Base64.DEFAULT);
    }

    /**
     * 用公钥解密
     *
     * @param data 加密数据
     * @param key  密钥
     * @return
     * @throws Exception
     */
    public static String decryptByPublicKey(@NonNull String data, String key) throws Exception {
        //对公钥解密
        byte[] keyBytes = Base64.decode(key.getBytes(), Base64.DEFAULT);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_RES);
        Key publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        //对数据解密
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        Cipher cipher = Cipher.getInstance(KEY_ALGORTHM);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] dataBytes = Base64.decode(data.getBytes(), Base64.DEFAULT);
        byte[] resultBytes = cipher.doFinal(dataBytes);
        return new String(resultBytes, "UTF-8");
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       //加密数据
     * @param privateKey //私钥
     * @return
     * @throws Exception
     */
    public static String sign(@NonNull byte[] data, String privateKey) throws Exception {
        // 解密私钥
        byte[] keyBytes = Base64.decode(privateKey.getBytes(), Base64.DEFAULT);
        // 构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        // 指定加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_RES);
        // 取私钥匙对象
        PrivateKey privateKey2 = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey2);
        signature.update(data);
        return Base64.encodeToString(signature.sign(), Base64.DEFAULT);
    }

    /**
     * 校验数字签名
     *
     * @param data      加密数据
     * @param publicKey 公钥
     * @param sign      数字签名
     * @return
     * @throws Exception
     */
    public static boolean verify(@NonNull byte[] data, String publicKey, String sign) throws Exception {
        //解密公钥
        byte[] keyBytes = Base64.decode(publicKey.getBytes(), Base64.DEFAULT);
        //构造X509EncodedKeySpec对象
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        //指定加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_RES);
        //取公钥匙对象
        PublicKey publicKey2 = keyFactory.generatePublic(x509EncodedKeySpec);

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey2);
        signature.update(data);
        //验证签名是否正常
        return signature.verify(Base64.decode(sign, Base64.DEFAULT));

    }

    /**
     * <P>
     * 私钥分段解密
     * </p>
     *
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String decrypt4PrivateKey(@NonNull String data, String privateKey) throws Exception {
        byte[] encryptedData = Base64.decode(data.getBytes(), Base64.DEFAULT);
        byte[] keyBytes = Base64.decode(privateKey, Base64.DEFAULT);

        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        Cipher cipher = Cipher.getInstance(KEY_ALGORTHM);
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
//        Log.e("QL", "-----de----- " + privateKey.length() + " -- " + keyBytes.length + " -- " + inputLen);
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {//len = k_len/8
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData, "utf-8");
    }

    /**
     * <p>
     * 公钥分段解密
     * </p>
     *
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String decrypt4PublicKey(@NonNull String data, String publicKey) throws Exception {
        byte[] encryptedData = Base64.decode(data.getBytes(), Base64.DEFAULT);
        byte[] keyBytes = Base64.decode(publicKey, Base64.DEFAULT);

        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        Cipher cipher = Cipher.getInstance(KEY_ALGORTHM);
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {//len = k_len/8
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData, "utf-8");
    }

    /**
     * <p>
     * 公钥分段加密
     * </p>
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(@NonNull byte[] data, String publicKey) throws Exception {
        byte[] keyBytes = Base64.decode(publicKey, Base64.DEFAULT);

        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        Cipher cipher = Cipher.getInstance(KEY_ALGORTHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) { //len = k_len/8-11
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return Base64.encodeToString(encryptedData, Base64.DEFAULT);
    }

    /**
     * <p>
     * 私钥分段加密
     * </p>
     *
     * @param data       源数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String encryptByPrivateKey(@NonNull byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decode(privateKey, Base64.DEFAULT);

        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        Cipher cipher = Cipher.getInstance(KEY_ALGORTHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {//len = k_len/8-11
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return Base64.encodeToString(encryptedData, Base64.DEFAULT);
    }
    /*-----------------非对称 加解密---------end---------*/

}
