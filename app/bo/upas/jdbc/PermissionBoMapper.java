package bo.upas.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import bo.upas.PermissionBo;

public class PermissionBoMapper implements RowMapper<PermissionBo> {

    public final static PermissionBoMapper instance = new PermissionBoMapper();

    public final static String COL_ID = "pid";
    public final static String COL_TITLE = "ptitle";
    public final static String COL_DESC = "pdesc";

    public final static String[] _COLS_ALL = { COL_ID, COL_TITLE, COL_DESC };
    public final static String[] _COLS_CREATE = _COLS_ALL;
    public final static String[] _COLS_KEY = { COL_ID };
    public final static String _COLS_KEY_WHERE_CLAUSE = COL_ID + "=?";
    public final static String _COLS_UPDATE_CLAUSE = StringUtils
            .join(new String[] { COL_TITLE + "=?", COL_DESC + "=?" }, ',');

    public static Object[] valuesForCreate(PermissionBo bo) {
        return new Object[] { bo.getId(), bo.getTitle(), bo.getDescription() };
    }

    public static Object[] valuesForDelete(PermissionBo bo) {
        return new Object[] { bo.getId() };
    }

    public static Object[] valuesForSelect(String id) {
        return new Object[] { id };
    }

    public static Object[] valuesForUpdate(PermissionBo bo) {
        return new Object[] { bo.getTitle(), bo.getDescription() };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PermissionBo mapRow(ResultSet rs, int rowNum) throws SQLException {
        return newObjFromResultSet(rs);
    }

    private static PermissionBo newObjFromResultSet(ResultSet rs) throws SQLException {
        PermissionBo bo = new PermissionBo();
        bo.setId(rs.getString(COL_ID));
        bo.setTitle(rs.getString(COL_TITLE));
        bo.setDescription(rs.getString(COL_DESC));
        bo.markClean();
        return bo;
    }
}
