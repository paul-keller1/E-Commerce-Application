package com.app.exception;

import com.app.dto.APIResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MyGlobalExceptionHandlerTest {

    private final MyGlobalExceptionHandler handler = new MyGlobalExceptionHandler();

    static class DummyController {
        public void bodyEndpoint(Object body) {}
        public void pathVarEndpoint(Long id) {}
    }

    @Test
    void apiAndResourceExceptionsShouldReturnExpectedStatusAndBody() {
        ResponseEntity<APIResponse> resourceResponse = handler.myResourceNotFoundException(
                new ResourceNotFoundException("Product", "id", 5L)
        );
        assertEquals(HttpStatus.NOT_FOUND, resourceResponse.getStatusCode());
        assertTrue(resourceResponse.getBody().getMessage().contains("Product not found with id: 5"));
        assertFalse(resourceResponse.getBody().isStatus());

        ResponseEntity<APIResponse> apiResponse = handler.myAPIException(new APIException("Bad request"));
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
        assertEquals("Bad request", apiResponse.getBody().getMessage());
        assertFalse(apiResponse.getBody().isStatus());
    }

    @Test
    void methodArgumentNotValidShouldReturnFieldErrorsMap() throws NoSuchMethodException {
        Method method = DummyController.class.getMethod("bodyEndpoint", Object.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "dummy");
        bindingResult.addError(new FieldError("dummy", "name", "must not be blank"));
        bindingResult.addError(new FieldError("dummy", "email", "must be valid"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);
        ResponseEntity<Map<String, String>> response = handler.myMethodArgumentNotValidException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertEquals("must not be blank", body.get("name"));
        assertEquals("must be valid", body.get("email"));
    }

    @Test
    void constraintViolationShouldReturnViolationsMap() {
        ConstraintViolation<?> violation1 = new SimpleViolation("field1", "error1");
        ConstraintViolation<?> violation2 = new SimpleViolation("field2", "error2");

        ResponseEntity<Map<String, String>> response = handler.myConstraintsViolationException(
                new ConstraintViolationException(Set.of(violation1, violation2))
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertEquals("error1", body.get("field1"));
        assertEquals("error2", body.get("field2"));
    }

    @Test
    void authenticationExceptionShouldReturnMessage() {
        ResponseEntity<String> response = handler.myAuthenticationException(new BadCredentialsException("bad creds"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("bad creds", response.getBody());
    }

    @Test
    void missingPathVariableShouldReturnBadRequest() throws NoSuchMethodException {
        Method method = DummyController.class.getMethod("pathVarEndpoint", Long.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        MissingPathVariableException ex = new MissingPathVariableException("id", parameter);

        ResponseEntity<APIResponse> response = handler.myMissingPathVariableException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isStatus());
        assertTrue(response.getBody().getMessage().contains("id"));
    }

    @Test
    void dataIntegrityViolationShouldReturnBadRequest() {
        ResponseEntity<APIResponse> response = handler.myDataIntegrityException(
                new DataIntegrityViolationException("duplicate key")
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("duplicate key", response.getBody().getMessage());
        assertFalse(response.getBody().isStatus());
    }

    @Test
    void exceptionConstructorsExposeMessages() {
        APIException api = new APIException("api error");
        assertEquals("api error", api.getMessage());
        assertNull(new APIException().getMessage());

        ResourceNotFoundException resByName = new ResourceNotFoundException("User", "email", "a@b.com");
        assertTrue(resByName.getMessage().contains("User not found with email: a@b.com"));

        ResourceNotFoundException resById = new ResourceNotFoundException("User", "id", 10L);
        assertTrue(resById.getMessage().contains("User not found with id: 10"));
        assertNull(new ResourceNotFoundException().getMessage());

        UserNotFoundException userEx = new UserNotFoundException("missing");
        assertEquals("missing", userEx.getMessage());
        assertNull(new UserNotFoundException().getMessage());
    }

    private static class SimpleViolation implements ConstraintViolation<Object> {
        private final Path propertyPath;
        private final String message;

        SimpleViolation(String property, String message) {
            this.propertyPath = new SimplePath(property);
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public String getMessageTemplate() {
            return null;
        }

        @Override
        public Object getRootBean() {
            return null;
        }

        @Override
        public Class<Object> getRootBeanClass() {
            return Object.class;
        }

        @Override
        public Object getLeafBean() {
            return null;
        }

        @Override
        public Object[] getExecutableParameters() {
            return new Object[0];
        }

        @Override
        public Object getExecutableReturnValue() {
            return null;
        }

        @Override
        public Path getPropertyPath() {
            return propertyPath;
        }

        @Override
        public Object getInvalidValue() {
            return null;
        }

        @Override
        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return null;
        }

        @Override
        public <U> U unwrap(Class<U> type) {
            return null;
        }
    }

    private static class SimplePath implements Path {
        private final String value;

        SimplePath(String value) {
            this.value = value;
        }

        @Override
        public Iterator<Node> iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
