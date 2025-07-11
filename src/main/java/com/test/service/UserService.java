package com.test.service;

import com.minis.batis.SqlSession;
import com.minis.batis.SqlSessionFactory;
import com.minis.beans.factory.annotation.Autowired;
import com.minis.jdbc.core.JdbcTemplate;
import com.minis.jdbc.core.RowMapper;
import com.test.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    public UserService() {}

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public User getUserInfoByMyBits(int userId) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return (User) sqlSession.selectOne("com.test.entity.User.getUserInfo", new Object[]{new Integer(userId)},
               (pstmt) -> {
                   ResultSet rs = pstmt.executeQuery();
                   User rtnUser = null;
                   if(rs.next()) {
                       rtnUser = new User();
                       rtnUser.setId(rs.getInt("id"));
                       rtnUser.setName(rs.getString("name"));
                       // java.sql.Date 转换为 java.util.Date
                       rtnUser.setBirthday(new java.util.Date(rs.getDate("birthday").getTime()));
                   }
                   return rtnUser;
               }
       );
    }

    public User getUserInfo(int userId) {
        final String sql = "select * from user where id = ?";
        return (User) jdbcTemplate.query(sql, new Object[]{new Integer(userId)},
                (pstmt) -> {
                    ResultSet rs = pstmt.executeQuery();
                    User rtnUser = null;
                    if(rs.next()) {
                        rtnUser = new User();
                        rtnUser.setId(rs.getInt("id"));
                        rtnUser.setName(rs.getString("name"));
                        // java.sql.Date 转换为 java.util.Date
                        rtnUser.setBirthday(new java.util.Date(rs.getDate("birthday").getTime()));
                    }
                    return rtnUser;
                }
        );
    }

    public List<User> getUsers(int userId) {
        final String sql = "select * from user where id > ?";
        return (List<User>) jdbcTemplate.query(sql, new Object[]{new Integer(userId)},
                new RowMapper<User>() {
                    public User mapRow(ResultSet rs, int i) throws SQLException {
                        User rtnUser = new User();
                        rtnUser.setId(rs.getInt("id"));
                        rtnUser.setName(rs.getString("name"));
                        // java.sql.Date 转换为 java.util.Date
                        rtnUser.setBirthday(new java.util.Date(rs.getDate("birthday").getTime()));

                        return rtnUser;
                    }
                }
        );
    }
}
