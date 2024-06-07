package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.model.BeerOrderDTO;
import guru.springframework.spring6restmvc.services.BeerOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(BeerOrderController.PATH)
@Slf4j
public class BeerOrderController {
    public static final String PATH = "/api/v1/beerOrder";
    public static final String ID = "/{beerDtoId}";
    public static final String PATH_ID = PATH + ID;
    private final BeerOrderService service;

    @GetMapping(ID)
    public BeerOrderDTO findBeerById(@PathVariable("beerDtoId") UUID id) {
        log.debug("Get Beer by Id - in controller. Id: {}", id);
        return service.findById(id).orElseThrow(NotFountException::new);
    }

    @GetMapping
    public Page<BeerOrderDTO> beers(
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return service.orders(pageNumber, pageSize);
    }

    @PostMapping
    public ResponseEntity<BeerOrderDTO> addNewBeer(@Valid @RequestBody BeerOrderDTO BeerOrderDTO) {
        BeerOrderDTO newBeerOrderDTO = service.saveNewBeerOrder(BeerOrderDTO);
        return ResponseEntity.created(URI.create("http://localhost:8080/api/v1/beerOrder/" + newBeerOrderDTO.getId())).body(newBeerOrderDTO);
    }

    @PutMapping(ID)
    public ResponseEntity<?> updateById(@PathVariable("beerDtoId") UUID id, @Valid @RequestBody BeerOrderDTO BeerOrderDTO) {
        if (service.updateById(id, BeerOrderDTO).isEmpty())
            throw new NotFountException();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(ID)
    public ResponseEntity<?> deleteById(@PathVariable("beerDtoId") UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(ID)
    public ResponseEntity<?> updateBeerPatchById(@PathVariable("beerDtoId") UUID id, @Valid @RequestBody BeerOrderDTO BeerOrderDTO) {
        service.patchById(id, BeerOrderDTO);
        return ResponseEntity.noContent().build();
    }
}