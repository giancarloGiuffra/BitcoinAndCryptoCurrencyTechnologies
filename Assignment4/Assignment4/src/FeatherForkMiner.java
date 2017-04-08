import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FeatherForkMiner extends Miner {

   private long myId;

   private boolean revealingStrategy;
   private boolean changingAddressStrategy;
   private HashSet<Long> blackListedTxOwnerIds;

   private int rejoinCutOff;
   HashMap<Block, Boolean> partOfBlackListedChain; 

   private int myHeight;
   private Block myMaxHeightBlock;

   private int blackListedHeight;
   private Block blackListedMaxHeightBlock;

   HashSet<Simulation.Transaction> myTxPool;
   HashSet<Simulation.Transaction> networkTxPool;

   ArrayList<Block> blocksToPublish;

   String message;

   public FeatherForkMiner(Block genesisBlock) {
      super(genesisBlock);

      myId = Simulation.getNewId(this);
      blackListedTxOwnerIds = new HashSet<Long>();

      rejoinCutOff = 5;
      partOfBlackListedChain = new HashMap<Block, Boolean>();

      myHeight = 1;
      myMaxHeightBlock = genesisBlock;

      blackListedHeight = 0;
      blackListedMaxHeightBlock = null;

      myTxPool = new HashSet<Simulation.Transaction>();
      networkTxPool = new HashSet<Simulation.Transaction>();

      blocksToPublish = new ArrayList<Block>();
   }

   public void setRevealingStrategy(boolean parameter) {
      revealingStrategy = parameter;
      // Feather Forking:5,<ids>
      if (message == null && blackListedTxOwnerIds != null) {
         message = "Feather Forking:" + rejoinCutOff;
         for (Long id : blackListedTxOwnerIds) {
            message += "," + id;
         }
      }
   }

   public void setChangingAddressStrategy(boolean parameter) {
      changingAddressStrategy = parameter;
   }

   public void setBlackListedTxOwnerIds(HashSet<Long> blackList) {
      blackListedTxOwnerIds = blackList;
      if (revealingStrategy) {
         message = "Feather Forking";
         for (Long id : blackList) {
            message += "," + id;
         }
      } else {
         message = null;
      }
   }

   // Receive a transaction. 
   public void hearTransaction(Simulation.Transaction tx) {
      if (!blackListedTxOwnerIds.contains(tx.ownerId)) {
         myTxPool.add(tx);
         networkTxPool.add(tx);
      }
   }

   // Hear about a new block found by someone else;
   public void hearBlock(Block block) {
      boolean blackListed = false;
      for (Simulation.Transaction tx : block.transactions) {
         if (blackListedTxOwnerIds.contains(tx.ownerId)) {
            blackListed = true;
            break;
         }
      }
      if (!blackListed) {
         Boolean exists = partOfBlackListedChain.get(block.parent);
         if (exists != null)
            blackListed = exists;
      }
      partOfBlackListedChain.put(block, blackListed);
      if (blackListed) {
         for (Simulation.Transaction tx : block.transactions) {
            networkTxPool.remove(tx);
         }
         if (block.height > blackListedHeight) {
            blackListedHeight = block.height;
            blackListedMaxHeightBlock = block;  
         }
         if (blackListedHeight - myHeight > rejoinCutOff) {
            myHeight = blackListedHeight;
            myMaxHeightBlock = blackListedMaxHeightBlock;
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