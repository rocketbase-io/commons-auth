package io.rocketbase.commons.model.embedded;

import io.rocketbase.commons.dto.address.Gender;
import io.rocketbase.commons.model.converter.SetOnlineProfileConverter;
import io.rocketbase.commons.model.converter.SetPhoneNumberConverter;
import io.rocketbase.commons.model.user.OnlineProfile;
import io.rocketbase.commons.model.user.PhoneNumber;
import io.rocketbase.commons.model.user.UserProfile;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.util.Set;

@Embeddable
@Data
@NoArgsConstructor
public class UserProfileJpaEmbedded implements UserProfile {

    private Gender gender;

    @Column(length = 10)
    private String salutation;

    @Column(length = 10)
    private String title;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(length = 2000)
    private String avatar;

    @Column(length = 500)
    private String about;

    @Column(name = "phone_number_json", length = 2000)
    @Convert(converter = SetPhoneNumberConverter.class)
    private Set<PhoneNumber> phoneNumbers;

    @Column(name = "online_profile_json", length = 2000)
    @Convert(converter = SetOnlineProfileConverter.class)
    private Set<OnlineProfile> onlineProfiles;

    @Column(length = 255)
    private String location;

    @Column(length = 2)
    private String country;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(length = 100)
    private String organization;

    public UserProfileJpaEmbedded(UserProfile userProfile) {
        if (userProfile != null) {
            gender = userProfile.getGender();
            salutation = userProfile.getSalutation();
            title = userProfile.getTitle();
            firstName = userProfile.getFirstName();
            lastName = userProfile.getLastName();
            avatar = userProfile.getAvatar();
            about = userProfile.getAbout();
            phoneNumbers = userProfile.getPhoneNumbers();
            onlineProfiles = userProfile.getOnlineProfiles();
            location = userProfile.getLocation();
            country = userProfile.getCountry();
            jobTitle = userProfile.getJobTitle();
            organization = userProfile.getOrganization();
        }
    }
}
