package io.rocketbase.sample.repository;

import io.rocketbase.sample.model.Company;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompanyRepository extends MongoRepository<Company, String> {

    Company findByName(String name);
}
