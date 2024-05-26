package com.mycompany.syos.adapters;

import com.mycompany.syos.domain.Item;
import com.mycompany.syos.frameworks.database.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("/{code}")
    public ResponseEntity<Item> getItemByCode(@PathVariable String code) {
        Item item = itemRepository.getItemByCode(code);
        if (item == null) {
            throw new ItemNotFoundException("Item not found with code: " + code);
        }
        return ResponseEntity.ok(item);
    }

    @GetMapping("/currentStock")
    public ResponseEntity<List<Item>> getCurrentStock() {
        List<Item> items = itemRepository.getAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/reorderLevels")
    public ResponseEntity<List<Item>> getReorderLevels() {
        List<Item> items = itemRepository.getAllItems()
                                         .stream()
                                         .filter(item -> item.getStock() < 50)
                                         .collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }
}

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}
