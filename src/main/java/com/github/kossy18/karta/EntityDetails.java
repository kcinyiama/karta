/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta;

import java.util.LinkedList;
import java.util.List;

public final class EntityDetails {

    private final Class entityClass;

    private final List<Property> properties;

    public EntityDetails(Class clazz, List<Property> properties) {
        this.entityClass = clazz;
        this.properties = properties;
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public List<Property> getProperties() {
        return new LinkedList<>(properties);
    }
}
