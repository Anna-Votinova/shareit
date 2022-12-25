package ru.practicum.shareit.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.*;


import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ExceptionControllerTest {
    @Autowired
    private ExceptionController controller;

    @DisplayName("Test errorResponse IllegalArgumentException")
    @Test
    void testIllegalArgumentException() {
        ErrorResponse responseEntity = controller.handleIllegalArgumentException(new IllegalArgumentException("cause"));
        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getError());
        assertNotNull(responseEntity.getMessage());

    }

    @DisplayName("Test errorResponse UnknownStateException")
    @Test
    void testUnknownStateException() {
        ErrorResponse responseEntity = controller.handleUnknownStateException(new UnknownStateException("short cause"));
        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getError());
        assertNotNull(responseEntity.getMessage());
    }

    @DisplayName("Test errorResponse ValidationException")
    @Test
    void testValidationException() {
        ErrorResponse responseEntity = controller.handleValidationException(new ValidationException("short cause"));
        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getError());
        assertNotNull(responseEntity.getMessage());

    }

    @DisplayName("Test errorResponse Throwable")
    @Test
    void testThrowable() {
        ErrorResponse responseEntity = controller.handleThrowable(new TestAbortedException());
        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getError());
    }

    @DisplayName("Test errorResponse ConstraintViolationException")
    @Test
    void testConstraintValidationException() {

        Violation violation = new Violation("error", "short cause");
        assertNotNull(violation.getFieldName());
        assertNotNull(violation.getMessage());

        ValidationErrorResponse responseEntity = controller
                .onConstraintValidationException(new ConstraintViolationException(Set.of()));
        assertNotNull(responseEntity.getViolations());
        assertNotNull(responseEntity);
    }

}
