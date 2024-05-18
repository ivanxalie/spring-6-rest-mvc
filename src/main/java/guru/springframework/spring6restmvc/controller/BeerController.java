package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerService;
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
@RequestMapping(BeerController.PATH)
@Slf4j
public class BeerController {
    public static final String PATH = "/api/v1/beer";
    public static final String ID = "/{beerId}";
    public static final String PATH_ID = PATH + ID;
    private final BeerService service;

    @GetMapping(ID)
    public BeerDTO findBeerById(@PathVariable("beerId") UUID id) {
        log.debug("Get Beer by Id - in controller. Id: {}", id);
        return service.findById(id).orElseThrow(NotFountException::new);
    }

    @GetMapping
    public Page<BeerDTO> beers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "beerStyle", required = false) BeerStyle style,
            @RequestParam(value = "showInventory", required = false) Boolean showInventory,
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return service.beers(name, style, showInventory, pageNumber, pageSize);
    }

    @PostMapping
    public ResponseEntity<BeerDTO> addNewBeer(@Valid @RequestBody BeerDTO beerDto) {
        BeerDTO newBeerDTO = service.saveNewBeer(beerDto);
        return ResponseEntity.created(URI.create("http://localhost:8080/api/v1/beer/" + newBeerDTO.getId())).body(newBeerDTO);
    }

    @PutMapping(ID)
    public ResponseEntity<?> updateById(@PathVariable("beerId") UUID id, @Valid @RequestBody BeerDTO beerDto) {
        if (service.updateById(id, beerDto).isEmpty())
            throw new NotFountException();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(ID)
    public ResponseEntity<?> deleteById(@PathVariable("beerId") UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(ID)
    public ResponseEntity<?> updateBeerPatchById(@PathVariable("beerId") UUID id, @Valid @RequestBody BeerDTO beerDto) {
        service.patchById(id, beerDto);
        return ResponseEntity.noContent().build();
    }
}