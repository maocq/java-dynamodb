package co.com.bancolombia.dynamodb.repository.customer;

import co.com.bancolombia.dynamodb.repository.customer.data.CustomerData;
import co.com.bancolombia.model.customer.Customer;
import co.com.bancolombia.model.customer.gateways.CustomerRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;

@Repository
public class CustomerDBRepository implements CustomerRepository {

    private final DynamoDbAsyncTable<CustomerData> customerTable;
    private final ObjectMapper mapper;


    public CustomerDBRepository(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper) {
        customerTable = connectionFactory.table("customerother", TableSchema.fromBean(CustomerData.class));
        this.mapper = mapper;
        //customerTable.createTable();
    }


    @Override
    public Mono<Customer> findById(String id) {
        Key key = Key.builder()
                .partitionValue(id)
                .sortValue("hello@yopmail.com")
                .build();
        return Mono.fromFuture(customerTable.getItem(key))
                .map(this::toEntity);
    }

    @Override
    public Mono<List<Customer>> findCustomers(String id) {
        QueryConditional keyEqual = QueryConditional.keyEqualTo(b -> b.partitionValue(id));
        QueryEnhancedRequest tableQuery = QueryEnhancedRequest.builder()
                .queryConditional(keyEqual)
                .build();

        return Flux.from(customerTable.query(tableQuery).items().map(this::toEntity))
                .collectList();
    }

    @Override
    public Mono<Customer> register(Customer customer) {
        /* or
        return Mono.fromFuture(customerTable.putItem(toData(customer)))
                .thenReturn(customer);
         */
        return Mono.fromFuture(customerTable.putItem(toData(customer))
                        .thenApply(empty -> customer));
    }

    private Customer toEntity(CustomerData data) {
        return mapper.mapBuilder(data, Customer.CustomerBuilder.class).build();
    }

    private CustomerData toData(Customer entity) {
        //return mapper.map(entity, CustomerData.class);
        return new CustomerData(entity.id(), entity.email());
    }
}
