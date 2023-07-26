package co.com.bancolombia.dynamodb.repository.customer.data;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
public class CustomerData {

    private String id;
    private String email;

    public CustomerData() {
    }

    public CustomerData(String id, String email) {
        this.id = id;
        this.email = email;
    }

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDbAttribute("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
