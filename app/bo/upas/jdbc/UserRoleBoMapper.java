package bo.upas.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import bo.upas.UserRoleBo;

public class UserRoleBoMapper implements RowMapper<UserRoleBo> {

    public final static UserRoleBoMapper instance = new UserRoleBoMapper();

    public final static String COL_UID = "uid";
    public final static String COL_GID = "gid";

    public final static String[] _COLS_ALL = { COL_UID, COL_GID };
    public final static String[] _COLS_CREATE = _COLS_ALL;
    public final static String[] _COLS_KEY = { COL_UID, COL_GID };
    public final static String _COLS_KEY_WHERE_CLAUSE = COL_UID + "=? AND " + COL_GID + "=?";

    public static Object[] valuesForCreate(UserRoleBo bo) {
        return new Object[] { bo.getUserId(), bo.getGroupId() };
    }

    public static Object[] valuesForDelete(UserRoleBo bo) {
        return new Object[] { bo.getUserId(), bo.getGroupId() };
    }

    public static Object[] valuesForSelect(String uid, String gid) {
        return new Object[] { uid, gid };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserRoleBo mapRow(ResultSet rs, int rowNum) throws SQLException {
        return newObjFromResultSet(rs);
    }

    private static UserRoleBo newObjFromResultSet(ResultSet rs) throws SQLException {
        UserRoleBo bo = new UserRoleBo();
        bo.setUserId(rs.getString(COL_UID));
        bo.setGroupId(rs.getString(COL_GID));
        bo.markClean();
        return bo;
    }
}
