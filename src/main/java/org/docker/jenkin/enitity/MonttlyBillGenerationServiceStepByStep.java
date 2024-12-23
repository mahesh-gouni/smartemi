package org.docker.jenkin.enitity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.sql.Timestamp;
import java.util.*;

public class MonttlyBillGenerationServiceStepByStep {





//        public List<TranscationEnity> getTransactionsForMonthStart( int year, int month) {
//            try {
//                EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hibernateDemo");
//                EntityManager entityManager = entityManagerFactory.createEntityManager();
//                // Calculate the start and end dates for the specified month
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(Calendar.YEAR, year);
//                calendar.set(Calendar.MONTH, month - 1); // Calendar months are 0-based
//                calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the 1st day of the month
//                Timestamp startDate = new Timestamp(calendar.getTimeInMillis());
//
//                // Move to the next month and subtract one millisecond for the last moment of the current month
//                calendar.add(Calendar.MONTH, 1);
//                Timestamp endDate = new Timestamp(calendar.getTimeInMillis() - 1);
//
//                // Query to fetch transactions within the date range
//                String jpql = "SELECT t FROM TranscationEnity t WHERE t.startDate >= :startDate AND t.startDate <= :endDate";
//                TypedQuery<TranscationEnity> query = entityManager.createQuery(jpql, TranscationEnity.class);
//                query.setParameter("startDate", startDate);
//                query.setParameter("endDate", endDate);
//
//                return query.getResultList();
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        }

    public Map<String, Double> getTotalAmountByMonth() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hibernateDemo");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        List<TranscationEnity> transactions = new ArrayList<>();

        try {
            entityManager.getTransaction().begin();
        String jpql = "SELECT MONTH(t.startDate) as month, YEAR(t.startDate) as year, SUM(t.amount) as totalAmount " +
                "FROM TranscationEnity t " +
                " WHERE t.isDebit = true " +
                "GROUP BY YEAR(t.startDate), MONTH(t.startDate) " +
                "ORDER BY YEAR(t.startDate), MONTH(t.startDate)";

        List<Object[]> results = entityManager.createQuery(jpql, Object[].class).getResultList();

        Map<String, Double> monthlyTotals = new LinkedHashMap<>();

        for (Object[] result : results) {
            int month = (int) result[0];
            int year = (int) result[1];
            double totalAmount = (double) result[2];

            String key = String.format("%04d-%02d", year, month); // e.g., "2024-01"
            monthlyTotals.put(key, totalAmount);
        }

        return monthlyTotals;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }


    public Map<String, Double> getTotalAdjustedAmountByMonth() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hibernateDemo");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Map<String, Double> adjustedTotals = new LinkedHashMap<>();

        try {
            entityManager.getTransaction().begin();

            // Step 1: Get monthly totals from TranscationEnity
            String jpql = "SELECT MONTH(t.startDate) as month, YEAR(t.startDate) as year, SUM(t.amount) as totalAmount " +
                    "FROM TranscationEnity t " +
                    "WHERE t.isDebit = true " +
                    "GROUP BY YEAR(t.startDate), MONTH(t.startDate) " +
                    "ORDER BY YEAR(t.startDate), MONTH(t.startDate)";

            List<Object[]> results = entityManager.createQuery(jpql, Object[].class).getResultList();

            // Step 2: Process monthly totals
            for (Object[] result : results) {
                int month = (int) result[0];
                int year = (int) result[1];
                double totalAmount = (double) result[2];

                String monthKey = String.format("%04d-%02d", year, month);

                // Step 3: Check for any false amounts
                String checkFalseAmountJpql = "SELECT COUNT(t) FROM TranscationEnity t WHERE MONTH(t.startDate) = :month " +
                        "AND YEAR(t.startDate) = :year AND t.isDebit = false";
                Long falseAmountCount = entityManager.createQuery(checkFalseAmountJpql, Long.class)
                        .setParameter("month", month)
                        .setParameter("year", year)
                        .getSingleResult();

                if (falseAmountCount > 0) {
                    // Step 4: Fetch EMI amount from Loan table
                    String loanJpql = "SELECT l.emiAmount FROM loan l WHERE l.id = :loanId";
                    Double emiAmount = entityManager.createQuery(loanJpql, Double.class)
                            .setParameter("loanId", 1L) // Example Loan ID, adjust as needed
                            .getSingleResult();

                    // Step 5: Update EMI_Status table and add EMI to totals
                    boolean allSetTrue = false;
                    while (!allSetTrue) {
                        // Update total amount with EMI
                        totalAmount += emiAmount;

                        // Update EMI_Status for the current month
                        String updateEmiStatusJpql = "UPDATE EMI_Status e SET e.status = true " +
                                "WHERE e.month = :month AND e.year = :year";
                        int updatedRows = entityManager.createQuery(updateEmiStatusJpql)
                                .setParameter("month", month)
                                .setParameter("year", year)
                                .executeUpdate();

                        // Check if all months are now true
                        String checkAllTrueJpql = "SELECT COUNT(e) FROM EMI_Status e WHERE e.status = false";
                        Long falseStatusCount = entityManager.createQuery(checkAllTrueJpql, Long.class)
                                .getSingleResult();
                        allSetTrue = (falseStatusCount == 0);

                        // Increment to the next month if needed
                        if (!allSetTrue) {
                            if (month == 12) {
                                month = 1;
                                year++;
                            } else {
                                month++;
                            }
                        }
                    }
                }

                // Add final total to the adjusted totals map
                adjustedTotals.put(monthKey, totalAmount);
            }

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            return null;
        } finally {
            entityManager.close();
            entityManagerFactory.close();
        }

        return adjustedTotals;
    }

