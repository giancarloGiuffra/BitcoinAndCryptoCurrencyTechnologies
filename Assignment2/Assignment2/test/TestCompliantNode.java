// Example of a Simulation. This test runs the nodes on a random graph.
// At the end, it will print out the Transaction ids which each node
// believes consensus has been reached upon. You can use this simulation to
// test your nodes. You will want to try creating some deviant nodes and
// mixing them in the network to fully test.

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;

public class TestCompliantNode {

   public static void main(String[] args) throws IOException {

      int numNodes = 100;
      double p_graph = .2; // parameter for random graph: prob. that an edge will exist
      double p_malicious = .2; // prob. that a node will be set to be malicious
      double p_txDistribution = .1; // probability of assigning an initial transaction to each node 
      int numRounds = 10; // number of simulation rounds your nodes will run for

      boolean[][] followees = new boolean[numNodes][numNodes]; // followees[i][j] is true iff i follows j
      boolean[] malicious = new boolean[100];

      BufferedReader br = new BufferedReader(new FileReader("files/20_20_1.txt"));
      try {
         String[] line = br.readLine().split(" ");
         for(String s : line)
            malicious[Integer.parseInt(s)] = true;

         br.readLine();

         for(int i = 0; i < 100; i++) {
            line = br.readLine().split(" ");
            for(String s : line)
               followees[i][Integer.parseInt(s)] = true;
         }
      } finally {
         br.close();
      }

      // pick which nodes are malicious and which are compliant
      Node[] nodes = new Node[numNodes];
      for (int i = 0; i < numNodes; i++) {
         if(malicious[i])
            nodes[i] = new MaliciousNode(p_graph, p_malicious, p_txDistribution, numRounds);
         else
            nodes[i] = new CompliantNode(p_graph, p_malicious, p_txDistribution, numRounds);
      }

      // notify all nodes of their followees
      for (int i = 0; i < numNodes; i++)
         nodes[i].setFollowees(followees[i]);

      // initialize a set of 500 valid Transactions with random ids
      int numTx = 500;
      HashSet<Integer> validTxIds = new HashSet<Integer>();

      HashMap<Integer, Set<Transaction>> allTx = new HashMap<Integer, Set<Transaction>>();
      for (int i = 0; i < numNodes; i++)
         allTx.put(i, new HashSet<Transaction>());

      br = new BufferedReader(new FileReader("files/10percent_1.txt"));
      try {
         for(int i = 0; i < numTx; i++) {
            int txID = Integer.parseInt(br.readLine());
            validTxIds.add(txID);
            String[] line = br.readLine().split(" ");
            for(String s : line)
               allTx.get(Integer.parseInt(s)).add(new Transaction(txID));
         }
      } finally {
         br.close();
      }

      for (int i = 0; i < numNodes; i++) {
         nodes[i].setPendingTransaction(allTx.get(i));
      }

      // Simulate for numRounds times
      for (int round = 0; round < numRounds; round++) { // numRounds is either 10 or 20

         // gather all the proposals into a map. The key is the index of the node receiving
         // proposals. The value is an ArrayList containing 1x2 Integer arrays. The first
         // element of each array is the id of the transaction being proposed and the second
         // element is the index # of the node proposing the transaction.
         HashMap<Integer, ArrayList<Integer[]>> allProposals = new HashMap<Integer, ArrayList<Integer[]>>();

         for (int i = 0; i < numNodes; i++) {
            Set<Transaction> proposals = nodes[i].getProposals();
            for (Transaction tx : proposals) {
               if (!validTxIds.contains(tx.id))
                  continue; // ensure that each tx is actually valid

               for (int j = 0; j < numNodes; j++) {
                  if(!followees[j][i]) continue; // tx only matters if j follows i

                  if(allProposals.containsKey(j)) {
                     Integer[] candidate = new Integer[2]; 
                     candidate[0] = tx.id;
                     candidate[1] = i; 
                     allProposals.get(j).add(candidate);
                  } else {
                     ArrayList<Integer[]> candidates = new ArrayList<Integer[]>();
                     Integer[] candidate = new Integer[2]; 
                     candidate[0] = tx.id; 
                     candidate[1] = i;   
                     candidates.add(candidate);
                     allProposals.put(j, candidates);
                  }
               }
            }
         }

         // Distribute the Proposals to their intended recipients as Candidates
         for (int i = 0; i < numNodes; i++) {
            if (allProposals.containsKey(i))
               nodes[i].receiveCandidates(allProposals.get(i));
         }
      }

      // print results
      System.out.println("Malicious node code in MaliciousNode.java.");
      System.out.println("These are the number of transactions outputted by each of your compliant nodes:");
      for (int i = 0; i < numNodes; i++) {
         if (malicious[i]) continue;
         Set<Transaction> transactions = nodes[i].getProposals();
         System.out.print(transactions.size() + " ");
      }
      System.out.println();
      System.out.println();
      
      
      
      
      
      // pick which nodes are malicious and which are compliant
      nodes = new Node[numNodes];
      for (int i = 0; i < numNodes; i++) {
         if(malicious[i])
            nodes[i] = new MalOne(p_graph, p_malicious, p_txDistribution, numRounds);
         else
            nodes[i] = new CompliantNode(p_graph, p_malicious, p_txDistribution, numRounds);
      }
      
      // notify all nodes of their followees
      for (int i = 0; i < numNodes; i++)
         nodes[i].setFollowees(followees[i]);
      
      for (int i = 0; i < numNodes; i++) {
         nodes[i].setPendingTransaction(allTx.get(i));
      }
      
      // Simulate for numRounds times
      for (int round = 0; round < numRounds; round++) { // numRounds is either 10 or 20

         // gather all the proposals into a map. The key is the index of the node receiving
         // proposals. The value is an ArrayList containing 1x2 Integer arrays. The first
         // element of each array is the id of the transaction being proposed and the second
         // element is the index # of the node proposing the transaction.
         HashMap<Integer, ArrayList<Integer[]>> allProposals = new HashMap<Integer, ArrayList<Integer[]>>();

         for (int i = 0; i < numNodes; i++) {
            Set<Transaction> proposals = nodes[i].getProposals();
            for (Transaction tx : proposals) {
               if (!validTxIds.contains(tx.id))
                  continue; // ensure that each tx is actually valid

               for (int j = 0; j < numNodes; j++) {
                  if(!followees[j][i]) continue; // tx only matters if j follows i

                  if(allProposals.containsKey(j)) {
                     Integer[] candidate = new Integer[2]; 
                     candidate[0] = tx.id;
                     candidate[1] = i; 
                     allProposals.get(j).add(candidate);
                  } else {
                     ArrayList<Integer[]> candidates = new ArrayList<Integer[]>();
                     Integer[] candidate = new Integer[2]; 
                     candidate[0] = tx.id; 
                     candidate[1] = i;   
                     candidates.add(candidate);
                     allProposals.put(j, candidates);
                  }
               }
            }
         }

         // Distribute the Proposals to their intended recipients as Candidates
         for (int i = 0; i < numNodes; i++) {
            if (allProposals.containsKey(i))
               nodes[i].receiveCandidates(allProposals.get(i));
         }
      }

      // print results
      System.out.println("Malicious node code in MalOne.java.");
      System.out.println("These are the number of transactions outputted by each of your compliant nodes:");
      for (int i = 0; i < numNodes; i++) {
         if (malicious[i]) continue;
         Set<Transaction> transactions = nodes[i].getProposals();
         System.out.print(transactions.size() + " ");
      }
      System.out.println();
      System.out.println();
      
      
      
      
      
      // pick which nodes are malicious and which are compliant
      nodes = new Node[numNodes];
      for (int i = 0; i < numNodes; i++) {
         if(malicious[i])
            nodes[i] = new MalTwo(p_graph, p_malicious, p_txDistribution, numRounds);
         else
            nodes[i] = new CompliantNode(p_graph, p_malicious, p_txDistribution, numRounds);
      }
      
      // notify all nodes of their followees
      for (int i = 0; i < numNodes; i++)
         nodes[i].setFollowees(followees[i]);
      
      for (int i = 0; i < numNodes; i++) {
         nodes[i].setPendingTransaction(allTx.get(i));
      }
      
      // Simulate for numRounds times
      for (int round = 0; round < numRounds; round++) { // numRounds is either 10 or 20

         // gather all the proposals into a map. The key is the index of the node receiving
         // proposals. The value is an ArrayList containing 1x2 Integer arrays. The first
         // element of each array is the id of the transaction being proposed and the second
         // element is the index # of the node proposing the transaction.
         HashMap<Integer, ArrayList<Integer[]>> allProposals = new HashMap<Integer, ArrayList<Integer[]>>();

         for (int i = 0; i < numNodes; i++) {
            Set<Transaction> proposals = nodes[i].getProposals();
            for (Transaction tx : proposals) {
               if (!validTxIds.contains(tx.id))
                  continue; // ensure that each tx is actually valid

               for (int j = 0; j < numNodes; j++) {
                  if(!followees[j][i]) continue; // tx only matters if j follows i

                  if(allProposals.containsKey(j)) {
                     Integer[] candidate = new Integer[2]; 
                     candidate[0] = tx.id;
                     candidate[1] = i; 
                     allProposals.get(j).add(candidate);
                  } else {
                     ArrayList<Integer[]> candidates = new ArrayList<Integer[]>();
                     Integer[] candidate = new Integer[2]; 
                     candidate[0] = tx.id; 
                     candidate[1] = i;   
                     candidates.add(candidate);
                     allProposals.put(j, candidates);
                  }
               }
            }
         }

         // Distribute the Proposals to their intended recipients as Candidates
         for (int i = 0; i < numNodes; i++) {
            if (allProposals.containsKey(i))
               nodes[i].receiveCandidates(allProposals.get(i));
         }
      }

      // print results
      System.out.println("Malicious node code in MalTwo.java.");
      System.out.println("These are the number of transactions outputted by each of your compliant nodes:");
      for (int i = 0; i < numNodes; i++) {
         if (malicious[i]) continue;
         Set<Transaction> transactions = nodes[i].getProposals();
         System.out.print(transactions.size() + " ");
      }
      System.out.println();
      System.out.println();
      
      
      
      
      
      // pick which nodes are malicious and which are compliant
      nodes = new Node[numNodes];
      for (int i = 0; i < numNodes; i++) {
         if(malicious[i])
            nodes[i] = new MalThree(p_graph, p_malicious, p_txDistribution, numRounds);
         else
            nodes[i] = new CompliantNode(p_graph, p_malicious, p_txDistribution, numRounds);
      }
      
      // notify all nodes of their followees
      for (int i = 0; i < numNodes; i++)
         nodes[i].setFollowees(followees[i]);
      
      for (int i = 0; i < numNodes; i++) {
         nodes[i].setPendingTransaction(allTx.get(i));
      }
      
      // Simulate for numRounds times
      for (int round = 0; round < numRounds; round++) { // numRounds is either 10 or 20

         // gather all the proposals into a map. The key is the index of the node receiving
         // proposals. The value is an ArrayList containing 1x2 Integer arrays. The first
         // element of each array is the id of the transaction being proposed and the second
         // element is the index # of the node proposing the transaction.
         HashMap<Integer, ArrayList<Integer[]>> allProposals = new HashMap<Integer, ArrayList<Integer[]>>();

         for (int i = 0; i < numNodes; i++) {
            Set<Transaction> proposals = nodes[i].getProposals();
            for (Transaction tx : proposals) {
               if (!validTxIds.contains(tx.id))
                  continue; // ensure that each tx is actually valid

               for (int j = 0; j < numNodes; j++) {
                  if(!followees[j][i]) continue; // tx only matters if j follows i

                  if(allProposals.containsKey(j)) {
                     Integer[] candidate = new Integer[2]; 
                     candidate[0] = tx.id;
                     candidate[1] = i; 
                     allProposals.get(j).add(candidate);
                  } else {
                     ArrayList<Integer[]> candidates = new ArrayList<Integer[]>();
                     Integer[] candidate = new Integer[2]; 
                     candidate[0] = tx.id; 
                     candidate[1] = i;   
                     candidates.add(candidate);
                     allProposals.put(j, candidates);
                  }
               }
            }
         }

         // Distribute the Proposals to their intended recipients as Candidates
         for (int i = 0; i < numNodes; i++) {
            if (allProposals.containsKey(i))
               nodes[i].receiveCandidates(allProposals.get(i));
         }
      }

      // print results
      System.out.println("Malicious node code in MalThree.java.");
      System.out.println("These are the number of transactions outputted by each of your compliant nodes:");
      for (int i = 0; i < numNodes; i++) {
         if (malicious[i]) continue;
         Set<Transaction> transactions = nodes[i].getProposals();
         System.out.print(transactions.size() + " ");
      }
   }
}

