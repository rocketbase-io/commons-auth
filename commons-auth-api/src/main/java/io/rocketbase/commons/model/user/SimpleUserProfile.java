package io.rocketbase.commons.model.user;

import io.rocketbase.commons.dto.address.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleUserProfile implements UserProfile {

    private Gender gender;

    private String salutation;

    private String title;

    private String firstName;

    private String lastName;

    private String avatar;

    private String about;

    private Set<PhoneNumber> phoneNumbers;

    private Set<OnlineProfile> onlineProfiles;

    private String location;

    private String country;

    private String jobTitle;

    private String organization;

    public SimpleUserProfile(UserProfile other) {
        this.gender = other.getGender();
        this.title = other.getTitle();
        this.firstName = other.getFirstName();
        this.lastName = other.getLastName();
        this.avatar = other.getAvatar();
        this.about = other.getAbout();
        this.phoneNumbers = other.getPhoneNumbers() != null ? new HashSet<>(other.getPhoneNumbers()) : null;
        this.onlineProfiles = other.getOnlineProfiles() != null ? new HashSet<>(other.getOnlineProfiles()) : null;
        this.location = other.getLocation();
        this.country = other.getCountry();
        this.jobTitle = other.getJobTitle();
        this.organization = other.getOrganization();
    }
}
