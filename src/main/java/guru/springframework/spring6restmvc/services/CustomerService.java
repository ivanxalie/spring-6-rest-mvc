package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
    List<Customer> customers();

    Optional<Customer> findById(UUID id);

    Customer saveNewCustomer(Customer customer);

    void updateById(UUID id, Customer customer);

    void deleteById(UUID id);

    void patchById(UUID id, Customer customer);
}
