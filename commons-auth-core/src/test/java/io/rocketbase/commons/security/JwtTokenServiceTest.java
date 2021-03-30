package io.rocketbase.commons.security;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import io.rocketbase.commons.config.JwtProperties;
import io.rocketbase.commons.dto.address.Gender;
import io.rocketbase.commons.dto.appgroup.AppGroupShort;
import io.rocketbase.commons.dto.appteam.AppTeamRole;
import io.rocketbase.commons.dto.appteam.AppTeamShort;
import io.rocketbase.commons.dto.appteam.AppUserMembership;
import io.rocketbase.commons.dto.authentication.JwtTokenBundle;
import io.rocketbase.commons.model.SimpleAppUserToken;
import io.rocketbase.commons.model.TokenParseResult;
import io.rocketbase.commons.model.user.OnlineProfile;
import io.rocketbase.commons.model.user.PhoneNumber;
import io.rocketbase.commons.model.user.SimpleUserProfile;
import io.rocketbase.commons.model.user.SimpleUserSetting;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class JwtTokenServiceTest {

    @Test
    public void generateTokenBundle() {
        // given
        SimpleAppUserToken appUserToken = SimpleAppUserToken.builderToken()
                .id(UUID.randomUUID().toString())
                .username("test")
                .profile(SimpleUserProfile.builder()
                        .firstName("firstName")
                        .lastName("lastName")
                        .avatar("https://i.pravatar.cc/300")
                        .build())
                .email("sample@rocketbase.io")
                .capabilities(Sets.newHashSet("blog/post"))
                .keyValues(ImmutableMap.of("special", "v1", "clientId", "1233"))
                .build();

        JwtTokenService jwtTokenService = getJwtTokenService();

        // when
        JwtTokenBundle jwtTokenBundle = jwtTokenService.generateTokenBundle(appUserToken);
        TokenParseResult meta = jwtTokenService.parseToken(jwtTokenBundle.getToken());

        // then
        assertThat(jwtTokenBundle, notNullValue());
        assertThat(jwtTokenBundle.getToken(), notNullValue());
        assertThat(jwtTokenBundle.getRefreshToken(), notNullValue());
        assertThat(appUserToken, equalTo(meta.getUser()));
    }

    @Test
    public void checkKeyValueFiltered() {
        // given
        SimpleAppUserToken appUserToken = SimpleAppUserToken.builderToken()
                .id(UUID.randomUUID().toString())
                .capabilities(Sets.newHashSet("ROLE_USER"))
                .keyValues(ImmutableMap.of("_secret", "v1", "clientId", "1233", "#hidden", "hidden"))
                .build();

        JwtTokenService jwtTokenService = getJwtTokenService();

        // when
        JwtTokenBundle jwtTokenBundle = jwtTokenService.generateTokenBundle(appUserToken);
        TokenParseResult meta = jwtTokenService.parseToken(jwtTokenBundle.getToken());

        // then
        assertThat(jwtTokenBundle, notNullValue());
        assertThat(jwtTokenBundle.getToken(), notNullValue());
        assertThat(jwtTokenBundle.getRefreshToken(), notNullValue());
        assertThat(meta.getUser().getId(), equalTo(appUserToken.getId()));
        assertThat(meta.getUser().getCapabilities(), equalTo(appUserToken.getCapabilities()));
        assertThat(meta.getUser().getKeyValues().size(), equalTo(1));
        assertThat(meta.getUser().getKeyValue("#hidden"), nullValue());
        assertThat(meta.getUser().getKeyValue("_secret"), nullValue());
        assertThat(meta.getUser().getKeyValue("clientId"), equalTo("1233"));
    }

    @Test
    public void createAndParse() {
        // given
        SimpleAppUserToken appUserToken = SimpleAppUserToken.builderToken()
                .id("401fb225-057e-4e0a-a0ff-e99e76030d52")
                .username("marten")
                .profile(SimpleUserProfile.builder()
                        .firstName("Marten")
                        .lastName("Prieß")
                        .salutation("Mr.")
                        .gender(Gender.MALE)
                        .location("Winsen (Luhe)")
                        .phoneNumbers(Sets.newHashSet(new PhoneNumber("mobile", "+491512 333 6263")))
                        .onlineProfiles(Sets.newHashSet(new OnlineProfile("xing", "https://www.xing.com/profile/Marten_Priess"),
                                new OnlineProfile("github", "melistik")))
                        .build())
                .setting(SimpleUserSetting.builder()
                        .currentTimeZone("Europe/Berlin")
                        .dateTimeFormat("dd.MM.yyyy HH:mm")
                        .timeFormat("HH:mm")
                        .dateFormat("dd.MM.yyyy")
                        .locale("de")
                        .build())
                .email("marten@rocketbase.io")
                .keyValues(ImmutableMap.of("workspace", "1", "displayMode", "short"))
                .capabilities(Sets.newHashSet("*", "blog"))
                .groups(Sets.newHashSet(new AppGroupShort(123L, "group 123")))
                .activeTeam(new AppUserMembership(new AppTeamShort(1L, "Team One"), AppTeamRole.MEMBER))
                .build();

        JwtTokenService jwtTokenService = getJwtTokenService();

        // when
        JwtTokenBundle jwtTokenBundle = jwtTokenService.generateTokenBundle(appUserToken);
        TokenParseResult meta = jwtTokenService.parseToken(jwtTokenBundle.getToken());

        // then
        assertThat(jwtTokenBundle, notNullValue());
        assertThat(jwtTokenBundle.getToken(), notNullValue());
        assertThat(jwtTokenBundle.getRefreshToken(), notNullValue());
        assertThat(meta.getUser().getId(), equalTo(appUserToken.getId()));
        assertThat(meta.getUser().getCapabilities(), equalTo(appUserToken.getCapabilities()));
        assertThat(meta.getUser().getSetting(), equalTo(appUserToken.getSetting()));
        assertThat(meta.getUser().getProfile(), equalTo(appUserToken.getProfile()));
        assertThat(meta.getUser().getGroups(), equalTo(appUserToken.getGroups()));
        assertThat(meta.getUser().getActiveTeam(), equalTo(appUserToken.getActiveTeam()));
        assertThat(meta.getUser().getKeyValues(), equalTo(appUserToken.getKeyValues()));
    }

    protected JwtTokenService getJwtTokenService() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("P0UoSCtNYlBlU2hWbVlxM3Q2dzl6JEMmRilKQE5jUmZUalduWnI0dTd4IUElRCpHLUthUGRTZ1ZrWHAyczV2OA==");
        return new JwtTokenService(jwtProperties, new EmptyCustomAuthoritiesProvider());
    }
}