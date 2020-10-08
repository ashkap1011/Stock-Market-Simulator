import java.io.Serializable;
import java.util.Scanner;
//this class is for placing/executing limit and market orders dealing specifically with ordinary shares
public class Trade implements Serializable {
    private Exchange exchange;
    private User user;

    public Trade(Exchange exchg, User test){
        exchange = exchg;
        user = test;
    }
    //todo throw exception
    public void placeLimitOrder(Company company,double price,int quantity) throws NotEnoughFunds{
        LimitOrder order = new LimitOrder(price, quantity);
        boolean isBid = (company.getOrdinaryShare().getPrice() > price)? true:false;
        if (user.checkFundsAvailableForLimitOrder(company, order)) { //check funds available
            exchange.placeLimitOrder(company, order);   //places the order in the Company's orderbook
            user.addLimitOrder(company, order);         //adds order to user's active limit orders
            user.printActiveOrders();                   //prints user's active limit orders
        } else{
            String errorMessage;
            if(!isBid){
                int numberOfShares = user.getPortfolio().numberOfSharesAvailable(company);
                errorMessage = "For " + company.getName() +" you have " + numberOfShares + " shares available for selling";
            }else{
                errorMessage = "you don't have the funds to place that limit Order";
            }
            throw new NotEnoughFunds(errorMessage);
        }

    }
    //todo cancel order doesn't work correctly 
    public void executeOrderCancellation(Company company, LimitOrder order){
        //if (price==-1 || quantity==-1){                                            //user has only one active limit order or has only one limit order from that company
            //LimitOrder order = user.getActiveLimitOrder(company, price, quantity);   //gets limit order from the user's active limit order

            user.removeActiveLimitOrder(company, order);                            //remove from user's active limit order
            user.unlockAssets(company, order);                                      //unlock resources for the user (remove from lockedAssets and put it back in the balance/stock portfolio)
            company.getCompanyStockSpecs().getOrderBook().cancelOrder(order);      //remove from orderbook
       // }

        //todo also update the locked funds/stock
    }
        //TODO this method can be rewritten to remove repeated code
    public void placeMarketOrder(Company company, Boolean isBuy, int value) throws NotEnoughFunds {      //value can refer to money if isBuy or Amount of shares.
        double[] marketOrderSpecs;
        if(isBuy){
            if(user.getAvailableBalance()>= value && value >= company.getOrdinaryShare().getPrice()){
               marketOrderSpecs= company.getCompanyStockSpecs().getOrderBook().placeMarketOrder(isBuy, value,company.getCompanyStockSpecs().getMarketTrades(),exchange.getDay());                                                 //execute order
                   //this should be the current price

                double sharesBought = marketOrderSpecs[0];
                double orderExecutionValue = marketOrderSpecs[1];
                double newCurrentPrice = marketOrderSpecs[2];
                double limitOrderExecuted = marketOrderSpecs[3];

                double averagePricePerShare= orderExecutionValue/sharesBought;
                user.postMarketOrderUpdate(company, isBuy, orderExecutionValue, sharesBought);
                exchange.updatePriceAfterMarketOrderExecution(company,newCurrentPrice,limitOrderExecuted, isBuy);



            }else {
                String errorMessage = (user.getAvailableBalance()< value) ? "You lack the funds for the market order": "Minimum order value is: " + company.getOrdinaryShare().getPrice();
                throw new NotEnoughFunds(errorMessage);
            }
                                                                                            //check balance is this much or more
        } else {  //else it is ask so check shares are available in the user's portfolio
            if(user.getPortfolio().checkStockAvailability(company, value)){
                marketOrderSpecs= company.getCompanyStockSpecs().getOrderBook().placeMarketOrder(isBuy, value,company.getCompanyStockSpecs().getMarketTrades(),exchange.getDay());


                double sharesSold = value;
                double orderExecutionValue = marketOrderSpecs[1];
                double newCurrentPrice = marketOrderSpecs[2];
                double limitOrderExecuted = marketOrderSpecs[3];
                double averagePricePerShare= orderExecutionValue/value;
                user.postMarketOrderUpdate(company, isBuy, orderExecutionValue, value);
                exchange.updatePriceAfterMarketOrderExecution(company,newCurrentPrice,limitOrderExecuted, isBuy);

            }
            else{
                int numberOfShares = user.getPortfolio().numberOfSharesAvailable(company);
                String errorMessage = "For " + company.getName() +" you have " + numberOfShares + " shares available for selling";
                throw new NotEnoughFunds(errorMessage);
            }
            //check shares in portfolio
            //then execute order
            //
        }


                                                        // -> execute order--> change price of share, print orders executed and update user if need be.
                                                        ////print all executed orders
        //
    }

    public void print(String message){
        System.out.println(message);
    }

    public String readInput(){
        Scanner reader = new Scanner(System.in);
        return reader.nextLine();
    }


}
