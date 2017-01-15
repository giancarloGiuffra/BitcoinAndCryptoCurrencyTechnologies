final public class Transaction {
   final int id;

   // if tx id is x, then you can "retrieve" the tx as "new Transaction(x)"
   public Transaction(int id) {
      this.id = id;
   }

   public static void main(String[] args) {
      System.out.println("hello world");
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final Transaction other = (Transaction) obj;
      if (this.id != other.id) {
         return false;
      }
      return true;
   }
   
   @Override
   public int hashCode() {
      return id;
   }

}