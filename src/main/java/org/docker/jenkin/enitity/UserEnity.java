package com.technologies.SmartEMI.enitity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
public class UserEnity {

    @Id
    private int userId;

    @Column(name = "userName")
    private String userName;

    @OneToOne(mappedBy = "userEnity", cascade = CascadeType.ALL)
    private CardEnity cardEnity;



}
