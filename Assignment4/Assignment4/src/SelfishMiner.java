import java.util.ArrayList;
import java.util.HashSet;

public class SelfishMiner extends Miner {
   private long myId;

   private boolean revealingStrategy;
   private boolean changingAddressStrategy;

   private int myHeight;
   private Block myMaxHeightBlock;

   private int networkHeight;
   private Block networkMaxHeightBlock;

   private int state;

   HashSet<Simulation.Transaction> txPool;

   ArrayList<Block> blocksToPublish;
   ArrayList<Block> blocksFound;

   public SelfishMiner(Block genesisBlock) {
      super(genesisBlock);

      myId = Simulation.getNewId(this);

      myHeight = 1;
      myMaxHeightBlock = genesisBlock;

      networkHeight = 1;
      networkMaxHeightBlock = genesisBlock;

      state = 0;

      txPool = new HashSet<Simulation.Transaction>();

      blocksToPublish = new ArrayList<Block>();
      blocksFound = new ArrayList<Block>();
   }

   public void setRevealingStrategy(boolean parameter) {
      revealingStrategy = parameter;
   }

   public void setChangingAddressStrategy(boolean parameter) {
      changingAddressStrategy = parameter;
   }

   // Receive a transaction. 
   public void hearTransaction(Simulation.Transaction tx) {
      txPool.add(tx);
   }

   // Hear about a new block found by someone else;
   public void hearBlock(Block block) {
      // remove all transactions in the block from tx pool
      // this might not be an optimal strategy to handle tx fees but 
      // this selfish miner does not care about tx fees
      for (Simulation.Transaction tx : block.transactions)
         txPool.remove(tx);

      if (block.height <= networkHeight)
         return;

      networkHeight = block.height;
      networkMaxHeightBlock = block;
      int diffHeight = myHeight - networkHeight;
      if (diffHeight < 0) {
         myHeight = networkHeight;
         myMaxHeightBlock = networkMaxHeightBlock;
         state = 0;
         return;
      }

      if (state == 1) {
         if (diffHeight == 0) {
            state = -1;
            blocksToPublish.add(blocksFound.get(0));
            blocksFound.remove(0);
         }
      } else {
         if (diffHeight == state - 1) {
            state -= 2;
            blocksToPublish.add(blocksFound.get(0));
            blocksToPublish.add(blocksFound.get(1));
            blocksFound.remove(0);
            blocksFound.remove(0);
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
      if (revealingStrategy) 
         message = "Mining selfishly";

      Block parent = myMaxHeightBlock;

      Block block = new Block(myId, parent, new HashSet<Simulation.Transaction>(txPool), message);
      txPool.clear();

      myHeight += 1;
      myMaxHeightBlock = block;
      if (state == -1) {
         state = 0;
         blocksToPublish.add(block);
      } else if (state >= 0) {
         state++;
         blocksFound.add(block);
      }
      return block;   
   }
}