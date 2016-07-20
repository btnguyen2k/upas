package bo.upas.jdbc;

import org.apache.commons.lang3.ArrayUtils;

import bo.app.AppBo;
import play.Logger;

public class MySqlUpasDao extends JdbcUpasDao {

    private String tableBasePermission = "app_permission_base";
    private String tableBaseUser = "app_user_base";
    private String tableBaseUsergroup = "app_usergroup_base";
    private String tableBaseUsergroupPermission = "app_usergroup_permission_base";
    private String tableBaseUserRole = "app_user_role_base";

    public String getTableBasePermission() {
        return tableBasePermission;
    }

    public MySqlUpasDao setTableBasePermission(String tableBasePermission) {
        this.tableBasePermission = tableBasePermission;
        return this;
    }

    public String getTableBaseUser() {
        return tableBaseUser;
    }

    public MySqlUpasDao setTableBaseUser(String tableBaseUser) {
        this.tableBaseUser = tableBaseUser;
        return this;
    }

    public String getTableBaseUsergroup() {
        return tableBaseUsergroup;
    }

    public MySqlUpasDao setTableBaseUsergroup(String tableBaseUsergroup) {
        this.tableBaseUsergroup = tableBaseUsergroup;
        return this;
    }

    public String getTableBaseUsergroupPermission() {
        return tableBaseUsergroupPermission;
    }

    public MySqlUpasDao setTableBaseUsergroupPermission(String tableBaseUsergroupPermission) {
        this.tableBaseUsergroupPermission = tableBaseUsergroupPermission;
        return this;
    }

    public String getTableBaseUserRole() {
        return tableBaseUserRole;
    }

    public MySqlUpasDao setTableBaseUserRole(String tableBaseUserRole) {
        this.tableBaseUserRole = tableBaseUserRole;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initApp(AppBo app) {
        final String[] tablesToCreate = { calcTableNamePermission(app), calcTableNameUser(app),
                calcTableNameUsergroup(app), calcTableNameUsergroupPerm(app),
                calcTableNameUserRole(app) };
        final String[] tablesBase = { tableBasePermission, tableBaseUser, tableBaseUsergroup,
                tableBaseUsergroupPermission, tableBaseUserRole };

        for (int i = 0; i < tablesToCreate.length; i++) {
            final String SQL = "CREATE TABLE " + tablesToCreate[i] + " LIKE " + tablesBase[i];
            try {
                execute(SQL, ArrayUtils.EMPTY_OBJECT_ARRAY);
            } catch (Exception e) {
                Logger.warn(e.getMessage(), e);
            }

        }
    }

}
