import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//this class has the user's stock portfolio and also locked stock/funds for when a limit order is placed.
class Portfolio implements Serializable {
    private List<Stock> stockPortfolio;
    private double lockedFunds;
    private List<Stock> lockedStock;

    public Portfolio() {
        stockPortfolio = new ArrayList<Stock>();
        lockedFunds = 0;
        lockedStock = new ArrayList<Stock>();
    }

    public String getPortfolioAsString(){
        String str = "";
        for(Stock s: stockPortfolio){
            str += s + "\n";
        }
        return str;

    }

    public boolean checkStockAvailability(Company company, int quantity){   //check the existence of the stock and enough quantity
        for(Stock stock: stockPortfolio){
            if(stock.getShare()==company.getOrdinaryShare()
                    && stock.getQuantity()>= quantity){
                return true;
            }
        }
        return false;
    }
    public int numberOfSharesAvailable(Company company){
        for(Stock stock: stockPortfolio){
            if(stock.getShare() == company.getOrdinaryShare()){
                return stock.getQuantity();
            }
        }
        return 0;
    }

    public void addToLockedStock(Stock stock){
        lockedStock.add(stock);
    }

    public Stock removeLockedStock(Stock stock){
        for(int i = 0; i <lockedStock.size(); i++){
            if(stock.equals(lockedStock.get(i))){
                return lockedStock.remove(i);
            }
        } return null; //todo throw exception
    }

    public void removeLockedFunds(double deduct){
        lockedFunds -= deduct;
    }



    public List<Stock> getPortfolioList() {
        return stockPortfolio;
    }


    public void setStockPortfolio(List<Stock> stockPortfolio) {
        this.stockPortfolio = stockPortfolio;
    }
    public Stock getOrdinaryStock(Company company){
        for(Stock stock: stockPortfolio){
            if(stock.getShare() == company.getOrdinaryShare()){
                return stock;
            }
        } return null;
    }

    public void addStock(Stock stk){
        boolean added = false;
        for(int i =0; i<stockPortfolio.size(); i++){
            if(stockPortfolio.get(i).getShare() == stk.getShare()){
                stockPortfolio.get(i).setQuantity(stockPortfolio.get(i).getQuantity()+ stk.getQuantity());
                added = true;
            }
        }
        if(!added) {
            stockPortfolio.add(stk);
        }
    }

    public void reduceStock(Share shr, int quantity){
        for (Stock stock : stockPortfolio){
            if(stock.getShare().equals(shr)){
                stock.setQuantity(stock.getQuantity()-quantity);
            }
        }
        Iterator<Stock> it = stockPortfolio.iterator();
        while(it.hasNext()){
            if(it.next().getQuantity()==0){
                it.remove();
            }
        }
    }

    public double getLockedFunds() {
        return lockedFunds;
    }

    public void setLockedFunds(double lockedFunds) {
        this.lockedFunds = lockedFunds;
    }

    public double getTotalPortfolioValuation(){
        double total =0;
        for(Stock stk : stockPortfolio){
            total += stk.getQuantity()*stk.getShare().getPrice();
        }
        return total;
    }

    public double appreciateDividends(){
        double payout =0;
        for(Stock stk: stockPortfolio){
            payout += stk.getQuantity()*stk.getShare().getPrice()*stk.getShare().getDividend()/500;
        }
        System.out.println("THIS IS THE PAYOUT AFTERWARDS: " + payout );
        return payout;
    }



}
