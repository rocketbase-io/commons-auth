package io.rocketbase.commons;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.junit.Test;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class GenerateSchema {

    @Test
    public void generate() {
        Map<String, String> settings = new HashMap<>();
        settings.put("connection.driver_class", "org.mariadb.jdbc.Driver");

        settings.put("dialect", "org.hibernate.dialect.MariaDB103Dialect");
        settings.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/generate");

        settings.put("hibernate.connection.username", "root");
        settings.put("hibernate.connection.password", "my-secret-pw");
        settings.put("hibernate.show_sql", "true");
        settings.put("hibernate.format_sql", "true");

        ServiceRegistry serviceRegistry =
                new StandardServiceRegistryBuilder().applySettings(settings).build();

        MetadataSources metadata =
                new MetadataSources(serviceRegistry);
        metadata.addPackage("io.rocketbase.commons.model");

        EnumSet<TargetType> enumSet = EnumSet.of(TargetType.DATABASE);
        SchemaExport schemaExport = new SchemaExport();
        schemaExport.setDelimiter(";");
        schemaExport.setFormat(true);
        schemaExport.setOutputFile("schema.sql");
        schemaExport.execute(enumSet, SchemaExport.Action.BOTH, metadata.buildMetadata());
    }
}
