package org.docker.jenkin.enitity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GettingTest {
    public static void main(String[] args) {


        Service s = new Service();

//    List<TranscationEnity> translist =  s.getAllTransactions();
//        translist.stream().forEach(System.out::println);
//




        List<TranscationEnity> transactions = s.getAllTransactions();


        System.out.println("individual amount list");

        List<Double> amounts = transactions.stream()
                .map(TranscationEnity::getAmount)
                .collect(Collectors.toList());

        amounts.forEach(System.out::println);







// Print or use the amounts





        //max amount

//        Optional<Double> maxAmount = transactions.stream()
//                .map(TranscationEnity::getAmount)
//                .max(Double::compare);
//
//        System.out.println("selected amount usually any body select the max amount only");
//
//        maxAmount.ifPresent(System.out::println);





    //    System.out.println("for the specific amount");


    //    double targetAmount = 20000.0;

//        Optional<Double> specificAmount = transactions.stream()
//                .map(TranscationEnity::getAmount)
//                .filter(amount -> amount == targetAmount)
//                .findFirst();
//
//        specificAmount.ifPresentOrElse(
//                amount -> System.out.println("Found amount: " + amount),
//                () -> System.out.println("Amount not found")
//        );


//        System.out.println("total amount");
//        double totalAmount = transactions.stream()
//                .mapToDouble(TranscationEnity::getAmount)
//                .sum();
//
//        System.out.println("Total Amount: " + totalAmount);


        System.out.println("slecting the smart emi");

        double targetAmount = 20000.0;

// their is a change of amojnt transctios are duplicate that y composite key is used
        TranscationComposite key = new TranscationComposite();
        LocalDateTime startDateTime10 = LocalDateTime.of(2024, 1, 1, 10, 39, 0);
        key.setStartDate(Timestamp.valueOf(startDateTime10));
        LocalDateTime endDateTime10 = LocalDateTime.of(2024, 1, 1, 10, 40, 0);
        key.setEndDate(Timestamp.valueOf(endDateTime10));



        s.updateTransactionByCompositeKeyAndAmount(key, targetAmount);




    }



}
