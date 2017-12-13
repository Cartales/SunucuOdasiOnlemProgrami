/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import classes.DatabaseValues;
import classes.Users;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.InsertValueToDatabase;
import model.ListValuesFromDatabase;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author YunusEmre
 */
public class MainScreen extends javax.swing.JFrame implements SerialPortEventListener {

    /**
     * Creates new form MainScreen
     */
    
    String inputLine;
    String[] readArray = new String[100];
    SerialPort serialPort;
    int i = 0;
    int j = 0;
    private static String portName = "COM5";

    private static final String PORT_NAMES[] = {getPortName(),};

    private BufferedReader input;
    private static OutputStream output;
    private static int TIME_OUT = 2000;
    private static int DATA_RATE = 9600;
    DefaultCategoryDataset dcd = new DefaultCategoryDataset();
    DefaultCategoryDataset dcd2 = new DefaultCategoryDataset();
    JFreeChart jchart = ChartFactory.createBarChart("Sıcaklık", " ", "Sıcaklık Değerleri", dcd, PlotOrientation.VERTICAL, true, true, false);
    JFreeChart jcharts = ChartFactory.createBarChart("Duman", " ", "Duman Yoğunluğu", dcd2, PlotOrientation.VERTICAL, true, true, false);
    ChartPanel panel = new ChartPanel(jchart);
    ChartPanel panel2 = new ChartPanel(jcharts);
    
    List<String> tempValues = new ArrayList<String>();
    List<String> smokeValues = new ArrayList<String>();
    
    public static void setPortName(String sPortName) {
        portName = sPortName;
    }
     public static String getPortName() {
        return portName;
    }
     
     public void initialize() {
        System.setProperty("gnu.io.rxtx.SerialPorts", getPortName());        
        CommPortIdentifier portId = null;        
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portNames : PORT_NAMES) {
                if (currPortId.getName().equals(portNames)) {
                    System.out.println(portNames);
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            
            JOptionPane.showMessageDialog(null," PORTUNA BAĞLI CİHAZ YOK!","HATA",JOptionPane.ERROR_MESSAGE);
            System.out.println("PORTA BAĞLI CİHAZ YOK!");
            return;
        }
        System.out.println(portId);
        try {
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

            serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.out.println("TURGAY");
            System.err.println(e.toString());
        }
    }
     
    public synchronized void close() {
      if (serialPort != null) {
          serialPort.removeEventListener();
          serialPort.close();
      }
    }
    
    private void insertTemp(String node) throws SQLException{
        LocalDateTime date = java.time.LocalDateTime.now();
        DatabaseValues databaseValues = new DatabaseValues();
        InsertValueToDatabase insert = new InsertValueToDatabase();
        databaseValues.setTempDate(date);
        databaseValues.setTempAdmin(labelUsername.getText());
        databaseValues.setTempValue(node);
        insert.insertToTempTable(databaseValues);
        
    }
    
    private void insertSmoke(String node) throws SQLException{
        LocalDateTime date = java.time.LocalDateTime.now();
        DatabaseValues databaseValues = new DatabaseValues();
        InsertValueToDatabase insert = new InsertValueToDatabase();
        databaseValues.setSmokeDate(date);
        databaseValues.setSmokeAdmin(labelUsername.getText());
        databaseValues.setSmokeValue(node);
        insert.insertToSmokeTable(databaseValues);
    }
    
    private void listTempTable(){
        DefaultTableModel model = (DefaultTableModel)sicaklikTablo.getModel();
        model.getDataVector().clear();
        sicaklikTablo.repaint();
        
        List<ListValuesFromDatabase> vList = new ListValuesFromDatabase().listValuesFromTempTable();
        Vector[] v = new Vector[vList.size()];
        for (int j = 0; j < vList.size(); j++) {
            v[i] = new Vector();
            v[i].add(vList.get(j).getDate());
            v[i].add(vList.get(j).getAdmin());
            v[i].add(vList.get(j).getValue());
            model.insertRow(j, v[i]);
        }
    }
    private void listSmokeTable(){
        DefaultTableModel model = (DefaultTableModel)dumanTablo.getModel();
        model.getDataVector().clear();
        dumanTablo.repaint();
        
        List<ListValuesFromDatabase> vList = new ListValuesFromDatabase().listValuesFromSmokeTable();
        Vector[] v = new Vector[vList.size()];
        for (int j = 0; j < vList.size(); j++) {
            v[i] = new Vector();
            v[i].add(vList.get(j).getDate());
            v[i].add(vList.get(j).getAdmin());
            v[i].add(vList.get(j).getValue());
            model.insertRow(j, v[i]);
        }
    }
    private void drawBarChartForTemp(List<String> values){
        
        for (int k = 0; k <=j-1; k++) {
            String s = String.valueOf(k);
            Float value = Float.valueOf(values.get(k).toString());
            dcd.setValue(value, "Sıcaklık", s);
   
        }
        
        
        
        
        
        CategoryPlot plot = jchart.getCategoryPlot();
        plot.setRangeGridlinePaint(Color.BLACK);
        
        
        
        
        
        jPanel2.add(panel, BorderLayout.CENTER);
        jPanel2.validate();
        
    }
    
