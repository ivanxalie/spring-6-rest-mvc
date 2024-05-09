package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.Customer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

import static java.time.LocalDateTime.now;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final Map<UUID, Customer> customers = new HashMap<>();

    public CustomerServiceImpl() {
        Customer customer1 = createCustomer("Alex");
        Customer customer2 = createCustomer("Alice");
        Customer customer3 = createCustomer("Roberto");

        customers.put(customer1.getId(), customer1);
        customers.put(customer2.getId(), customer2);
        customers.put(customer3.getId(), customer3);
    }

    private static Customer createCustomer(String name) {
        return Customer.builder()
                .id(UUID.randomUUID())
                .name(name)
                .version(1)
                .createdDate(now())
                .lastModifiedDate(now())
                .build();
    }

    @Override
    public List<Customer> customers() {
        return new ArrayList<>(customers.values());
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return Optional.of(customers.get(id));
    }

    @Override
    public Customer saveNewCustomer(Customer customer) {
        customer.setId(UUID.randomUUID());
        customer.setVersion(1);
        customer.setCreatedDate(now());
        customer.setLastModifiedDate(now());
        customers.put(customer.getId(), customer);
        return customer;
    }

    @Override
    public void updateById(UUID id, Customer customer) {
        Customer saved = customers.get(id);
        if (saved != null) {
            saved.setVersion(saved.getVersion() + 1);
            saved.setLastModifiedDate(now());
            saved.setName(customer.getName());
        }
    }

    @Override
    public void deleteById(UUID id) {
        customers.remove(id);
    }

    @Override
    public void patchById(UUID id, Customer customer) {
        Customer saved = customers.get(id);
        if (saved != null) {
            if (StringUtils.hasText(customer.getName()))
                saved.setName(customer.getName());
        }
    }
}
