import java.io.Serializable;
import java.util.ArrayList;
/*
This is the start of the dataStructure, where each Bid/Ask Book has a node that contains orders of the same price (called samePricedOrders),
The samePricedOrders is a doubly linked list queue and so adheres to the FIFO principle, this would allow execution of orders placed before first.
 */
public class Book implements Serializable {
                                       //"Book" refers to the half of the "books" i.e. bids or asks
        private class BookNode implements Serializable{
            private SameOrderPricesQueue samePricedOrders;    //the data each node has
            private BookNode next;
            private BookNode prev;

            private BookNode(SameOrderPricesQueue ordersOfSamePrice){
                samePricedOrders = ordersOfSamePrice;
                next = null;
                prev = null;
            }
        }

        private BookNode root;                     //perhaps have further optimisation where you remember the 25/100, 50/100
        private BookNode last;                      // 75/100 node which will make it possible to traverse the book faster.
        private double totalBookValue;                            //todo add total book value, total shares
    //use this value to check is a market order would completely clear the book, if so throw an exception, saying the order is too great

        public String[] getBookAsStringArray(){

            String[] array = new String[getNumberOfPrices()];

                BookNode bookPointer = last;
                int i =0;
                while(bookPointer != null){
                    array[i]= bookPointer.samePricedOrders.getQueueDetails();
                    bookPointer = bookPointer.prev;
                    i++;
                }
                return array;
        }
        public int getNumberOfPrices(){
            BookNode bookPointer = last;
            int i =0;
            while(bookPointer != null){
                bookPointer = bookPointer.prev;
                i++;
            }
            return i;
        }


    @Override
    public String toString() {
        String string = "";
        BookNode bookPointer = root;

        while(bookPointer != null) {   //iterates through all the nodes in the book

            string += bookPointer.samePricedOrders;
            bookPointer = bookPointer.next;
        }

        return string;
    }
    /*
    There are two method for adding an order to the orderbook, either an array of LimitOrders or a single LimitOrder
    Though only one method is enough namely 'place limit order', the arraylist order is useful for when a large amount
    of orders are being added- otherwise there would be several method calls.
     */
    public void appendArrayOfOrders(ArrayList<LimitOrder> ordersArray){

            ArrayList<SameOrderPricesQueue> ordersForBookNodes= createQueues(ordersArray);
            for(SameOrderPricesQueue orders: ordersForBookNodes){
                addOrder(orders);
            }

        }

        public void placeSingleLimitOrder(LimitOrder order){
            SameOrderPricesQueue orderQueue = new SameOrderPricesQueue();
            orderQueue.enqueue(order);
            addOrder(orderQueue);
        }



        private ArrayList<SameOrderPricesQueue> createQueues(ArrayList<LimitOrder> orders){
            ArrayList<SameOrderPricesQueue> queues = new ArrayList<SameOrderPricesQueue>();     //list of  all the queues where each queue is of a different price

            SameOrderPricesQueue first = new SameOrderPricesQueue();
                first.enqueue(orders.get(0));       //this adds the first node to the first queue- useful later on for price comparison
                //first.printSamePriceOrders(); //Todo delete after TEST
                //System.out.println("print same price order for first executed above line 55");
                queues.add(first);
            if (orders.size() > 1){
                SameOrderPricesQueue pointer = first;       //Initially the pointer is where orders of the same price go for the first price in the array.
                for(int i = 1; i < orders.size(); i++){         //i starts from 1 since we have previously added the first element in the orders array

                    if(pointer.getPriceOfQueue() != orders.get(i).getPrice()){
                    pointer = new SameOrderPricesQueue();
                    queues.add(pointer);                                        //make a new same price queue, and make that the new pointer
                    }
                 pointer.enqueue(orders.get(i));

                }
                /* //TODO used for testing each queue has the same price orders
                for(SameOrderPricesQueue a: queues){
                    System.out.println("TEST--- new queue");
                    a.printSamePriceOrders();
                }

                 */
            }
            return queues;
        }

        private boolean isEmpty(){
            if (root == null){
                return true;
            }
            return false;
        }

