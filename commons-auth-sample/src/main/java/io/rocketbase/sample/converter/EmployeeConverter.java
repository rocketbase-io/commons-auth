package io.rocketbase.sample.converter;

import io.rocketbase.commons.converter.EntityReadWriteConverter;
import io.rocketbase.sample.dto.employee.EmployeeRead;
import io.rocketbase.sample.dto.employee.EmployeeWrite;
import io.rocketbase.sample.model.Employee;
import org.mapstruct.*;

import java.util.List;

@Mapper(config = CentralConfig.class, uses = {CompanyConverter.class})
public interface EmployeeConverter extends EntityReadWriteConverter<Employee, EmployeeRead, EmployeeWrite> {
    @Mappings({
            @Mapping(target = "company", ignore = true)
    })
    Employee toEntity(EmployeeRead data);

    @InheritInverseConfiguration
    EmployeeRead fromEntity(Employee entity);

    List<EmployeeRead> fromEntities(List<Employee> entities);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "company", ignore = true),
    })
    Employee newEntity(EmployeeWrite edit);

    @InheritConfiguration()
    Employee updateEntityFromEdit(EmployeeWrite edit, @MappingTarget Employee entity);
}
