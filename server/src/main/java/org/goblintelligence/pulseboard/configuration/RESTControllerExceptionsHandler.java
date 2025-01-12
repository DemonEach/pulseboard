package org.goblintelligence.pulseboard.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@Slf4j
@ControllerAdvice
public class RESTControllerExceptionsHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleException(Exception ex) {
        if ((ex instanceof IOException && ex.getMessage().contains("Broken pipe"))
                || ex instanceof ClientAbortException) {
            log.error(ex.getMessage());
        } else {
            log.error(ex.getMessage(), ex);
        }

        return ResponseEntity.internalServerError().body("Internal server error!");
    }
}
