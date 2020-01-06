/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.mapping;

import com.github.kossy18.karta.EntityDetails;
import com.github.kossy18.karta.Property;
import com.github.kossy18.karta.converters.EntityConverter;
import com.github.kossy18.karta.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * <p>Only intended for internal use.
 */
public class EntityMappingProcessor {

    // Allow characters, digits, spaces, _ and -
    private static final String COLUMN_NAME_PATTERN = "^[\\w_\\s\\-]+$";

    private Class entityClass;
    private LinkedList<Property> propertyList;
    private Set<String> propertyColumnPatternSet;
    private Map<String, EntityConverter> converterMap;

    public EntityMappingProcessor() {
        entityClass = null;
        converterMap = new HashMap<>();
        propertyList = new LinkedList<>();
        propertyColumnPatternSet = new HashSet<>();
    }

    public void setEntity(String className) throws InvalidMappingException {
        try {
            this.entityClass = ReflectionUtils.getClassObject(className);
        }
        catch (ClassNotFoundException e) {
            throw new InvalidMappingException("Class attribute name: " + className + " could not be found", e);
        }
    }

    public void addProperty(String name, String column, String order, String converterRef, String converterData) {
        if (!StringUtils.isEmpty(column) && !column.matches(COLUMN_NAME_PATTERN)) {
            throw new InvalidMappingException("Property attribute column: " + column + " is not valid");
        }

        if (!ReflectionUtils.isFieldExist(entityClass, name)) {
            throw new InvalidMappingException("Property attribute name: " + name + " is not a member of class " + entityClass.getName());
        }

        int orderInt = !StringUtils.isEmpty(order) ? Integer.parseInt(order) : 0;
        propertyList.add(new Property(name, verifyAndBuildConverter(column, converterRef, converterData), orderInt));

        if (!StringUtils.isEmpty(converterRef)) {
            propertyColumnPatternSet.add(converterRef);
        }
    }

    public void addColumn(String name, String converterRef, String converterData) {
        Property property = propertyList.peekLast();

        // Avoid duplicate column names across all properties of a class
        int prevColumnSize = propertyColumnPatternSet.size();
        propertyColumnPatternSet.add(name);

        int newColumnSize = propertyColumnPatternSet.size();
        if (newColumnSize != (prevColumnSize + 1)) {
            throw new InvalidMappingException("Column attribute name: " + name + " already exists for property: " + property.getName());
        }

        property.setColumnPattern(verifyAndBuildConverter(name, converterRef, converterData));
    }

    private Map<String, Property.Converter> verifyAndBuildConverter(String columnName, String converterRef, String converterData) {
        if (!StringUtils.isEmpty(converterRef) && !converterMap.containsKey(converterRef)) {
            throw new InvalidMappingException("Property attribute converter-ref: " + converterRef + " not found in global converter list");
        }

        boolean hasConverterRef = !StringUtils.isEmpty(converterRef);

        Map<String, Property.Converter> columnConverterMap = new HashMap<>();
        columnConverterMap.put(columnName, hasConverterRef ? new Property.Converter(converterRef, converterData): null);

        return columnConverterMap;
    }

    public void addConverter(String name, String className) {
        if (StringUtils.isEmpty(name)) {
            name = className;
        }

        try {
            Class converter = ReflectionUtils.getClassObject(className);

            if (!ReflectionUtils.isInterfaceOf(converter, EntityConverter.class.getName())) {
                throw new InvalidMappingException("Converter attribute value: " + className + " does not extend the EntityConverter interface");
            }

            converterMap.put(name, (EntityConverter) converter.newInstance());
        }
        catch (ClassNotFoundException e) {
            throw new InvalidMappingException("Converter attribute value: " + className + " not found", e);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new InvalidMappingException("Converter attribute value: " + className + " could not be instantiated", e);
        }
    }

    public Map<String, EntityConverter> getConverterMap() {
        return converterMap;
    }

    public EntityDetails buildEntity() {
        Map<String, Class<?>[]> setterMethodMap = ReflectionUtils.buildMethodMap(entityClass, "set");

        for (Property p : propertyList) {
            if (p.getColumnSize() == 0) {
                Map<String, Property.Converter> columnMap = new HashMap<>();
                columnMap.put(p.getName(), null);
                p.setColumnPattern(columnMap);
            }

            Class<?>[] parameterTypes = setterMethodMap.get(ReflectionUtils.getCamelCaseName("set", p.getName()));
            if (parameterTypes == null) {
                // check for snake case convention
                parameterTypes = setterMethodMap.get(ReflectionUtils.getSnakeCaseName("set", p.getName()));
            }

            if (parameterTypes != null) {
                if (p.getColumnSize() != parameterTypes.length) {
                    throw new InvalidMappingException("Column length for property " + p.getName()
                            + " differs from its setter's parameter length by " + Math.abs(parameterTypes.length - p.getColumnSize()));
                }
            }
            else {
                throw new InvalidMappingException("Setter method for property " + p.getName() + " of class " + entityClass.getName() +
                        " does not exist. Note that camel or snake case naming convention should used.");
            }

            p.setParameterTypes(parameterTypes);
        }

        Collections.sort(propertyList, new Comparator<Property>() {
            @Override
            public int compare(Property o1, Property o2) {
                if (o1.getOrder() != 0 || o2.getOrder() != 0) {
                    return Integer.compare(o1.getOrder(), o2.getOrder());
                }
                return o1.getName().compareTo(o2.getName());
            }
        });

        return new EntityDetails(entityClass, new LinkedList<>(propertyList));
    }

    public void clear() {
        entityClass = null;
        propertyList.clear();
        propertyColumnPatternSet.clear();
    }

    public void finish() {
        converterMap.clear();
    }
}
