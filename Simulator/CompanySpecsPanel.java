import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.List;
//this panel has the orderbook
public class CompanySpecsPanel extends JPanel {
    private JTabbedPane companyTab;
    private List<Company> companyList;
    private User user;
    //todo make Company panel initialise in the construtor with different companies
    public CompanySpecsPanel(List<Company> ccompanyList, User user){
        companyTab = new JTabbedPane();
        companyList = ccompanyList;
        this.user = user;
        creaateCompanytabs(companyTab);
        add(companyTab);
    }

    public void companySpecPanelUpdate(){
        remove(0);
        companyTab = new JTabbedPane();
        creaateCompanytabs(companyTab);
        add(companyTab);
        revalidate();
        repaint();
    }
    //creates a tab (JPanel) for each company
    public void creaateCompanytabs(JTabbedPane tabbedPane){
        for(int i =0; i< companyList.size(); i++){
            CompanyPanel panel = new CompanyPanel();
            panel.appendPrice(companyList.get(i).getOrdinaryShare().getPrice());
            panel.createBooks(true, companyList.get(i).getCompanyStockSpecs().getOrderBook().getBidsBookAsStringArray(), companyList.get(i));
            panel.createBooks(false, companyList.get(i).getCompanyStockSpecs().getOrderBook().getAsksBookAsStringArray(),companyList.get(i));
            tabbedPane.add(companyList.get(i).getTicker(), panel);
        }
        tabbedPane.setBorder(BorderFactory.createTitledBorder("Companies"));
    }

    private class CompanyPanel extends JPanel {
        JLabel price= new JLabel("");
        JPanel Orders = new JPanel();
        JTextPane Bids =new JTextPane();;
        JTextPane Asks =new JTextPane();;

        private CompanyPanel(){
            setLayout(new BorderLayout());
            Orders.setLayout(new GridLayout(1,2));
            Orders.setPreferredSize(new Dimension(400, 400));   //TODO THIS MIGHT NEED TO GO
            add(Orders, BorderLayout.CENTER);
        }

        private void appendPrice(double curPrice){
            price = new JLabel("Bids     Current Price: " + curPrice + "     Asks", SwingConstants.CENTER);
            add(price,BorderLayout.NORTH);
        }

        private void createBooks(Boolean isBids, String[] orders,Company company){
            if(isBids){
                Bids = createBookPane(orders,company);
                JScrollPane Scrp = new JScrollPane(Bids);
                Scrp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                Orders.add(Scrp);
            } else{
                Asks = createBookPane(orders,company);
                JScrollPane Scrp = new JScrollPane(Asks);
                Scrp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                Orders.add(Scrp);
            }
        }
        //this JTextpane adds the order to the GUI orderbook but first check if the user has any orders at the order price point
        public JTextPane createBookPane(String[] orders,Company company){
            JTextPane pane = new JTextPane();
            for(int i=0; i< orders.length; i++) {
                if (userHasLimitOrderAtOrderPriceFromBook(orders[i],company)){
                    addOrderToPane(pane, orders[i] , Color.RED);
                } else{
                    addOrderToPane(pane, orders[i] , Color.BLUE);
                }
            }
            return pane;
        }
        public void addOrderToPane(JTextPane pane, String text, Color color) {
            Style style = pane.addStyle("Has Client Limit Order", null);
            StyleConstants.setForeground(style, color);
            StyledDocument doc = pane.getStyledDocument();
            try {
                doc.insertString(doc.getLength(), text, style);
            }
            catch (Exception e) {}
        }
        public boolean userHasLimitOrderAtOrderPriceFromBook(String orderFromBook,Company company){
            String orderPricePart = orderFromBook.substring(0,15).replaceAll("[^\\d.]", "");;
            double orderPrice = Double.parseDouble(orderPricePart);

            double[] userActiveLimitOrdersPrices = user.getActiveLimitOrdersPriceArrayForSpecificCompany(company);

            for(int i=0; i<userActiveLimitOrdersPrices.length;i++){
                if(orderPrice == userActiveLimitOrdersPrices[i]){
                    return true;
                }
            }return false;
        }
    }








    //have tabbed pane where each panel is a company
    //each company has its orderbook so one




}
/*
public class CompanySpecsPanel extends JPanel {
    JTabbedPane companyTab;
    List<Company> companyList;

    //todo make Company panel initialise in the construtor with different companies
    public CompanySpecsPanel(List<Company> ccompanyList){
        companyTab = new JTabbedPane();
        companyList = ccompanyList;
        creaateCompanytabs(companyTab);
        add(companyTab);
        test();
    }

    public void test(){
        JTextPane pane = new JTextPane();;

        addColoredText(pane, "Red Text\n", Color.RED);
        addColoredText(pane, "Blue Text\n", Color.BLUE);
        add(pane);
    }

    public void addColoredText(JTextPane pane, String text, Color color) {
        StyledDocument doc = pane.getStyledDocument();

        Style style = pane.addStyle("Color Style", null);
        StyleConstants.setForeground(style, color);
        try {
            doc.insertString(doc.getLength(), text, style);
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }



    public void companySpecPanelUpdate(){
        remove(0);
        companyTab = new JTabbedPane();
        creaateCompanytabs(companyTab);
        add(companyTab);
        revalidate();
        repaint();
    }

    public void creaateCompanytabs(JTabbedPane tabbedPane){
        for(int i =0; i< companyList.size(); i++){
            CompanyPanel panel = new CompanyPanel();
            panel.appendPrice(companyList.get(i).getOrdinaryShare().getPrice());
            panel.setBooks(true, companyList.get(i).getCompanyStockSpecs().getOrderBook().getBidsBookAsString());
            panel.setBooks(false, companyList.get(i).getCompanyStockSpecs().getOrderBook().getAsksBookAsString());
            tabbedPane.add(companyList.get(i).getTicker(), panel);
        }
        tabbedPane.setBorder(BorderFactory.createTitledBorder("Companies"));
    }

    private class CompanyPanel extends JPanel {
        JLabel price= new JLabel("");
        JPanel Orders = new JPanel();
        JTextArea Bids =new JTextArea("15");;
        JTextArea Asks =new JTextArea("2");;

        private CompanyPanel(){
            setLayout(new BorderLayout());
            Orders.setLayout(new GridLayout(1,2));
            Orders.setPreferredSize(new Dimension(400, 400));   //TODO THIS MIGHT NEED TO GO
            add(Orders, BorderLayout.CENTER);
        }

        private void appendPrice(double curPrice){
            price = new JLabel("Bids     Current Price: " + curPrice + "     Asks", SwingConstants.CENTER);
            add(price,BorderLayout.NORTH);
        }

        private void setBooks(Boolean isBids, String orders){
            if(isBids){
                Bids = new JTextArea(orders);
                JScrollPane Scrp = new JScrollPane(Bids);
                Scrp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                Orders.add(Scrp);
            } else{
                Asks = new JTextArea(orders);
                JScrollPane Scrp = new JScrollPane(Asks);
                Scrp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                Orders.add(Scrp);
            }

        }






    }
 */
