package bo.upas;

import java.util.Set;

import bo.app.AppBo;
import utils.UpasConstants;

/**
 * API to access UPAS storage.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public interface IUpasDao {
    /**
     * Initializes storage for a new app.
     * 
     * @param app
     */
    public void initApp(AppBo app);

    /**
     * Destroys & cleans-up existing app's storage
     * 
     * @param app
     */
    public void destroyApp(AppBo app);

    /*----------------------------------------------------------------------*/

    /**
     * Creates a new app permission.
     * 
     * @param app
     * @param perm
     * @return see {@link UpasConstants#DAO_RESULT_OK},
     *         {@link UpasConstants#DAO_RESULT_DUPLICATED} and
     *         {@link UpasConstants#DAO_RESULT_NOT_AFFECTED}
     */
    public int create(AppBo app, PermissionBo perm);

    /**
     * Deletes an existing app permission.
     * 
     * @param app
     * @param perm
     * @return
     */
    public boolean delete(AppBo app, PermissionBo perm);

    /**
     * Fetches an existing app permission.
     * 
     * @param app
     * @param id
     * @return
     */
    public PermissionBo getPermission(AppBo app, String id);

    /**
     * Fetches all app permissions.
     * 
     * @param app
     * @return
     */
    public PermissionBo[] getAllPermissions(AppBo app);

    /**
     * Updates an existing app permission.
     * 
     * @param app
     * @param perm
     * @return
     */
    public boolean update(AppBo app, PermissionBo perm);

    /*----------------------------------------------------------------------*/
    /**
     * Creates a new app user.
     * 
     * @param app
     * @param user
     * @return see {@link UpasConstants#DAO_RESULT_OK},
     *         {@link UpasConstants#DAO_RESULT_DUPLICATED} and
     *         {@link UpasConstants#DAO_RESULT_NOT_AFFECTED}
     */
    public int create(AppBo app, UserBo user);

    /**
     * Deletes an existing app user.
     * 
     * @param app
     * @param user
     * @return
     */
    public boolean delete(AppBo app, UserBo user);

    /**
     * Fetches an existing app user.
     * 
     * @param app
     * @param id
     * @return
     */
    public UserBo getUser(AppBo app, String id);

    /**
     * Fetches all app users.
     * 
     * @param app
     * @return
     */
    public UserBo[] getAllUsers(AppBo app);

    /**
     * Updates an existing app user.
     * 
     * @param app
     * @param user
     * @return
     */
    public boolean update(AppBo app, UserBo user);

    /*----------------------------------------------------------------------*/
    /**
     * Creates a new app usergroup.
     * 
     * @param app
     * @param usergroup
     * @return see {@link UpasConstants#DAO_RESULT_OK},
     *         {@link UpasConstants#DAO_RESULT_DUPLICATED} and
     *         {@link UpasConstants#DAO_RESULT_NOT_AFFECTED}
     */
    public int create(AppBo app, UsergroupBo usergroup);

    /**
     * Deletes an existing app usergroup.
     * 
     * @param app
     * @param usergroup
     * @return
     */
    public boolean delete(AppBo app, UsergroupBo usergroup);

    /**
     * Fetches an existing app usergroup.
     * 
     * @param app
     * @param id
     * @return
     */
    public UsergroupBo getUsergroup(AppBo app, String id);

    /**
     * Fetches all app usergroups.
     * 
     * @param app
     * @return
     */
    public UsergroupBo[] getAllUsergroups(AppBo app);

    /**
     * Updates an existing app usergroup.
     * 
     * @param app
     * @param usergroup
     * @return
     */
    public boolean update(AppBo app, UsergroupBo usergroup);

    /*----------------------------------------------------------------------*/
    /**
     * Creates a new app usergroup-permission mapping.
     * 
     * @param app
     * @param usergroupPerm
     * @return see {@link UpasConstants#DAO_RESULT_OK},
     *         {@link UpasConstants#DAO_RESULT_DUPLICATED} and
     *         {@link UpasConstants#DAO_RESULT_NOT_AFFECTED}
     */
    public int create(AppBo app, UsergroupPermBo usergroupPerm);

    /**
     * Deletes an existing app usergroup-permission mapping.
     * 
     * @param app
     * @param usergroupPerm
     * @return
     */
    public boolean delete(AppBo app, UsergroupPermBo usergroupPerm);

    /**
     * Fetches an existing app usergroup-permission mapping.
     * 
     * @param app
     * @param usergroupId
     * @param permId
     * @return
     */
    public UsergroupPermBo getUsergroupPerm(AppBo app, String usergroupId, String permId);

    /*----------------------------------------------------------------------*/
    /**
     * Creates a new app user-group mapping.
     * 
     * @param app
     * @param userRole
     * @return see {@link UpasConstants#DAO_RESULT_OK},
     *         {@link UpasConstants#DAO_RESULT_DUPLICATED} and
     *         {@link UpasConstants#DAO_RESULT_NOT_AFFECTED}
     */
    public int create(AppBo app, UserRoleBo userRole);

    /**
     * Deletes an existing app user-group mapping.
     * 
     * @param app
     * @param userRole
     * @return
     */
    public boolean delete(AppBo app, UserRoleBo userRole);

    /**
     * Fetches an existing app user-group mapping.
     * 
     * @param app
     * @param usergroupId
     * @param groupId
     * @return
     */
    public UserRoleBo getUserRole(AppBo app, String userId, String groupId);

    /*----------------------------------------------------------------------*/
    /**
     * Fetches all usergroups of an app user.
     * 
     * @param app
     * @param user
     * @return
     */
    public Set<UsergroupBo> getUserRoles(AppBo app, UserBo user);

    /**
     * Fetches all permissions of an app usergroup.
     * 
     * @param app
     * @param usergroup
     * @return
     */
    public Set<PermissionBo> getUsergroupPermissions(AppBo app, UsergroupBo usergroup);
}
