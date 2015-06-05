
/*
 * Copyright (c) 2013 - 2015 Naios <naios-dev@live.de>
 *
 * This file is part of WIde which is released under Creative Commons 4.0 (by-nc-sa)
 * See file LICENSE for full license details.
 */

package com.github.naios.wide.ide.internal;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.WeakSetChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.property.editor.PropertyEditor;

import com.github.naios.wide.api.config.schema.MappingMetaData;
import com.github.naios.wide.api.framework.storage.mapping.MappingBeans;
import com.github.naios.wide.api.framework.storage.mapping.MappingPlan;
import com.github.naios.wide.api.framework.storage.server.ServerStorageStructure;
import com.github.naios.wide.api.property.EnumProperty;
import com.github.naios.wide.api.property.FlagProperty;
import com.github.naios.wide.api.property.ReadOnlyEnumProperty;
import com.github.naios.wide.api.property.ReadOnlyFlagProperty;
import com.github.naios.wide.api.property.SimpleEnumProperty;
import com.github.naios.wide.api.property.SimpleFlagProperty;
import com.github.naios.wide.api.util.Flags;
import com.google.common.collect.Iterables;


abstract class MetaDataPropertyItem implements PropertySheet.Item
{
    private final MappingMetaData metaData;

    public MetaDataPropertyItem(final MappingMetaData metaData)
    {
        this.metaData = metaData;
    }

    @Override
    public String getName()
    {
        return metaData.getName();
    }

    @Override
    public String getDescription()
    {
        return metaData.getDescription();
    }

    @Override
    public String getCategory()
    {
        return metaData.getCategory().isEmpty() ? "default" : " " + metaData.getCategory();
    }
}

/**
 * Used as values for property editors to extends functionality
 */
interface Content
{
    /**
     * @return Returns the distributor property that represent one or multiple values.
     */
    public Property<?> distributorProperty();

    /**
     * Sets the default value to the property
     */
    public void setDefaultValue();

    /**
     * @return Returns the mapping meta data
     */
    public MappingMetaData getMetaData();

    /**
     * @return Returns a boolean property that represents if the property is editable.
     */
    public BooleanProperty editableProperty();

    /**
     * @return Returns a boolean property that represents if all values have the same value
     */
    public BooleanProperty uniqueProperty();
}

abstract class ContentPropertyEditor implements PropertyEditor<Content>
{
    private Node editor;

    protected abstract Node setupEditor(final Content content);

    @Override
    public Node getEditor()
    {
        return editor;
    }

    @Override
    public Content getValue()
    {
        // Currently it is never called from the underlaying PropertySheet superclass.
        throw new UnsupportedOperationException();
    }

    @Override
    public void setValue(final Content content)
    {
        editor = setupEditor(content);
    }
}

interface Distributor
{
    public Property<?> get();

    public void setDefault();
}

enum MappingPropertyTypeFactory implements Callback<Item, PropertyEditor<?>>
{
    INSTANCE;

    @Override
    public PropertyEditor<?> call(final Item item)
    {
        if (ReadOnlyStringProperty.class.isAssignableFrom(item.getType()))
            return supplyStringEditor();
        else if (ReadOnlyEnumProperty.class.isAssignableFrom(item.getType()))
            return supplyEnumEditor();
        else if (ReadOnlyFlagProperty.class.isAssignableFrom(item.getType()))
            return supplyFlagEditor();
        else // Default
            return supplyDefaultEditor();
    }

    public Distributor supplyDistributor(final Class<?> underlayingType, final MappingMetaData metaData)
    {
        if (ReadOnlyStringProperty.class.isAssignableFrom(underlayingType))
        {
            return new Distributor()
            {
                StringProperty distributor = new SimpleStringProperty();

                @Override
                public void setDefault()
                {
                    distributor.set("");
                }

                @Override
                public Property<?> get()
                {
                    return distributor;
                }
            };
        }
        else if (ReadOnlyEnumProperty.class.isAssignableFrom(underlayingType))
        {
            return new Distributor()
            {
                @SuppressWarnings({ "unchecked", "rawtypes" })
                private EnumProperty distributor = new SimpleEnumProperty(
                        Services.getEntityService().requestEnumForName(metaData.getAlias()));

                @SuppressWarnings({ "unchecked" })
                @Override
                public void setDefault()
                {
                    distributor.set(distributor.getDefaultValue());
                }

                @Override
                public Property<?> get()
                {
                    return distributor;
                }
            };
        }
        else if (ReadOnlyFlagProperty.class.isAssignableFrom(underlayingType))
        {
            return new Distributor()
            {
                @SuppressWarnings("unchecked")
                private FlagProperty<?> distributor = new SimpleFlagProperty<>
                    ((Class)Services.getEntityService().requestEnumForName(metaData.getAlias()));

                @Override
                public void setDefault()
                {
                    distributor.reset();
                }

                @Override
                public Property<?> get()
                {
                    return distributor;
                }
            };
        }
        else // Default
        {
            return new Distributor()
            {
                private ObjectProperty<?> distributor = new SimpleObjectProperty<>();

                @Override
                public void setDefault()
                {
                    distributor.set(null);
                }

                @Override
                public Property<?> get()
                {
                    return distributor;
                }
            };
        }
    }

