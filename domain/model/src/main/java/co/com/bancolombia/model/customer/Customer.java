package co.com.bancolombia.model.customer;
import lombok.Builder;

@Builder(toBuilder = true)
public record Customer(String id, String email) {
}
