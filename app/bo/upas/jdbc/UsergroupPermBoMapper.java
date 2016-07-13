package bo.upas.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import bo.upas.UsergroupPermBo;

public class UsergroupPermBoMapper implements RowMapper<UsergroupPermBo> {

    public final static UsergroupPermBoMapper instance = new UsergroupPermBoMapper();

    public final static String COL_GID = "gid";
    public final static String COL_PID = "pid";

    public final static String[] _COLS_ALL = { COL_GID, COL_PID };
    public final static String[] _COLS_CREATE = _COLS_ALL;
    public final static String[] _COLS_KEY = { COL_GID, COL_PID };
    public final static String _COLS_KEY_WHERE_CLAUSE = COL_GID + "=? AND " + COL_PID + "=?";

    public static Object[] valuesForCreate(UsergroupPermBo bo) {
        return new Object[] { bo.getGroupId(), bo.getPermissionId() };
    }

    public static Object[] valuesForDelete(UsergroupPermBo bo) {
        return new Object[] { bo.getGroupId(), bo.getPermissionId() };
    }

    public static Object[] valuesForSelect(String gid, String pid) {
        return new Object[] { gid, pid };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsergroupPermBo mapRow(ResultSet rs, int rowNum) throws SQLException {
        return newObjFromResultSet(rs);
    }

    private static UsergroupPermBo newObjFromResultSet(ResultSet rs) throws SQLException {
        UsergroupPermBo bo = new UsergroupPermBo();
        bo.setGroupId(rs.getString(COL_GID));
        bo.setPermissionId(rs.getString(COL_PID));
        bo.markClean();
        return bo;
    }
}
