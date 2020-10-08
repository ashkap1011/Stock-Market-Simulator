import java.io.Serializable;
import java.util.*;


class User implements Serializable {
    private String name;
    private double availableBalance =0;
    private Portfolio portfolio;
    private Map<Company, List<LimitOrder>> activeLimitOrders;
    private double initialBalance =0;
    private double additionalTopups=0;

    public User(String name){
	this.name = name;
	this.portfolio = new Portfolio();
    activeLimitOrders = new HashMap<Company,List<LimitOrder>>();
    }

    public void printActiveOrders(){
        for (Map.Entry<Company, List<LimitOrder>> entry : activeLimitOrders.entrySet()) {
            //System.out.println(entry.getKey().getOrdinaryShare().getShareInfo() + "with the following limit orders:");
            for (LimitOrder order: entry.getValue()){
                String orderType = ((entry.getKey().getOrdinaryShare().getPrice()<order.getPrice()) ? "ASK: " : "BID: ");
                //System.out.println(orderType + order);
            }//todo maybe later print percentage away from execution
        }
    }

    //TODO OVERRIDE TOEQUAL METHOD Company.
    public void addLimitOrder(Company company, LimitOrder order){
        if (activeLimitOrders.containsKey(company)){
            activeLimitOrders.get(company).add(order);
        } else {
            activeLimitOrders.put(company, new ArrayList<>(){{ this.add(order);}});
        }
        lockAssets(company, order); //updates the account, keeping the relavant funds locked from the user
    }

    public void lockAssets(Company company, LimitOrder order){            //used for changing all the balance and locking funds
        if(isAsk(company,order)){                                           //locks the stock, removing them from portfolio
            Stock userStock = portfolio.getOrdinaryStock(company);          //gets the relevant stock from user's portfolio
            portfolio.reduceStock(company.getOrdinaryShare(), order.getQuantity());//removes the relavant amount of stock (i.e. quantity of share depending on order)
            //userStock.setQuantity(userStock.getQuantity()-order.quantity);
            //TODO ABOVE LINE WAS previous CODE NOT THE LINE Above
            portfolio.addToLockedStock(new Stock(userStock.getShare(), order.quantity));    //adds the removed funds into the locked stock.

        } else{                              // if not ask then bid so updates balance
            availableBalance -= order.getValue();
            portfolio.setLockedFunds(portfolio.getLockedFunds() + order.getValue());
        }
    }

    public boolean checkFundsAvailableForLimitOrder(Company company, LimitOrder order){          //TODO CHECK SHOULD INCLUDE EXCHANGE FEES- SEND MULTIPLIER E.G. 1.01 FOR 1% FEES
        if(isAsk(company, order)){                                            // if Ask order, user must have enough stock to sell
            if(portfolio.checkStockAvailability(company, order.getQuantity())){
                return true;
            } else{
                //System.out.println("sorry the funds(shares) aren't in your account");   //todo remove after testing
                return false;
            }
        } else{                                //if it isn't ask then Bid, check enough balance
            if(order.getValue() <= availableBalance){
                return true;
            } else{
               // System.out.println("sorry the funds aren't in your account ");    //todo remove after testing
                return false;
            }
        }
    }
    public boolean isAsk(Company company, LimitOrder order){

        if(company.getOrdinaryShare().getPrice() < order.getPrice()){   //therefore asks
            return true;
        } else{
            return false;
        }
    }

    public void postMarketOrderUpdate(Company company, boolean isBuy, double orderExecutionValue, double sharesTransacted){
        if(isBuy){
            availableBalance -= orderExecutionValue;         //todo make this a method called update user account so it works for both types of market orders
            getPortfolio().addStock(new Stock(company.getOrdinaryShare(), (int) sharesTransacted));//add shares to portfolio
        } else{
            availableBalance += orderExecutionValue;
            getPortfolio().reduceStock(company.getOrdinaryShare(), (int) sharesTransacted);
        }
    }

    public LimitOrder getActiveLimitOrder(Company company, double price, double quantity){
        if(activeLimitOrders.size() ==1 && activeLimitOrders.get(company).size() ==1){ //satisfies 2 conditions: user has only 1 limit order, or they have have one limit order from the company
            return activeLimitOrders.get(company).get(0);
        } else { //iterate through companies when company matches, check price and id
            for(Company companyKey: activeLimitOrders.keySet()){
                if(companyKey == company){
                    for (LimitOrder order: activeLimitOrders.get(companyKey)){
                        if(order.getPrice() == price && order.getQuantity() == quantity){
                            return order;
                        }
                    }
                }
            }
        }
        //System.out.println("User[class], getActiveLimitOrder(): sorry don't have the limit order");
        return null;
    }

