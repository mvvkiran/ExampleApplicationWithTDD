package com.autoinsurance.quote.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Quote Validation Configuration Tests")
class QuoteValidationConfigTest {

    private QuoteValidationConfig config;

    @BeforeEach
    void setUp() {
        config = new QuoteValidationConfig();
    }

    @Test
    @DisplayName("Should initialize with default values")
    void should_InitializeWithDefaultValues() {
        // Then
        assertThat(config.getVinPattern()).isEqualTo("^[A-HJ-NPR-Z0-9]{17}$");
        assertThat(config.getMinDriverAge()).isEqualTo(18);
        assertThat(config.getMaxDriverAge()).isEqualTo(85);
        assertThat(config.getMaxVehicleAge()).isEqualTo(20);
        assertThat(config.getCompiledVinPattern()).isNotNull();
        assertThat(config.getCompiledVinPattern().pattern()).isEqualTo("^[A-HJ-NPR-Z0-9]{17}$");
    }

    @Test
    @DisplayName("Should set and get VIN pattern correctly")
    void should_SetAndGetVinPatternCorrectly() {
        // Given
        String newPattern = "^[A-Z0-9]{10}$";
        
        // When
        config.setVinPattern(newPattern);
        
        // Then
        assertThat(config.getVinPattern()).isEqualTo(newPattern);
        assertThat(config.getCompiledVinPattern().pattern()).isEqualTo(newPattern);
    }

    @Test
    @DisplayName("Should compile VIN pattern when set")
    void should_CompileVinPatternWhenSet() {
        // Given
        String testPattern = "^TEST[0-9]{3}$";
        
        // When
        config.setVinPattern(testPattern);
        
        // Then
        Pattern compiledPattern = config.getCompiledVinPattern();
        assertThat(compiledPattern).isNotNull();
        assertThat(compiledPattern.matcher("TEST123").matches()).isTrue();
        assertThat(compiledPattern.matcher("INVALID").matches()).isFalse();
    }

    @Test
    @DisplayName("Should set and get minimum driver age correctly")
    void should_SetAndGetMinimumDriverAgeCorrectly() {
        // Given
        int newMinAge = 21;
        
        // When
        config.setMinDriverAge(newMinAge);
        
        // Then
        assertThat(config.getMinDriverAge()).isEqualTo(newMinAge);
    }

    @Test
    @DisplayName("Should set and get maximum driver age correctly")
    void should_SetAndGetMaximumDriverAgeCorrectly() {
        // Given
        int newMaxAge = 90;
        
        // When
        config.setMaxDriverAge(newMaxAge);
        
        // Then
        assertThat(config.getMaxDriverAge()).isEqualTo(newMaxAge);
    }

    @Test
    @DisplayName("Should set and get maximum vehicle age correctly")
    void should_SetAndGetMaximumVehicleAgeCorrectly() {
        // Given
        int newMaxAge = 25;
        
        // When
        config.setMaxVehicleAge(newMaxAge);
        
        // Then
        assertThat(config.getMaxVehicleAge()).isEqualTo(newMaxAge);
    }

    @Test
    @DisplayName("Should validate VIN pattern matching with default pattern")
    void should_ValidateVinPatternMatchingWithDefaultPattern() {
        // Given
        Pattern pattern = config.getCompiledVinPattern();
        
        // Then - Valid VINs
        assertThat(pattern.matcher("1HGBH41JXMN109186").matches()).isTrue();
        assertThat(pattern.matcher("JM1BK32F581174677").matches()).isTrue();
        assertThat(pattern.matcher("WVWAA71K08W201030").matches()).isTrue();
        
        // Then - Invalid VINs
        assertThat(pattern.matcher("").matches()).isFalse();
        assertThat(pattern.matcher("1234567890123456").matches()).isFalse(); // 16 chars
        assertThat(pattern.matcher("123456789012345678").matches()).isFalse(); // 18 chars
        assertThat(pattern.matcher("1HGBH41JXMN10918I").matches()).isFalse(); // Contains 'I'
        assertThat(pattern.matcher("1HGBH41JXMN10918O").matches()).isFalse(); // Contains 'O'
        assertThat(pattern.matcher("1HGBH41JXMN10918Q").matches()).isFalse(); // Contains 'Q'
    }

    @Test
    @DisplayName("Should handle complex VIN pattern changes")
    void should_HandleComplexVinPatternChanges() {
        // Given
        String originalPattern = config.getVinPattern();
        
        // When - Set a new pattern
        String newPattern = "^[A-Z]{3}[0-9]{6}[A-Z]{2}[0-9]{6}$";
        config.setVinPattern(newPattern);
        
        // Then
        assertThat(config.getVinPattern()).isEqualTo(newPattern);
        assertThat(config.getCompiledVinPattern().matcher("ABC123456DE789012").matches()).isTrue();
        assertThat(config.getCompiledVinPattern().matcher("1HGBH41JXMN109186").matches()).isFalse();
        
        // When - Reset to original pattern
        config.setVinPattern(originalPattern);
        
        // Then
        assertThat(config.getVinPattern()).isEqualTo(originalPattern);
        assertThat(config.getCompiledVinPattern().matcher("1HGBH41JXMN109186").matches()).isTrue();
        assertThat(config.getCompiledVinPattern().matcher("SHORT").matches()).isFalse();
    }

    @Test
    @DisplayName("Should maintain age boundary settings")
    void should_MaintainAgeBoundarySettings() {
        // When
        config.setMinDriverAge(16);
        config.setMaxDriverAge(100);
        config.setMaxVehicleAge(30);
        
        // Then
        assertThat(config.getMinDriverAge()).isEqualTo(16);
        assertThat(config.getMaxDriverAge()).isEqualTo(100);
        assertThat(config.getMaxVehicleAge()).isEqualTo(30);
        
        // Verify independence of settings
        config.setMinDriverAge(25);
        assertThat(config.getMaxDriverAge()).isEqualTo(100); // Should remain unchanged
        assertThat(config.getMaxVehicleAge()).isEqualTo(30); // Should remain unchanged
    }
}