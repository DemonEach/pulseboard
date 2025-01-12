package org.goblintelligence.pulseboard.utils;

import java.util.Objects;
import java.util.function.Consumer;

public final class DataUtils {

    private DataUtils() {

    }

    public static <T> boolean checkAndSetValue(T oldValue, T newValue, Consumer<T> setter) {
        return checkAndSetValue(oldValue, newValue, setter, false);
    }

    public static <T> boolean checkAndSetValue(T oldValue, T newValue, Consumer<T> setter, boolean nullAllowed) {
        if (checkValuesInequality(oldValue, newValue)
                && (Objects.nonNull(newValue) || nullAllowed)) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }

    private static <T> boolean checkValuesInequality(T oldValue, T newValue) {
        return Objects.isNull(oldValue) || !oldValue.equals(newValue);
    }
}
