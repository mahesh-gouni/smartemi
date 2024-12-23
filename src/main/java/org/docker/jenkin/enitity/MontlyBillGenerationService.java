package org.docker.jenkin.enitity;


import jakarta.persistence.*;


import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class MontlyBillGenerationService {


// we are using object array because of the  time retuning the mutiple filed types

    public void getTotalDebitedAmountGroupedByMonth() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hibernateDemo");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<Object[]> transactions = new ArrayList<>();

        try {
            entityManager.getTransaction().begin();

            // Query to group debited transactions by month and year
            String jpql = "SELECT EXTRACT(YEAR FROM t.startDate) AS year, " +
                    "EXTRACT(MONTH FROM t.startDate) AS month, " +
                    "SUM(t.amount) AS totalDebitedAmount, " +
                    "COUNT(t) AS totalTransactions " +
                    "FROM TranscationEnity t " +
                    "WHERE t.isDebit = true " +
                    "GROUP BY EXTRACT(YEAR FROM t.startDate), EXTRACT(MONTH FROM t.startDate) " +
                    "ORDER BY year DESC, month DESC";

            Query query = entityManager.createQuery(jpql);
            transactions = query.getResultList();

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public List<TranscationEnity> getFalseTransactions() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernateDemo");
        EntityManager em = emf.createEntityManager();

        try {
            String jpql = "SELECT t FROM TranscationEnity t WHERE t.isDebit = false";
            TypedQuery<TranscationEnity> query = em.createQuery(jpql, TranscationEnity.class);
            return query.getResultList();
        } finally {
            em.close();
            emf.close();
        }
    }







//        public List<CreditCardBillEntity> processMonthlyTransactionsAndGenerateBill() {
//            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hibernateDemo");
//            EntityManager entityManager = entityManagerFactory.createEntityManager();
//            List<CreditCardBillEntity> generatedBills = new ArrayList<>();
//
//            try {
//                entityManager.getTransaction().begin();
//
//                // Step 1: Get the list of transactions for every month (from the 5th to the 5th)
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(Calendar.DAY_OF_MONTH, 5);
//                int currentMonth = calendar.get(Calendar.MONTH);
//                int currentYear = calendar.get(Calendar.YEAR);
//
//                // Fetch loan entities and get the noOfMonths
//                String jpqlLoan = "SELECT l FROM LoanEntity l";
//                List<LoanEntity> loanEntities = entityManager.createQuery(jpqlLoan, LoanEntity.class).getResultList();
//
//                // Iterate over the loan entities to get noOfMonths and process transactions accordingly
//                for (LoanEntity loan : loanEntities) {
//                    int noOfMonths = loan.getNoOfMonths();  // Dynamic noOfMonths from LoanEntity
//
//                    // Iterate through each month for the loan's repayment period
//                    for (int monthsBack = 0; monthsBack < noOfMonths; monthsBack++) {
//                        calendar.add(Calendar.MONTH, -1); // Go back one month
//                        Date startDate = getStartOfMonth(calendar);
//                        Date endDate = getEndOfMonth(calendar);
//
//                        // Step 2: Fetch transactions within this date range
//                        String jpql = "SELECT t FROM TranscationEnity t WHERE t.startDate BETWEEN :startDate AND :endDate";
//                        List<TranscationEnity> transactions = entityManager.createQuery(jpql, TranscationEnity.class)
//                                .setParameter("startDate", startDate)
//                                .setParameter("endDate", endDate)
//                                .getResultList();
//
//                        double totalAmount = 0.0;
//                        double totalEmiAmount = 0.0;
//
//                        for (TranscationEnity transaction : transactions) {
//                            if (transaction.isDebit()) {
//                                totalAmount += transaction.getAmount();
//                            } else {
//                                // For false debit (smart EMI) transactions
//                                if (loan != null) {
//                                    totalEmiAmount += loan.getEmiAmount();
//                                    updateEmiStatus(loan, currentMonth, entityManager);
//                                }
//                            }
//                        }
//
//                        // Add the total amount and EMI for the current month
//                        double totalCreditCardBill = totalAmount + totalEmiAmount;
//
//                        // Step 3: Create CreditCardBillEntity
//                        CreditCardBillEntity billEntity = new CreditCardBillEntity();
//                        billEntity.setYear(calendar.get(Calendar.YEAR));
//                        billEntity.setMonth(calendar.get(Calendar.MONTH) + 1); // months are 0-based
//                        billEntity.setTotalAmount(totalCreditCardBill);
//
//                        // Persist the bill
//                        entityManager.persist(billEntity);
//                        generatedBills.add(billEntity);
//                    }
//                }

//                entityManager.getTransaction().commit();
//            } catch (Exception e) {
//                if (entityManager.getTransaction().isActive()) {
//                    entityManager.getTransaction().rollback();
//                }
//                e.printStackTrace();
//            } finally {
//                entityManager.close();
//                entityManagerFactory.close();
//            }
//
//            return generatedBills;
//        }
//
//        private Date getStartOfMonth(Calendar calendar) {
//            calendar.set(Calendar.DAY_OF_MONTH, 1);
//            return calendar.getTime();
//        }
//
//        private Date getEndOfMonth(Calendar calendar) {
//            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
//            return calendar.getTime();
//        }
//
//        private void updateEmiStatus(LoanEntity loan, int currentMonth, EntityManager entityManager) {
//            Map<Integer, Boolean> emiStatus = loan.getEmiStatus();
//            if (emiStatus != null && !emiStatus.getOrDefault(currentMonth, false)) {
//                emiStatus.put(currentMonth, true);
//                loan.setLoanAmount(loan.getLoanAmount() - loan.getEmiAmount());
//                loan.setNoOfMonths(loan.getNoOfMonths() - 1);
//                loan.setEmiStatus(emiStatus);
//                entityManager.merge(loan);  // Update loan entity with new EMI status
//            }
     //   }


//
//    private List<Object[]> getTotalDebitedAmountGroupedByMonth(EntityManager entityManager) {
//        String jpql = "SELECT EXTRACT(YEAR FROM t.startDate) AS year, " +
//                "EXTRACT(MONTH FROM t.startDate) AS month, " +
//                "SUM(t.amount) AS totalDebitedAmount, " +
//                "COUNT(t) AS totalTransactions " +
//                "FROM TranscationEnity t " +
//                "WHERE t.isDebit = true " +
//                "GROUP BY EXTRACT(YEAR FROM t.startDate), EXTRACT(MONTH FROM t.startDate) " +
//                "ORDER BY year DESC, month DESC";
//
//        return entityManager.createQuery(jpql).getResultList();
//    }



}
