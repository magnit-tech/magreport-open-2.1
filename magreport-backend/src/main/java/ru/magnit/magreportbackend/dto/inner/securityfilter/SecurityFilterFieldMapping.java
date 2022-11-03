package ru.magnit.magreportbackend.dto.inner.securityfilter;


public record SecurityFilterFieldMapping(
    Long reportId,
    Long securityFilterId,
    Long filterInstanceFieldId,
    String fieldName
) {
}