    public static ContentPropertyEditor supplyDefaultEditor()
    {
        return new ContentPropertyEditor()
        {
            @Override
            protected Node setupEditor(final Content content)
            {
                final TextField field = new TextField();
                field.setDisable(true);

                field.textProperty().bind(Bindings.createStringBinding(() ->
                {
                    if (content.distributorProperty().getValue() == null)
                        return "";
                    else
                        return Objects.toString(content.distributorProperty().getValue());

                }, content.distributorProperty()));

                return field;
            }
        };
    }

    public static ContentPropertyEditor supplyStringEditor()
    {
        return new ContentPropertyEditor()
        {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            protected Node setupEditor(final Content content)
            {
                final TextField field = TextFields.createClearableTextField();
                field.disableProperty().bind(content.editableProperty().not());

                field.textProperty().bindBidirectional((Property) content.distributorProperty());
                return field;
            }
        };
    }

    public static ContentPropertyEditor supplyEnumEditor()
    {
        return new ContentPropertyEditor()
        {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            protected Node setupEditor(final Content content)
            {
                final EnumProperty<? extends Enum<?>> enumProperty = (EnumProperty)content.distributorProperty();

                final ObservableList<Enum<?>> flags = FXCollections.unmodifiableObservableList(
                        FXCollections.observableArrayList(enumProperty.getEnumConstants()));

                final ChangeListener<? extends Enum<?>> listener = (observable, oldValue, newValue) ->
                {
                    ((EnumProperty)enumProperty).set(newValue);
                };

                final ComboBox box = new ComboBox(flags);
                content.uniqueProperty().addListener((observable, oldValue, newValue) ->
                {
                    if (newValue)
                    {
                        box.valueProperty().removeListener(listener);
                        box.valueProperty().bindBidirectional(enumProperty);
                    }
                    else
                    {
                        box.valueProperty().unbindBidirectional(enumProperty);
                        box.getSelectionModel().clearSelection();
                        box.valueProperty().addListener(listener);
                    }
                });

                if (content.uniqueProperty().get())
                    box.valueProperty().bindBidirectional(enumProperty);
                else
                {
                    box.getSelectionModel().clearSelection();
                    box.valueProperty().addListener(listener);
                }

                box.disableProperty().bind(content.editableProperty().not());
                return box;
            }
        };
    }

    public static ContentPropertyEditor supplyFlagEditor()
    {
        return new ContentPropertyEditor()
        {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            protected Node setupEditor(final Content content)
            {
                final FlagProperty<? extends Enum<?>> flag = (FlagProperty)content.distributorProperty();

                final ObservableList<Enum<?>> flags = FXCollections.unmodifiableObservableList(
                        FXCollections.observableArrayList(flag.getEnumConstants()));

                final CheckComboBox<Enum<?>> box = new CheckComboBox<>(flags);

                final ObjectProperty<ChangeListener<Number>> flagListener = new SimpleObjectProperty<>();
                final ObjectProperty<ListChangeListener<Enum<?>>> checkedListener = new SimpleObjectProperty<>();

                flagListener.set(new ChangeListener<Number>()
                {
                    @Override
                    public void changed(final ObservableValue<? extends Number> observable,
                            final Number oldValue, final Number newValue)
                    {
                        final Collection<Enum<?>> add = new HashSet<>(), remove = new HashSet<>();
                        Flags.calculateDifferenceTo((Class)flag.getEnumClass(), oldValue.intValue(), newValue.intValue(), add, remove);

                        if (!add.isEmpty() || !remove.isEmpty())
                        {
                            box.getCheckModel().getCheckedItems().removeListener(checkedListener.get());

                            add.forEach(i -> box.getCheckModel().check(i));
                            remove.forEach(i -> box.getCheckModel().clearCheck(i));

                            box.getCheckModel().getCheckedItems().addListener(checkedListener.get());
                        }
                    }
                });

                checkedListener.set(new ListChangeListener<Enum<?>>()
                {
                    @Override
                    public void onChanged(final ListChangeListener.Change<? extends Enum<?>> change)
                    {
                        flag.removeListener(flagListener.get());

                        flag.set(Flags.<Enum<?>>createFlag(change.getList().toArray(new Enum<?>[change.getList().size()])));

                        flag.addListener(flagListener.get());
                    }
                });

                // content.uniqueProperty().bind(observable);

                Flags.flagSet(flag.getEnumClass(), flag.get()).forEach(i -> box.getCheckModel().check(i));

                box.getCheckModel().getCheckedItems().addListener(checkedListener.get());
                flag.addListener(flagListener.get());

                box.disableProperty().bind(content.editableProperty().not());

                // Create the Tooltip
                final Tooltip tooltip = new Tooltip();
                tooltip.textProperty().bind(Bindings.createStringBinding(() ->
                {
                    return flag.getFlags()
                        .stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",\n", "", String.format("\n-> %s", flag.getValueAsHex())));

                }, flag));

                box.setTooltip(tooltip);
                return box;
            }
        };
    }
}

