import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Exchange implements Serializable {
    private int day;
    private List<Company> companyList;
    private User user;


    public Exchange(User user) {
        companyList = new ArrayList<Company>();
        day = 1;
        this.user = user;
    }

    public void nextDay(){
        day++;
        for(Company y: companyList){
            for (Share s: y.getCompanyStockSpecs().getShareTypes()){

                s.setVolatility();  //changes the volatility for certain type of shares
                double newPrice =  newPriceChangeAmount(s);
                if(!(s instanceof OrdinaryShare)) {
                    s.setPrice(newPrice);
                } else{
                    changeTradingSharePrice(s,newPrice);
                }
            }
        }
        if(day%5 ==0){
            user.receiveDividendPayouts();
        }
    }

    public void placeLimitOrder(Company company, LimitOrder order){
        company.getCompanyStockSpecs().getOrderBook().placeLimitOrder(order);
    }
    //when executing new day the randomly generated price called new price causes orders between that price and old price to be executed.
    public void changeTradingSharePrice(Share s, double newPrice){
        if(newPrice==s.getPrice()){ //todo make it still generate some bids and asks before returning
            return;
        }
        Company companyOfShare = s.getCompany();
        OrderBook companyOrderBook = companyOfShare.getCompanyStockSpecs().getOrderBook();

        boolean isIncreasedPrice = newPrice>s.getPrice();

        double[] marketOrderArray = new double[3];
        marketOrderArray[2] =-1;
        int i = 1;
        LimitOrder nextLimitOrderForExecution = (isIncreasedPrice) ? companyOrderBook.getAskBook().getFirstLimitOrder(): companyOrderBook.getBidBook().getHighestBidLimitOrder();
        while((!isIncreasedPrice && nextLimitOrderForExecution.getPrice() >= newPrice)|| isIncreasedPrice && nextLimitOrderForExecution.getPrice() <= newPrice){   //TODO MAKE THIS RIGHT- SHOULDN'T BE
            nextLimitOrderForExecution = (isIncreasedPrice) ? companyOrderBook.getAskBook().getFirstLimitOrder(): companyOrderBook.getBidBook().getHighestBidLimitOrder();

            int valueOfOrderDependentOnBuyOrSell = (isIncreasedPrice)? (int) nextLimitOrderForExecution.getValue()+1 : nextLimitOrderForExecution.getQuantity();
            marketOrderArray = companyOfShare.getCompanyStockSpecs().getOrderBook().placeMarketOrder(isIncreasedPrice, valueOfOrderDependentOnBuyOrSell,companyOfShare.getCompanyStockSpecs().getMarketTrades(),day);                                                 //execute order
            i++;

        }
        updatePriceAfterMarketOrderExecution( companyOfShare, newPrice, nextLimitOrderForExecution.getQuantity(), isIncreasedPrice);

    }
//simulation, randomly generates different numbers
    public double newPriceChangeAmount(Share s){

            // Instead of a fixed volatility, pick a random volatility
            // each time, between 2 and 10.
        double volatility = s.getVolatility();
        double oldPrice = s.getPrice();

        Random random = new Random();
        //int randomInt = random.nextInt(2)+1;
        double randomDouble = random.nextGaussian();

            double newPrice = oldPrice + (randomDouble * volatility);
            return Math.round((newPrice*2)/2);
        }

    //this method is executed after market order is placed, it does a few things,
    // sets the new price and adds orders to the book to create artificial reaction to price changes and for the longevity of the stock exchange.
    public void updatePriceAfterMarketOrderExecution(Company company, double newPrice, double numOflimitOrdersExecuted, boolean needBids){
        //set company ordinary share price to new price
        double shareOldPrice = company.getOrdinaryShare().getPrice();
       // double roughTransactioInValue = shareOldPrice *sharesTransacted;
        //int randomInt = new Random().nextInt(2) +4;
        double deviance = (needBids)? 0:shareOldPrice/3;
        deviance = Math.round(deviance*2/2);
        company.getOrdinaryShare().setPrice(newPrice);

        company.getCompanyStockSpecs().getOrderBook().addNewOrders(needBids, numOflimitOrdersExecuted, deviance); //adds orders to fill in the gap between current price and previous price
        deviance = (!needBids)? 0:shareOldPrice/3;
        deviance = Math.round(deviance*2/2);
        company.getCompanyStockSpecs().getOrderBook().addNewOrders(!needBids, numOflimitOrdersExecuted, deviance); //adds orders to extend orderbook

    }






    public int getDay() {
        return day;
    }


    public void addCompany(Company plc) {
        companyList.add(plc);
    }


    public List<Company> getCompanyList() {
        return companyList;
    }

    public String[] getCompanyListAsTickerStringArray(){
        String[] tickerList = new String[companyList.size()];
        for (int i = 0; i < companyList.size(); i++){
            tickerList[i] = companyList.get(i).getTicker();
        }
        return tickerList;
    }

    public void establish() {   //makes the companies
        Company s = new Company("Google Inc", "GOOG", 100, 200, 1); //multiplier 1-5
        addCompany(s);
        s.createCompanyStockSpecsAtRunTime();

        Company t = new Company("Amazon Inc", "AMZN", 150,130,2);
        addCompany(t);
        t.createCompanyStockSpecsAtRunTime();

        Company J = new Company("Facebook PLC", "FBC", 150,110,1);
        addCompany(J);
        J.createCompanyStockSpecsAtRunTime();

        Company E = new Company("Netflix", "NET", 120,300,2);
        addCompany(E);
        E.createCompanyStockSpecsAtRunTime();

        Company g = new Company("Discord", "NET", 160,200,2);
        addCompany(g);
        g.createCompanyStockSpecsAtRunTime();

    }

    public String[] getDaysToStringArray(){
        //System.out.println(day);
        String[] s = new String[day];

        for(int i =1; i<= day; i++ ){
            s[i-1] = Integer.toString(i);
        }
        return s;
    }


    public void print(String message) {
        System.out.println(message);
    }


}
