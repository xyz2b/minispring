package com.minis.jdbc.core;

import com.minis.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class JdbcTemplate {
    @Autowired
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public JdbcTemplate() {}

    public Object query(StatementCallback statementCallback) {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = dataSource.getConnection();

            stmt = conn.createStatement();

            return statementCallback.doInStatement(stmt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                stmt.close();
                conn.close();
            } catch (SQLException e) {

            }
        }
    }

    public Object query(String sql, Object[] args, PreparedStatementCallback pstmtcallback) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn =dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            pstmt = conn.prepareStatement(sql);

            // 通过 argumentSetter统一设置参数值
            ArgumentPreparedStatementSetter argumentPreparedStatementSetter = new ArgumentPreparedStatementSetter(args);
            argumentPreparedStatementSetter.setValues(pstmt);

            return pstmtcallback.doInPreparedStatement(pstmt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException e) {

            }
        }
    }

    public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) {
        RowMapperResultSetExtractor<T> resultExtractor = new RowMapperResultSetExtractor<>(rowMapper);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // 建立数据库连接
            conn = dataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            ArgumentPreparedStatementSetter argumentPreparedStatementSetter = new ArgumentPreparedStatementSetter(args);
            argumentPreparedStatementSetter.setValues(pstmt);

            // 执行语句
            rs = pstmt.executeQuery();

            // 数据库结果集映射为对象列表，返回
            return resultExtractor.extractData(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException e) {

            }
        }
    }
}
