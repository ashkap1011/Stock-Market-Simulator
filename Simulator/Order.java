import java.io.Serializable;

public class Order implements Serializable { //this class executes the order for preferred and unlisted shares
    private Exchange exchange;
    private User user;

    public Order(Exchange exchg, User test){
        exchange = exchg;
        user = test;
    }
    //executes buy order
    public void buyOrder(Company comp, String type, int quantity ) throws NotEnoughFunds{
        Share shr = getShareType(comp, type);
        boolean enoughFunds = checkEnoughFunds(shr, quantity);
        if (enoughFunds){
            executeBuyOrder(shr,quantity);
        }
        else {
            throw new NotEnoughFunds("You don't have enough funds for the order");
        }
    }
    //executes sell order
    public void sellOrder(Company comp,String type, int quantity) throws NotEnoughFunds{
        Share shr = getShareType(comp, type);
        boolean shareAvailability = checkShareAvailability(shr,quantity);
        if(shareAvailability){
            executeSellOrder(shr,quantity);
        } else{
            throw new NotEnoughFunds("You don't have enough " + type + " Share for the order");
        }
    }

    public boolean checkShareAvailability(Share shr, int quantity){
       for (Stock s :user.getPortfolio().getPortfolioList()){
            if (s.getShare().getCompany().getName()==shr.getCompany().getName() && s.getQuantity()>= quantity){
                return true;
            }
       }
       return false;
    }

    public void executeSellOrder(Share shr, int quantity){
        user.getPortfolio().reduceStock(shr, quantity);
        user.setAvailableBalance(user.getAvailableBalance()+(shr.getPrice()*quantity));
    }


    public Share getShareType(Company comp, String type){
        for(Company y: exchange.getCompanyList()){
            if (y.getName().equals(comp.getName())){
                for (Share s: y.getCompanyStockSpecs().getShareTypes()){
                    if(s.getTypeofShare().equals(actualShareType(type))){
                        return s;
                    }
                }
            }
        }
        return null; //throw an exception here in the future
    }

    public String actualShareType(String type){
        if (type.equals("Preferred")) {
            return "Preferred Share";
        } else{
            return "Unlisted Share";
        }
    }

    public boolean checkEnoughFunds(Share shr, int quantity){
        if (user.getAvailableBalance() >= (shr.getPrice()*quantity)){
            return true;
        }
        return false;
    }

    public void executeBuyOrder(Share shr, int quantity){
        user.setAvailableBalance(user.getAvailableBalance() - (shr.getPrice()*quantity));
        //here check if the share is already in the portfolio, then just increase quantity
        user.getPortfolio().addStock(new Stock(shr,quantity));
    }


}