        private void addOrder(SameOrderPricesQueue orders){            //this method adds orders to the book in ascending order of price
            BookNode node = new BookNode(orders);
            if(isEmpty()){
                root = node;
            }
            else {                      //more than one node in the book, DONE FOR ASKS ONLY
                BookNode pointer = root;

                while (pointer != null) {

                        if (pointer.samePricedOrders.getPriceOfQueue() == node.samePricedOrders.getPriceOfQueue()) {   //if the added queue is the same price as the first node
                            joinNodesOfSamePriceOrders(pointer, node);          //joins both nodes
                            break;
                        } else if (pointer.samePricedOrders.getPriceOfQueue() < node.samePricedOrders.getPriceOfQueue()){//assumes the node will be inserted inbetween other BookNodes or at the end

                            if(last==null) {
                                pointer.next = node;
                                last = pointer.next;
                                last.prev = pointer;
                                 //below method should iterate through until pointer is just before the node
                                break;
                            } if(last.samePricedOrders.getPriceOfQueue()<node.samePricedOrders.getPriceOfQueue()){
                                BookNode oldLast = last;
                                oldLast.next = node;
                                last = node;
                                last.prev = oldLast;
                                break;
                            }
                            if(pointer.next.samePricedOrders.getPriceOfQueue() > node.samePricedOrders.getPriceOfQueue()){
                                //inserts node in between pointer
                                BookNode oldNextNode = pointer.next;
                                pointer.next = node;
                                node.prev = pointer;
                                node.next = oldNextNode;
                                oldNextNode.prev = node;
                                break;
                            }

                        } else {                             // make it the new first since (old)first queue price is higher than new node queue price
                            BookNode oldFirst = root;                       //need to make a new last here too
                            root = node;
                            root.next = oldFirst;
                            oldFirst.prev = root;
                            break;
                        }
                        pointer = pointer.next;
                    }  //it is not AskOrders, so it is Bids, so first will be closest to share price
                    // {
            }

        }
        public void orderCancellation(LimitOrder order){      //used for cancelling orders
            BookNode pointer = root;
            while (pointer != null){
                if(pointer.samePricedOrders.getPriceOfQueue() == order.getPrice()){
                    pointer.samePricedOrders.cancellationDequeue(order);
                    if(pointer.samePricedOrders.getShareQuantity() == 0){
                        removeNode(pointer);
                    }
                }
                pointer = pointer.next;
            }
        }

        public void removeNode(BookNode node){
        BookNode pointer = root;

        while(pointer != null){
            if(root == node && root.next == null){      //i.e. has only one node
                root = null;
                return;
            } if(root == node){                         //i.e. is first node
                root =pointer.next;
                root.prev =null; //todo THIS ADDED
                return;
            }
            if(pointer.next == last && pointer.next == node){   //i.e. is last node
                pointer.next = null;
                last = pointer;
                return;
            }
            if(pointer.next == node){       //i.e. in between nodes
                pointer.next = pointer.next.next;
                pointer.next.prev = pointer;    //todo this added
                return;
            }
            pointer = pointer.next;

        }
        }


        /*join the nodes by appending the addition to the base if they are the same price
        then the addition node has addition.next == null- essentially addition node is
         just 1 queue of limit orders of same price
        */
        public void joinNodesOfSamePriceOrders(BookNode base, BookNode addition){
            ArrayList<LimitOrder> list = addition.samePricedOrders.linkedListToArrayList();
            for (LimitOrder order: list) {
                base.samePricedOrders.enqueue(order);
            }

        }

