package utils;

import java.nio.charset.Charset;

/**
 * Commonly used constants.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class UpasConstants {

    public final static Charset UTF8 = Charset.forName("UTF-8");

    public final static int DAO_RESULT_OK = 1;
    public final static int DAO_RESULT_DUPLICATED = 2;
    public final static int DAO_RESULT_NOT_AFFECTED = 0;

    public final static String FLASH_MSG_PREFIX_ERROR = "_E_:";

    public final static String SYSTEM_APP_ID = "upas";

    public final static String SYSTEM_ADMIN_USER_ID = "1";
    public final static String SYSTEM_ADMIN_USER_NAME = "admin";
    public final static String SYSTEM_ADMIN_USER_PASSWORD = "secret";
    public final static String SYSTEM_ADMIN_USER_EMAIL = "admin@localhost";
    public final static int GROUP_ADMIN = 1;

    public final static String SYSTEM_APP_PERM_PREFIX = "app_";

    public final static String DF_FULL = "yyyy-MM-dd HH:mm:ss";
    public final static String DF_HHMMSS = "HH:mm:ss";

    public final static int RESPONSE_OK = 200;
    public final static int RESPONSE_NOT_FOUND = 404;
    public final static int RESPONSE_ACCESS_DENIED = 403;
    public final static int RESPONSE_CLIENT_ERROR = 400;
    public final static int RESPONSE_SERVER_ERROR = 500;
}
