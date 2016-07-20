package api;

import java.util.Map;

import bo.app.AppBo;
import bo.upas.IUpasDao;
import bo.upas.PermissionBo;
import bo.upas.UserBo;
import bo.upas.UsergroupBo;
import utils.UpasGlobals;
import utils.UpasConstants;

/**
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class UpasApi {
    /**
     * Creates or Updates an app permission.
     * 
     * @param app
     * @param id
     * @param title
     * @param description
     * @return
     */
    public PermissionBo addPermission(AppBo app, String id, String title, String description) {
        IUpasDao upasDao = UpasGlobals.registry.getUpasDao();
        PermissionBo bo = PermissionBo.newInstance(id, title, description);
        int result = upasDao.create(app, bo);
        switch (result) {
        case UpasConstants.DAO_RESULT_OK:
            return bo;
        case UpasConstants.DAO_RESULT_DUPLICATED:
            return upasDao.update(app, bo) ? bo : null;
        default:
            return null;
        }
    }

    /**
     * Removes an existing app permission.
     * 
     * @param app
     * @param perm
     */
    public void remove(AppBo app, PermissionBo perm) {
        IUpasDao upasDao = UpasGlobals.registry.getUpasDao();
        upasDao.delete(app, perm);
    }

    /**
     * Creates or Updates an app user.
     * 
     * @param app
     * @param id
     * @return
     */
    public UserBo createUser(AppBo app, String id) {
        return createUser(app, id, null);
    }

    /**
     * Creates or Updates an app user.
     * 
     * @param app
     * @param id
     * @param data
     * @return
     */
    public UserBo createUser(AppBo app, String id, Map<String, Object> data) {
        IUpasDao upasDao = UpasGlobals.registry.getUpasDao();
        UserBo bo = UserBo.newInstance(id, data);
        int result = upasDao.create(app, bo);
        switch (result) {
        case UpasConstants.DAO_RESULT_OK:
            return bo;
        case UpasConstants.DAO_RESULT_DUPLICATED:
            return upasDao.update(app, bo) ? bo : null;
        default:
            return null;
        }
    }

    /**
     * Removes an existing app user.
     * 
     * @param app
     * @param user
     */
    public void remove(AppBo app, UserBo user) {
        IUpasDao upasDao = UpasGlobals.registry.getUpasDao();
        upasDao.delete(app, user);
    }

    /**
     * Creates or Updates an app usergroup.
     * 
     * @param app
     * @param id
     * @param title
     * @param description
     * @return
     */
    public UsergroupBo createUsergroup(AppBo app, String id, String title, String description) {
        return createUsergroup(app, id, false, title, description);
    }

    public UsergroupBo createUsergroup(AppBo app, String id, boolean isGod, String title,
            String description) {
        IUpasDao upasDao = UpasGlobals.registry.getUpasDao();
        UsergroupBo bo = UsergroupBo.newInstance(id, isGod, title, description);
        int result = upasDao.create(app, bo);
        switch (result) {
        case UpasConstants.DAO_RESULT_OK:
            return bo;
        case UpasConstants.DAO_RESULT_DUPLICATED:
            return upasDao.update(app, bo) ? bo : null;
        default:
            return null;
        }
    }

    /**
     * Removes an existing app usergroup.
     * 
     * @param app
     * @param usergroup
     */
    public void remove(AppBo app, UsergroupBo usergroup) {
        IUpasDao upasDao = UpasGlobals.registry.getUpasDao();
        upasDao.delete(app, usergroup);
    }
}
