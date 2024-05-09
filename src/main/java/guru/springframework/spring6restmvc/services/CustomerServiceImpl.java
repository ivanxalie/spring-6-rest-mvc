package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.CustomerDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

import static java.time.LocalDateTime.now;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final Map<UUID, CustomerDTO> customers = new HashMap<>();

    public CustomerServiceImpl() {
        CustomerDTO customerDTO1 = createCustomer("Alex");
        CustomerDTO customerDTO2 = createCustomer("Alice");
        CustomerDTO customerDTO3 = createCustomer("Roberto");

        customers.put(customerDTO1.getId(), customerDTO1);
        customers.put(customerDTO2.getId(), customerDTO2);
        customers.put(customerDTO3.getId(), customerDTO3);
    }

    private static CustomerDTO createCustomer(String name) {
        return CustomerDTO.builder()
                .id(UUID.randomUUID())
                .name(name)
                .version(1)
                .createdDate(now())
                .lastModifiedDate(now())
                .build();
    }

    @Override
    public List<CustomerDTO> customers() {
        return new ArrayList<>(customers.values());
    }

    @Override
    public Optional<CustomerDTO> findById(UUID id) {
        return Optional.of(customers.get(id));
    }

    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO customerDTO) {
        customerDTO.setId(UUID.randomUUID());
        customerDTO.setVersion(1);
        customerDTO.setCreatedDate(now());
        customerDTO.setLastModifiedDate(now());
        customers.put(customerDTO.getId(), customerDTO);
        return customerDTO;
    }

    @Override
    public void updateById(UUID id, CustomerDTO customerDTO) {
        CustomerDTO saved = customers.get(id);
        if (saved != null) {
            saved.setVersion(saved.getVersion() + 1);
            saved.setLastModifiedDate(now());
            saved.setName(customerDTO.getName());
        }
    }

    @Override
    public void deleteById(UUID id) {
        customers.remove(id);
    }

    @Override
    public void patchById(UUID id, CustomerDTO customerDTO) {
        CustomerDTO saved = customers.get(id);
        if (saved != null) {
            if (StringUtils.hasText(customerDTO.getName()))
                saved.setName(customerDTO.getName());
        }
    }
}
