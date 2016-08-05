package bo.upas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.ddth.dao.BaseBo;

/**
 * Mapping {user:usergroup}
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class UserRoleBo extends BaseBo {

    public static UserRoleBo newInstance() {
        UserRoleBo role = new UserRoleBo();
        return role;
    }

    public static UserRoleBo newInstance(String userId, String groupId) {
        UserRoleBo role = newInstance();
        role.setUserId(userId).setGroupId(groupId);
        return role;
    }

    private final static String ATTR_UID = "uid";
    private final static String ATTR_GID = "gid";

    @JsonIgnore
    public String getUserId() {
        return getAttribute(ATTR_UID, String.class);
    }

    public UserRoleBo setUserId(String uid) {
        setAttribute(ATTR_UID, uid);
        return this;
    }

    @JsonIgnore
    public String getGroupId() {
        return getAttribute(ATTR_GID, String.class);
    }

    public UserRoleBo setGroupId(String gid) {
        setAttribute(ATTR_GID, gid);
        return this;
    }
}
