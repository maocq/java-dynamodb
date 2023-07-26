package co.com.bancolombia.model.customer.gateways;

import co.com.bancolombia.model.customer.Customer;
import reactor.core.publisher.Mono;

public interface CustomerRepository {

    Mono<Customer> findById(String id);
    Mono<Customer> register(Customer customer);
}
