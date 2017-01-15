import java.util.ArrayList;
import java.util.Set;

public interface Node {

   // NOTE: Node is an interface and does not have a constructor.
   // However, your CompliantNode.java class requires a 4 argument
   // constructor as defined in Simulation.java

   // followees[i] is True iff this node follows node i
   void setFollowees(boolean[] followees); 

   //initialize proposal list of transactions
   void setPendingTransaction(Set<Transaction> pendingTransactions);

   // return proposals to send to my followers.
   // REMEMBER: After final round, behavior of getProposals changes and
   // it should return the transactions upon which consensus has been reached.
   Set<Transaction> getProposals();

   // receive candidates from other nodes.
   void receiveCandidates(ArrayList<Integer[]> candidates);
}