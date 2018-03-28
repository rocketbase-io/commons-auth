package io.rocketbase.commons.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.rocketbase.commons.adapters.JwtRestTemplate;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.appuser.AppUserCreate;
import io.rocketbase.commons.dto.appuser.AppUserRead;
import io.rocketbase.commons.dto.appuser.AppUserUpdate;
import io.rocketbase.commons.request.PageableRequest;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class AppUserResource extends AbstractRestResource {

    public static final String API_USER = "/api/user/";
    protected JwtRestTemplate restTemplate;

    public AppUserResource(JwtRestTemplate restTemplate) {
        super(new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        this.restTemplate = restTemplate;
    }

    protected <R> R handleResponse(ResponseEntity<R> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        return null;
    }

    @SneakyThrows
    public PageableResult<AppUserRead> find(int page, int pagesize) {
        ResponseEntity<String> response = restTemplate.exchange(appendParams(restTemplate.getBaseAuthApiBuilder(),
                new PageableRequest(page, pagesize, null))
                        .path(API_USER)
                        .toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(createHeaderWithLanguage()),
                String.class);

        return renderResponse(response, createPagedTypeReference());
    }

    @SneakyThrows
    public PageableResult<AppUserRead> find(PageableRequest request) {
        ResponseEntity<String> response = restTemplate.exchange(appendParams(restTemplate.getBaseAuthApiBuilder(), request)
                        .path(API_USER)
                        .toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(createHeaderWithLanguage()),
                String.class);

        return renderResponse(response, createPagedTypeReference());
    }

    @SneakyThrows
    public AppUserRead create(AppUserCreate create) {
        ResponseEntity<AppUserRead> response = restTemplate.exchange(restTemplate.getBaseAuthApiBuilder()
                        .path(API_USER)
                        .toUriString(),
                HttpMethod.POST,
                new HttpEntity<>(create, createHeaderWithLanguage()),
                AppUserRead.class);

        return handleResponse(response);
    }

    @SneakyThrows
    public AppUserRead patch(String id, AppUserUpdate update) {
        ResponseEntity<AppUserRead> response = restTemplate.exchange(restTemplate.getBaseAuthApiBuilder()
                        .path(API_USER)
                        .path(id)
                        .toUriString(),
                HttpMethod.PATCH,
                new HttpEntity<>(update, createHeaderWithLanguage()),
                AppUserRead.class);

        return handleResponse(response);
    }

    @SneakyThrows
    public void delete(String id) {
        restTemplate.exchange(restTemplate.getBaseAuthApiBuilder()
                        .path(API_USER)
                        .path(id)
                        .toUriString(),
                HttpMethod.DELETE,
                new HttpEntity<>(createHeaderWithLanguage()),
                AppUserRead.class);
    }

    protected TypeReference<PageableResult<AppUserRead>> createPagedTypeReference() {
        return new TypeReference<PageableResult<AppUserRead>>() {
        };
    }
}
