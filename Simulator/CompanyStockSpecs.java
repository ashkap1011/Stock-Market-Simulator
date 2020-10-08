import com.sun.jdi.event.ExceptionEvent;

import java.io.Serializable;
import java.util.*;

class CompanyStockSpecs implements Serializable { //stores the types of shares in the company, the orderbook and markettrades for the company
	private List<Share> shareTypes;
	private OrderBook orderBook;
	private MarketTrades marketTrades;

	public CompanyStockSpecs(){
	    shareTypes = new ArrayList<Share>();
	}

    public void createStockSpec(Company comp, double initialPrice,int multiplier){				//subsitution principle
	Share pre = new PreferredShare(comp, initialPrice, multiplier);
	Share ord = new OrdinaryShare(comp,initialPrice,multiplier);
	Share un = new UnlistedShare(comp,initialPrice,multiplier);
	shareTypes.add(pre);
	shareTypes.add(ord);
	shareTypes.add(un);
	executeRunTimeMethods();
	orderBook = new OrderBook(ord, 100);
	orderBook.generateInitialOrderBook();
	marketTrades = new MarketTrades(comp);
	}

	public void executeRunTimeMethods(){
		for (Share s :shareTypes){
			s.setVolatility();
		}
	}

	public MarketTrades getMarketTrades() {
		return marketTrades;
	}

	public double getPreferredSharePrice() throws NotAvailable{
		for(Share shr: shareTypes){
			if(shr instanceof PreferredShare){
				return shr.getPrice();
			}
		}
		throw new NotAvailable("Price Not Available");
	}

	public List<Share> getShareTypes() {
		return shareTypes;
	}
	public OrderBook getOrderBook() {
		return orderBook;
	}
	@Override
	public String toString() {
		String str = "With the following shares \n";
		for (Share s : shareTypes){
			str = str + s + "\n";
		}
		return str;
	}


}
