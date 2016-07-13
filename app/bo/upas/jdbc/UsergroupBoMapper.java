package bo.upas.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import bo.upas.UsergroupBo;

public class UsergroupBoMapper implements RowMapper<UsergroupBo> {

    public final static UsergroupBoMapper instance = new UsergroupBoMapper();

    public final static String COL_ID = "gid";
    public final static String COL_IS_GOD = "is_god";
    public final static String COL_TITLE = "gtitle";
    public final static String COL_DESC = "gdesc";

    public final static String[] _COLS_ALL = { COL_ID, COL_IS_GOD, COL_TITLE, COL_DESC };
    public final static String[] _COLS_CREATE = _COLS_ALL;
    public final static String[] _COLS_KEY = { COL_ID };
    public final static String _COLS_KEY_WHERE_CLAUSE = COL_ID + "=?";
    public final static String _COLS_UPDATE_CLAUSE = StringUtils
            .join(new String[] { COL_IS_GOD + "=?", COL_TITLE + "=?", COL_DESC + "=?" }, ',');

    public static Object[] valuesForCreate(UsergroupBo bo) {
        return new Object[] { bo.getId(), bo.isGod() ? 1 : 0, bo.getTitle(), bo.getDescription() };
    }

    public static Object[] valuesForDelete(UsergroupBo bo) {
        return new Object[] { bo.getId() };
    }

    public static Object[] valuesForSelect(String id) {
        return new Object[] { id };
    }

    public static Object[] valuesForUpdate(UsergroupBo bo) {
        return new Object[] { bo.isGod() ? 1 : 0, bo.getTitle(), bo.getDescription() };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsergroupBo mapRow(ResultSet rs, int rowNum) throws SQLException {
        return newObjFromResultSet(rs);
    }

    private static UsergroupBo newObjFromResultSet(ResultSet rs) throws SQLException {
        UsergroupBo bo = new UsergroupBo();
        bo.setId(rs.getString(COL_ID));
        bo.setIsGod(rs.getInt(COL_IS_GOD));
        bo.setTitle(rs.getString(COL_TITLE));
        bo.setDescription(rs.getString(COL_DESC));
        bo.markClean();
        return bo;
    }
}
