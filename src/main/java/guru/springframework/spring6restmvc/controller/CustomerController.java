package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(CustomerController.PATH)
public class CustomerController {
    public static final String PATH = "/api/v1/customer";
    public static final String ID = "/{customerId}";
    public static final String PATH_ID = PATH + ID;
    private final CustomerService service;

    @GetMapping
    public List<CustomerDTO> customers() {
        return service.customers();
    }

    @GetMapping(ID)
    public CustomerDTO getById(@PathVariable("customerId") UUID id) {
        return service.findById(id).orElseThrow(NotFountException::new);
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> saveNewCustomer(@RequestBody CustomerDTO customerDTO) {
        CustomerDTO saved = service.saveNewCustomer(customerDTO);
        return ResponseEntity.created(URI.create("http://localhost:8080/api/v1/customer/" + saved.getId())).body(saved);
    }

    @PutMapping(ID)
    public ResponseEntity<?> updateById(@PathVariable("customerId") UUID id, @RequestBody CustomerDTO customerDTO) {
        service.updateById(id, customerDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(ID)
    public ResponseEntity<?> deleteById(@PathVariable("customerId") UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(ID)
    public ResponseEntity<?> patchById(@PathVariable("customerId") UUID id, @RequestBody CustomerDTO customerDTO) {
        service.patchById(id, customerDTO);
        return ResponseEntity.noContent().build();
    }
}
