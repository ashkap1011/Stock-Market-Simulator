import java.io.Serializable;
import java.util.*;

//holds the the different books for a company
public class OrderBook implements Serializable {
    protected Share share;
    private final int ORDERS;         //total number of buyers and sellers initially generated (each)
    Random random;
    private Book askBook;
    private Book bidBook;

    public OrderBook(Share shr, int oorder){
        share = shr;
        ORDERS = oorder;
        random = new Random();
    }

    public void generateInitialOrderBook(){            //generate data based on the pirce, then input that data to the datastrcuture which would hold all the orders

       boolean isBid = true;
       ArrayList<LimitOrder> askOrders = generateData(!isBid,ORDERS);
       askBook = new Book();
       askBook.appendArrayOfOrders(askOrders);
       ArrayList<LimitOrder> bidOrders = generateData(isBid,ORDERS);
       bidBook = new Book();
       bidBook.appendArrayOfOrders(bidOrders);
       //createbids and asks --> then feed it into the linked list data structure
        //initliase the book

    }

    public void placeLimitOrder(LimitOrder order){
        if(isAskOrder(order.getPrice())){
            askBook.placeSingleLimitOrder(order);
        } else{
            bidBook.placeSingleLimitOrder(order);
        }
    }

    public double[] placeMarketOrder(boolean isBuy, int value, MarketTrades marketTrades,int exchangeDay){
        double[] marketOrderSpecs;
        if(isBuy){      //if isBuy then it should work its way through the asks' orderbook
             marketOrderSpecs = askBook.executeMarketOrder(isBuy,value, marketTrades,exchangeDay);
        }else{
             marketOrderSpecs = bidBook.executeMarketOrder(isBuy, value, marketTrades,exchangeDay);
        }
        if((int) marketOrderSpecs[2] == -1){
            marketOrderSpecs[2] = share.getPrice();
        }

        return marketOrderSpecs;
    }

    public boolean isAskOrder(double price){
        if(price > share.getPrice()){
            return true;
        }
        return false;
    }

    public void cancelOrder(LimitOrder order){
        if(isAskOrder(order.getPrice())){
            askBook.orderCancellation(order);
        } else{
            bidBook.orderCancellation(order);
        }

    }

    public String[] getBidsBookAsStringArray(){
        return bidBook.getBookAsStringArray();
    }
    public String[] getAsksBookAsStringArray(){

        String[] ary = askBook.getBookAsStringArray();
        String[] reversedArray = new String[ary.length];
        int j= ary.length-1;
        for(int i=0; i < ary.length; i++){
            reversedArray[i] = ary[j];
            j--;
        }

        return reversedArray;
    }

    public void addNewOrders(boolean isBid, double minimumOrders, double dev){ //, double dev
        int orderToCreate = random.nextInt(50) + 20;

        ArrayList<LimitOrder> orders = generateData(isBid,orderToCreate);

            for (LimitOrder order: orders){
               // System.out.println("first price" + order.getPrice());
                order.appreciateDevianceOnPrice(dev);
                //System.out.println("after deviance price" + order.getPrice());
            }
       // }
        appendOrders(isBid, orders);
    }

    public void appendOrders(boolean isBid, ArrayList<LimitOrder> orders){
        if(isBid){
            bidBook.appendArrayOfOrders(orders);
        } else{
            askBook.appendArrayOfOrders(orders);
        }

    }



    public ArrayList<LimitOrder> generateTestOrders(){
        ArrayList<LimitOrder> list = new ArrayList<LimitOrder>();

        for (int i =0; i< 5; i++){
            for(int j =1; j<=4; j++){
                list.add(new LimitOrder(10+i,j));
            }
        }
        System.out.println("these are the generated orders");
        for (LimitOrder s: list){
            System.out.println(s);
        }
        return list;

    }

    public ArrayList<LimitOrder> generateData(boolean isBid, int requiredOrders){         //will generate bids given -1, and asks given 1

        ArrayList<LimitOrder> list = new ArrayList<LimitOrder>();

        for (int i = 0; i< requiredOrders; i++) {
            double price = generateRandomPrice(isBid);
            int quantity = random.nextInt(6)+1;
            list.add(new LimitOrder(price,quantity));
        }
        return list;
    }

    public double generateRandomPrice(Boolean isBid){
        double currPrice = share.getPrice();
        double StdDev = currPrice/2;
        double value = -1;

        while((isBid && ( value < 0 || value >= currPrice)) || !isBid && value <=currPrice ) {
            value = (random.nextGaussian() * StdDev) + currPrice;
            value = Math.round(value*2)/2.0;
        }
        return value;
    }

    public Book getAskBook() {
        return askBook;
    }

    public Book getBidBook() {
        return bidBook;
    }
}










