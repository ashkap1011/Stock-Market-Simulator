import java.io.Serializable;
import java.util.*;
//this class is used for noting down market trades of a company
public class MarketTrades implements Serializable {
    private Company company;
    private Map<Integer, List<LimitOrder>> marketTradeList;
    private List<LimitOrder> orders;

    public MarketTrades(Company company){
        this.company = company;
        orders = new ArrayList<LimitOrder>();
        marketTradeList = new HashMap<Integer, List<LimitOrder>>();
    }

    public void addMarketTrade(LimitOrder order, int exchangeDay){
        if (marketTradeList.containsKey(exchangeDay)){
            marketTradeList.get(exchangeDay).add(order);
        } else {
            marketTradeList.put(exchangeDay, new ArrayList<>(){{ this.add(order);}});
        }

    }

    public String getExecutedOrders(){
        String s = "";
        for(Integer exchgeDay: marketTradeList.keySet()){
            s += "DAY " + exchgeDay;
            for(LimitOrder order: marketTradeList.get(exchgeDay)){
               s+= order + "\n";
            }
        }

        return s;
    }


    public String getMarketOrdersForSpecificDay(int day){
        String str = company + " Day: " + day + ": \n" ;

        if(marketTradeList.get(day) != null){
        for(LimitOrder order: marketTradeList.get(day)){
            str+= order + "\n";
            }
        }
        return str;

    }


}
