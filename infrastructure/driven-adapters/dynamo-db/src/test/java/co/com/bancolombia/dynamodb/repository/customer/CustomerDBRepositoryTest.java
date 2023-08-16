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
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
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