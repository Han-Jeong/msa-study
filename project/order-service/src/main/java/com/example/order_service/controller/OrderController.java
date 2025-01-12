package com.example.order_service.controller;

import com.example.order_service.dto.OrderDto;
import com.example.order_service.jpa.OrderEntity;
import com.example.order_service.service.OrderService;
import com.example.order_service.vo.RequestOrder;
import com.example.order_service.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final Environment env;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's working in OrderService on Port %s",
                env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable String userId,
                                                     @RequestBody RequestOrder requestOrder) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(requestOrder, OrderDto.class);
        orderDto.setUserId(userId);
        OrderDto createdOrderDto = orderService.createOrder(orderDto);
        ResponseOrder result = mapper.map(createdOrderDto, ResponseOrder.class);

        return ResponseEntity.status(CREATED).body(result);

    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getByUserId(@PathVariable String userId) {
        Iterable<OrderEntity> orders = orderService.getOrdersByUserId(userId);
        List<ResponseOrder> result = new ArrayList<>();
        orders.forEach(o -> {
            ResponseOrder responseOrder = new ModelMapper().map(o, ResponseOrder.class);
            result.add(responseOrder);
        });
        return ResponseEntity.status(OK).body(result);
    }
}
