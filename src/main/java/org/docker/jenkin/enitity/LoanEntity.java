package org.docker.jenkin.enitity;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "loan")
public class LoanEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int loanId;

    @Column(name = "loan_amount")
    private double loanAmount;

    @Column(name = "no_of_months")
    private int noOfMonths;

    @Column(name = "emi_amount")
    private double emiAmount;

    @ElementCollection
    @CollectionTable(name = "emi_status", joinColumns = @JoinColumn(name = "loan_id"))
    @Column(name = "status")
    private List<Boolean> emiStatus;

    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "trans_start_date", referencedColumnName = "startDate"),
            @JoinColumn(name = "trans_end_date", referencedColumnName = "endDate")
    })
    private TranscationEnity transcationEnity;

    // Constructors, getters, setters
}
