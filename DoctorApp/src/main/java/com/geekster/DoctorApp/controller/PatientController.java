package com.geekster.DoctorApp.controller;

import com.geekster.DoctorApp.model.Appointment;
import com.geekster.DoctorApp.model.Patient;
import com.geekster.DoctorApp.model.dto.SignInInput;
import com.geekster.DoctorApp.model.dto.SignUpOutput;
import com.geekster.DoctorApp.service.AuthenticationService;
import com.geekster.DoctorApp.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
public class PatientController {

    @Autowired
    PatientService patientService;

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("patient/signup")
    public SignUpOutput signUpPatient(@RequestBody Patient patient){
       return patientService.signUpPatient(patient);
    }

    @PostMapping("patient/signIn")
    public String signInPatient(@RequestBody @Valid SignInInput signInInput){
       return patientService.signInPatient(signInInput);
    }

    @GetMapping("patients")
    public List<Patient> getAllPatients(){
        return patientService.getAllPatients();
    }

    @PostMapping("appointment/schedule")
    public String scheduleAppointment(@RequestBody Appointment appointment, String email, String token){

        if(authenticationService.authenticate(email,token)) {
           boolean status = patientService.scheduleAppointment(appointment);
           return status ?  "appointment Scheduled.": "error occurred during scheduling appointment";
        }else{
            return "Scheduling failed Because of Invalid authentication";
        }
    }

    @DeleteMapping("appointment/cancel")
    public String cancelAppointment(String email, String token){

        if(authenticationService.authenticate(email,token)) {
            patientService.cancelAppointment(email);
            return "canceled appointment successfully.";
        }else{
            return "Scheduling failed Because of Invalid authentication";
        }
    }
}
