package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.model.Beer;
import guru.springframework.spring6restmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
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
    public Beer getBeerById(@PathVariable("beerId") UUID id) {
        log.debug("Get Beer by Id - in controller. Id: {}", id);
        return service.findBeerById(id).orElseThrow(NotFountException::new);
    }

    @GetMapping
    public List<Beer> beers() {
        return service.beers();
    }

    @PostMapping
    public ResponseEntity<Beer> addNewBeer(@RequestBody Beer beer) {
        Beer newBeer = service.saveNewBeer(beer);
        return ResponseEntity.created(URI.create("http://localhost:8080/api/v1/beer/" + newBeer.getId())).body(newBeer);
    }

    @PutMapping(ID)
    public ResponseEntity<?> updateById(@PathVariable("beerId") UUID id, @RequestBody Beer beer) {
        service.updateById(id, beer);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(ID)
    public ResponseEntity<?> deleteById(@PathVariable("beerId") UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(ID)
    public ResponseEntity<?> updateBeerPatchById(@PathVariable("beerId") UUID id, @RequestBody Beer beer) {
        service.patchBeerById(id, beer);
        return ResponseEntity.noContent().build();
    }
}