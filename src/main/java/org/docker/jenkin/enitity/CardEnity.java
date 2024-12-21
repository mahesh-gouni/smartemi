package com.technologies.SmartEMI.enitity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@Table(name = "card")
public class CardEnity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cardId;
    @Column(name = "cardName")
    private String cardName;
    @Column(name = "cardLimit")
    private double cardLimit;

    @OneToOne
    @MapsId
    private UserEnity userEnity;

    @OneToMany(mappedBy = "cardEnity", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TranscationEnity> transcationEnityList = new ArrayList<>();


}
