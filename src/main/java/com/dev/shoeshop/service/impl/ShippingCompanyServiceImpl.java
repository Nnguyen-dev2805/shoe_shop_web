package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.entity.ShippingCompany;
import com.dev.shoeshop.repository.ShippingCompanyRepository;
import com.dev.shoeshop.service.ShippingCompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingCompanyServiceImpl implements ShippingCompanyService {
    
    private final ShippingCompanyRepository shippingCompanyRepository;
    
    @Override
    @Transactional
    public ShippingCompany saveShippingCompany(ShippingCompany shippingCompany) {
        log.info("Saving shipping company: {}", shippingCompany.getName());
        return shippingCompanyRepository.save(shippingCompany);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ShippingCompany> getAllShippingCompanies(Pageable pageable) {
        log.info("Getting all shipping companies with pagination: {}", pageable);
        return shippingCompanyRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ShippingCompany> getAllShippingCompanies() {
        log.info("Getting all shipping companies without pagination");
        return shippingCompanyRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ShippingCompany> getShippingCompanyById(Integer id) {
        log.info("Getting shipping company by id: {}", id);
        return shippingCompanyRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ShippingCompany> searchByName(String name, Pageable pageable) {
        log.info("Searching shipping companies by name: {} with pagination: {}", name, pageable);
        return shippingCompanyRepository.findByNameContainingIgnoreCase(name, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ShippingCompany> getByIsActive(Boolean isActive, Pageable pageable) {
        log.info("Getting shipping companies by isActive: {} with pagination: {}", isActive, pageable);
        return shippingCompanyRepository.findByIsActive(isActive, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ShippingCompany> searchByNameAndIsActive(String name, Boolean isActive, Pageable pageable) {
        log.info("Searching shipping companies by name: {} and isActive: {} with pagination: {}", name, isActive, pageable);
        return shippingCompanyRepository.findByNameContainingIgnoreCaseAndIsActive(name, isActive, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ShippingCompany> getActiveShippingCompanies() {
        log.info("Getting all active shipping companies");
        return shippingCompanyRepository.findByIsActiveTrue();
    }
    
    @Override
    @Transactional
    public void deleteShippingCompany(Integer id) {
        log.info("Deleting shipping company with id: {}", id);
        shippingCompanyRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countByIsActive(Boolean isActive) {
        log.info("Counting shipping companies by isActive: {}", isActive);
        return shippingCompanyRepository.countByIsActive(isActive);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countAllShippingCompanies() {
        log.info("Counting all shipping companies");
        return shippingCompanyRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        log.info("Checking if shipping company exists by name: {}", name);
        return shippingCompanyRepository.existsByNameIgnoreCase(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndIdNot(String name, Integer id) {
        log.info("Checking if shipping company exists by name: {} and not id: {}", name, id);
        return shippingCompanyRepository.existsByNameIgnoreCaseAndIdNot(name, id);
    }
}
