package com.mycompany.syos.frameworks.database;

import com.mycompany.syos.domain.Transaction;
import com.mycompany.syos.domain.TransactionItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

@Repository
public class MySQLTransactionRepository implements TransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    public MySQLTransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Transaction transaction) {
        String sql = "INSERT INTO transactions (total_price, discount, cash_tendered, change_amount, date) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setBigDecimal(1, transaction.getTotalPrice());
            ps.setBigDecimal(2, transaction.getDiscount());
            ps.setBigDecimal(3, transaction.getCashTendered());
            ps.setBigDecimal(4, transaction.getChangeAmount());
            ps.setTimestamp(5, new java.sql.Timestamp(transaction.getDate().getTime()));
            return ps;
        }, keyHolder);

        int transactionId = keyHolder.getKey().intValue();

        for (TransactionItem item : transaction.getItems()) {
            String itemSql = "INSERT INTO transaction_items (transaction_id, item_id, quantity) VALUES (?, ?, ?)";
            jdbcTemplate.update(itemSql, transactionId, item.getItem().getId(), item.getQuantity());
        }
    }

    @Override
    public List<Transaction> getTransactionsByDate(Date date) {
        String sql = "SELECT * FROM transactions WHERE DATE(date) = DATE(?)";
        return jdbcTemplate.query(sql, new Object[]{new java.sql.Date(date.getTime())}, (rs, rowNum) -> {
            Transaction transaction = new Transaction();
            transaction.setId(rs.getInt("id"));
            transaction.setDate(rs.getTimestamp("date"));
            transaction.setTotalPrice(rs.getBigDecimal("total_price"));
            transaction.setDiscount(rs.getBigDecimal("discount"));
            transaction.setCashTendered(rs.getBigDecimal("cash_tendered"));
            transaction.setChangeAmount(rs.getBigDecimal("change_amount"));
            return transaction;
        });
    }

    @Override
    public List<Transaction> getAllTransactions() {
        String sql = "SELECT * FROM transactions";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Transaction transaction = new Transaction();
            transaction.setId(rs.getInt("id"));
            transaction.setDate(rs.getTimestamp("date"));
            transaction.setTotalPrice(rs.getBigDecimal("total_price"));
            transaction.setDiscount(rs.getBigDecimal("discount"));
            transaction.setCashTendered(rs.getBigDecimal("cash_tendered"));
            transaction.setChangeAmount(rs.getBigDecimal("change_amount"));
            return transaction;
        });
    }
}
