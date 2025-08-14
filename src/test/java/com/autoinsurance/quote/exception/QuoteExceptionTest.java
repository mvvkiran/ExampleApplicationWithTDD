package com.autoinsurance.quote.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Quote Exception Tests")
class QuoteExceptionTest {

    @Nested
    @DisplayName("QuoteNotFoundException Tests")
    class QuoteNotFoundExceptionTests {

        @Test
        @DisplayName("Should create exception with message")
        void should_CreateException_When_MessageProvided() {
            // Given
            String message = "Quote not found with ID: 12345";

            // When
            QuoteNotFoundException exception = new QuoteNotFoundException(message);

            // Then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception.getCause()).isNull();
        }

        @Test
        @DisplayName("Should create exception with message and cause")
        void should_CreateException_When_MessageAndCauseProvided() {
            // Given
            String message = "Quote not found with ID: 12345";
            Throwable cause = new RuntimeException("Database connection failed");

            // When
            QuoteNotFoundException exception = new QuoteNotFoundException(message, cause);

            // Then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.getCause().getMessage()).isEqualTo("Database connection failed");
        }

        @Test
        @DisplayName("Should be throwable as RuntimeException")
        void should_BeThrowable_When_ExceptionCreated() {
            // Given
            String message = "Quote not found";

            // When & Then
            assertThatThrownBy(() -> {
                throw new QuoteNotFoundException(message);
            })
            .isInstanceOf(RuntimeException.class)
            .isInstanceOf(QuoteNotFoundException.class)
            .hasMessage(message)
            .hasNoCause();
        }

        @Test
        @DisplayName("Should handle null message")
        void should_HandleNullMessage_When_NullProvided() {
            // When
            QuoteNotFoundException exception = new QuoteNotFoundException(null);

            // Then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isNull();
            assertThat(exception.getCause()).isNull();
        }

        @Test
        @DisplayName("Should handle null cause")
        void should_HandleNullCause_When_NullCauseProvided() {
            // Given
            String message = "Quote not found";

            // When
            QuoteNotFoundException exception = new QuoteNotFoundException(message, null);

            // Then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception.getCause()).isNull();
        }

        @Test
        @DisplayName("Should preserve stack trace")
        void should_PreserveStackTrace_When_ExceptionThrown() {
            // When & Then
            assertThatThrownBy(() -> {
                throw new QuoteNotFoundException("Test exception");
            })
            .satisfies(exception -> {
                assertThat(exception.getStackTrace()).isNotEmpty();
                assertThat(exception.getStackTrace()[0].getMethodName()).contains("should_PreserveStackTrace");
            });
        }
    }

    @Nested
    @DisplayName("InvalidQuoteRequestException Tests")
    class InvalidQuoteRequestExceptionTests {

        @Test
        @DisplayName("Should create exception with message")
        void should_CreateException_When_MessageProvided() {
            // Given
            String message = "Invalid quote request: vehicle year is too old";

            // When
            InvalidQuoteRequestException exception = new InvalidQuoteRequestException(message);

            // Then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception.getCause()).isNull();
        }

        @Test
        @DisplayName("Should create exception with message and cause")
        void should_CreateException_When_MessageAndCauseProvided() {
            // Given
            String message = "Invalid quote request: validation failed";
            Throwable cause = new IllegalArgumentException("Driver age is below minimum");

            // When
            InvalidQuoteRequestException exception = new InvalidQuoteRequestException(message, cause);

            // Then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.getCause().getMessage()).isEqualTo("Driver age is below minimum");
        }

        @Test
        @DisplayName("Should be throwable as RuntimeException")
        void should_BeThrowable_When_ExceptionCreated() {
            // Given
            String message = "Invalid request";

            // When & Then
            assertThatThrownBy(() -> {
                throw new InvalidQuoteRequestException(message);
            })
            .isInstanceOf(RuntimeException.class)
            .isInstanceOf(InvalidQuoteRequestException.class)
            .hasMessage(message)
            .hasNoCause();
        }

        @Test
        @DisplayName("Should handle null message")
        void should_HandleNullMessage_When_NullProvided() {
            // When
            InvalidQuoteRequestException exception = new InvalidQuoteRequestException(null);

            // Then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isNull();
            assertThat(exception.getCause()).isNull();
        }

        @Test
        @DisplayName("Should handle null cause")
        void should_HandleNullCause_When_NullCauseProvided() {
            // Given
            String message = "Invalid request";

            // When
            InvalidQuoteRequestException exception = new InvalidQuoteRequestException(message, null);

            // Then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception.getCause()).isNull();
        }

        @Test
        @DisplayName("Should chain exceptions properly")
        void should_ChainExceptions_When_CauseProvided() {
            // Given
            IllegalArgumentException rootCause = new IllegalArgumentException("Invalid parameter");
            InvalidQuoteRequestException middleException = new InvalidQuoteRequestException("Validation failed", rootCause);
            RuntimeException topException = new RuntimeException("Request processing failed", middleException);

            // Then
            assertThat(topException.getCause()).isInstanceOf(InvalidQuoteRequestException.class);
            assertThat(topException.getCause().getCause()).isInstanceOf(IllegalArgumentException.class);
            assertThat(topException.getCause().getCause().getMessage()).isEqualTo("Invalid parameter");
        }

        @Test
        @DisplayName("Should preserve stack trace")
        void should_PreserveStackTrace_When_ExceptionThrown() {
            // When & Then
            assertThatThrownBy(() -> {
                throw new InvalidQuoteRequestException("Test exception");
            })
            .satisfies(exception -> {
                assertThat(exception.getStackTrace()).isNotEmpty();
                assertThat(exception.getStackTrace()[0].getMethodName()).contains("should_PreserveStackTrace");
            });
        }
    }
}