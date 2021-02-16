package io.rocketbase.commons.model.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.rocketbase.commons.dto.address.Gender;
import io.rocketbase.commons.model.HasFirstAndLastName;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@JsonDeserialize(as = SimpleUserProfile.class)
public interface UserProfile extends Serializable, HasFirstAndLastName {

    Gender getGender();

    @Nullable
    void setGender(Gender gender);

    String getSalutation();

    @Nullable
    @Size(max = 10)
    void setSalutation(String salutation);

    String getTitle();

    @Nullable
    @Size(max = 10)
    void setTitle(String title);

    @Nullable
    @Size(max = 100)
    void setFirstName(String firstName);

    @Nullable
    @Size(max = 100)
    void setLastName(String lastName);

    String getAvatar();

    @Nullable
    @Size(max = 2000)
    void setAvatar(String avatar);

    String getAbout();

    @Nullable
    @Size(max = 500)
    void setAbout(String about);

    Set<PhoneNumber> getPhoneNumbers();

    @Nullable
    void setPhoneNumbers(Set<PhoneNumber> phoneNumbers);

    Set<OnlineProfile> getOnlineProfiles();

    @Nullable
    void setOnlineProfiles(Set<OnlineProfile> onlineProfiles);

    String getLocation();

    @Nullable
    @Size(max = 255)
    void setLocation(String location);

    String getCountry();

    /**
     * Alpha-2 code - ISO 3166<br>
     * for example: de, gb, us
     */
    @Nullable
    @Size(max = 2)
    void setCountry(String country);

    String getJobTitle();

    @Nullable
    @Size(max = 100)
    void setJobTitle(String jobTitle);

    String getOrganization();

    @Nullable
    @Size(max = 100)
    void setOrganization(String organization);
}
