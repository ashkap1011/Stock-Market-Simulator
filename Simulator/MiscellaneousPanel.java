import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
//This panel has the various buttons, File I/O, market trades and active limit order cancellations.
public class MiscellaneousPanel extends JPanel implements ActionListener {
    private JPanel thisPanel;
    private JPanel exchangeButtonsPanel;
    private GUI guiWindow;
    private Simulation stockExchange;
    private User user;
    private JTextArea marketTradesText;
    private int previouslySelectedDay =1;
    private String previouslySelectedCompanyTicker;

    public MiscellaneousPanel(Simulation exchange, GUI gguiWindow){
        stockExchange = exchange;
        guiWindow = gguiWindow;
        user = exchange.getUser();
        thisPanel = new JPanel();
        exchangeButtonsPanel = new JPanel();
        previouslySelectedCompanyTicker = stockExchange.getExchange().getCompanyList().get(0).getTicker();
        createExchangeButtonsPanel();
        createThisPanel(thisPanel);
    }
    /*this creates the panel, the panel has a subpanles
    e.g.it has the exhcangebutton panel which ahs File I/O and next day
    it has the cancelOrderPanel where the user can cancel the orders
    it has the markettradePanel for market trades and viewing depending on the company and day selected

     */
    public void createThisPanel(JPanel thisPanel){
        thisPanel.setLayout(new BorderLayout());
        thisPanel.add(exchangeButtonsPanel, BorderLayout.WEST);
        JPanel centralPanel = new JPanel();
        centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.PAGE_AXIS));
        JPanel cancelOrderPanel = createCancelOrderPanel();
        JPanel marketTradesPanel = createMarketTradesPanel();



        JScrollPane scrpCancelOrderPanel = new JScrollPane(cancelOrderPanel);
        scrpCancelOrderPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrpCancelOrderPanel.setMaximumSize(new Dimension(800,200));
        centralPanel.add(marketTradesPanel);
        centralPanel.add(scrpCancelOrderPanel);

        thisPanel.add(centralPanel, BorderLayout.CENTER);
        thisPanel.setMaximumSize(new Dimension(900,500));
        add(thisPanel);

    }
    public JPanel createMarketTradesPanel(){
        JPanel marketTradesPanel = new JPanel();
        marketTradesPanel.setBorder(BorderFactory.createTitledBorder("Market Trades"));
        marketTradesPanel.setLayout(new BorderLayout());

        JComboBox companyListComboBox = new JComboBox(stockExchange.getExchange().getCompanyListAsTickerStringArray());
        JComboBox exchangeDayListComboBox = new JComboBox(stockExchange.getExchange().getDaysToStringArray());

        companyListComboBox.setSelectedItem(previouslySelectedCompanyTicker);
        exchangeDayListComboBox.setSelectedItem(previouslySelectedDay);

        marketTradesText = new JTextArea(stockExchange.getCompany(previouslySelectedCompanyTicker)
                .getCompanyStockSpecs().getMarketTrades().getMarketOrdersForSpecificDay(previouslySelectedDay));

        /*
        The item listener cleverly figures out which combobox has had the event by converting the actionevent item to integer,
        if a number format exception was thrown then the item is not an integer and so must be a selection of a compnay ticker
        from the companyListComboBox. if the item selected doesn't throw an exception then a different day has been selected from the
        exchangeDayListComboBox.
         */
        ItemListener companySelectedListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                    try{
                        Integer.parseInt(itemEvent.getItem().toString());
                        //here an event with the daycombobox takes place occurs
                        marketTradesText.setText(stockExchange.getCompany(companyListComboBox.getSelectedItem().toString())
                                .getCompanyStockSpecs().getMarketTrades().getMarketOrdersForSpecificDay(Integer.parseInt(itemEvent.getItem().toString())));
                        previouslySelectedDay = Integer.parseInt(itemEvent.getItem().toString());
                    } catch(NumberFormatException e){
                        marketTradesText.setText(stockExchange.getCompany(itemEvent.getItem().toString())
                                .getCompanyStockSpecs().getMarketTrades().getMarketOrdersForSpecificDay(previouslySelectedDay));
                        previouslySelectedCompanyTicker = itemEvent.getItem().toString();
                    }
                }
            }
        };

        companyListComboBox.addItemListener(companySelectedListener);
        exchangeDayListComboBox.addItemListener(companySelectedListener);
        JPanel marketTradesSidePanel = new JPanel();
        marketTradesSidePanel.setLayout(new BoxLayout(marketTradesSidePanel, BoxLayout.PAGE_AXIS));
        marketTradesSidePanel.add(new JLabel("Select Company"));
        marketTradesSidePanel.add(companyListComboBox);
        marketTradesSidePanel.add(new JLabel("Select Day"));
        marketTradesSidePanel.add(exchangeDayListComboBox);
        marketTradesSidePanel.setMaximumSize(new Dimension(50,100));
        marketTradesPanel.add(marketTradesSidePanel,BorderLayout.WEST);
        JScrollPane scrpMarkeTradeText = new JScrollPane(marketTradesText);
        scrpMarkeTradeText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        marketTradesPanel.add(scrpMarkeTradeText,BorderLayout.CENTER);

        return marketTradesPanel;
    }

    /*
    This panel creates the File I/O, and allow the user the ability to proceed to the next day.
     */
    public void createExchangeButtonsPanel() {
        exchangeButtonsPanel.setLayout(new BoxLayout(exchangeButtonsPanel,BoxLayout.PAGE_AXIS));
        JButton nextDayButton = new JButton("Next Day");
        nextDayButton.setActionCommand("next");
        JButton saveButton = new JButton("Save");
        saveButton.setActionCommand("save");
        JButton loadButton = new JButton("Load");
        loadButton.setActionCommand("load");

        ActionListener IOAndMisc = actionEvent -> {
            try{
                File f = new File("obj.txt");
            if(actionEvent.getActionCommand().equals("save")) {
                FileOutputStream file = new FileOutputStream(f);
                ObjectOutputStream output = new ObjectOutputStream(file);
                output.writeObject(stockExchange);
                file.flush();
                output.close();
            }
            else if (actionEvent.getActionCommand().equals("load")){
                FileInputStream fileInputStream = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fileInputStream);
                Simulation loadedObject = (Simulation) ois.readObject();
                guiWindow.loadFile(loadedObject);
                fileInputStream.close();
                ois.close();
            } else {
                stockExchange.getExchange().nextDay();
                guiWindow.updateAllPanels();
            } }
            catch(Exception g){
                g.printStackTrace();
                JOptionPane.showMessageDialog(null, "argh.. if you are reading this " +
                        "then you have been playing for many days or your were unlucky to generate a random price number which" +
                        " is beyond the limit order books, the only thing you can do now is restart, Apologies");
        }};
        saveButton.addActionListener(IOAndMisc);
        loadButton.addActionListener(IOAndMisc);
        nextDayButton.addActionListener(IOAndMisc);


        exchangeButtonsPanel.add(nextDayButton);
        exchangeButtonsPanel.add(saveButton);
        exchangeButtonsPanel.add(loadButton);
    }
    //this method creates cancel orders with a button when a new active limit order is placed.
    public JPanel createCancelOrderPanel(){
        JPanel cancelOrderPanel = new JPanel();
        cancelOrderPanel.setBorder(BorderFactory.createTitledBorder("Cancel Limit Order"));
        cancelOrderPanel.setLayout(new BoxLayout(cancelOrderPanel,BoxLayout.PAGE_AXIS));
        for(int i =0 ; i<user.getNumberOfActiveLimitOrders(); i++){
            JPanel order = new JPanel();
            order.setLayout(new FlowLayout());
            JTextArea orderText = new JTextArea(user.getActiveLimitOrderWithIndexToString(i));
            orderText.setEditable(false);
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(this);
            cancelButton.setActionCommand(Integer.toString(i));
            order.add(orderText);
            order.add(cancelButton);
            cancelOrderPanel.add(order);
        }
        return cancelOrderPanel;
    }

    public void miscellaneousPanelUpdate(){
        remove(0);
        thisPanel = new JPanel();
        createThisPanel(thisPanel);
        revalidate();
        repaint();
    }
    //this actionPerformed method looks out for a cancel order button being pressed, which would cancel the order and unlock those funds for the user
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        int orderIndex = Integer.parseInt(actionEvent.getActionCommand());
        LimitOrder orderForCancellation = user.getActiveLimitOrderWithIndex(orderIndex);
        Company orderCompany = user.getCompanyfromLimitOrder(orderForCancellation);
        stockExchange.getTrade().executeOrderCancellation(orderCompany,orderForCancellation);
        guiWindow.updateAllPanels();
    }


}