    public String getActiveLimitOrdersAsString(){
        String str = "";
        for(Company companyKey: activeLimitOrders.keySet()){
            for(LimitOrder order: activeLimitOrders.get(companyKey)){
                String bidOrAsk = (companyKey.getOrdinaryShare().getPrice() > order.getPrice())? "BID: ": "ASK: ";
                str += companyKey.getName() + ": " + bidOrAsk + order + "\n";
            }
        }
        return str;
    }

    public double[] getActiveLimitOrdersPriceArrayForSpecificCompany(Company company){
        if(activeLimitOrders.keySet().contains(company)){
            double[] array = new double[activeLimitOrders.get(company).size()];
            int i=0;
            for(LimitOrder order: activeLimitOrders.get(company)){
                array[i] = order.getPrice();
                i++;
            }
            return array;
        }
        return new double[0];
    }

    public int getNumberOfActiveLimitOrders(){
        int total = 0;
        for(Company companykey: activeLimitOrders.keySet()){
            total += activeLimitOrders.get(companykey).size();
        }
        return total;
    }

    public LimitOrder getActiveLimitOrderWithIndex(int indexOfLimitOrder) throws IndexOutOfBoundsException{
        int i = 0;
        for(Company companyKey: activeLimitOrders.keySet()){
            for(LimitOrder order: activeLimitOrders.get(companyKey)){
                if(i == indexOfLimitOrder){
                    return order;
                }
                i++;
            }
        }
        throw new IndexOutOfBoundsException();
    }
    public String getActiveLimitOrderWithIndexToString(int indexOfLimitOrder){
        int i = 0;

        for(Company companyKey: activeLimitOrders.keySet()){
            for(LimitOrder order: activeLimitOrders.get(companyKey)){
                if(i == indexOfLimitOrder){
                    String bidOrAsk = (companyKey.getOrdinaryShare().getPrice() > order.getPrice())? "BID: ": "ASK: ";
                    return companyKey.getName() + ": " + bidOrAsk + order;
                }
                i++;
            }
        }
        return null;
    }

    public Company getCompanyfromLimitOrder(LimitOrder orderParameter){
        for(Company companyKey: activeLimitOrders.keySet()){
            for(LimitOrder order: activeLimitOrders.get(companyKey)){
                if(order == orderParameter){
                    return companyKey;
                }
            }
        } return null;
    }

    public void unlockAssets(Company company, LimitOrder order){     //remove stock from locked stock and place back in the user's portfolio
        if(isAsk(company, order)){
            Stock unlockedStock =  portfolio.removeLockedStock(new Stock(company.getOrdinaryShare(), order.quantity));     //remove from locked shares
            portfolio.addStock(unlockedStock);                                                                            //and is added back to portfolio
        } else{                                                          // adds the money from locked funds into the balance
            portfolio.removeLockedFunds(order.getValue());
            availableBalance += order.getValue();
        }
    }

    public void removeActiveLimitOrder(Company company, LimitOrder order){
        Iterator<LimitOrder> it = activeLimitOrders.get(company).iterator();
        while(it.hasNext()){
            if(it.next().equals(order)){
                it.remove();
            }
        }

        if(activeLimitOrders.get(company).size() ==0){
            activeLimitOrders.remove(company);
        }
    }


    public double getTotalUserPortfolioValuation(){
        double total = portfolio.getTotalPortfolioValuation() + availableBalance;
        for(Company companykey: activeLimitOrders.keySet()){
            for(LimitOrder order: activeLimitOrders.get(companykey)){
                total+= order.getValue();
            }
        }
        return total;
    }

    public String getReturnsOnInvestmentAsString(){
        String str = "";
        if(getTotalUserPortfolioValuation()> additionalTopups+initialBalance){
            str+="+";
        } else if (getTotalUserPortfolioValuation()< additionalTopups+initialBalance){
            str+="-";
        } else{
            str+="";
        }
        str +=Double.toString(Math.round(Math.abs(getTotalUserPortfolioValuation()-initialBalance-additionalTopups)));
        return str;
    }

    public void receiveDividendPayouts(){
        availableBalance += portfolio.appreciateDividends();
    }

    public Map<Company, List<LimitOrder>> getActiveLimitOrders() {
        return activeLimitOrders;
    }

    public void deposit(int topup) throws IllegalArgumentException{
        if(topup <= 0){
            throw new IllegalArgumentException("Please enter an integer value greater than 0");
        }
        if(availableBalance ==0){
            initialBalance = topup;
            this.availableBalance += topup;
        return;
        }
	    this.availableBalance += topup;
        this.additionalTopups += topup;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(double availableBalance) {
        this.availableBalance = availableBalance;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }
}

