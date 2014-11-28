package com.github.naios.wide.core.framework.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kupal 3kb
 */
public class ClassUtil
{

    /**
     * Create new instance of specified class and type
     *
     * @param type
     *            of instance
     * @param <T>
     *            type of object
     * @return new Class instance
     */
    public static <T> T getInstance(final Class<T> type)
    {
        T t = null;
        try
        {
            t = type.newInstance();
        }
        catch (final InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (final IllegalAccessException e)
        {
            e.printStackTrace();
        }

        return t;
    }

    /**
     * Retrieving fields list of specified class If recursively is true,
     * retrieving fields from all class hierarchy
     *
     * @param type
     *            where fields are searching
     * @param recursively
     *            param
     * @return list of fields
     */
    public static Field[] getDeclaredFields(final Class type, final boolean recursively)
    {
        final List<Field> fields = new LinkedList<Field>();
        final Field[] declaredFields = type.getDeclaredFields();
        Collections.addAll(fields, declaredFields);

        final Class superClass = type.getSuperclass();

        if (superClass != null && recursively)
        {
            final Field[] declaredFieldsOfSuper = getDeclaredFields(superClass,
                    recursively);
            if (declaredFieldsOfSuper.length > 0)
                Collections.addAll(fields, declaredFieldsOfSuper);
        }

        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * Retrieving fields list of specified class and which are annotated by
     * incoming annotation class If recursively is true, retrieving fields from
     * all class hierarchy
     *
     * @param type
     *            - where fields are searching
     * @param annotationClass
     *            - specified annotation class
     * @param recursively
     *            param
     * @return list of annotated fields
     */
    public static Field[] getAnnotatedDeclaredFields(final Class type,
            final Class<? extends Annotation> annotationClass, final boolean recursively)
    {
        final Field[] allFields = getDeclaredFields(type, recursively);
        final List<Field> annotatedFields = new LinkedList<Field>();

        for (final Field field : allFields)
        {
            if (field.isAnnotationPresent(annotationClass))
                annotatedFields.add(field);
        }

        return annotatedFields.toArray(new Field[annotatedFields.size()]);
    }
}
