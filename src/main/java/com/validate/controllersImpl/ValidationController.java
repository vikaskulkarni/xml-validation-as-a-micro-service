package com.validate.controllersImpl;

import com.validate.IControllers.IValidate;
import com.validate.services.ResourceFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ValidationController implements IValidate {
    @Autowired
    ResourceFileReader resourceFileReader;

    @Override
    public String isValid() throws IOException {
        String resourceFile = "students.xml";
        String resourceFilePath = resourceFileReader.getResourceFilePath(resourceFile);
        return resourceFileReader.readFileUsingStream(resourceFilePath);
    }
}
