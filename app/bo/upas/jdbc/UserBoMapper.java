package bo.upas.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import bo.upas.UserBo;

public class UserBoMapper implements RowMapper<UserBo> {

    public final static UserBoMapper instance = new UserBoMapper();

    public final static String COL_ID = "uid";
    public final static String COL_DATA = "udata";

    public final static String[] _COLS_ALL = { COL_ID, COL_DATA };
    public final static String[] _COLS_CREATE = _COLS_ALL;
    public final static String[] _COLS_KEY = { COL_ID };
    public final static String _COLS_KEY_WHERE_CLAUSE = COL_ID + "=?";
    public final static String _COLS_UPDATE_CLAUSE = StringUtils
            .join(new String[] { COL_DATA + "=?" }, ',');

    public static Object[] valuesForCreate(UserBo bo) {
        return new Object[] { bo.getId(), bo.getData() };
    }

    public static Object[] valuesForDelete(UserBo bo) {
        return new Object[] { bo.getId() };
    }

    public static Object[] valuesForSelect(String id) {
        return new Object[] { id };
    }

    public static Object[] valuesForUpdate(UserBo bo) {
        return new Object[] { bo.getData(), bo.getId() };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserBo mapRow(ResultSet rs, int rowNum) throws SQLException {
        return newObjFromResultSet(rs);
    }

    private static UserBo newObjFromResultSet(ResultSet rs) throws SQLException {
        UserBo bo = new UserBo();
        bo.setId(rs.getString(COL_ID));
        bo.setData(rs.getString(COL_DATA));
        bo.markClean();
        return bo;
    }
}
