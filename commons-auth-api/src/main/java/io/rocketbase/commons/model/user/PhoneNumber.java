package io.rocketbase.commons.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneNumber implements Serializable {

    /**
     * for example: phone, cellphone, fax, personal, business...
     */
    @NotNull
    @Size(max = 15)
    private String type;

    @NotNull
    @Size(max = 20)
    private String number;
}