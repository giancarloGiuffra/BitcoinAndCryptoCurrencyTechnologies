
public class DropboxTestStudentMiner {
   public static void main(String args[]) {
      /*String[] s = new String[1];
      for (int i = 1; i <= 30; i++) {
         s[0] = new Integer(i).toString();
         if (i < 10) s[0] = "0" + s[0];
         s[0] = "test" + s[0] + ".txt";
         System.out.println(s[0]);
         long minerRevenue[] = Simulation.run(s);
         System.out.println();
      }*/
      
      long[] minerRevenue;
      double[] revenuesInBTC = new double[9];
      System.out.println("Notations:");
      System.out.println("----------------------------------------------------------------");
      System.out.println("Selfish Miner - SM");
      System.out.println("Feather Forking Miner - FF");
      System.out.println("Pick Newest Block Miner - PN");
      System.out.println("Pick Oldest Block Miner - PO");
      System.out.println("Blacklist(Prevent) High Tx Fee Miner - BH");
      System.out.println("Student Miner - ST");
      System.out.println("Config 0: Not revealing strategy, Not changing addresses");
      System.out.println("Config 1: Not revealing strategy, Changing addresses");
      System.out.println("Config 2: Revealing strategy, Not changing addresses");
      System.out.println("Config 3: Revealing strategy, Changing addresses");
      System.out.println("----------------------------------------------------------------");
      
      System.out.println();
      System.out.println("With only one type of miners");
      
      System.out.println();
      System.out.println("Test1: HashRateDistribution is SM0(50%), ST(50%)");
      minerRevenue = Simulation.run("files/test01.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are SM0:" + String.format("%.2f", revenuesInBTC[4])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test2: HashRateDistribution is SM0(67%), ST(33%)");
      minerRevenue = Simulation.run("files/test02.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are SM0:" + String.format("%.2f", revenuesInBTC[4]) 
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test3: HashRateDistribution is SM0(33%), ST(67%)");
      minerRevenue = Simulation.run("files/test03.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are SM0:" + String.format("%.2f", revenuesInBTC[4]) 
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test4: HashRateDistribution is SM0(33%), SM0(33%), ST(34%)");
      minerRevenue = Simulation.run("files/test04.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are SM0:" + String.format("%.2f", revenuesInBTC[4])
            + ", SM0:" + String.format("%.2f", revenuesInBTC[5])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test5: HashRateDistribution is SM0(50%), SM0(25%), ST(25%)");
      minerRevenue = Simulation.run("files/test05.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are SM0:" + String.format("%.2f", revenuesInBTC[4])
            + ", SM0:" + String.format("%.2f", revenuesInBTC[5])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test6: HashRateDistribution is SM1(50%), SM0(25%), ST(25%)");
      minerRevenue = Simulation.run("files/test06.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are SM1:" + String.format("%.2f", revenuesInBTC[4])
            + ", SM0:" + String.format("%.2f", revenuesInBTC[5])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test7: HashRateDistribution is SM1(50%), SM1(25%), ST(25%)");
      minerRevenue = Simulation.run("files/test07.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are SM1:" + String.format("%.2f", revenuesInBTC[4])
            + ", SM1:" + String.format("%.2f", revenuesInBTC[5])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test8: HashRateDistribution is FF2(50%), ST(50%)");
      minerRevenue = Simulation.run("files/test08.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are FF2:" + String.format("%.2f", revenuesInBTC[2])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test9: HashRateDistribution is FF0(50%), ST(50%)");
      minerRevenue = Simulation.run("files/test09.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are FF0:" + String.format("%.2f", revenuesInBTC[2])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test10: HashRateDistribution is FF2(75%), ST(25%)");
      minerRevenue = Simulation.run("files/test10.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are FF2:" + String.format("%.2f", revenuesInBTC[2])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test11: HashRateDistribution is FF0(75%), ST(25%)");
      minerRevenue = Simulation.run("files/test11.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are FF0:" + String.format("%.2f", revenuesInBTC[2])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test12: HashRateDistribution is FF2(33%), FF2(33%), ST(34%)");
      minerRevenue = Simulation.run("files/test12.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are FF2:" + String.format("%.2f", revenuesInBTC[2])
            + ", FF2:" + String.format("%.2f", revenuesInBTC[3])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test13: HashRateDistribution is FF2(33%), FF0(33%), ST(34%)");
      minerRevenue = Simulation.run("files/test13.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are FF2:" + String.format("%.2f", revenuesInBTC[2])
            + ", FF0:" + String.format("%.2f", revenuesInBTC[3])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test14: HashRateDistribution is FF2(50%), FF2(25%), ST(25%)");
      minerRevenue = Simulation.run("files/test14.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are FF2:" + String.format("%.2f", revenuesInBTC[2])
            + ", FF2:" + String.format("%.2f", revenuesInBTC[3])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test15: HashRateDistribution is FF3(50%), FF2(25%), ST(25%)");
      minerRevenue = Simulation.run("files/test15.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are FF3:" + String.format("%.2f", revenuesInBTC[2])
            + ", FF2:" + String.format("%.2f", revenuesInBTC[3])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test16: HashRateDistribution is BH2(50%), ST(50%)");
      minerRevenue = Simulation.run("files/test16.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are BH2:" + String.format("%.2f", revenuesInBTC[0])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test17: HashRateDistribution is BH0(50%), ST(50%)");
      minerRevenue = Simulation.run("files/test17.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are BH0:" + String.format("%.2f", revenuesInBTC[0])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test18: HashRateDistribution is BH2(75%), ST(25%)");
      minerRevenue = Simulation.run("files/test18.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are BH2:" + String.format("%.2f", revenuesInBTC[0])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test19: HashRateDistribution is BH0(75%), ST(25%)");
      minerRevenue = Simulation.run("files/test19.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are BH0:" + String.format("%.2f", revenuesInBTC[0])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test20: HashRateDistribution is BH2(33%), BH2(33%), ST(34%)");
      minerRevenue = Simulation.run("files/test20.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are BH2:" + String.format("%.2f", revenuesInBTC[0])
            + ", BH2:" + String.format("%.2f", revenuesInBTC[1])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test21: HashRateDistribution is BH2(33%), BH0(33%), ST(34%)");
      minerRevenue = Simulation.run("files/test21.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are BH2:" + String.format("%.2f", revenuesInBTC[0])
            + ", BH0:" + String.format("%.2f", revenuesInBTC[1])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test22: HashRateDistribution is BH2(50%), BH2(25%), ST(25%)");
      minerRevenue = Simulation.run("files/test22.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are BH2:" + String.format("%.2f", revenuesInBTC[0])
            + ", BH2:" + String.format("%.2f", revenuesInBTC[1])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test23: HashRateDistribution is BH3(50%), BH2(25%), ST(25%)");
      minerRevenue = Simulation.run("files/test23.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are BH3:" + String.format("%.2f", revenuesInBTC[0])
            + ", BH2:" + String.format("%.2f", revenuesInBTC[1])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test24: HashRateDistribution is PN0(50%), ST(50%)");
      minerRevenue = Simulation.run("files/test24.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are PN0:" + String.format("%.2f", revenuesInBTC[6])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test25: HashRateDistribution is PN0(75%), ST(25%)");
      minerRevenue = Simulation.run("files/test25.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are PN0:" + String.format("%.2f", revenuesInBTC[6])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test26: HashRateDistribution is PN0(25%), ST(75%)");
      minerRevenue = Simulation.run("files/test26.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are PN0:" + String.format("%.2f", revenuesInBTC[6])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test27: HashRateDistribution is PO0(50%), ST(50%)");
      minerRevenue = Simulation.run("files/test27.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are PO0:" + String.format("%.2f", revenuesInBTC[7])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test28: HashRateDistribution is PO0(75%), ST(25%)");
      minerRevenue = Simulation.run("files/test28.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are PO0:" + String.format("%.2f", revenuesInBTC[7])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("Test29: HashRateDistribution is PO0(25%), ST(75%)");
      minerRevenue = Simulation.run("files/test29.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are PO0:" + String.format("%.2f", revenuesInBTC[7])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
      
      System.out.println();
      System.out.println("With more than one type of miners");
      System.out.println();
      System.out.println("Test30: HashRateDistribution is SM2(33%), PN2(33%), ST(34%)");
      minerRevenue = Simulation.run("files/test30.txt");
      for (int i = 0; i < 9; i++) 
         revenuesInBTC[i] = (minerRevenue[i]*1.0)/100000000;
      System.out.println("==> Earnings in BTC are SM2:" + String.format("%.2f", revenuesInBTC[4])
            + ", PN2:" + String.format("%.2f", revenuesInBTC[6])
            + ", ST:" + String.format("%.2f", revenuesInBTC[8]));
   }
}
