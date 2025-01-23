package br.com.estudo.microservice.orderservice.services.impl;

import br.com.estudo.microservice.orderservice.clients.UserServiceFeignClient;
import br.com.estudo.microservice.orderservice.entities.Order;
import br.com.estudo.microservice.orderservice.mapper.OrderMapper;
import br.com.estudo.microservice.orderservice.repositories.OrderRepository;
import br.com.estudo.microservice.orderservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.enums.OrderStatusEnum;
import models.exceptions.ResourceNotFoundException;
import models.requests.CreateOrderRequest;
import models.requests.UpdateOrderRequest;
import models.responses.OrderResponse;
import models.responses.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserServiceFeignClient userServiceFeignClient;

    private Order getById(final Long id) {
        return orderRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Object not found. Id: " + id + ", type: " + Order.class.getSimpleName())
                );
    }

    @Override
    public void save(CreateOrderRequest request) {
        validateUserId(request.requestId());
        Order entity = orderRepository.save(orderMapper.fromRequest(request));
        log.info("Order created: {}", entity);
    }

    @Override
    public OrderResponse findById(final Long id) {
        return orderMapper.fromEntity(getById(id));
    }



    @Override
    public OrderResponse update(final Long id, UpdateOrderRequest request) {
        Order entity = getById(id);
        Order updated = orderMapper.fromRequest(entity, request);

        if (updated.getStatus().equals(OrderStatusEnum.CLOSED)) {
            updated.setClosedAt(now());
        }

        return orderMapper.fromEntity(orderRepository.save(updated));
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.delete(getById(id));
    }

    @Override
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream().map(orderMapper::fromEntity).toList();
    }

    @Override
    public Page<OrderResponse> findAllPageable(PageRequest pageRequest) {
        return orderRepository.findAll(pageRequest).map(orderMapper::fromEntity);
    }

    void validateUserId(final String userId) {
        UserResponse body = userServiceFeignClient.findById(userId).getBody();
        log.info("User found: {}", body);
    }
}
