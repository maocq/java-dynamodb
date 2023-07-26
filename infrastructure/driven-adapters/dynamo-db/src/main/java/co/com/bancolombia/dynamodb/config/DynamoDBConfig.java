package co.com.bancolombia.dynamodb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.net.URI;

@Configuration
public class DynamoDBConfig {

    @Bean
    @Profile("!local")
    public DynamoDbAsyncClient amazonDynamoDBAsync(MetricPublisher publisher, @Value("${aws.region}") String region) {
        return DynamoDbAsyncClient.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .region(Region.of(region))
                .overrideConfiguration(o -> o.addMetricPublisher(publisher))
                .build();
    }

    @Bean
    @Profile("local")
    public DynamoDbAsyncClient amazonDynamoDB(@Value("${aws.dynamodb.endpoint}") String endpoint,
                                              @Value("${aws.region}") String region,
                                              MetricPublisher publisher) {
        return DynamoDbAsyncClient.builder()
                .credentialsProvider(ProfileCredentialsProvider.create("default"))
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .overrideConfiguration(o -> o.addMetricPublisher(publisher))
                .build();
    }


    @Bean
    public DynamoDbEnhancedAsyncClient getDynamoDbEnhancedAsyncClient(DynamoDbAsyncClient amazonDynamoDBAsync) {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(amazonDynamoDBAsync)
                .build();
    }
}
