package com.minis.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ArgumentPreparedStatementSetter {
    private final Object[] args;

    public ArgumentPreparedStatementSetter(Object[] args) {
        this.args = args;
    }

    // 设置 SQL 参数
    public void setValues(PreparedStatement pstmt) throws SQLException {
        if(args != null) {
            for(int i = 0; i < args.length; i++) {
                Object arg = args[i];
                doSetValue(pstmt, i + 1, arg);
            }
        }
    }

    // 对某个参数，设置参数值
    protected void doSetValue(PreparedStatement pstmt, int parameterPosition, Object argValue) throws SQLException {
        Object arg = argValue;
        // 判断参数类型，调用相应的 JDBC set 方法
        if(arg instanceof String) {
            pstmt.setString(parameterPosition, (String) arg);
        } else if(arg instanceof Integer) {
            pstmt.setInt(parameterPosition, (int) arg);
        } else if(arg instanceof java.util.Date) {
            pstmt.setDate(parameterPosition, new java.sql.Date(((java.util.Date) arg).getTime()));
        }
    }
}
