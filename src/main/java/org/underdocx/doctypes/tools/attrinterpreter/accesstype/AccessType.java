package org.underdocx.doctypes.tools.attrinterpreter.accesstype;

public enum AccessType {
    ACCESS_MODEL_BY_NAME("*"),
    ACCESS_VARIABLE_BY_NAME("$"),
    ACCESS_ATTR_VALUE(""),
    ACCESS_VAR_CONTAINS_NAME_OF_VAR("$$"), /* TODO
    ACCESS_VAR_CONTAINS_NAME_OF_MODEL("*$"),
    ACCESS_MODEL_CONTAINS_NAME_OF_VAR("$*"),
    ACCESS_MODEL_CONTAINS_NAME_OF_MODEL("**"),*/
    ACCESS_CURRENT_MODEL_NODE(null),
    MISSING_ACCESS(null);

    private final String prefix;

    AccessType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public static AccessType getTypeOf(String name) {
        if (name.startsWith(ACCESS_VAR_CONTAINS_NAME_OF_VAR.prefix)) {
            return ACCESS_VAR_CONTAINS_NAME_OF_VAR;
        }
        if (name.startsWith(ACCESS_MODEL_BY_NAME.prefix)) {
            return ACCESS_MODEL_BY_NAME;
        }
        if (name.startsWith(ACCESS_VARIABLE_BY_NAME.prefix)) {
            return ACCESS_VARIABLE_BY_NAME;
        }
        return ACCESS_ATTR_VALUE;
    }

    public static String getPureName(String propertyName) {
        String result = propertyName;
        if (propertyName.startsWith(ACCESS_VAR_CONTAINS_NAME_OF_VAR.prefix)) {
            result = result.substring(2);
        } else if (propertyName.startsWith(ACCESS_MODEL_BY_NAME.prefix) || propertyName.startsWith(ACCESS_VARIABLE_BY_NAME.prefix)) {
            result = result.substring(1);
        }
        return result;
    }

    public String rename(String propertyName) {
        String result = getPureName(propertyName);
        result = prefix + result;
        return result;
    }
}