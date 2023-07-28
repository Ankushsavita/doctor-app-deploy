package com.geekster.DoctorApp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpOutput {

    // we are encapsulate the data and will return the object
    private Boolean signUpStatus;
    private String signUpStatusMessage;
}
