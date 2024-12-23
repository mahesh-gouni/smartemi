package org.docker.jenkin.enitity;



import java.util.List;
import java.util.Map;

public class FinalTest {

    public static void main(String[] args) {
//        MontlyBillGenerationService processor = new MontlyBillGenerationService();
//        List<CreditCardBillEntity> bills = processor.processMonthlyTransactionsAndGenerateBill();
//
//        // Print the generated bills to the console
//        System.out.println("Generated Credit Card Bills:");
//        for (CreditCardBillEntity bill : bills) {
//            System.out.println("Year: " + bill.getYear() + ", Month: " + bill.getMonth() + ", Total Amount: " + bill.getTotalAmount());
//        }


        MonttlyBillGenerationServiceStepByStep m= new MonttlyBillGenerationServiceStepByStep();
//      List<TranscationEnity> t = m.getTransactionsForMonthStart(2024,1);
//      t.stream().forEach(System.out::println);
//
//      MontlyBillGenerationService ms = new MontlyBillGenerationService();


//   List<CreditCardBillEntity> cdbill =  ms.processMonthlyTransactionsAndGenerateBill();




//        Map<String, Double> monthlyTotals =m.getTotalAmountByMonth();
//        monthlyTotals.forEach((month, total) -> {
//            System.out.println(month + ": " + total);
//        });
//    }

        Map<String, Double> monthlyTotals =m.getTotalAdjustedAmountByMonths();

//        monthlyTotals.forEach((month, total) -> {
//            System.out.println(month + ": " + total);
      //  });
        if (monthlyTotals != null) {
        monthlyTotals.forEach((month, total) -> {
            System.out.println(month + ": " + total);
            });
        } else {
            System.out.println("No data found.");
        }

    }



}
