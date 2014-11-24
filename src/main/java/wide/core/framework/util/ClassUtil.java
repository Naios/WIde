package wide.core.framework.util;

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
     * @param clazz
     *            of instance
     * @param <T>
     *            type of object
     * @return new Class instance
     */
    public static <T> T getInstance(Class<T> clazz)
    {
        T t = null;
        try
        {
            t = clazz.newInstance();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        return t;
    }

    /**
     * Retrieving fields list of specified class If recursively is true,
     * retrieving fields from all class hierarchy
     *
     * @param clazz
     *            where fields are searching
     * @param recursively
     *            param
     * @return list of fields
     */
    public static Field[] getDeclaredFields(Class clazz, boolean recursively)
    {
        List<Field> fields = new LinkedList<Field>();
        Field[] declaredFields = clazz.getDeclaredFields();
        Collections.addAll(fields, declaredFields);

        Class superClass = clazz.getSuperclass();

        if (superClass != null && recursively)
        {
            Field[] declaredFieldsOfSuper = getDeclaredFields(superClass,
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
     * @param clazz
     *            - where fields are searching
     * @param annotationClass
     *            - specified annotation class
     * @param recursively
     *            param
     * @return list of annotated fields
     */
    public static Field[] getAnnotatedDeclaredFields(Class clazz,
            Class<? extends Annotation> annotationClass, boolean recursively)
    {
        Field[] allFields = getDeclaredFields(clazz, recursively);
        List<Field> annotatedFields = new LinkedList<Field>();

        for (Field field : allFields)
        {
            if (field.isAnnotationPresent(annotationClass))
                annotatedFields.add(field);
        }

        return annotatedFields.toArray(new Field[annotatedFields.size()]);
    }
}
