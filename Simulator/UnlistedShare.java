import java.io.Serializable;

public class UnlistedShare extends Share implements Serializable {
    private boolean votingRights;
    private int DaysSincePurchase;
    private int Price;
    private String typeOfShare;
    //unlisedted shares don't have a price
    public UnlistedShare(Company comp, double sharePrice, int multiplier){
        super(comp,0,multiplier);
        votingRights = true;
        typeOfShare = "Unlisted Share";
    }

    public String getShareInfo(){
        return "Company: " + getCompany().getName() +" Type:" + typeOfShare
                + "Current Price: "+ getPrice();
    }
    @Override
    public String toString() {
        return typeOfShare + "with no price nor dividend but with " + votingRights + " voting rights"; // something about days since purchase.
    }

    @Override
    public void setVolatility() {
        volatility = 1;
    }

    @Override
    public double getVolatility() {
        return 0;
    }

    @Override
    public String getTypeofShare() {
        return typeOfShare;
    }

    public double getDividend(){
        return 0;
    }

}
