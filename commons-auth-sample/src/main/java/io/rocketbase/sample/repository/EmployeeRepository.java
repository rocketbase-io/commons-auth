package io.rocketbase.sample.repository;

import io.rocketbase.sample.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepository extends MongoRepository<Employee, String> {

    Employee findOneByCompanyIdAndId(String companyId, String id);

    Page<Employee> findAllByCompanyId(String companyId, Pageable pageRequest);
}
