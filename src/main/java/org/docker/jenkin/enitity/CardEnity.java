package org.docker.jenkin.enitity;


import jakarta.persistence.*;


import java.util.ArrayList;
import java.util.List;


@Entity

@Table(name = "card")
public class CardEnity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cardId;
    @Column(name = "cardType")
    private String cardType;
    @Column(name = "cardLimit")
    private double cardLimit;

    @OneToOne
    @MapsId
    private UserEnity userEnity;

    @OneToMany(mappedBy = "cardEnity", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TranscationEnity> transcationEnityList = new ArrayList<>();

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public double getCardLimit() {
        return cardLimit;
    }

    public void setCardLimit(double cardLimit) {
        this.cardLimit = cardLimit;
    }

    public UserEnity getUserEnity() {
        return userEnity;
    }

    public void setUserEnity(UserEnity userEnity) {
        this.userEnity = userEnity;
    }

    public List<TranscationEnity> getTranscationEnityList() {
        return transcationEnityList;
    }

    public void setTranscationEnityList(List<TranscationEnity> transcationEnityList) {
        this.transcationEnityList = transcationEnityList;
    }

    public CardEnity() {
    }

    @Override
    public String toString() {
        return "CardEnity{" +
                "cardId=" + cardId +
                ", cardType='" + cardType + '\'' +
                ", cardLimit=" + cardLimit +
                ", userEnity=" + userEnity +
             //   ", transcationEnityList=" + transcationEnityList +
                '}';
    }
}
