package bo.upas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.ddth.dao.BaseBo;

import bo.app.AppBo;
import utils.UpasConstants;

public class PermissionBo extends BaseBo {

    public final static PermissionBo[] EMPTY_ARRAY = new PermissionBo[0];

    public static PermissionBo newInstance() {
        PermissionBo bo = new PermissionBo();
        return bo;
    }

    public static PermissionBo newInstance(String id, String title, String desc) {
        PermissionBo bo = newInstance();
        bo.setId(id).setTitle(title).setDescription(desc);
        return bo;
    }

    /**
     * Creates a new system permission attached to an application.
     * 
     * @param app
     * @return
     */
    public static PermissionBo newInstance(AppBo app) {
        final String appId = app.getId();
        final String permId = UpasConstants.SYSTEM_APP_PERM_PREFIX + appId;
        return newInstance(permId, permId, "System permission attached to app [" + appId + "].");
    }

    private final static String ATTR_ID = "id";
    private final static String ATTR_TITLE = "title";
    private final static String ATTR_DESC = "desc";

    @JsonIgnore
    public String getId() {
        return getAttribute(ATTR_ID, String.class);
    }

    public PermissionBo setId(String id) {
        setAttribute(ATTR_ID, id != null ? id.trim() : null);
        return this;
    }

    @JsonIgnore
    public String getTitle() {
        return getAttribute(ATTR_TITLE, String.class);
    }

    public PermissionBo setTitle(String title) {
        setAttribute(ATTR_TITLE, title != null ? title.trim() : null);
        return this;
    }

    @JsonIgnore
    public String getDescription() {
        return getAttribute(ATTR_DESC, String.class);
    }

    public PermissionBo setDescription(String desc) {
        setAttribute(ATTR_DESC, desc != null ? desc.trim() : null);
        return this;
    }
}
