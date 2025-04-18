package com.minis.jdbc.pool;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class PooledDataSource implements DataSource {
    private List<PooledConnection> connections;
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private int initialSize = 2;
    private Properties connectionProperties;

    public String getDriverClassName() {
        return driverClassName;
    }

    // 设置driver class name的方法，要加载driver类
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Could not load JDBC driver class [" + driverClassName + "]", e);
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Properties getConnectionProperties() {
        return connectionProperties;
    }

    public void setConnectionProperties(Properties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    public void setInitialSize(int initialSize) {
        System.out.println("*******************initialSize: " + initialSize);
        this.initialSize = initialSize;
    }

    public int getInitialSize() {
        return initialSize;
    }

    private void initPool(Properties properties) throws SQLException {
        // 多线程并发
        this.connections = Collections.synchronizedList(new ArrayList<>(initialSize));
        for (int i = 0; i < initialSize; i++) {
            Connection connection = DriverManager.getConnection(url, properties);
            PooledConnection pooledConnection = new PooledConnection(connection, false);
            this.connections.add(pooledConnection);
            System.out.println("********add connection pool*********");
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnectionFromDriver(getUsername(), getPassword());
    }

    //将参数组织成Properties结构，然后拿到实际的数据库连接
    protected Connection getConnectionFromDriver(String username, String password) throws SQLException {
        Properties mergedProperties = new Properties();
        Properties connPros = getConnectionProperties();
        if(connPros != null) {
            mergedProperties.putAll(connPros);
        }
        if(username != null) {
            mergedProperties.put("user", username);
        }
        if(password != null) {
            mergedProperties.put("password", password);
        }

        if(this.connections == null) {
            initPool(mergedProperties);
        }

        PooledConnection pooledConnection = getAvailableConnection();

        while(pooledConnection == null){
            pooledConnection = getAvailableConnection();

            if(pooledConnection == null){
                try {
                    TimeUnit.MILLISECONDS.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return pooledConnection;
    }

    private PooledConnection getAvailableConnection() throws SQLException{
        for(PooledConnection pooledConnection : this.connections){
            if (!pooledConnection.isActive()){
                pooledConnection.setActive(true);
                return pooledConnection;
            }
        }

        return null;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnectionFromDriver(username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
