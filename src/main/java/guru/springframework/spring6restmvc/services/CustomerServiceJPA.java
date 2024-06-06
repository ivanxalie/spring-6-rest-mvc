package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.time.LocalDateTime.now;

@Service
@Primary
@RequiredArgsConstructor
public class CustomerServiceJPA implements CustomerService {
    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    @Override
    @Cacheable(cacheNames = "customerListCache")
    public List<CustomerDTO> customers() {
        return repository.findAll().stream().map(mapper::toCustomerDto).toList();
    }

    @Override
    @Cacheable(cacheNames = "customerCache")
    public Optional<CustomerDTO> findById(UUID id) {
        return repository.findById(id).map(mapper::toCustomerDto);
    }

    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO customerDTO) {
        return mapper.toCustomerDto(repository.save(mapper.toCustomer(customerDTO)));
    }

    @Override
    public Optional<CustomerDTO> updateById(UUID id, CustomerDTO customerDTO) {
        return repository.findById(id).map(customer -> {
            customer.setVersion(customer.getVersion() + 1);
            customer.setLastModifiedDate(now());
            customer.setName(customerDTO.getName());
            return repository.save(customer);
        }).map(mapper::toCustomerDto);
    }

    @Override
    public Optional<CustomerDTO> deleteById(UUID id) {
        Optional<Customer> customer = repository.findById(id);
        repository.deleteById(id);
        return customer.map(mapper::toCustomerDto);
    }

    @Override
    public Optional<CustomerDTO> patchById(UUID id, CustomerDTO customerDTO) {
        return repository.findById(id).map(customer -> {
            if (StringUtils.hasText(customerDTO.getName()))
                customer.setName(customerDTO.getName());
            return repository.save(customer);
        }).map(mapper::toCustomerDto);
    }
}
