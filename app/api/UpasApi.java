package api;

import java.util.Map;

import bo.app.AppBo;
import bo.upas.IUpasDao;
import bo.upas.PermissionBo;
import bo.upas.UserBo;
import bo.upas.UsergroupBo;
import utils.UpasConstants;

/**
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class UpasApi {

    private IUpasDao upasDao;

    public UpasApi init() {
        return this;
    }

    public void destroy() {
        // EMPTY
    }

    public IUpasDao getUpasDao() {
        return upasDao;
    }

    public UpasApi setUpasDao(IUpasDao upasDao) {
        this.upasDao = upasDao;
        return this;
    }

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
        upasDao.delete(app, perm);
    }

    /**
     * Creates or Updates an app user.
     * 
     * @param app
     * @param id
     * @return
     */
    public UserBo addUser(AppBo app, String id) {
        return addUser(app, id, null);
    }

    /**
     * Creates or Updates an app user.
     * 
     * @param app
     * @param id
     * @param data
     * @return
     */
    public UserBo addUser(AppBo app, String id, Map<String, Object> data) {
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
    public UsergroupBo addUsergroup(AppBo app, String id, String title, String description) {
        return addUsergroup(app, id, false, title, description);
    }

    /**
     * Creates or Updates an app usergroup.
     * 
     * @param app
     * @param id
     * @param isGod
     * @param title
     * @param description
     * @return
     */
    public UsergroupBo addUsergroup(AppBo app, String id, boolean isGod, String title,
            String description) {
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
        upasDao.delete(app, usergroup);
    }
}
