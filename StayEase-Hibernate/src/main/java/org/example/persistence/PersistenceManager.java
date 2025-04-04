package org.example.persistence;

import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;
import org.hibernate.jpa.HibernatePersistenceProvider;

import java.util.HashMap;
import java.util.Map;

public class PersistenceManager {
    @Getter
    private static final EntityManagerFactory entityManagerFactory;

    static {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.hbm2ddl.auto", "update");

        CustomPersistenceUnitInfo persistenceUnitInfo = new CustomPersistenceUnitInfo();

        entityManagerFactory = new HibernatePersistenceProvider()
                .createContainerEntityManagerFactory(persistenceUnitInfo, properties);
    }

    public static void close() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }
}
