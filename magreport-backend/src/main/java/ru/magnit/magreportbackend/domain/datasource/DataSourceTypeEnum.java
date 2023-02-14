package ru.magnit.magreportbackend.domain.datasource;

public enum DataSourceTypeEnum {
    H2,
    TERADATA,
    IMPALA,
    ORACLE,
    MSSQL,
    POSTGRESQL,
    DB2,
    SAP_HANA,
    CLICK_HOUSE;

    public static DataSourceTypeEnum getByOrdinal(long id) {
        return DataSourceTypeEnum.values()[(int) id];
    }
}
