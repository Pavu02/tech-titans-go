package com.examly.springapp.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String mysqlUrl;

    @Value("${spring.datasource.username}")
    private String mysqlUser;

    @Value("${spring.datasource.password}")
    private String mysqlPass;

    @Bean
    @Primary
    public DataSource dataSource() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            DriverManager.setLoginTimeout(3);
            try (Connection conn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPass)) {
                HikariDataSource ds = new HikariDataSource();
                ds.setJdbcUrl(mysqlUrl);
                ds.setUsername(mysqlUser);
                ds.setPassword(mysqlPass);
                ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
                return ds;
            }
        } catch (Exception e) {
            HikariDataSource h2Ds = new HikariDataSource();
            h2Ds.setJdbcUrl("jdbc:h2:mem:appdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE");
            h2Ds.setUsername(mysqlUser);
            h2Ds.setPassword(mysqlPass);
            h2Ds.setDriverClassName("org.h2.Driver");
            return h2Ds;
        }
    }
}
