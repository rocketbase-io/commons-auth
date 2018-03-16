package io.rocketbase.sample.dto.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRead implements Serializable {

    private String id;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private boolean female;

    private String email;

    private CompanyRead company;

}
