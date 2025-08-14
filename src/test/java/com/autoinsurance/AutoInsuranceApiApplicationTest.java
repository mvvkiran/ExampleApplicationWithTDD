package com.autoinsurance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb_app",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DisplayName("AutoInsuranceApiApplication Tests")
class AutoInsuranceApiApplicationTest {

    @Test
    @DisplayName("Should load application context successfully")
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Should run main method without exceptions")
    void should_RunMainMethod_When_ApplicationStarts() {
        // Given & When - Testing main method execution
        // This ensures the main method can be called without throwing exceptions
        AutoInsuranceApiApplication.main(new String[] {
            "--spring.profiles.active=test",
            "--server.port=0",
            "--spring.datasource.url=jdbc:h2:mem:testdb_main",
            "--spring.jpa.hibernate.ddl-auto=create-drop"
        });
        
        // Then - If we reach here, the application started successfully
        assertThat(true).isTrue();
    }
}