class PropertyItem implements PropertySheet.Item
{
    // All properties registered to this item
    private final ObservableMap<ServerStorageStructure, ReadOnlyProperty<?>> properties =
            FXCollections.observableHashMap();

    private final Content content;

    private final MappingMetaData metaData;

    private final ChangeListener<Object> propertyListener;

    // If the distributor changes update all observables
    private final ChangeListener<Object> distributorListener = new ChangeListener<Object>()
    {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public void changed(final ObservableValue<? extends Object> observable,
                final Object oldValue, final Object newValue)
        {
            if (properties.isEmpty())
                return;

            // If the properties aren't editable skip it too
            if (!content.editableProperty().get())
                return;

            properties
                .values()
                .stream()
                .map(p -> (Property)p)
                .forEach(p ->
                {
                    p.removeListener(propertyListener);
                    p.setValue(newValue);
                    p.addListener(propertyListener);
                });

            content.uniqueProperty().set(true);
        }
    };

    public PropertyItem(final Class<?> underlayingType, final MappingMetaData metaData)
    {
        content = createContent(underlayingType, metaData);
        this.metaData = metaData;

        propertyListener = new ChangeListener<Object>()
        {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public void changed(final ObservableValue<? extends Object> observable,
                    final Object oldValue, final Object newValue)
            {
                if (!areAllUnique())
                {
                    content.uniqueProperty().set(false);
                    return;
                }

                runDistributorListenerFree(() -> ((Property)content.distributorProperty()).setValue(newValue));

                content.uniqueProperty().set(true);
            }
        };

        properties.addListener(new MapChangeListener<ServerStorageStructure, ReadOnlyProperty<?>>()
        {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public void onChanged(final MapChangeListener.Change<? extends ServerStorageStructure, ? extends ReadOnlyProperty<?>> change)
            {
                // On structure add
                if (change.wasAdded())
                {
                    change.getValueAdded().addListener(propertyListener);
                }

                // On structure remove
                if (change.wasRemoved())
                {
                    change.getValueRemoved().removeListener(propertyListener);
                }

                if (change.wasAdded() || change.wasRemoved())
                {
                    content.editableProperty().set(areAllEditable());

                    if (areAllUnique())
                        runDistributorListenerFree(() ->
                        {
                            // If all values are unique set the value to the distributor
                            content.uniqueProperty().set(true);
                            ((Property)content.distributorProperty()).setValue(Iterables.get(change.getMap().values(), 0).getValue());
                        });
                    else
                        runDistributorListenerFree(() ->
                        {
                            // If any value is different set the default value to the distributor
                            content.uniqueProperty().set(false);
                            content.setDefaultValue();
                        });
                }
            }
        });

        content.distributorProperty().addListener(distributorListener);
    }

    private void runDistributorListenerFree(final Runnable runnable)
    {
        content.distributorProperty().removeListener(distributorListener);
        runnable.run();
        content.distributorProperty().addListener(distributorListener);
    }