        public double[] executeMarketOrder(boolean isBuy, int value, MarketTrades marketTrades, int exchangeDay) {
            double[] actualOrderExecutionSpecs = new double[4]; //[0] is shares bought and [1] is orderExecutionCost [2]
            if (isBuy) {
                BookNode pointer = root;
                double orderExecutionCost = 0;
                boolean orderExecuted = false;
                double sharesBought = 0;
                double limitOrdersExecuted = 0;
                double lastCompletedPriceOfOrders= -1; //pointer.samePricedOrders.getPriceOfQueue(); TODO THIS WAS REMOVED FOR TEST
                while (pointer != null && !orderExecuted) {

                    LimitOrder order = pointer.samePricedOrders.getFirstLimitOrder();   //initially it is the first order after the following code is executed, the next limit order in the queue will become the first if this one is fully executed
                    if (orderExecutionCost + order.getPrice() <= value) {       //checks to see if shares can be bought from next ask order
                        int sharesboughtFromOrder = 0;
                        for (int i = 0; i < order.getQuantity(); i++) {
                            if (orderExecutionCost + order.getPrice() > value) { //checks to see if next share can be bought from the ask order
                                break;
                            }
                            if(i == order.quantity-1 && pointer.samePricedOrders.getShareQuantity()== order.getQuantity()){      //if this is the first(essentially last order of sameOrderPriceQueue) and this limit order will be
                                                                                                                    //fully executed then this will make it the lastCompeltedPriceOfOrders
                                lastCompletedPriceOfOrders = pointer.samePricedOrders.getPriceOfQueue();
                            }
                            orderExecutionCost += order.getPrice();
                            sharesBought++;
                            sharesboughtFromOrder++;
                        }
                        if (sharesboughtFromOrder != order.getQuantity()) {            //if limit order not fully executed then it updates the order's quantity based on how many shares were bought
                            //System.out.println("this should be executed when less shares sold than ask order" + sharesboughtFromOrder);TODO
                            marketTrades.addMarketTrade(new LimitOrder(order.getPrice(),sharesboughtFromOrder),exchangeDay);
                            order.setQuantity(order.getQuantity() - sharesboughtFromOrder);
                        } else {        //otherwise the ask limit order was completely executed and so is dequeued.
                            marketTrades.addMarketTrade(order,exchangeDay);
                            limitOrdersExecuted++;
                            pointer.samePricedOrders.dequeue(order);
                        }
                        pointer.samePricedOrders.updateShareQuantity();
                    } else{
                        orderExecuted = true;
                    }
                    if (pointer.samePricedOrders.getShareQuantity() == 0) {
                        //System.out.println("executeMarketOrder: pointer share quantity = 0");TODO
                        removeNode(pointer);
                        pointer = pointer.next;
                    }

                }
                actualOrderExecutionSpecs[0] = sharesBought;
                actualOrderExecutionSpecs[1] = orderExecutionCost;
                actualOrderExecutionSpecs[2]= lastCompletedPriceOfOrders;
                actualOrderExecutionSpecs[3] = limitOrdersExecuted;

                return actualOrderExecutionSpecs;

            } else{
                /*
                value is number of shares to be sold
                need to return: how much money made, last price of execution
                iterate from last till root and execute limit orders if they align with '.
                 */
                BookNode pointer = last;
                double executedShares =0;
                double orderExecutionGain = 0;
                double lastCompletedPriceOfOrders = -1;
                double limitOrdersExecuted = 0;

                boolean orderExecuted = false;
                while(pointer != null && !orderExecuted){
                    LimitOrder order = pointer.samePricedOrders.getFirstLimitOrder();   //initially it is the first order after the following code is executed, the next limit order in the queue will become the first if this one is fully executed
                    int sharesSoldToLimitOrder = 0;
                    if (executedShares + 1 <= value) {
                        for (int i =0; i < order.getQuantity(); i++){
                            if(executedShares +1 > value){
                                break;
                            }
                            executedShares++;
                            orderExecutionGain+= order.getPrice();
                            sharesSoldToLimitOrder++;

                            if(i == order.quantity-1 && pointer.samePricedOrders.getShareQuantity()== order.getQuantity()){      //if this is the first(essentially last order of sameOrderPriceQueue) and this limit order will be
                                //fully executed then this will make it the lastCompeltedPriceOfOrders
                                lastCompletedPriceOfOrders = pointer.samePricedOrders.getPriceOfQueue();
                            }

                        }
                        if (sharesSoldToLimitOrder != order.getQuantity()) {            //if limit order not fully executed then it updates the order's quantity based on how many shares were bought
                            //System.out.println("this should be executed when less shares sold than ask order" + sharesSoldToLimitOrder);TODO
                            marketTrades.addMarketTrade(new LimitOrder(order.getPrice(),sharesSoldToLimitOrder),exchangeDay);
                            order.setQuantity(order.getQuantity() - sharesSoldToLimitOrder);
                        } else {        //otherwise the ask limit order was completely executed and so is dequeued.
                            marketTrades.addMarketTrade(order, exchangeDay);
                            limitOrdersExecuted++;

                            pointer.samePricedOrders.dequeue(order);
                        }
                        pointer.samePricedOrders.updateShareQuantity();

                    } else{
                        orderExecuted = true;
                    }
                    if (pointer.samePricedOrders.getShareQuantity() == 0) {
                        //System.out.println("executeMarketOrder: pointer share quantity = 0");TODO
                        removeNode(pointer);
                        pointer = pointer.prev;
                    }
                }

                actualOrderExecutionSpecs[1] = orderExecutionGain;
                actualOrderExecutionSpecs[2]= lastCompletedPriceOfOrders;
                actualOrderExecutionSpecs[3] = limitOrdersExecuted;
                return actualOrderExecutionSpecs;


            }
        }

        public LimitOrder getFirstLimitOrder(){
            return root.samePricedOrders.getFirstLimitOrder();
        }

        public LimitOrder getHighestBidLimitOrder(){
            return last.samePricedOrders.getFirstLimitOrder();
        }

        //else (is ask) so




}
