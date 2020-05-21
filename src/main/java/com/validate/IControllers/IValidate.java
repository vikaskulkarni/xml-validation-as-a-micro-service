package com.validate.IControllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@RequestMapping("/")
public interface IValidate {

    @GetMapping("isValid")
    String isValid() throws IOException;
}
