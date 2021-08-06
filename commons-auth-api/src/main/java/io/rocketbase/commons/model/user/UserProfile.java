package io.rocketbase.commons.model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.rocketbase.commons.dto.address.Gender;
import io.rocketbase.commons.model.HasFirstAndLastName;

import javax.annotation.Nullable;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(as = SimpleUserProfile.class)
public interface UserProfile extends Serializable, HasFirstAndLastName {

    @Nullable
    Gender getGender();

    void setGender(Gender gender);

    @Nullable
    String getSalutation();

    void setSalutation(@Size(max = 10) String salutation);

    @Nullable
    String getTitle();

    void setTitle(@Size(max = 10) String title);

    void setFirstName(@Size(max = 100) String firstName);

    void setLastName(@Size(max = 100) String lastName);

    @Nullable
    String getAvatar();

    void setAvatar(@Size(max = 2000) String avatar);

    @Nullable
    String getAbout();

    void setAbout(@Size(max = 500) String about);

    @Nullable
    Set<PhoneNumber> getPhoneNumbers();

    void setPhoneNumbers(Set<PhoneNumber> phoneNumbers);

    @Nullable
    Set<OnlineProfile> getOnlineProfiles();

    void setOnlineProfiles(Set<OnlineProfile> onlineProfiles);

    @Nullable
    String getLocation();

    void setLocation(@Size(max = 255) String location);

    @Nullable
    String getCountry();

    /**
     * Alpha-2 code - ISO 3166<br>
     * for example: de, gb, us
     */
    void setCountry(@Size(max = 2) String country);

    @Nullable
    String getJobTitle();

    void setJobTitle(@Size(max = 100) String jobTitle);

    @Nullable
    String getOrganization();

    void setOrganization(@Size(max = 100) String organization);
}
