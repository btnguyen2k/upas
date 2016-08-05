package bo.upas.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;

import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.dao.jdbc.BaseJdbcDao;

import bo.app.AppBo;
import bo.upas.IUpasDao;
import bo.upas.PermissionBo;
import bo.upas.UserBo;
import bo.upas.UserRoleBo;
import bo.upas.UsergroupBo;
import bo.upas.UsergroupPermBo;
import utils.UpasConstants;

/**
 * Jdbc-implement of {@link IUpasDao}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public abstract class JdbcUpasDao extends BaseJdbcDao implements IUpasDao {

    private String tableTemplatePermission = "app_permission_{0}";
    private String tableTemplateUser = "app_user_{0}";
    private String tableTemplateUsergroup = "app_usergroup_{0}";
    private String tableTemplateUsergroupPermission = "app_usergroup_permission_{0}";
    private String tableTemplateUserRole = "app_user_role_{0}";

    private String cacheNamePermission = "UPAS_APP_PERM";
    private String cacheNameUser = "UPAS_APP_USER";
    private String cacheNameUsergroup = "UPAS_APP_USERGROUP";
    private String cacheNameUsergroupPermission = "UPAS_APP_USERGROUP_PERM";
    private String cacheNameUserRole = "UPAS_APP_USER_ROLE";

    public String getTableTemplatePermission() {
        return tableTemplatePermission;
    }

    public JdbcUpasDao setTableTemplatePermission(String tableTemplatePermission) {
        this.tableTemplatePermission = tableTemplatePermission;
        return this;
    }

    public String getTableTemplateUser() {
        return tableTemplateUser;
    }

    public JdbcUpasDao setTableTemplateUser(String tableTemplateUser) {
        this.tableTemplateUser = tableTemplateUser;
        return this;
    }

    public String getTableTemplateUsergroup() {
        return tableTemplateUsergroup;
    }

    public JdbcUpasDao setTableTemplateUsergroup(String tableTemplateUsergroup) {
        this.tableTemplateUsergroup = tableTemplateUsergroup;
        return this;
    }

    public String getTableTemplateUsergroupPermission() {
        return tableTemplateUsergroupPermission;
    }

    public JdbcUpasDao setTableTemplateUsergroupPermission(
            String tableTemplateUsergroupPermission) {
        this.tableTemplateUsergroupPermission = tableTemplateUsergroupPermission;
        return this;
    }

    public String getTableTemplateUserRole() {
        return tableTemplateUserRole;
    }

    public JdbcUpasDao setTableTemplateUserRole(String tableTemplateUserRole) {
        this.tableTemplateUserRole = tableTemplateUserRole;
        return this;
    }

    public String getCacheNamePermission() {
        return cacheNamePermission;
    }

    public JdbcUpasDao setCacheNamePermission(String cacheNamePermission) {
        this.cacheNamePermission = cacheNamePermission;
        return this;
    }

    public String getCacheNameUser() {
        return cacheNameUser;
    }

    public JdbcUpasDao setCacheNameUser(String cacheNameUser) {
        this.cacheNameUser = cacheNameUser;
        return this;
    }

    public String getCacheNameUsergroup() {
        return cacheNameUsergroup;
    }

    public JdbcUpasDao setCacheNameUsergroup(String cacheNameUsergroup) {
        this.cacheNameUsergroup = cacheNameUsergroup;
        return this;
    }

    public String getCacheNameUsergroupPermission() {
        return cacheNameUsergroupPermission;
    }

    public JdbcUpasDao setCacheNameUsergroupPermission(String cacheNameUsergroupPermission) {
        this.cacheNameUsergroupPermission = cacheNameUsergroupPermission;
        return this;
    }

    public String getCacheNameUserRole() {
        return cacheNameUserRole;
    }

    public JdbcUpasDao setCacheNameUserRole(String cacheNameUserRole) {
        this.cacheNameUserRole = cacheNameUserRole;
        return this;
    }

    /*----------------------------------------------------------------------*/

    protected String calcTableNamePermission(AppBo app) {
        return MessageFormat.format(tableTemplatePermission, app.getId());
    }

    private static String cacheKeyPermissionId(AppBo app, String id) {
        return app.getId() + "_" + id;
    }

    private static String cacheKey(AppBo app, PermissionBo perm) {
        return cacheKeyPermissionId(app, perm.getId());
    }

    private static String cacheKeyAllPermissions(AppBo app) {
        return app.getId();
    }

    private static String cacheKeyPermissionUsergroups(AppBo app, PermissionBo perm) {
        return app.getId() + "_UG_" + perm.getId();
    }

    private void invalidate(AppBo app, PermissionBo perm, boolean update) {
        String cacheKey = cacheKey(app, perm);
        if (update) {
            putToCache(cacheNamePermission, cacheKey, perm);
        } else {
            removeFromCache(cacheNamePermission, cacheKey);
        }
        removeFromCache(cacheNamePermission, cacheKeyAllPermissions(app));
        removeFromCache(cacheNamePermission, cacheKeyPermissionUsergroups(app, perm));
    }

    /*----------------------------------------------------------------------*/
    protected String calcTableNameUser(AppBo app) {
        return MessageFormat.format(tableTemplateUser, app.getId());
    }

    private static String cacheKeyUserId(AppBo app, String id) {
        return app.getId() + "_" + id;
    }

    private static String cacheKey(AppBo app, UserBo user) {
        return cacheKeyUserId(app, user.getId());
    }

    private static String cacheKeyAllUsers(AppBo app) {
        return app.getId();
    }

    private static String cacheKeyUserRoles(AppBo app, UserBo user) {
        return app.getId() + "_UR_" + user.getId();
    }

    private void invalidate(AppBo app, UserBo user, boolean update) {
        String cacheKey = cacheKey(app, user);
        if (update) {
            putToCache(cacheNameUser, cacheKey, user);
        } else {
            removeFromCache(cacheNameUser, cacheKey);
        }
        removeFromCache(cacheNameUser, cacheKeyAllUsers(app));
        removeFromCache(cacheNameUser, cacheKeyUserRoles(app, user));
    }

    /*----------------------------------------------------------------------*/
    protected String calcTableNameUsergroup(AppBo app) {
        return MessageFormat.format(tableTemplateUsergroup, app.getId());
    }

    private static String cacheKeyUsergroupId(AppBo app, String id) {
        return app.getId() + "_" + id;
    }

    private static String cacheKey(AppBo app, UsergroupBo usergroup) {
        return cacheKeyUsergroupId(app, usergroup.getId());
    }

    private static String cacheKeyAllUsergroups(AppBo app) {
        return app.getId();
    }

    private static String cacheKeyUsergroupPermissions(AppBo app, UsergroupBo usergroup) {
        return app.getId() + "_UGP_" + usergroup.getId();
    }

    private void invalidate(AppBo app, UsergroupBo usergroup, boolean update) {
        String cacheKey = cacheKey(app, usergroup);
        if (update) {
            putToCache(cacheNameUsergroup, cacheKey, usergroup);
        } else {
            removeFromCache(cacheNameUsergroup, cacheKey);
        }
        removeFromCache(cacheNameUsergroup, cacheKeyAllUsergroups(app));
        removeFromCache(cacheNameUsergroup, cacheKeyUsergroupPermissions(app, usergroup));
    }

    /*----------------------------------------------------------------------*/
    protected String calcTableNameUsergroupPerm(AppBo app) {
        return MessageFormat.format(tableTemplateUsergroupPermission, app.getId());
    }

    private static String cacheKeyUsergroupPermId(AppBo app, String usergroupId, String permId) {
        return app.getId() + "_" + usergroupId + "_" + permId;
    }

    private static String cacheKey(AppBo app, UsergroupPermBo usergroupPerm) {
        return cacheKeyUsergroupPermId(app, usergroupPerm.getGroupId(),
                usergroupPerm.getPermissionId());
    }

    private void invalidate(AppBo app, UsergroupPermBo usergroupPerm, boolean update) {
        String cacheKey = cacheKey(app, usergroupPerm);
        if (update) {
            putToCache(cacheNameUsergroupPermission, cacheKey, usergroupPerm);
        } else {
            removeFromCache(cacheNameUsergroupPermission, cacheKey);
        }
    }

    /*----------------------------------------------------------------------*/
    protected String calcTableNameUserRole(AppBo app) {
        return MessageFormat.format(tableTemplateUserRole, app.getId());
    }

    private static String cacheKeyUserRoleId(AppBo app, String userId, String groupId) {
        return app.getId() + "_" + userId + "_" + groupId;
    }

    private static String cacheKey(AppBo app, UserRoleBo userRole) {
        return cacheKeyUserRoleId(app, userRole.getUserId(), userRole.getGroupId());
    }

    private void invalidate(AppBo app, UserRoleBo userRole, boolean update) {
        String cacheKey = cacheKey(app, userRole);
        if (update) {
            putToCache(cacheNameUserRole, cacheKey, userRole);
        } else {
            removeFromCache(cacheNameUserRole, cacheKey);
        }
    }
    /*----------------------------------------------------------------------*/

    /**
     * {@inheritDoc}
     */
    @Override
    public JdbcUpasDao init() {
        super.init();

        return this;
    }

    /*----------------------------------------------------------------------*/
    private String SQL_CREATE_PERMISSION = "INSERT INTO {0} ("
            + StringUtils.join(PermissionBoMapper._COLS_CREATE, ',') + ") VALUES ("
            + StringUtils.repeat("?", ",", PermissionBoMapper._COLS_CREATE.length) + ")";
    private String SQL_DELETE_PERMISSION = "DELETE FROM {0} WHERE "
            + PermissionBoMapper._COLS_KEY_WHERE_CLAUSE;
    private String SQL_GET_PERMISSION = "SELECT "
            + StringUtils.join(PermissionBoMapper._COLS_ALL, ',') + " FROM {0} WHERE "
            + PermissionBoMapper._COLS_KEY_WHERE_CLAUSE;
    private String SQL_GET_ALL_PERMISSION_IDS = "SELECT " + PermissionBoMapper.COL_ID
            + " FROM {0} ORDER BY " + PermissionBoMapper.COL_ID;
    private String SQL_UPDATE_PERMISSION = "UPDATE {0} SET "
            + PermissionBoMapper._COLS_UPDATE_CLAUSE + " WHERE "
            + PermissionBoMapper._COLS_KEY_WHERE_CLAUSE;

    /**
     * {@inheritDoc}
     */
    @Override
    public int create(AppBo app, PermissionBo perm) {
        try {
            String SQL = MessageFormat.format(SQL_CREATE_PERMISSION, calcTableNamePermission(app));
            int numRows = execute(SQL, PermissionBoMapper.valuesForCreate(perm));
            invalidate(app, perm, true);
            return numRows > 0 ? UpasConstants.DAO_RESULT_OK
                    : UpasConstants.DAO_RESULT_NOT_AFFECTED;
        } catch (DuplicateKeyException dke) {
            return UpasConstants.DAO_RESULT_DUPLICATED;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(AppBo app, PermissionBo perm) {
        try {
            Connection conn = connection(true);
            try {
                String SQL = MessageFormat.format(SQL_DELETE_PERMISSION,
                        calcTableNamePermission(app));
                int numRows = execute(SQL, PermissionBoMapper.valuesForDelete(perm));
                if (numRows > 0) {
                    String permId = perm.getId();
                    Set<UsergroupBo> groups = getGroupsHasPermission(app, perm);
                    for (UsergroupBo group : groups) {
                        UsergroupPermBo bo = UsergroupPermBo.newInstance(permId, group.getId());
                        delete(app, bo);
                    }
                }
                invalidate(app, perm, false);
                commitTransaction(conn);
                return numRows > 0;
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw e;
            } finally {
                returnConnection(conn);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PermissionBo getPermission(AppBo app, String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        final String cacheKey = cacheKeyPermissionId(app, id);
        PermissionBo result = getFromCache(cacheNamePermission, cacheKey, PermissionBo.class);
        if (result == null) {
            try {
                String SQL = MessageFormat.format(SQL_GET_PERMISSION, calcTableNamePermission(app));
                List<PermissionBo> dbRows = executeSelect(PermissionBoMapper.instance, SQL,
                        PermissionBoMapper.valuesForSelect(id));
                result = dbRows != null && dbRows.size() > 0 ? dbRows.get(0) : null;
                putToCache(cacheNamePermission, cacheKey, result);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private String[] getAllPermissionIds(AppBo app) {
        final String cacheKey = cacheKeyAllPermissions(app);
        List<String> permIds = getFromCache(cacheNamePermission, cacheKey, List.class);
        if (permIds == null) {
            permIds = new ArrayList<>();
            try {
                String SQL = MessageFormat.format(SQL_GET_ALL_PERMISSION_IDS,
                        calcTableNamePermission(app));
                List<Map<String, Object>> dbRows = executeSelect(SQL,
                        ArrayUtils.EMPTY_OBJECT_ARRAY);
                for (Map<String, Object> dbRow : dbRows) {
                    String id = DPathUtils.getValue(dbRow, PermissionBoMapper.COL_ID, String.class);
                    if (!StringUtils.isBlank(id)) {
                        permIds.add(id);
                    }
                }
                putToCache(cacheNamePermission, cacheKey, permIds);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return permIds.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PermissionBo[] getAllPermissions(AppBo app) {
        String[] permIdList = getAllPermissionIds(app);
        List<PermissionBo> result = new ArrayList<>();
        for (String id : permIdList) {
            PermissionBo perm = getPermission(app, id);
            if (perm != null) {
                result.add(perm);
            }
        }
        return result.toArray(PermissionBo.EMPTY_ARRAY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(AppBo app, PermissionBo perm) {
        try {
            String SQL = MessageFormat.format(SQL_UPDATE_PERMISSION, calcTableNamePermission(app));
            int nunRows = execute(SQL, PermissionBoMapper.valuesForUpdate(perm));
            invalidate(app, perm, true);
            return nunRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*----------------------------------------------------------------------*/
    private String SQL_CREATE_USER = "INSERT INTO {0} ("
            + StringUtils.join(UserBoMapper._COLS_CREATE, ',') + ") VALUES ("
            + StringUtils.repeat("?", ",", UserBoMapper._COLS_CREATE.length) + ")";
    private String SQL_DELETE_USER = "DELETE FROM {0} WHERE " + UserBoMapper._COLS_KEY_WHERE_CLAUSE;
    private String SQL_GET_USER = "SELECT " + StringUtils.join(UserBoMapper._COLS_ALL, ',')
            + " FROM {0} WHERE " + UserBoMapper._COLS_KEY_WHERE_CLAUSE;
    private String SQL_GET_ALL_USER_IDS = "SELECT " + UserBoMapper.COL_ID + " FROM {0} ORDER BY "
            + UserBoMapper.COL_ID;
    private String SQL_UPDATE_USER = "UPDATE {0} SET " + UserBoMapper._COLS_UPDATE_CLAUSE
            + " WHERE " + UserBoMapper._COLS_KEY_WHERE_CLAUSE;

    /**
     * {@inheritDoc}
     */
    @Override
    public int create(AppBo app, UserBo user) {
        try {
            String SQL = MessageFormat.format(SQL_CREATE_USER, calcTableNameUser(app));
            int numRows = execute(SQL, UserBoMapper.valuesForCreate(user));
            invalidate(app, user, true);
            return numRows > 0 ? UpasConstants.DAO_RESULT_OK
                    : UpasConstants.DAO_RESULT_NOT_AFFECTED;
        } catch (DuplicateKeyException dke) {
            return UpasConstants.DAO_RESULT_DUPLICATED;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(AppBo app, UserBo user) {
        try {
            Connection conn = connection(true);
            try {
                String SQL = MessageFormat.format(SQL_DELETE_USER, calcTableNameUser(app));
                int numRows = execute(SQL, UserBoMapper.valuesForDelete(user));
                if (numRows > 0) {
                    String userId = user.getId();
                    Set<UsergroupBo> usergroups = getUserRoles(app, user);
                    for (UsergroupBo usergroup : usergroups) {
                        UserRoleBo bo = UserRoleBo.newInstance(userId, usergroup.getId());
                        delete(app, bo);
                    }
                }
                invalidate(app, user, false);
                commitTransaction(conn);
                return numRows > 0;
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw e;
            } finally {
                returnConnection(conn);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserBo getUser(AppBo app, String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        final String cacheKey = cacheKeyUserId(app, id);
        UserBo result = getFromCache(cacheNameUser, cacheKey, UserBo.class);
        if (result == null) {
            try {
                String SQL = MessageFormat.format(SQL_GET_USER, calcTableNameUser(app));
                List<UserBo> dbRows = executeSelect(UserBoMapper.instance, SQL,
                        UserBoMapper.valuesForSelect(id));
                result = dbRows != null && dbRows.size() > 0 ? dbRows.get(0) : null;
                putToCache(cacheNameUser, cacheKey, result);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private String[] getAllUserIds(AppBo app) {
        final String cacheKey = cacheKeyAllUsers(app);
        List<String> userIds = getFromCache(cacheNameUser, cacheKey, List.class);
        if (userIds == null) {
            userIds = new ArrayList<>();
            try {
                String SQL = MessageFormat.format(SQL_GET_ALL_USER_IDS, calcTableNameUser(app));
                List<Map<String, Object>> dbRows = executeSelect(SQL,
                        ArrayUtils.EMPTY_OBJECT_ARRAY);
                for (Map<String, Object> dbRow : dbRows) {
                    String id = DPathUtils.getValue(dbRow, UserBoMapper.COL_ID, String.class);
                    if (!StringUtils.isBlank(id)) {
                        userIds.add(id);
                    }
                }
                putToCache(cacheNameUser, cacheKey, userIds);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return userIds.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserBo[] getAllUsers(AppBo app) {
        String[] userIdList = getAllUserIds(app);
        List<UserBo> result = new ArrayList<>();
        for (String id : userIdList) {
            UserBo user = getUser(app, id);
            if (user != null) {
                result.add(user);
            }
        }
        return result.toArray(UserBo.EMPTY_ARRAY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(AppBo app, UserBo user) {
        try {
            String SQL = MessageFormat.format(SQL_UPDATE_USER, calcTableNameUser(app));
            int nunRows = execute(SQL, UserBoMapper.valuesForUpdate(user));
            invalidate(app, user, true);
            return nunRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /*----------------------------------------------------------------------*/

    private String SQL_CREATE_USERGROUP = "INSERT INTO {0} ("
            + StringUtils.join(UsergroupBoMapper._COLS_CREATE, ',') + ") VALUES ("
            + StringUtils.repeat("?", ",", UsergroupBoMapper._COLS_CREATE.length) + ")";
    private String SQL_DELETE_USERGROUP = "DELETE FROM {0} WHERE "
            + UsergroupBoMapper._COLS_KEY_WHERE_CLAUSE;
    private String SQL_GET_USERGROUP = "SELECT "
            + StringUtils.join(UsergroupBoMapper._COLS_ALL, ',') + " FROM {0} WHERE "
            + UsergroupBoMapper._COLS_KEY_WHERE_CLAUSE;
    private String SQL_GET_ALL_USERGROUP_IDS = "SELECT " + UsergroupBoMapper.COL_ID
            + " FROM {0} ORDER BY " + UsergroupBoMapper.COL_ID;
    private String SQL_UPDATE_USERGROUP = "UPDATE {0} SET " + UsergroupBoMapper._COLS_UPDATE_CLAUSE
            + " WHERE " + UsergroupBoMapper._COLS_KEY_WHERE_CLAUSE;

    /**
     * {@inheritDoc}
     */
    @Override
    public int create(AppBo app, UsergroupBo usergroup) {
        try {
            String SQL = MessageFormat.format(SQL_CREATE_USERGROUP, calcTableNameUsergroup(app));
            int numRows = execute(SQL, UsergroupBoMapper.valuesForCreate(usergroup));
            invalidate(app, usergroup, true);
            return numRows > 0 ? UpasConstants.DAO_RESULT_OK
                    : UpasConstants.DAO_RESULT_NOT_AFFECTED;
        } catch (DuplicateKeyException dke) {
            return UpasConstants.DAO_RESULT_DUPLICATED;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(AppBo app, UsergroupBo usergroup) {
        try {
            Connection conn = connection(true);
            try {
                String SQL = MessageFormat.format(SQL_DELETE_USERGROUP,
                        calcTableNameUsergroup(app));
                int numRows = execute(SQL, UsergroupBoMapper.valuesForDelete(usergroup));
                if (numRows > 0) {
                    String groupId = usergroup.getId();

                    Set<UserBo> users = getUsersInGroup(app, usergroup);
                    for (UserBo user : users) {
                        UserRoleBo bo = UserRoleBo.newInstance(user.getId(), groupId);
                        delete(app, bo);
                    }

                    Set<PermissionBo> perms = getUsergroupPermissions(app, usergroup);
                    for (PermissionBo perm : perms) {
                        UsergroupPermBo bo = UsergroupPermBo.newInstance(perm.getId(), groupId);
                        delete(app, bo);
                    }
                }
                invalidate(app, usergroup, false);
                commitTransaction(conn);
                return numRows > 0;
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw e;
            } finally {
                returnConnection(conn);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsergroupBo getUsergroup(AppBo app, String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        final String cacheKey = cacheKeyUsergroupId(app, id);
        UsergroupBo result = getFromCache(cacheNameUsergroup, cacheKey, UsergroupBo.class);
        if (result == null) {
            try {
                String SQL = MessageFormat.format(SQL_GET_USERGROUP, calcTableNameUsergroup(app));
                List<UsergroupBo> dbRows = executeSelect(UsergroupBoMapper.instance, SQL,
                        UsergroupBoMapper.valuesForSelect(id));
                result = dbRows != null && dbRows.size() > 0 ? dbRows.get(0) : null;
                putToCache(cacheNameUsergroup, cacheKey, result);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private String[] getAllUsergroupIds(AppBo app) {
        final String cacheKey = cacheKeyAllUsergroups(app);
        List<String> usergroupIds = getFromCache(cacheNameUsergroup, cacheKey, List.class);
        if (usergroupIds == null) {
            usergroupIds = new ArrayList<>();
            try {
                String SQL = MessageFormat.format(SQL_GET_ALL_USERGROUP_IDS,
                        calcTableNameUsergroup(app));
                List<Map<String, Object>> dbRows = executeSelect(SQL,
                        ArrayUtils.EMPTY_OBJECT_ARRAY);
                for (Map<String, Object> dbRow : dbRows) {
                    String id = DPathUtils.getValue(dbRow, UsergroupBoMapper.COL_ID, String.class);
                    if (!StringUtils.isBlank(id)) {
                        usergroupIds.add(id);
                    }
                }
                putToCache(cacheNameUsergroup, cacheKey, usergroupIds);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return usergroupIds.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsergroupBo[] getAllUsergroups(AppBo app) {
        String[] usergroupIdList = getAllUsergroupIds(app);
        List<UsergroupBo> result = new ArrayList<>();
        for (String id : usergroupIdList) {
            UsergroupBo usergroup = getUsergroup(app, id);
            if (usergroup != null) {
                result.add(usergroup);
            }
        }
        return result.toArray(UsergroupBo.EMPTY_ARRAY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(AppBo app, UsergroupBo usergroup) {
        try {
            String SQL = MessageFormat.format(SQL_UPDATE_USERGROUP, calcTableNameUsergroup(app));
            int nunRows = execute(SQL, UsergroupBoMapper.valuesForUpdate(usergroup));
            invalidate(app, usergroup, true);
            return nunRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*----------------------------------------------------------------------*/
    private String SQL_CREATE_USERGROUP_PERM = "INSERT INTO {0} ("
            + StringUtils.join(UsergroupPermBoMapper._COLS_CREATE, ',') + ") VALUES ("
            + StringUtils.repeat("?", ",", UsergroupPermBoMapper._COLS_CREATE.length) + ")";
    private String SQL_DELETE_USERGROUP_PERM = "DELETE FROM {0} WHERE "
            + UsergroupPermBoMapper._COLS_KEY_WHERE_CLAUSE;
    private String SQL_GET_USERGROUP_PERM = "SELECT "
            + StringUtils.join(UsergroupPermBoMapper._COLS_ALL, ',') + " FROM {0} WHERE "
            + UsergroupPermBoMapper._COLS_KEY_WHERE_CLAUSE;

    /**
     * {@inheritDoc}
     */
    @Override
    public int create(AppBo app, UsergroupPermBo usergroupPerm) {
        try {
            String SQL = MessageFormat.format(SQL_CREATE_USERGROUP_PERM,
                    calcTableNameUsergroupPerm(app));
            int numRows = execute(SQL, UsergroupPermBoMapper.valuesForCreate(usergroupPerm));
            invalidate(app, usergroupPerm, true);
            return numRows > 0 ? UpasConstants.DAO_RESULT_OK
                    : UpasConstants.DAO_RESULT_NOT_AFFECTED;
        } catch (DuplicateKeyException dke) {
            return UpasConstants.DAO_RESULT_DUPLICATED;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(AppBo app, UsergroupPermBo usergroupPerm) {
        try {
            String SQL = MessageFormat.format(SQL_DELETE_USERGROUP_PERM,
                    calcTableNameUsergroupPerm(app));
            int numRows = execute(SQL, UsergroupPermBoMapper.valuesForDelete(usergroupPerm));
            invalidate(app, usergroupPerm, false);
            return numRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UsergroupPermBo getUsergroupPerm(AppBo app, String usergroupId, String permId) {
        if (StringUtils.isBlank(usergroupId) || StringUtils.isBlank(permId)) {
            return null;
        }
        final String cacheKey = cacheKeyUsergroupPermId(app, usergroupId, permId);
        UsergroupPermBo result = getFromCache(cacheNameUsergroupPermission, cacheKey,
                UsergroupPermBo.class);
        if (result == null) {
            try {
                String SQL = MessageFormat.format(SQL_GET_USERGROUP_PERM,
                        calcTableNameUsergroup(app));
                List<UsergroupPermBo> dbRows = executeSelect(UsergroupPermBoMapper.instance, SQL,
                        UsergroupPermBoMapper.valuesForSelect(usergroupId, permId));
                result = dbRows != null && dbRows.size() > 0 ? dbRows.get(0) : null;
                putToCache(cacheNameUsergroupPermission, cacheKey, result);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    /*----------------------------------------------------------------------*/
    private String SQL_CREATE_USER_ROLE = "INSERT INTO {0} ("
            + StringUtils.join(UserRoleBoMapper._COLS_CREATE, ',') + ") VALUES ("
            + StringUtils.repeat("?", ",", UserRoleBoMapper._COLS_CREATE.length) + ")";
    private String SQL_DELETE_USER_ROLE = "DELETE FROM {0} WHERE "
            + UserRoleBoMapper._COLS_KEY_WHERE_CLAUSE;
    private String SQL_GET_USER_ROLE = "SELECT " + StringUtils.join(UserRoleBoMapper._COLS_ALL, ',')
            + " FROM {0} WHERE " + UserRoleBoMapper._COLS_KEY_WHERE_CLAUSE;

    /**
     * {@inheritDoc}
     */
    @Override
    public int create(AppBo app, UserRoleBo userRole) {
        try {
            String SQL = MessageFormat.format(SQL_CREATE_USER_ROLE, calcTableNameUserRole(app));
            int numRows = execute(SQL, UserRoleBoMapper.valuesForCreate(userRole));
            invalidate(app, userRole, true);
            return numRows > 0 ? UpasConstants.DAO_RESULT_OK
                    : UpasConstants.DAO_RESULT_NOT_AFFECTED;
        } catch (DuplicateKeyException dke) {
            return UpasConstants.DAO_RESULT_DUPLICATED;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(AppBo app, UserRoleBo userRole) {
        try {
            String SQL = MessageFormat.format(SQL_DELETE_USER_ROLE, calcTableNameUserRole(app));
            int numRows = execute(SQL, UserRoleBoMapper.valuesForDelete(userRole));
            invalidate(app, userRole, false);
            return numRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserRoleBo getUserRole(AppBo app, String userId, String groupId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(groupId)) {
            return null;
        }
        final String cacheKey = cacheKeyUserRoleId(app, userId, groupId);
        UserRoleBo result = getFromCache(cacheNameUserRole, cacheKey, UserRoleBo.class);
        if (result == null) {
            try {
                String SQL = MessageFormat.format(SQL_GET_USER_ROLE, calcTableNameUserRole(app));
                List<UserRoleBo> dbRows = executeSelect(UserRoleBoMapper.instance, SQL,
                        UserRoleBoMapper.valuesForSelect(userId, groupId));
                result = dbRows != null && dbRows.size() > 0 ? dbRows.get(0) : null;
                putToCache(cacheNameUserRole, cacheKey, result);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    /*----------------------------------------------------------------------*/
    private String SQL_GET_USER_ROLES = "SELECT " + UserRoleBoMapper.COL_GID + " FROM {0} WHERE "
            + UserRoleBoMapper.COL_UID + "=? ORDER BY " + UserRoleBoMapper.COL_GID;
    private String SQL_GET_USERS_IN_GROUP = "SELECT " + UserRoleBoMapper.COL_UID
            + " FROM {0} WHERE " + UserRoleBoMapper.COL_GID + "=? ORDER BY "
            + UserRoleBoMapper.COL_UID;

    private String SQL_GET_USERGROUP_PERMISSIONS = "SELECT " + UsergroupPermBoMapper.COL_PID
            + " FROM {0} WHERE " + UsergroupPermBoMapper.COL_GID + "=? ORDER BY "
            + UsergroupPermBoMapper.COL_PID;
    private String SQL_GET_GROUPS_HAS_PERMISSIONS = "SELECT " + UsergroupPermBoMapper.COL_GID
            + " FROM {0} WHERE " + UsergroupPermBoMapper.COL_PID + "=? ORDER BY "
            + UsergroupPermBoMapper.COL_GID;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Set<UsergroupBo> getUserRoles(AppBo app, UserBo user) {
        final String cacheKey = cacheKeyUserRoles(app, user);
        List<String> usergroupIds = getFromCache(cacheNameUser, cacheKey, List.class);
        if (usergroupIds == null) {
            usergroupIds = new ArrayList<>();
            try {
                String SQL = MessageFormat.format(SQL_GET_USER_ROLES, calcTableNameUserRole(app));
                List<Map<String, Object>> dbRows = executeSelect(SQL,
                        new Object[] { user.getId() });
                for (Map<String, Object> dbRow : dbRows) {
                    String id = DPathUtils.getValue(dbRow, UserRoleBoMapper.COL_GID, String.class);
                    if (!StringUtils.isBlank(id)) {
                        usergroupIds.add(id);
                    }
                }
                putToCache(cacheNameUser, cacheKey, usergroupIds);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        Set<UsergroupBo> result = new HashSet<>();
        for (String usergroupId : usergroupIds) {
            UsergroupBo usergroup = getUsergroup(app, usergroupId);
            if (usergroup != null) {
                result.add(usergroup);
            }
        }
        return result;
    }

    private Set<UserBo> getUsersInGroup(AppBo app, UsergroupBo usergroup) {
        List<String> userIds = new ArrayList<>();
        try {
            String SQL = MessageFormat.format(SQL_GET_USERS_IN_GROUP, calcTableNameUserRole(app));
            List<Map<String, Object>> dbRows = executeSelect(SQL,
                    new Object[] { usergroup.getId() });
            for (Map<String, Object> dbRow : dbRows) {
                String id = DPathUtils.getValue(dbRow, UserRoleBoMapper.COL_UID, String.class);
                if (!StringUtils.isBlank(id)) {
                    userIds.add(id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Set<UserBo> result = new HashSet<>();
        for (String userId : userIds) {
            UserBo user = getUser(app, userId);
            if (user != null) {
                result.add(user);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Set<PermissionBo> getUsergroupPermissions(AppBo app, UsergroupBo usergroup) {
        final String cacheKey = cacheKeyUsergroupPermissions(app, usergroup);
        List<String> permIds = getFromCache(cacheNameUsergroup, cacheKey, List.class);
        if (permIds == null) {
            permIds = new ArrayList<>();
            try {
                String SQL = MessageFormat.format(SQL_GET_USERGROUP_PERMISSIONS,
                        calcTableNameUsergroupPerm(app));
                List<Map<String, Object>> dbRows = executeSelect(SQL,
                        new Object[] { usergroup.getId() });
                for (Map<String, Object> dbRow : dbRows) {
                    String id = DPathUtils.getValue(dbRow, UsergroupPermBoMapper.COL_PID,
                            String.class);
                    if (!StringUtils.isBlank(id)) {
                        permIds.add(id);
                    }
                }
                putToCache(cacheNameUsergroup, cacheKey, permIds);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        Set<PermissionBo> result = new HashSet<>();
        for (String permId : permIds) {
            PermissionBo perm = getPermission(app, permId);
            if (perm != null) {
                result.add(perm);
            }
        }
        return result;
    }

    private Set<UsergroupBo> getGroupsHasPermission(AppBo app, PermissionBo perm) {
        List<String> usergroupIds = new ArrayList<>();
        try {
            String SQL = MessageFormat.format(SQL_GET_GROUPS_HAS_PERMISSIONS,
                    calcTableNameUsergroupPerm(app));
            List<Map<String, Object>> dbRows = executeSelect(SQL, new Object[] { perm.getId() });
            for (Map<String, Object> dbRow : dbRows) {
                String id = DPathUtils.getValue(dbRow, UsergroupPermBoMapper.COL_GID, String.class);
                if (!StringUtils.isBlank(id)) {
                    usergroupIds.add(id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Set<UsergroupBo> result = new HashSet<>();
        for (String usergroupId : usergroupIds) {
            UsergroupBo usergroup = getUsergroup(app, usergroupId);
            if (usergroup != null) {
                result.add(usergroup);
            }
        }
        return result;
    }
}
