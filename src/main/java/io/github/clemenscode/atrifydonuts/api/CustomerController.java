package io.github.clemenscode.atrifydonuts.api;


import io.github.clemenscode.atrifydonuts.model.Order;
import io.github.clemenscode.atrifydonuts.model.OrderView;
import io.github.clemenscode.atrifydonuts.service.DonutQueueService;
import io.github.clemenscode.atrifydonuts.utils.Constants;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(Constants.BASE_API_PATH + "/order")
@RestController
public class CustomerController {

    private final DonutQueueService service;

    public CustomerController(DonutQueueService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public ResponseEntity<String> add(
            @RequestBody Order order
    ) {
        if(isClientIdValid(order.getClientId())){
            try {
                service.addNewOrder(order);
                return ResponseEntity.ok(null);
            } catch (Exception e) {
                return badRequest().body(e.getMessage());
            }
        }else {
            return badRequest().body("Invalid Client ID!");
        }
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<String> delete(
            @PathVariable("clientId") String clientId
    ) {
        if (isClientIdValid(parseClientId(clientId))) {
            service.removeOrder(parseClientId(clientId));
            return ResponseEntity.ok(null);
        } else {
            return badRequest().body("Invalid Client ID!");
        }
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<OrderView> checkOrder(
            @PathVariable("clientId") String clientId
    ) {
        if (isClientIdValid(parseClientId(clientId))) {
            return ResponseEntity.ok(service.getOrderView(parseClientId(clientId)));
        } else {
            return badRequest().body(null);
        }
    }

    private int parseClientId(String clientId) {
        return Integer.parseInt(clientId);
    }

    private boolean isClientIdValid(int clientId) {
        return clientId < Constants.MAX_CLIENT_ID && clientId > 0;
    }

    private static ResponseEntity.@NotNull BodyBuilder badRequest() {
        return ResponseEntity.badRequest();
    }
}
