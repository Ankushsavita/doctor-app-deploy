package com.geekster.DoctorApp.service;

import com.geekster.DoctorApp.model.Appointment;
import com.geekster.DoctorApp.model.AuthenticationToken;
import com.geekster.DoctorApp.model.Patient;
import com.geekster.DoctorApp.model.dto.SignInInput;
import com.geekster.DoctorApp.model.dto.SignUpOutput;
import com.geekster.DoctorApp.repository.IAuthTokenRepo;
import com.geekster.DoctorApp.repository.IDoctorRepo;
import com.geekster.DoctorApp.repository.IPatientRepo;
import com.geekster.DoctorApp.service.utility.emailUtility.EmailHandler;
import com.geekster.DoctorApp.service.utility.hashingUtility.PasswordEncrypter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    @Autowired
    IPatientRepo patientRepo;

    @Autowired
    IDoctorRepo doctorRepo;

    @Autowired
    IAuthTokenRepo authTokenRepo;

    @Autowired
    AppointmentService appointmentService;

    // Adding patient to Database
    public SignUpOutput signUpPatient(Patient patient) {

        Boolean signUpStatus = true;
        String signUpStatusMessage = null;

        // extract email from patient
        String newEmail = patient.getPatientEmail();

        if (newEmail == null) {
            signUpStatusMessage = "Invalid Email";
            signUpStatus = false;
            return new SignUpOutput(signUpStatus, signUpStatusMessage);
        }

        // check if patient email already exists or not
        Patient existingPatient = patientRepo.findFirstByPatientEmail(newEmail);

        if (existingPatient != null) {
            signUpStatusMessage = "Email already registered";
            signUpStatus = false;
            return new SignUpOutput(signUpStatus, signUpStatusMessage);
        }

        // hash the password : encrypt the password
        try {
            String encryptedPassword = PasswordEncrypter.encryptPassword(patient.getPatientPassword());

            // saveAppointment the patient with the new encrypted password
            patient.setPatientPassword(encryptedPassword);
            patientRepo.save(patient);

            return new SignUpOutput(signUpStatus, "Patient registered successfully");

        } catch (Exception e) {
            signUpStatusMessage = "Internal error occured during sign up";
            signUpStatus = false;
            return new SignUpOutput(signUpStatus, signUpStatusMessage);
        }
    }

    public String signInPatient(SignInInput signInInput) {

        String signInStatusMessage = null;

        // extract email from patient
        String signInEmail = signInInput.getEmail();

        if (signInEmail == null) {
            signInStatusMessage = "Invalid Email";
            return signInStatusMessage;
        }

        // check if patient email already exists or not
        Patient existingPatient = patientRepo.findFirstByPatientEmail(signInEmail);

        if (existingPatient == null) {
            signInStatusMessage = "Email not registered";
            return signInStatusMessage;
        }

        // match password:
        // hash the password : encrypt the password
        try {
            String encryptedPassword = PasswordEncrypter.encryptPassword(signInInput.getPassword());

            if (existingPatient.getPatientPassword().equals(encryptedPassword)) {

                // session should be created since password matched and user id is valid
                AuthenticationToken authToken = new AuthenticationToken(existingPatient);
                // saveAppointment generated token in database
                authTokenRepo.save(authToken);

                EmailHandler.sendEmail("ankushverm14@gmail.com", "email testing", authToken.getTokenValue());
                return "Token sent to your email";
            } else {
                signInStatusMessage = "Invalid credentials!!!";
                return signInStatusMessage;
            }


        } catch (Exception e) {
            signInStatusMessage = "Internal error occured during sign in";
            return signInStatusMessage;
        }

    }


    public List<Patient> getAllPatients() {
        return patientRepo.findAll();
    }

    public boolean scheduleAppointment(Appointment appointment) {

        // id of doctor
        Long doctorId = appointment.getDoctor().getDoctorId();
        Boolean isDoctorValid = doctorRepo.existsById(doctorId);

        // id of patient
        Long patientId = appointment.getPatient().getPatientId();
        Boolean isPatientValid = patientRepo.existsById(patientId);

        if (isDoctorValid && isPatientValid) {
            appointmentService.saveAppointment(appointment);
            return true;
        }else{
            return false;
        }
    }

    public void cancelAppointment(String email) {

        // email -> patient -> appointment
        Patient patient = patientRepo.findFirstByPatientEmail(email);

       Appointment appointment = appointmentService.geAppointmentForPatient(patient);

       appointmentService.cancelAppointment(appointment);
    }
}
