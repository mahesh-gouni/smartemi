package com.technologies.SmartEMI.enitity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TranscationComposite implements Serializable {

    private LocalDate startDate;

    private LocalDate endDate;



}
