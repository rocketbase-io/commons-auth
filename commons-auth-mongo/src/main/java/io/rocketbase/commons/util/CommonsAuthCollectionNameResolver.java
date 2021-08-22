package io.rocketbase.commons.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CommonsAuthCollectionNameResolver {

    private final ConfigurableBeanFactory configurableBeanFactory;
    private final Map<String, String> collectionNameCache = new HashMap<>();

    protected String getCollectionName(String name) {
        if (!collectionNameCache.containsKey(name)) {
            collectionNameCache.put(name, configurableBeanFactory.resolveEmbeddedValue("${auth.entity.prefix:co_}" + name));
        }
        return collectionNameCache.get(name);
    }

    public String capability() {
        return getCollectionName("capability");
    }

    public String client() {
        return getCollectionName("client");
    }

    public String group() {
        return getCollectionName("group");
    }

    public String invite() {
        return getCollectionName("invite");
    }

    public String team() {
        return getCollectionName("team");
    }

    public String user() {
        return getCollectionName("user");
    }

    public String authcode() {
        return getCollectionName("authcode");
    }

}
