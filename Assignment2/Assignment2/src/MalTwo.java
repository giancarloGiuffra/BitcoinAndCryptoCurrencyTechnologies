/* This malicious node behaves erratically by broadcasting a random 
 * subset of the transactions it has received. */

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class MalTwo implements Node {

   Set<Transaction> pendingTransactions;

   public MalTwo(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
   }

   public void setFollowees(boolean[] followees) {
      return;
   }

   public void setPendingTransaction(Set<Transaction> pendingTransactions) {
      this.pendingTransactions = pendingTransactions; 
   }

   public Set<Transaction> getProposals() {
      HashSet<Transaction> randomTransactions = new HashSet<Transaction>();
      double p = .7; // take 70% of transactions
      for (Transaction tx : pendingTransactions) {
         if (Math.random() < p)
            randomTransactions.add(tx);
      }
      return randomTransactions;
   }

   // add all Transactions to pendingTransactions
   public void receiveCandidates(ArrayList<Integer[]> candidates) {     
      for(Integer[] candidate : candidates) {
         Transaction tx = new Transaction(candidate[0]);
         if(!this.pendingTransactions.contains(tx))
            this.pendingTransactions.add(tx);
      }

   }
}
