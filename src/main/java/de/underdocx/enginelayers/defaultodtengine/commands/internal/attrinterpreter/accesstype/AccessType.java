package de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.accesstype;

public enum AccessType {
    MODEL("@"),
    VAR("$"),
    ATTR("");

    private final String prefix;

    AccessType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public static AccessType getTypeOf(String name) {
        if (name.startsWith(MODEL.prefix)) {
            return MODEL;
        }
        if (name.startsWith(VAR.prefix)) {
            return VAR;
        }
        return ATTR;
    }

    public String getPureName(String propertyName) {
        String result = propertyName;
        if (propertyName.startsWith(MODEL.prefix) || propertyName.startsWith(VAR.prefix)) {
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