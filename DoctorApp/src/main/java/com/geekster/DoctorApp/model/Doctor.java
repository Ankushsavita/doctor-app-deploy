package com.geekster.DoctorApp.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.geekster.DoctorApp.model.enums.Qualification;
import com.geekster.DoctorApp.model.enums.Specialization;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,scope = Doctor.class,property = "doctorId")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doctorId;
    private String doctorName;
    @Pattern(regexp = "^[0-9]{10}$")
    private String doctorContactNumber;
    @Min(value = 200)
    @Max(value = 2000)
    private Double doctorConsultationFee;
    @Enumerated(value = EnumType.STRING)
    private Specialization specialization;
    @Enumerated(value = EnumType.STRING)
    private Qualification qualification;

    @OneToMany(mappedBy = "doctor")
    List<Appointment> appointments;
}
