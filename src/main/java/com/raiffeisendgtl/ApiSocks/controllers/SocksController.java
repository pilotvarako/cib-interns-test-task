package com.raiffeisendgtl.ApiSocks.controllers;

import com.raiffeisendgtl.ApiSocks.components.FinderFactory;
import com.raiffeisendgtl.ApiSocks.components.FinderOperation;
import com.raiffeisendgtl.ApiSocks.entities.Socks;
import com.raiffeisendgtl.ApiSocks.services.SocksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SocksController {

    private final FinderFactory finderFactory;
    private final SocksService socksService;

    @Autowired
    public SocksController(FinderFactory finderFactory, SocksService socksService) {
        this.finderFactory = finderFactory;
        this.socksService = socksService;
    }

    @PostMapping("api/socks/income")
    public ResponseEntity<String> income(@RequestBody Socks request) {
        socksService.income(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("api/socks/outcome")
    public ResponseEntity<String> outcome(@RequestBody Socks request) {
        socksService.outcome(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("api/socks")
    public ResponseEntity<String> socks(@RequestParam("color") String color,
                                         @RequestParam("operation") String operation,
                                         @RequestParam("cottonPart") int cottonPart) {
        FinderOperation finderOperation = FinderFactory.createFinder(operation);
        Integer result = socksService.getCountSocks(color, finderOperation, cottonPart);
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

}
