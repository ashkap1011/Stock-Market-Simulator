import java.awt.*;
import java.awt.event.WindowEvent;
import javax.swing.*;

//this si the main frame
public class GUI {
    private JFrame thisJFrame;
    private CompanySpecsPanel companySpecsPanel;    //has the orderbooks for the differnt companies
    private OrderPlacementPanel orderPanel;         //this is where you place orders for the different type of panels.
    private PortfolioPanel portfolioPanel;          //this has the user's portfolio e.g. active limit orders
    private MiscellaneousPanel miscPanel;           //has various components e.g. File I/O, market trades and cancelling Limit orders
    private Simulation stockExchange;               //this is the stock exchange that has all the data and hooks up to thr GUI

    public GUI(){
        thisJFrame = new JFrame("Stock Market Simulation");
        stockExchange = startSimulation();
        createFrame();
    }
    //this method creates the JFrame and all its components
    public void createFrame(){
        thisJFrame.setLayout(new GridLayout(2,2));
        companySpecsPanel = new CompanySpecsPanel(stockExchange.getExchange().getCompanyList(),stockExchange.getUser());
        portfolioPanel = new PortfolioPanel(stockExchange.getUser());
        orderPanel = new OrderPlacementPanel(stockExchange,this);
        miscPanel = new MiscellaneousPanel(stockExchange, this);
        thisJFrame.add(companySpecsPanel);
        thisJFrame.add(orderPanel);
        thisJFrame.add(portfolioPanel);
        thisJFrame.add(miscPanel);

        thisJFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        thisJFrame.setResizable(true);
        thisJFrame.setVisible(true);
        thisJFrame.pack();
    }
    //this starts the simulation (exchange) and establishes the necessary classes
    public Simulation startSimulation(){
        Simulation s = new Simulation();
        s.start();
        return s;
    }
    //this method is called by the isntance variable components when an update is needed.
    public void updateAllPanels(){
        companySpecsPanel.companySpecPanelUpdate();
        portfolioPanel.portfolioPanelUpdate();
        miscPanel.miscellaneousPanelUpdate();
    }
    //file I/O
    public void loadFile(Simulation loadedExchange){
        thisJFrame.dispatchEvent(new WindowEvent(thisJFrame, WindowEvent.WINDOW_CLOSING));
        thisJFrame = new JFrame("Stock Market Simulation");
        stockExchange = loadedExchange;
        createFrame();
    }


    public CompanySpecsPanel getCompanySpecsPanel() {
        return companySpecsPanel;
    }

    public OrderPlacementPanel getOrderPanel() {
        return orderPanel;
    }

    public PortfolioPanel getPortfolioPanel() {
        return portfolioPanel;
    }
}