    private static Content createContent(final Class<?> underlayingType, final MappingMetaData metaData)
    {
        return new Content()
        {
            private final BooleanProperty uniqueProperty = new SimpleBooleanProperty();

            private final BooleanProperty editableProperty = new SimpleBooleanProperty();

            private final Distributor distributor = MappingPropertyTypeFactory.INSTANCE
                    .supplyDistributor(underlayingType, metaData);

            @Override
            public MappingMetaData getMetaData()
            {
                return metaData;
            }

            @Override
            public Property<?> distributorProperty()
            {
                return distributor.get();
            }

            @Override
            public void setDefaultValue()
            {
                distributor.setDefault();
            }

            @Override
            public BooleanProperty uniqueProperty()
            {
                return uniqueProperty;
            }

            @Override
            public BooleanProperty editableProperty()
            {
                return editableProperty;
            }
        };
    }

    private boolean areAllEditable()
    {
        if (properties.isEmpty())
            return false;
        else
            return Iterables.get(properties.values(), 0) instanceof Property<?>;
    }

    private boolean areAllUnique()
    {
        if (properties.isEmpty())
            return false;
        else if (properties.size() <= 1)
            return true;
        else
        {
            final Collection<ReadOnlyProperty<?>> values = properties.values();

            final Iterator<ReadOnlyProperty<?>> itr = values.iterator();
            final Object first = itr.next().getValue();

            while (itr.hasNext())
                if (!Objects.equals(first, itr.next().getValue()))
                    return false;

            return true;
        }
    }

    @Override
    public String getName()
    {
        return metaData.getName();
    }

    @Override
    public String getDescription()
    {
        return metaData.getDescription();
    }

    @Override
    public String getCategory()
    {
        return metaData.getCategory().isEmpty() ? "without group" : " " + metaData.getCategory();
    }

    @Override
    public Class<?> getType()
    {
        return content.distributorProperty().getClass();
    }

    @Override
    public Object getValue()
    {
        return content;
    }

    @Override
    public void setValue(final Object value)
    {
    }

    public void addProperty(final ReadOnlyProperty<?> property)
    {
        properties.put(MappingBeans.getStructure(property), property);
        property.addListener(propertyListener);
    }

    public void removePropertiesOf(final ServerStorageStructure structure)
    {
        properties.remove(structure);
    }
}

/**
 * The Property Sheet is able to display one or several storage structures which you can edit.
 */
public class MappingPropertySheet extends PropertySheet
{
    private final ObservableSet<ServerStorageStructure> structures = FXCollections.observableSet();

    // Handles add/removing of structures.
    private final SetChangeListener<ServerStorageStructure> listener = new SetChangeListener<ServerStorageStructure>()
    {
        @Override
        public void onChanged(final SetChangeListener.Change<? extends ServerStorageStructure> change)
        {
            if (change.wasAdded())
                addStructure(change.getElementAdded());

            if (change.wasRemoved())
                removeStructure(change.getElementRemoved());
        }
    };

    /**
     * @param metaData Build a new empty property sheet from the metaData
     */
    public MappingPropertySheet(final MappingPlan<ReadOnlyProperty<?>> plan)
    {
        setPropertyEditorFactory(MappingPropertyTypeFactory.INSTANCE);

        // Add metaData to property item
        for (int i = 0; i < plan.getNumberOfElements(); ++i)
            getItems().add(new PropertyItem(plan.getMappedTypes().get(i).getRawType(), plan.getMetaData().get(i)));

        structures.addListener(new WeakSetChangeListener<>(listener));
    }

    /**
     * Creates a new MappingPropertySheet from a structure.
     * @param structures
     */
    public MappingPropertySheet(final ServerStorageStructure structure)
    {
        this(structure.getMappingPlan());
        structures.add(structure);
    }

    /**
     * Creates a new MappingPropertySheet from a non empty structure list.
     * @param structures
     */
    public MappingPropertySheet(final List<ServerStorageStructure> structures)
    {
        this(structures.get(0).getMappingPlan());
        structures.addAll(structures);
    }

    /**
     * @return Returns an editable list of displayed structures
     */
    public ObservableSet<ServerStorageStructure> getStructures()
    {
        return structures;
    }

    /**
     * @param structure That needs to be added to the property sheet.
     */
    private void addStructure(final ServerStorageStructure structure)
    {
        for (int i  = 0; i < structure.getValues().size(); ++i)
            ((PropertyItem)getItems().get(i)).addProperty(structure.getValues().get(i));
    }

    /**
     * @param structures That needs to be removed from the property sheet.
     */
    private void removeStructure(final ServerStorageStructure structure)
    {
        getItems()
            .stream()
            .map(i -> (PropertyItem)i)
            .forEach(i -> i.removePropertiesOf(structure));
    }
}
