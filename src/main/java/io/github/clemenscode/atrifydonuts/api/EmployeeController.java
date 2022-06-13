package io.github.clemenscode.atrifydonuts.api;

import io.github.clemenscode.atrifydonuts.model.Order;
import io.github.clemenscode.atrifydonuts.service.DonutQueueService;
import io.github.clemenscode.atrifydonuts.utils.Constants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping(Constants.BASE_API_PATH+"/cart")
@RestController
public class EmployeeController {
    private final DonutQueueService service;

    public EmployeeController(DonutQueueService service) {
        this.service = service;
    }

    @GetMapping("/next")
    public List<Order> nextCart() {
        return service.retrieveNextCart();
    }
}
