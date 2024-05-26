package com.mycompany.syos.frameworks;


import com.mycompany.syos.frameworks.database.MySQLItemRepository;
import com.mycompany.syos.frameworks.database.MySQLTransactionRepository;
import com.mycompany.syos.usecases.PurchaseService;
import com.mycompany.syos.usecases.PurchaseServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/SYOS");
        dataSource.setUsername("springstudent");
        dataSource.setPassword("springstudent");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public MySQLItemRepository itemRepository(JdbcTemplate jdbcTemplate) {
        return new MySQLItemRepository(jdbcTemplate);
    }

    @Bean
    public MySQLTransactionRepository transactionRepository(JdbcTemplate jdbcTemplate) {
        return new MySQLTransactionRepository(jdbcTemplate);
    }

    @Bean
    public PurchaseService purchaseService(MySQLItemRepository itemRepository, MySQLTransactionRepository transactionRepository) {
        return new PurchaseServiceImpl(itemRepository, transactionRepository);
    }
}

