package co.com.bancolombia.dynamodb.repository.customer;

import co.com.bancolombia.dynamodb.repository.customer.data.CustomerData;
import co.com.bancolombia.model.customer.Customer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapperImp;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class CustomerDBRepositoryTest {
    @Mock
    DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
    @Mock
    DynamoDbAsyncTable<CustomerData> customerTable;

    CustomerDBRepository customerDBRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(dynamoDbEnhancedAsyncClient.table(anyString(), ArgumentMatchers.<TableSchema<CustomerData>>any()))
                .thenReturn(customerTable);
        customerDBRepository = new CustomerDBRepository(dynamoDbEnhancedAsyncClient, new ObjectMapperImp());
    }

    @Test
    void findByIdTest() {
        CustomerData customerData = new CustomerData("id", "email");
        when(customerTable.getItem(any(Key.class)))
                .thenReturn(CompletableFuture.completedFuture(customerData));

        customerDBRepository.findById("id")
                .as(StepVerifier::create)
                .assertNext(customer ->  {
                    Assertions.assertThat(customer.id()).isEqualTo("id");
                    assertEquals("email", customer.email());
                }).verifyComplete();
    }

    @Test
    void findCustomersTest() {
        List<CustomerData> list = List.of(new CustomerData("id", "email"));
        Page<CustomerData> page = Page.create(list);
        SdkPublisher<Page<CustomerData>> sdkPublisher = SdkPublisher.adapt(Mono.just(page));
        PagePublisher<CustomerData> customerDataPagePublisher = PagePublisher.create(sdkPublisher);

        when(customerTable.query(any(QueryEnhancedRequest.class)))
                .thenReturn(customerDataPagePublisher);

        customerDBRepository.findCustomers("id")
                .as(StepVerifier::create)
                .assertNext(customers ->  {
                    assertEquals(1, customers.size());
                    var customer = customers.get(0);
                    Assertions.assertThat(customer.id()).isEqualTo("id");
                    assertEquals("email", customer.email());
                }).verifyComplete();
    }

    @Test
    void registerTest() {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        when(customerTable.putItem(any(CustomerData.class)))
                .thenReturn(future);

        Customer customer = Customer.builder().id("id").build();
        customerDBRepository.register(customer)
                .as(StepVerifier::create)
                .assertNext(newCustomer ->  {
                    assertEquals("id", newCustomer.id());
                }).verifyComplete();
    }
}