package vip.xiaonuo.dbs.core.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.core.util.StrUtil;

/**
 * 密码加密工具类
 *
 * @author xuyuxiang
 * @date 2022/10/27 16:15
 */
public class PasswordEncryptUtil {
    
    // 使用固定的16字节密钥（128位）
    private static final byte[] KEY = new byte[]{
        0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
        0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10
    };
    
    private static final AES AES_ENCRYPT = SecureUtil.aes(KEY);

    /**
     * 加密密码
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encrypt(String password) {
        if (StrUtil.isEmpty(password)) {
            return password;
        }
        return AES_ENCRYPT.encryptHex(password);
    }

    /**
     * 解密密码
     *
     * @param encryptedPassword 加密后的密码
     * @return 原始密码
     */
    public static String decrypt(String encryptedPassword) {
        if (StrUtil.isEmpty(encryptedPassword)) {
            return encryptedPassword;
        }
        return AES_ENCRYPT.decryptStr(encryptedPassword);
    }
}