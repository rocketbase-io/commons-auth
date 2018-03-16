package io.rocketbase.sample.initializer;

import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import io.rocketbase.sample.model.Company;
import io.rocketbase.sample.model.Employee;
import io.rocketbase.sample.repository.CompanyRepository;
import io.rocketbase.sample.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class DataInitializer {

    @Resource
    private EmployeeRepository personRepository;

    @Resource
    private CompanyRepository companyRepository;

    private AtomicInteger companyCounter = new AtomicInteger(0);

    private Map<String, Company> companyCache = new HashMap<>();

    @PostConstruct
    public void postConstruct() {
        if (companyRepository.count() == 0) {
            Fairy fairy = Fairy.create(Locale.GERMAN);
            List<Company> companyList = new ArrayList<>();
            for (int count = 0; count < 100; count++) {
                io.codearte.jfairy.producer.company.Company company = fairy.company();
                companyList.add(Company.builder()
                        .name(company.getName())
                        .email(company.getEmail())
                        .url(company.getUrl())
                        .build());
            }
            companyRepository.save(companyList);

            List<Employee> personList = new ArrayList<>();
            for (int count = 0; count < 1000; count++) {
                io.codearte.jfairy.producer.person.Person person = fairy.person();
                DateTime dateOfBirth = person.getDateOfBirth();
                personList.add(Employee.builder()
                        .firstName(person.getFirstName())
                        .lastName(person.getLastName())
                        .dateOfBirth(LocalDate.of(dateOfBirth.getYear(), dateOfBirth.getMonthOfYear(), dateOfBirth.getDayOfMonth()))
                        .female(person.getSex().equals(Person.Sex.FEMALE))
                        .email(person.getEmail())
                        .company(companyList.get(ThreadLocalRandom.current().nextInt(0, companyList.size())))
                        .build());
            }
            personRepository.save(personList);
            log.info("initialized {} persons and {} companies", personList.size(), companyCounter.get());

        }


    }
}
