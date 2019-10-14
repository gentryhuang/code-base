package com.code.async.message.client.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class MD5Util {
    private static final Logger logger = LoggerFactory.getLogger(MD5Util.class);

    private static final int LO_BYTE = 0x0f;
    private static final int MOVE_BIT = 4;
    private static final int HI_BYTE = 0xf0;

    private static final String[] HEX_DIGITS = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private static String byteArrayToHexString(byte[] b) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            buf.append(byteToHexString(b[i]));
        }
        return buf.toString();
    }

    private static String byteToHexString(byte b) {
        return HEX_DIGITS[(b & HI_BYTE) >> MOVE_BIT] + HEX_DIGITS[b & LO_BYTE];
    }


    /**
     * md5
     *
     * @param origin
     * @return
     */
    public static String MD5(String origin) {
        if (origin == null) {
            throw new IllegalArgumentException(("MULTI_000523"));
        }
        String resultString ;

        resultString = new String(origin);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString
                    .getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return resultString;

    }


    /**
     * hmac 加密
     * @param data
     * @param secret
     * @return
     * @throws IOException
     */
    public static String hmac(String data, String secret) throws IOException {
        byte[] bytes = null;
        try{
            SecretKey secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacMD5");
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            bytes = mac.doFinal(data.getBytes("UTF-8"));
        } catch (GeneralSecurityException e){
            logger.error("Error in MD5Util.md5:" + e);
        }
        return byte2hex(bytes);
    }

    /**
     *  把二进制转化为大写的十六进制
     *
     * @param bytes
     * @return
     */
    private static String byte2hex(byte[] bytes){
        StringBuilder sign = new StringBuilder();
        for(int i = 0;i < bytes.length; i++){
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() == 1){
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    /**
     * 获取加密后的字符串
     *
     * @param value 待加密的字符串
     * @return
     */
    public static String getMD5String(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] e = md.digest(value.getBytes());
            return toHexString(e);
        } catch (NoSuchAlgorithmException e) {
            return StringUtils.EMPTY;
        }
    }

    //字节码转字符串
    private static String toHexString(byte[] bytes) {
        StringBuilder hs = new StringBuilder();
        for (int n = 0; n < bytes.length; n++) {
            String tmp = Integer.toHexString(bytes[n] & 0xff);
            if (tmp.length() == 1) {
                hs.append("0").append(tmp);
            } else {
                hs.append(tmp);
            }
        }

        return hs.toString();
    }

    public static boolean stringIsEmpty(String str){
        return str == null || str.length() == 0;
    }
}
