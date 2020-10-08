import java.io.Serializable;
import java.util.*;

class Company implements Serializable {
    private String name; 
    private String ticker;             //e.g. GOOG
    private int issuedShares;          // number of issued shares
    private double initialPrice;       // price of share initially
    private CompanyStockSpecs spec;
    private int totalAssets;           //the total number of assets the company has
    private int multiplier;

    public Company(String nname, String tticker, int iissuedShares, double price, int mmultiplier){
	name= nname;
 	ticker = tticker;
	issuedShares = iissuedShares;
	initialPrice = price;
	multiplier = mmultiplier;
    }
    //create orderbook for ordinary price

    public void createCompanyStockSpecsAtRunTime(){
        spec = new CompanyStockSpecs();
	    spec.createStockSpec(this,initialPrice,multiplier);
    }

    public Share getOrdinaryShare(){
        for (Share shr: spec.getShareTypes()){
            if (shr instanceof OrdinaryShare){
                return shr;
            }
        }
        return null; ///TODO MAYBE THROW EXCEPTION.
    }


    @Override
    public String toString() {
        return "Company:" + name + " (" + ticker + ") " +
                ", issuedShares=" + issuedShares +
                ", initialPrice=" + initialPrice +
                ", spec=" + spec;
    }


//getters and setters
    
    public String getName(){
	return this.name; 
    }

    public String getTicker() {
	return ticker;
    }

    public CompanyStockSpecs getCompanyStockSpecs() {
	return spec;
    }
    

    
    
    
    





    

    

}
