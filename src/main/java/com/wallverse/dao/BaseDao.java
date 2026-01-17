package com.wallverse.dao;

import com.wallverse.db.DbUtil;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseDao {
    protected Connection getConnection() throws SQLException {
        return DbUtil.getConnection();
    }
}
