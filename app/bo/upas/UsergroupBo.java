package bo.upas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.ddth.dao.BaseBo;

public class UsergroupBo extends BaseBo {

    public final static UsergroupBo[] EMPTY_ARRAY = new UsergroupBo[0];

    private final static String ATTR_ID = "id";
    private final static String ATTR_TITLE = "title";
    private final static String ATTR_DESC = "desc";
    private final static String ATTR_IS_GOD = "god";

    @JsonIgnore
    public String getId() {
        return getAttribute(ATTR_ID, String.class);
    }

    public UsergroupBo setId(String id) {
        setAttribute(ATTR_ID, id);
        return this;
    }

    @JsonIgnore
    public boolean isGod() {
        Integer value = getAttribute(ATTR_IS_GOD, Integer.class);
        return value != null ? value.intValue() != 0 : false;
    }

    @JsonIgnore
    public int getIsGod() {
        Integer value = getAttribute(ATTR_IS_GOD, Integer.class);
        return value != null ? value.intValue() : 0;
    }

    public UsergroupBo setIsGod(boolean isGod) {
        setAttribute(ATTR_IS_GOD, isGod ? 1 : 0);
        return this;
    }

    public UsergroupBo setIsGod(int value) {
        setAttribute(ATTR_IS_GOD, value != 0 ? 1 : 0);
        return this;
    }

    @JsonIgnore
    public String getTitle() {
        return getAttribute(ATTR_TITLE, String.class);
    }

    public UsergroupBo setTitle(String title) {
        setAttribute(ATTR_TITLE, title);
        return this;
    }

    @JsonIgnore
    public String getDescription() {
        return getAttribute(ATTR_DESC, String.class);
    }

    public UsergroupBo setDescription(String desc) {
        setAttribute(ATTR_DESC, desc);
        return this;
    }
}
