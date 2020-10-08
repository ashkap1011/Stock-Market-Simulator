import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.Serializable;
//this panel has the ability to place orders for the different type of shares.
public class OrderPlacementPanel extends JPanel {
    private JPanel thisPanel;
    TradingPanel tradePanel;
    AuctionPanel auctionPanel;
    Simulation exchange;
    GUI guiWindow;

    public OrderPlacementPanel(Simulation exchange,GUI GUIwindow){//CompanySpecsPanel companySpecsPanel
        thisPanel = new JPanel();
        this.exchange = exchange;
        this.guiWindow = GUIwindow;
        createThisPanel();

    }

    public void  createThisPanel(){
        thisPanel.setLayout(new GridLayout(1,2));
        tradePanel = new TradingPanel(exchange,guiWindow);
        TitledBorder tradeTitle = BorderFactory.createTitledBorder("Trade");
        tradePanel.setBorder(tradeTitle);
        auctionPanel = new AuctionPanel(exchange,guiWindow);
        TitledBorder auctionTitle = BorderFactory.createTitledBorder("Auction");
        auctionPanel.setBorder(auctionTitle);

        thisPanel.add(tradePanel);
        thisPanel.add(auctionPanel);
        add(thisPanel);
    }












}
