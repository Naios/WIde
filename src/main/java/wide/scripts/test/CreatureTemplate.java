package wide.scripts.test;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.StringProperty;

public interface CreatureTemplate
{
    public ReadOnlyIntegerProperty entry();

    public StringProperty name();

    public IntegerProperty unit_flags();
}
