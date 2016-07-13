package bo.upas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.ddth.dao.BaseBo;

/**
 * Mapping {usergroup:permission}
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class UsergroupPermBo extends BaseBo {
    private final static String ATTR_GID = "gid";
    private final static String ATTR_PID = "pid";

    @JsonIgnore
    public String getPermissionId() {
        return getAttribute(ATTR_PID, String.class);
    }

    public UsergroupPermBo setPermissionId(String pid) {
        setAttribute(ATTR_PID, pid);
        return this;
    }

    @JsonIgnore
    public String getGroupId() {
        return getAttribute(ATTR_GID, String.class);
    }

    public UsergroupPermBo setGroupId(String gid) {
        setAttribute(ATTR_GID, gid);
        return this;
    }
}
