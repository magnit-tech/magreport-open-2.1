package ru.magnit.magreportbackend.dto.request.olap;

import org.junit.jupiter.api.Test;

import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.*;

class FieldDefinitionTest {

    @Test
    void equalsTest(){
        final var firstDefinition = new FieldDefinition(0L, OlapFieldTypes.DERIVED_FIELD);
        final var secondDefinition = new FieldDefinition(0L, OlapFieldTypes.DERIVED_FIELD);

        final var identityHashMap = new IdentityHashMap<FieldDefinition, String>();

        identityHashMap.put(firstDefinition, "First Definition");
        identityHashMap.put(secondDefinition, "Second Definition");

        assertEquals("First Definition", identityHashMap.get(firstDefinition));
        assertEquals("Second Definition", identityHashMap.get(secondDefinition));
    }

}
