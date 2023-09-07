package co.com.bancolombia.api;

import co.com.bancolombia.model.customer.Customer;
import co.com.bancolombia.model.customer.gateways.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final CustomerRepository customerRepository;

    public Mono<ServerResponse> listenGETCustomer(ServerRequest serverRequest) {
        String id = serverRequest.queryParam("id").orElse("123");
        return customerRepository.findById(id)
                .flatMap(customer -> ServerResponse.ok().bodyValue(customer));
    }

    public Mono<ServerResponse> listenGETCustomers(ServerRequest serverRequest) {
        String id = serverRequest.queryParam("id").orElse("123");
        return customerRepository.findCustomers(id)
                .flatMap(customer -> ServerResponse.ok().bodyValue(customer));
    }

    public Mono<ServerResponse> listenGETRegister(ServerRequest serverRequest) {
        String id = serverRequest.queryParam("id").orElse("123");
        var customer = Customer.builder()
                .id(id)
                .email("hello@yopmail.com").build();

        return customerRepository.register(customer)
                .flatMap(cust -> ServerResponse.ok().bodyValue(cust));
    }
}
