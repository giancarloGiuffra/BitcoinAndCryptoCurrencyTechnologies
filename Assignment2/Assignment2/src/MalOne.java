/* This malicious node only broadcasts its transactions once: two rounds before the end*/

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class MalOne implements Node {


   int numRounds;
   int round = 0; 
   int num_followees; 
   boolean[] followees; 
   Set<Transaction> pendingTransactions;

   public MalOne(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
      this.numRounds = numRounds;
   }

   public void setFollowees(boolean[] followees) {
      return;
   }

   public void setPendingTransaction(Set<Transaction> pendingTransactions) {
      this.pendingTransactions = pendingTransactions;
   }

   public Set<Transaction> getProposals() {
      this.round += 1;
      if (this.round == this.numRounds - 2)
         return pendingTransactions;
      return new HashSet<Transaction>();
   }

   public void receiveCandidates(ArrayList<Integer[]> candidates) {
      return;
   }
}
