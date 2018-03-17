package io.rocketbase.sample.converter;

import io.rocketbase.commons.converter.EntityReadWriteConverter;
import io.rocketbase.sample.dto.company.CompanyRead;
import io.rocketbase.sample.dto.company.CompanyWrite;
import io.rocketbase.sample.model.Company;
import org.mapstruct.*;

import java.util.List;

@Mapper(config = CentralConfig.class)
public interface CompanyConverter extends EntityReadWriteConverter<Company, CompanyRead, CompanyWrite> {

    Company toEntity(CompanyRead data);

    @InheritInverseConfiguration
    CompanyRead fromEntity(Company entity);

    List<CompanyRead> fromEntities(List<Company> entities);

    @Mappings({
            @Mapping(target = "id", ignore = true),
    })
    Company newEntity(CompanyWrite workspace);

    @InheritConfiguration()
    Company updateEntityFromEdit(CompanyWrite edit, @MappingTarget Company entity);
}
