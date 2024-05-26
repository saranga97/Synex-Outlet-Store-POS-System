package com.mycompany.syos.frameworks.database;

import com.mycompany.syos.domain.Item;
import java.util.List;

public interface ItemRepository {
    List<Item> getAllItems();
    Item getItemByCode(String code);
    void reduceStock(String code, int quantity);
    void updateStock(String code, int quantity);
    List<Item> getItemsToReshelve();
    List<Item> getReorderLevels();
}
