package com.technologies.SmartEMI.enitity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transcation")
@IdClass(TranscationComposite.class)
public class TranscationEnity {


    @Id
    private LocalDate startDate;
    @Id
    private LocalDate endDate;

    private double amount;

     private boolean isDebit;

    @ManyToOne
    @JoinColumn(name = "cardId")
     private CardEnity cardEnity;

    @OneToMany(mappedBy = "transcationEnity",cascade = CascadeType.ALL, orphanRemoval = true)
    List<SmartEMIEnity> transcationEnityList = new ArrayList<>();
}
