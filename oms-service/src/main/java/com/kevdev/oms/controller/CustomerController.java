package com.kevdev.oms.controller;

import com.kevdev.oms.entity.Customer;
import com.kevdev.oms.repository.CustomerRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository repository;

    public CustomerController(CustomerRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Customer> getAll() { return repository.findAll(); }

    @PostMapping
    public Customer create(@RequestBody Customer c) { return repository.save(c); }

    @GetMapping("/{id}")
    public Customer getById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repository.deleteById(id); }
}

