package ru.magnit.magreportbackend.domain.dataset;

import lombok.extern.slf4j.Slf4j;

import java.sql.JDBCType;

@Slf4j
public enum DataTypeEnum {
    INTEGER(false),
    STRING(true),
    DOUBLE(false),
    DATE(true),
    TIMESTAMP(true),
    BOOLEAN(false);

    DataTypeEnum(boolean quoted) {
        this.quoted = quoted;
    }

    public static DataTypeEnum valueOf(JDBCType columnType) {
        var result = switch (columnType) {
            case VARCHAR, CHAR, NVARCHAR, NCHAR, OTHER -> STRING;
            case BIGINT, INTEGER, TINYINT, SMALLINT, BIT -> INTEGER;
            case DATE -> DATE;
            case REAL, DOUBLE, FLOAT, DECIMAL, NUMERIC -> DOUBLE;
            case TIME, TIMESTAMP -> TIMESTAMP;
            case BOOLEAN -> BOOLEAN;
            default -> null;
        };

        if (result == null)
            log.error("Mapping for data type " + columnType + " not set");

        return result;
    }

    private final boolean quoted;

    public String quote(String value) {

        return (quoted ? "'" : "") + value + (quoted ? "'" : "");
    }

    public static DataTypeEnum getTypeByOrdinal(int ordinal) {
        return DataTypeEnum.values()[ordinal];
    }

    public static long getDataTypeId (String value){
       return valueOf(value).ordinal();
    }

    public boolean notIn(DataTypeEnum... variants) {
        for (final var variant: variants){
            if (variant == this) return false;
        }
        return true;
    }

    public boolean in(DataTypeEnum... variants) {
        for (final var variant: variants){
            if (variant == this) return true;
        }
        return false;
    }

    public DataTypeEnum widerNumeric(DataTypeEnum other) {
        return this == DataTypeEnum.DOUBLE || other == DataTypeEnum.DOUBLE ?
            DataTypeEnum.DOUBLE : DataTypeEnum.INTEGER;
    }

    public String toTypedString(double value){
        if (this == INTEGER) {
            return String.valueOf((long) value);
        } else {
            return String.valueOf(value);
        }
    }
}
