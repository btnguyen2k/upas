-- DB Schema - MySQL

-- table to store user accounts
DROP TABLE IF EXISTS upas_user;
CREATE TABLE upas_user (
    uid                 VARCHAR(32),
        PRIMARY KEY (uid),
    ugroup_id           INT,
    uname               VARCHAR(64),
        UNIQUE INDEX (uname),
    upassword           VARCHAR(255),
    uemail              VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

-- admin account: admin/password
-- MERGE INTO upas_user (uid, ugroup, uname, upassword, uemail) KEY (uid)
-- VALUES("1", 1, "admin", "3254a4988474afb14cdf2e3fc3d29066461d756f3647569acda6400aec3aecff", "admin@localhost");

-- table to store application info
DROP TABLE IF EXISTS upas_app;
CREATE TABLE upas_app (
    aid                 VARCHAR(64),
        PRIMARY KEY (aid),
    adisabled           TINYINT(4)                          NOT NULL DEFAULT 0,
    api_key             VARCHAR(255),
    timestamp_create    DATETIME,
    timestamp_update    DATETIME
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

-- table to store app's permission list
DROP TABLE IF EXISTS app_permission_base;
CREATE TABLE app_permission_base (
    pid                 VARCHAR(64),
        PRIMARY KEY (pid),
    ptitle              VARCHAR(255),
    pdesc               TEXT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

-- table to store app's usergroups
DROP TABLE IF EXISTS app_usergroup_base;
CREATE TABLE app_usergroup_base (
    gid                 VARCHAR(64),
        PRIMARY KEY (gid),
    is_god              TINYINT(4)                          NOT NULL DEFAULT 0,
    gtitle              VARCHAR(255),
    gdesc               TEXT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

-- table to store app's user accounts
DROP TABLE IF EXISTS app_user_base;
CREATE TABLE app_user_base (
    uid                 VARCHAR(64),
        PRIMARY KEY (uid),
    udata               TEXT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

-- mapping user:usergroup
DROP TABLE IF EXISTS app_user_role_base;
CREATE TABLE app_user_role_base (
    uid                 VARCHAR(64),
    gid                 VARCHAR(64),
        PRIMARY KEY (uid, gid)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;

-- mapping usergroup:permission
DROP TABLE IF EXISTS app_usergroup_permission_base;
CREATE TABLE app_usergroup_permission_base (
    gid                 VARCHAR(64),
    pid                 VARCHAR(64),
        PRIMARY KEY (gid, pid)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
