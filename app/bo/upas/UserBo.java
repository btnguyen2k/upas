package bo.upas;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.ddth.commons.utils.SerializationUtils;
import com.github.ddth.dao.BaseBo;

public class UserBo extends BaseBo {
    private final static String ATTR_ID = "id";
    private final static String ATTR_DATA = "data";

    @JsonIgnore
    public String getId() {
        return getAttribute(ATTR_ID, String.class);
    }

    public UserBo setId(String id) {
        setAttribute(ATTR_ID, id);
        return this;
    }

    @JsonIgnore
    public String getData() {
        return getAttribute(ATTR_DATA, String.class);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> toDataMap(String json) {
        try {
            return SerializationUtils.fromJsonString(json, Map.class);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public UserBo setData(String data) {
        setAttribute(ATTR_DATA, !StringUtils.isEmpty(data) ? data : "{}");
        synchronized (this) {
            dataMap = toDataMap(data);
        }
        return this;
    }

    @JsonIgnore
    private Map<String, Object> dataMap;

    @JsonIgnore
    public Map<String, Object> getDataMap() {
        if (dataMap == null) {
            synchronized (this) {
                dataMap = toDataMap(getData());
            }
        }
        return Collections.unmodifiableMap(dataMap);
    }

    public UserBo setDataMap(Map<String, Object> dataMap) {
        setData(dataMap != null ? SerializationUtils.toJsonString(dataMap) : "{}");
        return this;
    }
}
