package com.mycompany.syos.frameworks.database;

import com.mycompany.syos.domain.Item;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MySQLItemRepository implements ItemRepository {

    private final JdbcTemplate jdbcTemplate;

    public MySQLItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Item> getAllItems() {
        String sql = "SELECT * FROM items";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Item item = new Item();
            item.setId(rs.getInt("id"));
            item.setCode(rs.getString("code"));
            item.setName(rs.getString("name"));
            item.setPrice(rs.getDouble("price"));
            item.setStock(rs.getInt("stock"));
            return item;
        });
    }

    @Override
    public Item getItemByCode(String code) {
        String sql = "SELECT * FROM items WHERE code = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{code}, (rs, rowNum) -> {
            Item item = new Item();
            item.setId(rs.getInt("id"));
            item.setCode(rs.getString("code"));
            item.setName(rs.getString("name"));
            item.setPrice(rs.getDouble("price"));
            item.setStock(rs.getInt("stock"));
            return item;
        });
    }

    @Override
    public void reduceStock(String code, int quantity) {
        String sql = "UPDATE items SET stock = stock - ? WHERE code = ?";
        jdbcTemplate.update(sql, quantity, code);
    }

    @Override
    public void updateStock(String code, int quantity) {
        String sql = "UPDATE items SET stock = ? WHERE code = ?";
        jdbcTemplate.update(sql, quantity, code);
    }

    @Override
    public List<Item> getItemsToReshelve() {
        String sql = "SELECT * FROM items WHERE stock < 10";  // Example condition for reshelving
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Item item = new Item();
            item.setId(rs.getInt("id"));
            item.setCode(rs.getString("code"));
            item.setName(rs.getString("name"));
            item.setPrice(rs.getDouble("price"));
            item.setStock(rs.getInt("stock"));
            return item;
        });
    }

    @Override
    public List<Item> getReorderLevels() {
        String sql = "SELECT * FROM items WHERE stock < 5";  // Example condition for reorder levels
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Item item = new Item();
            item.setId(rs.getInt("id"));
            item.setCode(rs.getString("code"));
            item.setName(rs.getString("name"));
            item.setPrice(rs.getDouble("price"));
            item.setStock(rs.getInt("stock"));
            return item;
        });
    }
}
