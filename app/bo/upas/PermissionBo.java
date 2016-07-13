package bo.upas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.ddth.dao.BaseBo;

public class PermissionBo extends BaseBo {
    private final static String ATTR_ID = "id";
    private final static String ATTR_TITLE = "title";
    private final static String ATTR_DESC = "desc";

    @JsonIgnore
    public String getId() {
        return getAttribute(ATTR_ID, String.class);
    }

    public PermissionBo setId(String id) {
        setAttribute(ATTR_ID, id);
        return this;
    }

    @JsonIgnore
    public String getTitle() {
        return getAttribute(ATTR_TITLE, String.class);
    }

    public PermissionBo setTitle(String title) {
        setAttribute(ATTR_TITLE, title);
        return this;
    }

    @JsonIgnore
    public String getDescription() {
        return getAttribute(ATTR_DESC, String.class);
    }

    public PermissionBo setDescription(String desc) {
        setAttribute(ATTR_DESC, desc);
        return this;
    }
}
