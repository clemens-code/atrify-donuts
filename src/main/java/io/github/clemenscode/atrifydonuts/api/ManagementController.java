package io.github.clemenscode.atrifydonuts.api;

import io.github.clemenscode.atrifydonuts.model.OrderView;
import io.github.clemenscode.atrifydonuts.service.DonutQueueService;
import io.github.clemenscode.atrifydonuts.utils.Constants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping(Constants.BASE_API_PATH + "/management")
@RestController
public class ManagementController {
    private final DonutQueueService service;

    public ManagementController(DonutQueueService service) {
        this.service = service;
    }

    @GetMapping("/")
    public List<OrderView> allOrdersInQueue() {
        return service.allOrdersInQueue();
    }
}