//    public Map<String, Double> getTotalAdjustedAmountByMonths() {
//        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hibernateDemo");
//        EntityManager entityManager = entityManagerFactory.createEntityManager();
//        Map<String, Double> adjustedTotals = new LinkedHashMap<>();
//
//        try {
//            entityManager.getTransaction().begin();
//
//            // Step 1: Fetch monthly transaction totals
//            String jpql = "SELECT MONTH(t.startDate) as month, YEAR(t.startDate) as year, SUM(t.amount) as totalAmount " +
//                    "FROM TranscationEnity t WHERE t.isDebit = true " +
//                    "GROUP BY YEAR(t.startDate), MONTH(t.startDate) " +
//                    "ORDER BY YEAR(t.startDate), MONTH(t.startDate)";
//
//
//            List<Object[]> results = entityManager.createQuery(jpql, Object[].class).getResultList();
//
//            boolean allEmiSetTrue = false; // Tracks if all EMI statuses are set to true
//            Double emiAmount = null; // Holds the EMI amount fetched from the Loan table
//
//            for (Object[] result : results) {
//                int month = (int) result[0];
//                int year = (int) result[1];
//                double totalAmount = (double) result[2];
//                String monthKey = String.format("%04d-%02d", year, month);
//
//                if (!allEmiSetTrue) {
//                    // Step 2: Check for false amounts in this month
//                    String falseAmountQuery = "SELECT COUNT(t) FROM TranscationEnity t " +
//                            "WHERE MONTH(t.startDate) = :month AND YEAR(t.startDate) = :year " +
//                            "AND t.isDebit = false";
//
//                    Long falseAmountCount = entityManager.createQuery(falseAmountQuery, Long.class)
//                            .setParameter("month", month)
//                            .setParameter("year", year)
//                            .getSingleResult();
//
//                    if (falseAmountCount > 0) {
//                        if (emiAmount == null) {
//                            // Fetch EMI amount from Loan table (assumes one loan record for simplicity)
//                            String loanQuery = "SELECT l.emiAmount FROM LoanEntity l WHERE l.loanId = :loanId";
//                            emiAmount = entityManager.createQuery(loanQuery, Double.class)
//                                    .setParameter("loanId", 1) // Adjust loan ID dynamically as needed
//                                    .getSingleResult();
//                        }
//
//                        // Add EMI amount to the total
//                        totalAmount += emiAmount;
//
//                        // Update the EMI status for the current month to true
////                        String updateEmiStatusQuery = "SELECT SUM(l.adjustedAmount) FROM LoanEntity l WHERE l.loanId = :loanId";
////
////
////                        entityManager.createQuery(updateEmiStatusQuery)
////                                .setParameter("month", month)
////                                .executeUpdate();
//                        // Define the query to update the status for a specific loan and month
////                        String updateEmiStatusQuery = "UPDATE LoanEntity l " +
////                                "SET l.emiStatus = :emiStatus " +
////                                "WHERE l.loanId = :loanId";
////
////// Create a map for the EMI status for a specific month
////                        Map<Integer, Boolean> emiStatusMap = new HashMap<>();
////                        emiStatusMap.put(month, true);  // Assuming you want to set the status for the given month to true
////
////// Execute the update
////                        entityManager.createQuery(updateEmiStatusQuery)
////                                .setParameter("emiStatus", emiStatusMap)
////                                .setParameter("loanId", loanId)
////                                .executeUpdate();
//
//                        LoanEntity loanEntity = entityManager.find(LoanEntity.class, loanId);
//                        if (loanEntity != null) {
//                            loanEntity.getEmiStatus().put(month, true); // Update EMI status for the specific month
//                            entityManager.merge(loanEntity);  // Persist the changes
//                        }
//
//                        // Check if all EMI statuses are now true
//                        String checkAllTrueQuery = "SELECT COUNT(e) FROM EMI_Status e WHERE e.status = false";
//                        Long remainingFalseStatus = entityManager.createQuery(checkAllTrueQuery, Long.class)
//                                .getSingleResult();
//
//                        allEmiSetTrue = (remainingFalseStatus == 0);
//                    }
//                }
//
//                // Step 3: Add the total amount to the adjusted totals map
//                adjustedTotals.put(monthKey, totalAmount);
//
//                // If all EMI statuses are true, stop adding EMI amounts from the next month
//                if (allEmiSetTrue) {
//                    break; // Exit the loop to avoid further EMI additions
//                }
//            }
//
//            entityManager.getTransaction().commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (entityManager.getTransaction().isActive()) {
//                entityManager.getTransaction().rollback();
//            }
//            return null;
//        } finally {
//            entityManager.close();
//            entityManagerFactory.close();
//        }
//
//        return adjustedTotals;
//    }

    public Map<String, Double> getTotalAdjustedAmountByMonths() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hibernateDemo");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Map<String, Double> adjustedTotals = new LinkedHashMap<>();

        try {
            entityManager.getTransaction().begin();

            // Step 1: Fetch monthly transaction totals
            String jpql = "SELECT MONTH(t.startDate) as month, YEAR(t.startDate) as year, SUM(t.amount) as totalAmount " +
                    "FROM TranscationEnity t WHERE t.isDebit = true " +
                    "GROUP BY YEAR(t.startDate), MONTH(t.startDate) " +
                    "ORDER BY YEAR(t.startDate), MONTH(t.startDate)";

            List<Object[]> results = entityManager.createQuery(jpql, Object[].class).getResultList();

            boolean allEmiSetTrue = false; // Tracks if all EMI statuses are set to true
            Double emiAmount = null; // Holds the EMI amount fetched from the Loan table

            for (Object[] result : results) {
                int month = (int) result[0];
                int year = (int) result[1];
                double totalAmount = (double) result[2];
                String monthKey = String.format("%04d-%02d", year, month);

                if (!allEmiSetTrue) {
                    // Step 2: Check for false amounts in this month
                    String falseAmountQuery = "SELECT COUNT(t) FROM TranscationEnity t " +
                            "WHERE MONTH(t.startDate) = :month AND YEAR(t.startDate) = :year " +
                            "AND t.isDebit = false";

                    Long falseAmountCount = entityManager.createQuery(falseAmountQuery, Long.class)
                            .setParameter("month", month)
                            .setParameter("year", year)
                            .getSingleResult();

                    if (falseAmountCount > 0) {
                        if (emiAmount == null) {
                            // Fetch EMI amount from Loan table (assumes one loan record for simplicity)
                            String loanQuery = "SELECT l.emiAmount FROM LoanEntity l WHERE l.loanId = :loanId";
                            emiAmount = entityManager.createQuery(loanQuery, Double.class)
                                    .setParameter("loanId", 1) // Adjust loan ID dynamically as needed
                                    .getSingleResult();
                        }

                        // Add EMI amount to the total
                        totalAmount += emiAmount;

                        // Update the EMI status for the current month
//                        LoanEntity loanEntity = entityManager.find(LoanEntity.class, loanId);
//                        if (loanEntity != null) {
//                            loanEntity.getEmiStatus().put(month, true); // Update EMI status for the specific month
//                            entityManager.merge(loanEntity);  // Persist the changes
//                        }

                        // Check if all EMI statuses are now true
                        String checkAllTrueQuery = "SELECT COUNT(e) FROM EMI_Status e WHERE e.status = false";
                        Long remainingFalseStatus = entityManager.createQuery(checkAllTrueQuery, Long.class)
                                .getSingleResult();

                        allEmiSetTrue = (remainingFalseStatus == 0);
                    }
                }

                // Step 3: Add the total amount to the adjusted totals map
                adjustedTotals.put(monthKey, totalAmount);

                // If all EMI statuses are true, stop adding EMI amounts from the next month
                if (allEmiSetTrue) {
                    break; // Exit the loop to avoid further EMI additions
                }
            }

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            return null;
        } finally {
            entityManager.close();
            entityManagerFactory.close();
        }

        return adjustedTotals;
    }


}


