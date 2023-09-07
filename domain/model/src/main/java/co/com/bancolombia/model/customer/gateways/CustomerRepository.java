package co.com.bancolombia.model.customer.gateways;

import co.com.bancolombia.model.customer.Customer;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CustomerRepository {

    Mono<Customer> findById(String id);
    Mono<List<Customer>> findCustomers(String id);
    Mono<Customer> register(Customer customer);
}
