/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.karta.document;

import com.github.kossy18.karta.EntityDetails;
import com.github.kossy18.karta.ImporterConfig;
import com.github.kossy18.karta.ImporterException;
import com.github.kossy18.karta.Property;
import com.github.kossy18.karta.converters.EntityConverter;
import com.github.kossy18.karta.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Responsible for iterating through the import document via a
 * @param <T>
 */
public class ResourceToEntityProcessor<T> {

    private final int DEFAULT_BATCH_SIZE = 200;

    private ImporterConfig config;

    public ResourceToEntityProcessor(ImporterConfig config) {
        this.config = config;
    }

    public List<T> process(RowSeeker rowSeeker, Class<T> clazz) {
        final List<T> processedList = new LinkedList<>();

        processImpl(rowSeeker, clazz, DEFAULT_BATCH_SIZE, new EntityProcessorListener<T>() {
            @Override
            public void onProcessedEntity(List<T> processedEntities) {
                processedList.addAll(processedEntities);
            }
        });

        return processedList;
    }

    public void process(RowSeeker rowSeeker, Class<T> clazz, int batchSize, EntityProcessorListener<T> processorListener) {
        if (batchSize == Integer.MAX_VALUE || batchSize < 0) {
            batchSize = DEFAULT_BATCH_SIZE;
        }
        processImpl(rowSeeker, clazz, batchSize, processorListener);
    }

    private void processImpl(RowSeeker rowSeeker, Class<T> clazz, int batchSize, EntityProcessorListener<T> processorListener) {
        EntityDetails entity = config.getEntityDetailsMap().get(clazz);

        if (entity != null) {
            Map<String, EntityConverter> converterMap = config.getEntityConverterMap();

            int processed = 0;
            List<T> processedClassEntity = new LinkedList<>();

            while (true) {
                Row row = rowSeeker.next();

                if (row != null) {
                    if (row.getIndex() == 0) {
                        // Skip the header
                        continue;
                    }

                    try {
                        T classEntity = clazz.newInstance();

                        List<Property> properties = entity.getProperties();
                        Map<Cell, Property.Converter> pendingArgs = new LinkedHashMap<>();

                        for (Iterator<Cell> cellIterator = row.getCells().iterator(); cellIterator.hasNext(); ) {
                            Cell cell = cellIterator.next();

                            for (Iterator<Property> propertyIterator = properties.iterator(); propertyIterator.hasNext(); ) {
                                Property property = propertyIterator.next();

                                boolean foundColumn = false;
                                Property.Converter argConverter = null;

                                for (Map.Entry<Pattern, Property.Converter> entry : property.getPatternConverterMap().entrySet()) {
                                    if (entry.getKey().matcher(cell.getColumnName()).matches()) {
                                        argConverter = entry.getValue();
                                        foundColumn = true;
                                        break;
                                    }
                                }

                                if (foundColumn) {
                                    if (property.getColumnSize() > 1) {
                                        pendingArgs.put(cell, argConverter);

                                        if (property.getColumnSize() == pendingArgs.size()) {
                                            int index = 0;
                                            Object[] methodArgs = new Object[property.getColumnSize()];

                                            for (Map.Entry<Cell, Property.Converter> arg : pendingArgs.entrySet()) {
                                                Cell argCell = arg.getKey();
                                                Property.Converter argConvt = arg.getValue();

                                                if (argConvt != null) {
                                                    methodArgs[index] = converterMap.get(argConvt.getRef()).convert(argCell, argConvt.getData());
                                                }
                                                else {
                                                    methodArgs[index] = argCell.getValue();
                                                }
                                                index++;
                                            }

                                            findAndInvokeMethod(clazz, property, classEntity, methodArgs);
                                            propertyIterator.remove();
                                        }
                                    } else {
                                        Object methodArg = cell.getValue();

                                        if (argConverter != null) {
                                            EntityConverter converter = converterMap.get(argConverter.getRef());
                                            methodArg = converter.convert(cell, argConverter.getData());
                                        }

                                        findAndInvokeMethod(clazz, property, classEntity, methodArg);
                                        propertyIterator.remove();
                                    }
                                    cellIterator.remove();
                                    break;
                                }
                            }
                        }
                        processedClassEntity.add(classEntity);
                        processed++;

                        if (batchSize == processed) {
                            processorListener.onProcessedEntity(new LinkedList<>(processedClassEntity));
                            processed = 0;
                            processedClassEntity.clear();
                        }
                    }
                    catch (Exception e) {
                        throw new ImporterException("An error occurred while processing row: " + row.getIndex(), e);
                    }
                }
                else {
                    break;
                }
            }

            if (processedClassEntity.size() > 0) {
                processorListener.onProcessedEntity(processedClassEntity);
            }
        }
    }

    private void findAndInvokeMethod(Class clazz, Property property, T classEntity, Object... methodArg) {
        try {
            ReflectionUtils.findAndInvokeMethod(clazz, classEntity, property.getName(), "set", property.getParameterTypes(), methodArg);
        }
        catch (NoSuchMethodException e) {
            throw new ImporterException("Could not find setter method for field: " + property.getName() + " for class: " + clazz.getName());
        }
        catch (IllegalArgumentException e) {
            throw new ImporterException("Could not invoke setter method for field: " + property.getName()
                    + " with argument: " + Arrays.toString(methodArg) + ". Verify if a converter is being used", e);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new ImporterException("An error occurred while invoking method for field: " + property.getName(), e);
        }
    }

    public interface EntityProcessorListener<T> {
        void onProcessedEntity(List<T> processedEntities);
    }
}
