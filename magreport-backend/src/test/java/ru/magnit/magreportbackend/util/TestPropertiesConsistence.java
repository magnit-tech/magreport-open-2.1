package ru.magnit.magreportbackend.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

class TestPropertiesConsistence {

    private final String[] propPaths = {
        "/src/main/resources/application.properties",
        "/profiles/application.properties.template",
        "/profiles/application.properties.template",
        "/profiles/application.properties.template",
        "/profiles/application.properties.template"
    };


    @Test
    void testProperties() throws IOException {
        final var workingDir = System.getProperty("user.dir");
        final var allProperties = new HashSet<String>();
        final var propMap = new HashMap<String, Set<String>>();
        for (final var propPath : propPaths) {
            final var path = Paths.get(workingDir + propPath);
            final var props = new Properties();
            try(final var reader = Files.newBufferedReader(path)) {
                props.load(reader);
                final var propKeys = props.keySet().stream().map(Object::toString).toList();
                allProperties.addAll(propKeys);
                propMap.put(propPath, new HashSet<>(propKeys));
            }
        }
        for (final var propPath : propPaths) {
            final var properties = new HashSet<>(allProperties);
            properties.removeAll(propMap.get(propPath));
            if (!properties.isEmpty()) {
                System.out.println("Properties file: " + propPath + " does not contains following properties:\n" + properties);
            }
        }
    }
}
