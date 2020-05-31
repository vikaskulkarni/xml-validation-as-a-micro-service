package com.validate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ValidationResponse {
    boolean isValid;
    int lineNumber;
    int columnNumber;
    String message;
}
