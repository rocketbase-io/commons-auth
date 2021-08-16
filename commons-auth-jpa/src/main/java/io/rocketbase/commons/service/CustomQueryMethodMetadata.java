package io.rocketbase.commons.service;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.MutableQueryHints;
import org.springframework.data.jpa.repository.support.QueryHints;
import org.springframework.lang.Nullable;

import javax.persistence.LockModeType;
import java.lang.reflect.Method;
import java.util.Optional;

public class CustomQueryMethodMetadata implements CrudMethodMetadata {

    private final MutableQueryHints queryHints;

    public CustomQueryMethodMetadata(javax.persistence.EntityGraph entityGraph) {
        queryHints = new MutableQueryHints();
        queryHints.add("javax.persistence.loadgraph", entityGraph);
    }

    @Nullable
    @Override
    public LockModeType getLockModeType() {
        return null;
    }

    @Override
    public QueryHints getQueryHints() {
        return queryHints;
    }

    @Override
    public QueryHints getQueryHintsForCount() {
        return QueryHints.NoHints.INSTANCE;
    }

    @Override
    public Optional<EntityGraph> getEntityGraph() {
        return Optional.empty();
    }

    @Override
    public Method getMethod() {
        return null;
    }
}