    private void drawBarChartForSmoke(List<String> values){
         for (int k = 0; k <=j-1; k++) {
            String s = String.valueOf(k);
            Integer value = Integer.valueOf(values.get(k).toString());
            dcd2.setValue(value, "Duman", s);
   
        }
        
        
        
        
        
        CategoryPlot plot = jcharts.getCategoryPlot();
        plot.setRangeGridlinePaint(Color.BLACK);
        
        
        
        
        
        jPanel6.add(panel2, BorderLayout.CENTER);
        jPanel6.validate();
    }
    
    private void autoScrolling(){
        jScrollPane2.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent ae) {
                ae.getAdjustable().setValue(ae.getAdjustable().getMaximum());
            }
        });
        jScrollPane1.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent ae) {
                ae.getAdjustable().setValue(ae.getAdjustable().getMaximum());
            }
        });
        jScrollPane4.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent ae) {
                ae.getAdjustable().setValue(ae.getAdjustable().getMaximum());
            }
        });
    }
    DefaultListModel defaultListModel = new DefaultListModel();
     
    @Override
    public synchronized void serialEvent(SerialPortEvent spe) {
       if (spe.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                if (input.ready() == true) {
                    //**********************************************
                    //ARDUINO dan SERİ PORTTAN GELEN VERİ BURADAN KONTROL EDİLİR
                    //**********************************************
                    inputLine = input.readLine();
                    String[] node = inputLine.split("#");
                    String bilgi = "Duman: " + node[0] + " Sicaklik: " + node[1];
                   
                    defaultListModel.addElement(bilgi);
                    
                    
                    tempValues.add(node[1]);
                    smokeValues.add(node[0]);
                    
                    if(tempValues.size() == 10 && smokeValues.size() == 10){
                        tempValues.remove(0);
                        smokeValues.remove(0);
                        j = j -1;
                    }
                    
                    j = j + 1;
                    
                    
                    
                    insertSmoke(node[0]);
                    insertTemp(node[1]);
                    
                    if(rb_tablo.isSelected()){
                        listTempTable();
                        listSmokeTable();
                    }
                    else if(rb_grafik.isSelected()){
                        //grafik komutları
                        drawBarChartForTemp(tempValues);
                        drawBarChartForSmoke(smokeValues);
                    }
                   
                    System.out.println("OKUNAN VERİ:" + bilgi);
                    autoScrolling();
                    
                    
                  
                } else {
              }
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
    }
    
    public MainScreen(String username) {
        initComponents();
        portListele();
        labelUsername.setText(username);
    }
    
    void portListele(){
        cb_portName.removeAllItems();
        System.setProperty("gnu.io.rxtx.SerialPorts", getPortName());
        Enumeration enumeration = CommPortIdentifier.getPortIdentifiers();
        while (enumeration.hasMoreElements()) {
            CommPortIdentifier portID = (CommPortIdentifier) enumeration.nextElement();
            for (String portNames : PORT_NAMES) {
                cb_portName.addItem(portNames);
            }
        }
    }

  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        genelBilgiler = new java.awt.TextArea();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLabel3 = new javax.swing.JLabel();
        rb_tablo = new javax.swing.JRadioButton();
        rb_grafik = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        sicaklikTablo = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        portIsim = new javax.swing.JTextField();
        boundRate = new javax.swing.JTextField();
        timeOut = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        cb_portName = new javax.swing.JComboBox<>();
        btn_listPort = new javax.swing.JButton();
        btn_connect = new javax.swing.JButton();
        btn_stop = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        labelUsername = new javax.swing.JLabel();
        btnSensorStart = new javax.swing.JButton();
        btnSensorStop = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        dumanTablo = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(102, 204, 255));

        genelBilgiler.setEditable(false);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 36)); // NOI18N
        jLabel1.setText("Sunucu Odası Önlem Programı");

        jLabel2.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel2.setText("Genel Bilgiler");

        jScrollPane1.setAutoscrolls(true);

        jList1.setModel(defaultListModel);
        jScrollPane1.setViewportView(jList1);

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setText("Okunan Değerler");

        rb_tablo.setSelected(true);
        rb_tablo.setText("Tablolar");
        rb_tablo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rb_tabloActionPerformed(evt);
            }
        });

        rb_grafik.setText("Grafikler");
        rb_grafik.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rb_grafikActionPerformed(evt);
            }
        });

        jPanel2.setMinimumSize(new java.awt.Dimension(406, 427));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        sicaklikTablo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Yetkili", "Tarih", "Değer"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(sicaklikTablo);
        if (sicaklikTablo.getColumnModel().getColumnCount() > 0) {
            sicaklikTablo.getColumnModel().getColumn(0).setPreferredWidth(20);
            sicaklikTablo.getColumnModel().getColumn(2).setPreferredWidth(15);
        }

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4)
        );

        jPanel2.add(jPanel4);

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setText("Ortam Sıcaklığı");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setText("Duman Yoğıunluğu");

        portIsim.setText("COM5");

        boundRate.setText("9600");

        timeOut.setText("2000");

        jLabel6.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel6.setText("Bağlantı Bilgileri");

        jLabel7.setText("Port İsmi");

        jLabel8.setText("Bound Rate");

        jLabel9.setText("Time Out");

        btn_listPort.setText("Port Listele");
        btn_listPort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_listPortActionPerformed(evt);
            }
        });

        btn_connect.setText("Bağlan");
        btn_connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_connectActionPerformed(evt);
            }
        });

        btn_stop.setText("Durdur");
        btn_stop.setEnabled(false);
        btn_stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_stopActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel10.setText("Yetkili Bilgileri ve İşlemleri");

        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setText("Kullanıcı:");

        labelUsername.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        labelUsername.setText("username");

        btnSensorStart.setText("Sensör Etkinleştir");
        btnSensorStart.setEnabled(false);
        btnSensorStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSensorStartActionPerformed(evt);
            }
        });

        btnSensorStop.setText("Sensör Durdur");
        btnSensorStop.setEnabled(false);
        btnSensorStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSensorStopActionPerformed(evt);
            }
        });

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jPanel6.setMinimumSize(new java.awt.Dimension(386, 427));
        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));

        dumanTablo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Yetkili", "Tarih", "Değer"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(dumanTablo);
        if (dumanTablo.getColumnModel().getColumnCount() > 0) {
            dumanTablo.getColumnModel().getColumn(0).setPreferredWidth(20);
            dumanTablo.getColumnModel().getColumn(2).setPreferredWidth(20);
        }

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );

        jPanel6.add(jPanel5);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(492, 492, 492)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(portIsim, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                                    .addComponent(boundRate)
                                    .addComponent(timeOut))
                                .addGap(18, 18, 18)
                                .addComponent(cb_portName, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btn_connect, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btn_listPort, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btn_stop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelUsername))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnSensorStart)
                                .addGap(18, 18, 18)
                                .addComponent(btnSensorStop, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(150, 150, 150))
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addGap(136, 136, 136)
                                        .addComponent(rb_tablo)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(rb_grafik))
                                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(genelBilgiler, javax.swing.GroupLayout.PREFERRED_SIZE, 1076, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel6))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(portIsim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cb_portName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_listPort))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8)
                        .addGap(2, 2, 2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(boundRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_connect))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9)
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(timeOut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_stop)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(genelBilgiler, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(rb_grafik)
                    .addComponent(rb_tablo)
                    .addComponent(jLabel5)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel11)
                                    .addComponent(labelUsername))
                                .addGap(13, 13, 13)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnSensorStart, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnSensorStop, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_connectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_connectActionPerformed
        // TODO add your handling code here:
        btn_stop.setEnabled(true);
        btn_connect.setEnabled(false);
        btnSensorStart.setEnabled(true);
        setPortName(portIsim.getText());
        DATA_RATE = Integer.parseInt(boundRate.getText());
        TIME_OUT = Integer.parseInt(timeOut.getText());
  
        initialize();
 
        genelBilgiler.setText(portIsim.getText()+" PORT AÇILDI\n");
        genelBilgiler.setText(genelBilgiler.getText() + "Sensör Etkinleştirilmesi Gerekiyor.\n");
        defaultListModel.clear();
    }//GEN-LAST:event_btn_connectActionPerformed

    private void btn_stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_stopActionPerformed
        // TODO add your handling code here:
        btn_stop.setEnabled(false);
        btn_connect.setEnabled(true);
        btnSensorStart.setEnabled(false);
        close();
        genelBilgiler.setText(genelBilgiler.getText() + "KAPATILIYOR...\n");
    }//GEN-LAST:event_btn_stopActionPerformed

    private void btn_listPortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_listPortActionPerformed
        // TODO add your handling code here:
        portListele();
    }//GEN-LAST:event_btn_listPortActionPerformed

    private void btnSensorStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSensorStartActionPerformed
        try {
            // TODO add your handling code here:
            btnSensorStart.setEnabled(false);
            btnSensorStop.setEnabled(true);
            btn_stop.setEnabled(false);
            PrintWriter outputPrint = new PrintWriter(serialPort.getOutputStream());
            String msj = "1";
            outputPrint.print(msj);
            outputPrint.flush();
            genelBilgiler.setText(genelBilgiler.getText() + "Sensör Etkinleştirildi.\n");
            genelBilgiler.setText(genelBilgiler.getText() + "Veri Bekleniyor...\n");
        } catch (IOException ex) {
            btnSensorStart.setEnabled(true);
            btnSensorStop.setEnabled(false);
            genelBilgiler.setText(genelBilgiler.getText() + "Sensör Etkinleştirilemedi!\n");
            System.out.println(ex);
        }
    }//GEN-LAST:event_btnSensorStartActionPerformed

    private void btnSensorStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSensorStopActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            btnSensorStart.setEnabled(true);
            btnSensorStop.setEnabled(false);
            btn_stop.setEnabled(true);
            PrintWriter outputPrint = new PrintWriter(serialPort.getOutputStream());
            String msj = "2";
            outputPrint.print(msj);
            outputPrint.flush();
            genelBilgiler.setText(genelBilgiler.getText() + "Sensör Durduruldu.\n");
        } catch (IOException ex) {
            genelBilgiler.setText(genelBilgiler.getText() + "Sensör Durdurulamadı!\n");
            System.out.println(ex);
        }
    }//GEN-LAST:event_btnSensorStopActionPerformed

    private void rb_tabloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rb_tabloActionPerformed
        // TODO add your handling code here:
        if(rb_tablo.isSelected()){
            rb_grafik.setSelected(false);
            jPanel5.setVisible(true);
            jPanel4.setVisible(true);
            jPanel2.remove(panel);
            jPanel6.remove(panel2);
            dcd.clear();
            dcd2.clear();
            
        }
        
    }//GEN-LAST:event_rb_tabloActionPerformed

    private void rb_grafikActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rb_grafikActionPerformed
        // TODO add your handling code here:
        if(rb_grafik.isSelected()){
            rb_tablo.setSelected(false);
            jPanel5.setVisible(false);
            jPanel4.setVisible(false);
            jPanel2.setSize(452, 402);
            jPanel6.setSize(452, 402);
        }
        
    }//GEN-LAST:event_rb_grafikActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainScreen("").setVisible(true);
            }
        });
        
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField boundRate;
    private javax.swing.JButton btnSensorStart;
    private javax.swing.JButton btnSensorStop;
    private javax.swing.JButton btn_connect;
    private javax.swing.JButton btn_listPort;
    private javax.swing.JButton btn_stop;
    private javax.swing.JComboBox<String> cb_portName;
    private javax.swing.JTable dumanTablo;
    private java.awt.TextArea genelBilgiler;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel labelUsername;
    private javax.swing.JTextField portIsim;
    private javax.swing.JRadioButton rb_grafik;
    private javax.swing.JRadioButton rb_tablo;
    private javax.swing.JTable sicaklikTablo;
    private javax.swing.JTextField timeOut;
    // End of variables declaration//GEN-END:variables

    


}
