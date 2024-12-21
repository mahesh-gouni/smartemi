package org.docker.jenkin.enitity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class Service {


    public void addingUser(UserEnity userEntity) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hibernateDemo");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();
            entityManager.persist(userEntity);

            entityManager.getTransaction().commit();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void addCard(CardEnity cardEntity) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hibernateDemo");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();
            UserEnity user = entityManager.find(UserEnity.class, cardEntity.getUserEnity().getUserId());

            if (user == null) {
                System.out.println("User not found with ID: " + cardEntity.getUserEnity().getUserId());
            } else {
                cardEntity.setUserEnity(user);
                entityManager.persist(cardEntity);

                entityManager.getTransaction().commit();
                System.out.println("Card saved successfully for user: " + user.getUserName());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addTransactions(List<TranscationEnity> transactions, int cardId) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hibernateDemo");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            entityManager.getTransaction().begin();


            CardEnity card = entityManager.find(CardEnity.class, cardId);

            if (card == null || card.getCardId() != cardId) {
                System.out.println("Card not found with ID: ");
            } else {

                for (TranscationEnity transaction : transactions) {
                    transaction.setCardEnity(card);
                    card.getTranscationEnityList().add(transaction);
                    entityManager.persist(transaction);
                }

                entityManager.getTransaction().commit();
                System.out.println("Transactions added successfully to card: ");
            }
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
    }


    public List<TranscationEnity> getAllTransactions() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hibernateDemo");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<TranscationEnity> transactions = new ArrayList<>();

        try {
            entityManager.getTransaction().begin();

            Query query = entityManager.createQuery("SELECT t FROM TranscationEnity t", TranscationEnity.class);
            transactions = query.getResultList();

            entityManager.getTransaction().commit();
        } catch (Exception e) {

            e.printStackTrace();
        }

        return transactions;
    }


  //   not using this method for the chance of occuring amount may dupliacate

//    public void updateTransactionIfAmountMatches(double targetAmount) {
//        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hibernateDemo");
//        EntityManager entityManager = entityManagerFactory.createEntityManager();
//
//        try {
//            entityManager.getTransaction().begin();
//
//
//            TranscationEnity transaction = entityManager.createQuery(
//                            "SELECT t FROM TranscationEnity t WHERE t.amount = :targetAmount", TranscationEnity.class)
//                    .setParameter("targetAmount", targetAmount)
//                    .getResultStream()
//                    .findFirst()
//                    .orElse(null);
//
//            if (transaction != null) {
//
//                transaction.setDebit(false);
//
//
//                entityManager.merge(transaction);
//                System.out.println("Transaction updated to smart emi.");
//            } else {
//                System.out.println("Transaction with target amount not found.");
//            }
//
//            entityManager.getTransaction().commit();
//        } catch (Exception e) {
//            entityManager.getTransaction().rollback();
//            e.printStackTrace();
//        }
//    }







        public void updateTransactionByCompositeKeyAndAmount(TranscationComposite key, double targetAmount) {
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hibernateDemo");
            EntityManager entityManager = entityManagerFactory.createEntityManager();

            try {
                entityManager.getTransaction().begin();


                TranscationEnity transaction = entityManager.find(TranscationEnity.class, key);

                if (transaction != null) {

                    if (transaction.getAmount() == targetAmount) {

                        transaction.setDebit(false);
                        entityManager.merge(transaction);
                        System.out.println("Transaction updated to smartemi.");
                    } else {
                        System.out.println("Amount does not match the target amount.");
                    }
                } else {
                    System.out.println("Transaction not found for the given key.");
                }

                entityManager.getTransaction().commit();
            } catch (Exception e) {

                e.printStackTrace();
            }


        }
    }












