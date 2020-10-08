
/* This Queue Data Structure is implemented as a doubly linked list,
   Doubly linked list is used so that orders can be cancelled and hence removed -potentially from the middle of the queue.
   This feature can be achieved with a singly linked list but the code for removing an order could be quite messy.
   Being a queue it follows the FIFO principle to adhere to the realism that orders should be executed in order of their placement
*/

import java.io.Serializable;
import java.util.ArrayList;
public class SameOrderPricesQueue implements Serializable {                      //A queue which holds all the limit orders for the same price
    private PriceQueueNode first;
    private PriceQueueNode last;
    private int shareQuantity;    // refers to the sum of shares in all orders of this price queue

    private class PriceQueueNode implements Serializable {
        private LimitOrder order;
        private PriceQueueNode next;
        private PriceQueueNode prev;

        private PriceQueueNode(LimitOrder oorder){
            order = oorder;
            next = null;
            prev = null;
        }
    }

    public SameOrderPricesQueue(){          //constructor
        first = null;
        last = null;
        shareQuantity = 0;
    }

    public String getQueueDetails(){     //Used to print orderbook maybe


        return "Price: " + getPriceOfQueue() + "      Quantity: " + shareQuantity + "\n";

    }

    @Override
    public String toString() {
        String string = "";
        PriceQueueNode pointer = first;
        while(pointer !=null){
            string += pointer.order +"\n";
            pointer = pointer.next;
        }

        return string;
    }
    /* TODO old print method
     public void printSamePriceOrders(){
        PriceQueueNode pointer = first;
        while(pointer !=null){
            System.out.println(pointer.order);

            pointer = pointer.next;
        }
    }
     */

    private boolean isEmpty(){
        if (first == null){
            return true;
        }
        return false;
    }

    public void enqueue(LimitOrder order){
        PriceQueueNode node = new PriceQueueNode(order);
        if (first == null){
            first = node;
            shareQuantity += order.getQuantity();
            //System.out.println("first node set as" + first.order);
        }
        else if (last == null) {               //i.e. assumes only one node in the queue
           // System.out.println("last node set");
            last = node;
            //System.out.println(last.order);
            shareQuantity += order.getQuantity();
            first.next = last;
            last.prev = first;

            //System.out.println("last =" + last.order);
        } else {
            PriceQueueNode oldLast = last;
            oldLast.next = node;                //if last is not null (i.e. more than 2 nodes in the queue)
            last = node;
            last.prev = oldLast;
            shareQuantity += order.getQuantity();
            //node.next = null; TODO MAYBE KEEP THIS?
           // System.out.println("previous to last" + last.prev.order);
            //System.out.println("last =" + last.order);
        }

    }

    public LimitOrder cancellationDequeue(LimitOrder order){     //will remove a cancelled item
        if (isEmpty()){
              //if cancellation, check the id matches and calls the method again
            return null;    //todo throw exception here and next branch
        }
        if(!isNodePresent(first, order))   {
            return null;                                        //perhaps check if the id is of the limit order is the same
            // THROW EXCEPTION
        }
        else{
           return dequeue(order);
        }
    }
//todo this dequeu method could be simpliefies e.g. only one statement of returning at the end maybe.
    public LimitOrder dequeue(LimitOrder order){        //removes the node from the queue
        PriceQueueNode current = first;
        while(current != null){
            if (current.order.getId() == order.getId()){

                if (current == first && first.next==null) {     //if node is only node in price queue
                    LimitOrder dequeueOrder = current.order;
                    first = null;
                    shareQuantity -= order.getQuantity();
                    //System.out.println("current is first and nothing else in the price queue");
                    return dequeueOrder;
                } else if(current == first){                    //if node is first node in the price queue
                    first = current.next;
                    shareQuantity -= order.getQuantity();
                    return current.order;
                } else if (current==last) {                     //if node is last
                    current.prev.next = null;
                    last = current.prev;
                    shareQuantity -= order.getQuantity();
                    return current.order;
                } else{                                         //if node is in between other nodes
                    current.prev.next = current.next;
                    current.next.prev = current.prev;
                    shareQuantity -= order.getQuantity();
                    return current.order;
                }

                                                    //first.prev = null; causes NullPointer

            }
            current = current.next;
        }
        return null; //this should never be reached. (hopefully)
    }

    public boolean isNodePresent(PriceQueueNode node, LimitOrder order)     //when the method is called it is given the first node initially
    {
        if (node == null) {                 //since it is a recursive function, if the node is not present
            return false;                   //then it will eventually check the last node.next which would be null - returning false;
        }
        if (node.order.getId() == order.getId()) {
            return true;
        }
        return isNodePresent(node.next, order);
    }

    public void updateShareQuantity(){
        PriceQueueNode pointer = first;
       int quantity = 0;
        while(pointer != null){
            quantity += pointer.order.getQuantity();
            pointer = pointer.next;
        }
        setShareQuantity(quantity);
    }



    public ArrayList<LimitOrder> linkedListToArrayList(){               //turns the SameOrderPriceQueue into an ArrayList
        ArrayList<LimitOrder> arrList = new ArrayList<LimitOrder>();
        PriceQueueNode node = new PriceQueueNode(first.order);
        while(node != null){
            arrList.add(node.order);
            node = node.next;
        }
        return arrList;

    }

    public double getPriceOfQueue(){
        return first.order.getPrice();
    }
    public LimitOrder getFirstLimitOrder(){
        return first.order;
    }

    public LimitOrder getLastLimitOrder(){
        return last.order;
    }

    public PriceQueueNode getFirst() {
        return first;
    }

    public void setShareQuantity(int shareQuantity) {
        this.shareQuantity = shareQuantity;
    }

    public int getShareQuantity() {
        return shareQuantity;
    }
}


