import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PreventHighTxFeeMiner extends Miner {

   private long myId;

   private boolean revealingStrategy;
   private boolean changingAddressStrategy;
   private long feeCutOff;

   private int rejoinCutOff;
   HashMap<Block, Boolean> partOfHighFeeChain; 

   private int myHeight;
   private Block myMaxHeightBlock;

   private int highFeeHeight;
   private Block highFeeMaxHeightBlock;

   HashSet<Simulation.Transaction> myTxPool;
   HashSet<Simulation.Transaction> networkTxPool;

   ArrayList<Block> blocksToPublish;

   public PreventHighTxFeeMiner(Block genesisBlock) {
      super(genesisBlock);

      myId = Simulation.getNewId(this);

      rejoinCutOff = 5;
      partOfHighFeeChain = new HashMap<Block, Boolean>();

      myHeight = 1;
      myMaxHeightBlock = genesisBlock;

      highFeeHeight = 0;
      highFeeMaxHeightBlock = null;

      myTxPool = new HashSet<Simulation.Transaction>();
      networkTxPool = new HashSet<Simulation.Transaction>();

      blocksToPublish = new ArrayList<Block>();
   }

   public void setRevealingStrategy(boolean parameter) {
      revealingStrategy = parameter;
   }

   public void setChangingAddressStrategy(boolean parameter) {
      changingAddressStrategy = parameter;
   }

   public void setFeeCutOff(long cutOff) {
      feeCutOff = cutOff;
   }

   // Receive a transaction. 
   public void hearTransaction(Simulation.Transaction tx) {
      myTxPool.add(tx);
      networkTxPool.add(tx);
   }

   // Hear about a new block found by someone else;
   public void hearBlock(Block block) {
      boolean prevent = false;
      for (Simulation.Transaction tx : block.transactions) {
         if (tx.fee > feeCutOff) {
            prevent = true;
            break;
         }
      }
      if (!prevent) {
         Boolean exists = partOfHighFeeChain.get(block.parent);
         if (exists != null)
            prevent = exists;
      }
      partOfHighFeeChain.put(block, prevent);
      if (prevent) {
         for (Simulation.Transaction tx : block.transactions) {
            networkTxPool.remove(tx);
         }
         if (block.height > highFeeHeight) {
            highFeeHeight = block.height;
            highFeeMaxHeightBlock = block;  
         }
         if (highFeeHeight - myHeight > rejoinCutOff) {
            myHeight = highFeeHeight;
            myMaxHeightBlock = highFeeMaxHeightBlock;
            myTxPool = new HashSet<Simulation.Transaction>(networkTxPool);
         }
      } else {
         for (Simulation.Transaction tx : block.transactions) {
            myTxPool.remove(tx);
            networkTxPool.remove(tx);
         }
         if (block.height > myHeight) {
            myHeight = block.height;
            myMaxHeightBlock = block;  
         }
      }
   }

   public ArrayList<Block> publishBlock() {
      return blocksToPublish;
   }

   public Block findBlock() {
      if (changingAddressStrategy) 
         myId = Simulation.getNewId(this);

      String message = null;
      if (revealingStrategy) {
         message = "Preventing High Fees of " + feeCutOff + " and rejoinCutOff is " + rejoinCutOff;
      }

      Block parent = myMaxHeightBlock;

      Block block = new Block(myId, parent, new HashSet<Simulation.Transaction>(myTxPool), message);
      myTxPool.clear();
      networkTxPool.clear();

      myHeight += 1;
      myMaxHeightBlock = block;
      blocksToPublish.add(block);
      return block;   
   }
}