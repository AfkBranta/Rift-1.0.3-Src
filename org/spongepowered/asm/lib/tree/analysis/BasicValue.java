package org.spongepowered.asm.lib.tree.analysis;

import org.spongepowered.asm.lib.Type;

public class BasicValue implements Value {

    public static final BasicValue UNINITIALIZED_VALUE = new BasicValue((Type) null);
    public static final BasicValue INT_VALUE = new BasicValue(Type.INT_TYPE);
    public static final BasicValue FLOAT_VALUE = new BasicValue(Type.FLOAT_TYPE);
    public static final BasicValue LONG_VALUE = new BasicValue(Type.LONG_TYPE);
    public static final BasicValue DOUBLE_VALUE = new BasicValue(Type.DOUBLE_TYPE);
    public static final BasicValue REFERENCE_VALUE = new BasicValue(Type.getObjectType("java/lang/Object"));
    public static final BasicValue RETURNADDRESS_VALUE = new BasicValue(Type.VOID_TYPE);
    private final Type type;

    public BasicValue(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public int getSize() {
        return this.type != Type.LONG_TYPE && this.type != Type.DOUBLE_TYPE ? 1 : 2;
    }

    public boolean isReference() {
        return this.type != null && (this.type.getSort() == 10 || this.type.getSort() == 9);
    }

    public boolean equals(Object value) {
        return value == this ? true : (value instanceof BasicValue ? (this.type == null ? ((BasicValue) value).type == null : this.type.equals(((BasicValue) value).type)) : false);
    }

    public int hashCode() {
        return this.type == null ? 0 : this.type.hashCode();
    }

    public String toString() {
        return this == BasicValue.UNINITIALIZED_VALUE ? "." : (this == BasicValue.RETURNADDRESS_VALUE ? "A" : (this == BasicValue.REFERENCE_VALUE ? "R" : this.type.getDescriptor()));
    }
}
