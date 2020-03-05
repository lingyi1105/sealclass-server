use sealclass;

INSERT INTO t_school
    (sid, name, portrait, manager, appkey, secret, create_dt, update_dt)
VALUES
    ('rongcloud', 'rongcloud', null, 'superamdin', 'appkey', 'secret', CURRENT_DATE(), CURRENT_DATE());

INSERT INTO t_user
    (uid, sid, phone, name, portrait, role, password, salt, create_dt, update_dt)
VALUES
    ('superadmin', 'rongcloud', '10086', 'superamdin', null, 1, 'd4b5c4f2868209df84e8e5ac441f2c73', '123456', CURRENT_DATE(), CURRENT_DATE());
