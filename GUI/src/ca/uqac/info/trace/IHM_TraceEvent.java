/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uqac.info.trace;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import ca.uqac.info.ltl.Operator;
import ca.uqac.info.ltl.Operator.ParseException;
import ca.uqac.info.trace.conversion.JavaMopTranslator;
import ca.uqac.info.trace.conversion.JsonTranslator;
import ca.uqac.info.trace.conversion.MaudeTranslator;
import ca.uqac.info.trace.conversion.MonpolyTranslator;
import ca.uqac.info.trace.conversion.PromelaTranslator;
import ca.uqac.info.trace.conversion.SmvTranslator;
import ca.uqac.info.trace.conversion.SqlTranslator;
import ca.uqac.info.trace.conversion.Translator;
import ca.uqac.info.trace.conversion.XesTranslator;
import ca.uqac.info.trace.conversion.XmlTranslator;
import ca.uqac.info.trace.generation.AmazonEcsGenerator;
import ca.uqac.info.trace.generation.BookstoreGenerator;
import ca.uqac.info.trace.generation.CycleGenerator;
import ca.uqac.info.trace.generation.LlrpGenerator;
import ca.uqac.info.trace.generation.PetriNetGenerator;
import ca.uqac.info.trace.generation.TraceGenerator;

/**
 *
 * @author Samatar
 */
public class IHM_TraceEvent extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Trace translator
	private JLabel      LabTitre, lblTitre, lblInputLTL,
								    lblTitre1, lblTitre2,lblTitre3 ,
								    lblTitreLTL, lblTitreTranslate;
	private JButton     bClick, btnClear, btnClearLTL,
								    btnConvertir, btnSave, btnSaveLTL ;
	@SuppressWarnings("rawtypes")
	private JComboBox   comboBox;
	private JMenuBar     menuBar;
	private JMenuItem    itemAbout , itemQuit;
	private JPanel       panTranslator, panGenerator;
	private JScrollPane  spTranslate, spTranslateLTL;
	private JTextArea    txtArea, txtAreaLTL;
	private JMenu        menuHelp, menuFile;
	private JTextField   textFiel_path , textFiel_path_LTL;
	private JTabbedPane  tabbedPane;
	private JSeparator   separator;

	// Trace generator
	private JSplitPane splitPane;
	
	private JPanel     paneLeft, paneRight, paneMenu;
	
	private JButton     btnMenu1, btnMenu2, btnMenu3, btnMenu4,
						btnMenu5,  btnClearGen, btnSaveGen, btnGen;
	private JTextArea   resultGen, textAreaHelpGen;
	private JScrollPane  spResultGen, spHelpGen;
	private JLabel 		lblGenerate,lblMenu , lblTitreGen, 
						lblNbTraces,lblParametres, lblHelpGen;
	private JTextField   tfNbTraces ;
	private TraceGenerator t_gen;
	protected Vector<String> listParam;
	private Vector<JTextField> listTextFields = new Vector<JTextField>() ;
	private Vector<JLabel> listLabel = new Vector<JLabel>() ;
	private JSeparator   separatorGenMenu;
	//Runtime
	private JPanel      paneRuntime,paneTools ,paneTable, paneGraph;
	private JLabel 		lblRepertoireRun,lblTitreRunTime, lblTools ;
	private JTextField  tfRepertoireRun;
	private JButton 	btnRepertoireRun, btnGO, btnStop, btnSaveRun,
						btnBuilding;
	private JSeparator separatorRun ;
	private javax.swing.JCheckBox checkBeepBeep, checkJavaMop,checkMaude, 
								   checkMonopoly, checkMySQL, checkNuSMV,
								   checkProM, checkSaxon, checkSpin;
	private JTable executionTable;
	private JScrollPane spTable ;
	//General
	protected File myFile;
	protected Vector<String> outTrace,outTraceGen;
	protected JFileChooser filechoose;
	protected int status;
	protected boolean fieldsVisible;
	//Generate the number associated with the trace folder 
    Random randomGenerator = new Random();
	protected String path_file, output_format = "", input_filename = "",selectedMenu="";

	/**
	 * Creates new form IHM_TraceEvent
	 */
	public IHM_TraceEvent() {
		initComponents();
		this.fieldsVisible = false;
		initFields(fieldsVisible);
	}
    /**
	 * 
	 * @param bVisible
	 */
	private void initFields(boolean bVisible) {
		//Translator
		this.lblTitre3.setEnabled(bVisible);
		this.btnConvertir.setEnabled(bVisible);
		this.lblTitre2.setEnabled(true);
		this.comboBox.setEnabled(true);
		btnSave.setEnabled(bVisible);
		
		btnSaveLTL.setEnabled(false);
		
		
		//Generator
		btnSaveGen.setEnabled(bVisible);
		btnClearGen.setEnabled(bVisible);
		btnGen.setEnabled(bVisible);
	    resultGen.setEditable(bVisible);
	    lblParametres.setVisible(bVisible);
		
		
	}
	 @SuppressWarnings({ "rawtypes", "unchecked" })
	private void initComponents() {
		// Trace translator
		lblParametres = new javax.swing.JLabel();
		tabbedPane = new JTabbedPane();
		panTranslator = new JPanel();
		LabTitre = new JLabel();
		lblTitreTranslate = new JLabel();
		lblTitre1 = new JLabel();
		lblInputLTL = new JLabel();
		bClick = new JButton();
		textFiel_path = new JTextField();
		lblTitre2 = new JLabel();
		comboBox = new JComboBox();
		btnConvertir = new JButton();
		lblTitre3 = new JLabel();
		spTranslate = new JScrollPane();
		txtArea = new JTextArea();
		btnClearLTL = new JButton();
		lblTitreLTL = new JLabel();
		textFiel_path_LTL = new JTextField();
		spTranslateLTL = new JScrollPane();
		txtAreaLTL = new JTextArea();
		btnSaveLTL = new JButton();
		btnClear = new JButton();
		btnSave = new JButton();
		lblTitreLTL = new JLabel();
		panGenerator = new JPanel();
		lblTitre = new JLabel();
		menuBar = new JMenuBar();
		menuFile = new JMenu();
		itemQuit = new JMenuItem();
		menuHelp = new JMenu();
		itemAbout = new JMenuItem();
		separator = new JSeparator();

		// Trace Generator
		lblTitreGen = new JLabel();
		lblNbTraces = new JLabel();
		tfNbTraces = new JTextField();
		splitPane = new JSplitPane();
		paneLeft = new JPanel();
		paneRight = new JPanel();
		paneMenu = new JPanel();
		btnMenu1 = new JButton();
		btnMenu2 = new JButton();
		btnMenu3 = new JButton();
		btnMenu4 = new JButton();
		btnMenu5 = new JButton();
		lblMenu = new JLabel();
		btnClearGen = new JButton();
		btnSaveGen = new JButton();
		btnGen = new JButton();
		spResultGen = new JScrollPane();
		resultGen = new JTextArea();
		lblGenerate = new JLabel();
		separatorGenMenu = new JSeparator();
		lblHelpGen = new JLabel();
		textAreaHelpGen = new JTextArea();
		spHelpGen = new JScrollPane();
		
		//Runtime
		paneRuntime = new JPanel(); paneGraph = new JPanel();
		paneTable = new JPanel();	paneTools = new JPanel();
		
		lblTitreRunTime = new JLabel("", JLabel.CENTER);
		lblRepertoireRun = new JLabel(); lblTools = new JLabel();
		
		tfRepertoireRun = new JTextField();	
		btnRepertoireRun = new JButton();btnStop = new JButton() ; btnGO = new JButton() ; btnBuilding = new JButton();
		
		btnSaveRun = new JButton();	separatorRun = new JSeparator();
		
		checkBeepBeep = new JCheckBox() ; checkMaude = new JCheckBox() ;
		checkJavaMop = new JCheckBox() ; checkMonopoly = new JCheckBox() ;
		checkMySQL = new JCheckBox() ;checkNuSMV = new JCheckBox() ;
		checkProM = new JCheckBox() ;checkSaxon = new JCheckBox() ;
		checkSpin =  new JCheckBox() ;
		
		executionTable = new JTable() ; spTable = new JScrollPane();
		
		


	        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

	        tabbedPane.setBackground(new java.awt.Color(255, 255, 204));

	        panTranslator.setBackground(new java.awt.Color(255, 255, 204));
	        panTranslator.setToolTipText("");

	        LabTitre.setFont(new java.awt.Font("Arial Black", 1, 14)); 
	        LabTitre.setForeground(new java.awt.Color(0, 51, 102));
	        LabTitre.setText("  I . Converter traces of events");
	        LabTitre.setToolTipText("");

	        lblTitre1.setText("   1.  Select Directory From Your Computer");

	        bClick.setText("Browse");
	        bClick.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                bClickActionPerformed(evt);
	            }
	        });

	        lblTitre2.setText("3.  Select output format");

	        comboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "XML", "SQL", "SMV", "MONPOLY", "XES", "MOP", "JSON","PML","MAUDE" }));
	        comboBox.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                comboBoxActionPerformed(evt);
	            }
	        });

	        btnConvertir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/load.GIF"))); 
	        btnConvertir.setToolTipText("Translate");
	        btnConvertir.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                btnConvertirActionPerformed(evt);
	            }
	        });

	        lblTitre3.setText("Start Converting !!");

	        txtArea.setColumns(20);
	        txtArea.setRows(5);
	        spTranslate.setViewportView(txtArea);

	        btnClearLTL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/clear.GIF"))); 
	        btnClearLTL.setToolTipText("Clear");
	        btnClearLTL.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                btnClearActionPerformed(evt);
	            }
	        });

	        lblInputLTL.setText("2. Enter LTL property ");

	        txtAreaLTL.setColumns(20);
	        txtAreaLTL.setRows(5);
	        spTranslateLTL.setViewportView(txtAreaLTL);

	        btnSaveLTL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/sav.GIF")));
	        btnSaveLTL.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnSaveActionPerformed(evt);
				}
	        });

	        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/clear.GIF"))); 
	        btnClear.setToolTipText("Clear");
	        btnClear.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                btnClearActionPerformed(evt);
	            }
	        });

	        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/sav.GIF")));
	        btnSave.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                btnSaveActionPerformed(evt);
	            }
	        });

	        lblTitreLTL.setFont(new java.awt.Font("Arial Black", 1, 14));
	        lblTitreLTL.setForeground(new java.awt.Color(0, 51, 102));
	        lblTitreLTL.setText("Property  LTL");

	        lblTitreTranslate.setFont(new java.awt.Font("Arial Black", 0, 14)); 
	        lblTitreTranslate.setForeground(new java.awt.Color(0, 51, 102));
	        lblTitreTranslate.setText("Log  translated");

	        javax.swing.GroupLayout panTranslatorLayout = new javax.swing.GroupLayout(panTranslator);
	        panTranslator.setLayout(panTranslatorLayout);
	        panTranslatorLayout.setHorizontalGroup(
	            panTranslatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addComponent(separator)
	            .addGroup(panTranslatorLayout.createSequentialGroup()
	                .addGap(10, 10, 10)
	                .addGroup(panTranslatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(panTranslatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
	                        .addGroup(panTranslatorLayout.createSequentialGroup()
	                            .addGroup(panTranslatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
	                                .addComponent(lblTitre2)
	                                .addGroup(panTranslatorLayout.createSequentialGroup()
	                                    .addComponent(lblTitre3)
	                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                                    .addComponent(btnConvertir, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
	                                .addComponent(comboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
	                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                            .addComponent(spTranslate))
	                        .addGroup(panTranslatorLayout.createSequentialGroup()
	                            .addGap(136, 136, 136)
	                            .addComponent(spTranslateLTL, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)))
	                    .addComponent(lblTitre1, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addGroup(panTranslatorLayout.createSequentialGroup()
	                        .addGap(184, 184, 184)
	                        .addComponent(LabTitre))
	                    .addGroup(panTranslatorLayout.createSequentialGroup()
	                        .addComponent(bClick)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addComponent(textFiel_path, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE))
	                    .addComponent(textFiel_path_LTL, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(lblInputLTL)
	                    .addComponent(lblTitreTranslate, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(lblTitreLTL, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addGroup(panTranslatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(btnClearLTL, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(btnSaveLTL, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addContainerGap())
	        );
	        panTranslatorLayout.setVerticalGroup(
	            panTranslatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(panTranslatorLayout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(LabTitre, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(lblTitre1)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addGroup(panTranslatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(bClick)
	                    .addComponent(textFiel_path, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addGap(32, 32, 32)
	                .addComponent(lblInputLTL)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(textFiel_path_LTL, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(27, 27, 27)
	                .addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addGroup(panTranslatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(panTranslatorLayout.createSequentialGroup()
	                        .addComponent(lblTitreTranslate)
	                        .addGap(18, 18, 18)
	                        .addComponent(lblTitre2)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addComponent(comboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                        .addGap(21, 21, 21)
	                        .addGroup(panTranslatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                            .addComponent(lblTitre3)
	                            .addComponent(btnConvertir, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
	                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panTranslatorLayout.createSequentialGroup()
	                        .addGap(0, 15, Short.MAX_VALUE)
	                        .addGroup(panTranslatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                            .addComponent(spTranslate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
	                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panTranslatorLayout.createSequentialGroup()
	                                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
	                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))))
	                .addGroup(panTranslatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
	                    .addGroup(panTranslatorLayout.createSequentialGroup()
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addComponent(lblTitreLTL, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addComponent(spTranslateLTL, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
	                    .addGroup(panTranslatorLayout.createSequentialGroup()
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                        .addComponent(btnSaveLTL, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addComponent(btnClearLTL, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
	                .addContainerGap(36, Short.MAX_VALUE))
	        ); 
		tabbedPane.addTab("Trace Translator", panTranslator);

		panGenerator.setBackground(new java.awt.Color(255, 255, 204));

		lblTitreGen.setFont(new java.awt.Font("Arial Black", 0, 13)); 
		lblTitreGen.setForeground(new java.awt.Color(0, 51, 102));
		lblTitreGen.setText("You can generate random event trace");

		paneLeft.setBackground(new java.awt.Color(250, 218, 144));

		lblMenu.setFont(new java.awt.Font("Arial Black", 0, 13));
		lblMenu.setForeground(new java.awt.Color(0, 51, 102));
		lblMenu.setText(" Menu general");
		btnMenu1.setFont(new java.awt.Font("Arial Black", 0, 12));
		btnMenu1.setText("Amazone");
		btnMenu2.setFont(new java.awt.Font("Arial Black", 0, 12));
		btnMenu2.setText("Book store");
		btnMenu3.setFont(new java.awt.Font("Arial Black", 0, 12));
		btnMenu3.setText("Cycle Generator");
		btnMenu4.setFont(new java.awt.Font("Arial Black", 0, 12)); 
        btnMenu4.setText("LLRP Generator");
        btnMenu5.setFont(new java.awt.Font("Arial Black", 0, 12)); 
        btnMenu5.setText("Petri Net");
		btnMenu1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnMenuActionPerformed(evt);
			}
		});
		btnMenu2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnMenuActionPerformed(evt);
			}
		});
		btnMenu3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnMenuActionPerformed(evt);
			}
		});
		btnMenu4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnMenuActionPerformed(evt);
			}
		});
		btnMenu5.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnMenuActionPerformed(evt);
			}
		});
          
		textAreaHelpGen.setColumns(20);
        textAreaHelpGen.setRows(5);
        spHelpGen.setViewportView(textAreaHelpGen);
        
        lblHelpGen.setFont(new java.awt.Font("Arial Black", 0, 11));
        lblHelpGen.setText("   Help about input parameters");
          

	        javax.swing.GroupLayout paneLeftLayout = new javax.swing.GroupLayout(paneLeft);
	        paneLeft.setLayout(paneLeftLayout);
	        paneLeftLayout.setHorizontalGroup(
	                paneLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                .addComponent(spHelpGen)
	                .addComponent(separatorGenMenu)
	                .addGroup(paneLeftLayout.createSequentialGroup()
	                    .addContainerGap()
	                    .addGroup(paneLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                        .addGroup(paneLeftLayout.createSequentialGroup()
	                            .addGroup(paneLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
	                                .addComponent(lblMenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                                .addComponent(btnMenu5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                                .addComponent(btnMenu4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                                .addComponent(btnMenu3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                                .addComponent(btnMenu2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                                .addComponent(btnMenu1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                            .addGap(0, 0, Short.MAX_VALUE))
	                        .addComponent(lblHelpGen, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE))
	                    .addContainerGap())
	            );
	            paneLeftLayout.setVerticalGroup(
	                paneLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                .addGroup(paneLeftLayout.createSequentialGroup()
	                    .addContainerGap()
	                    .addComponent(lblMenu)
	                    .addGap(27, 27, 27)
	                    .addComponent(btnMenu1)
	                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                    .addComponent(btnMenu2)
	                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                    .addComponent(btnMenu3)
	                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                    .addComponent(btnMenu4)
	                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                    .addComponent(btnMenu5)
	                    .addGap(15, 15, 15)
	                    .addComponent(separatorGenMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(lblHelpGen, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                    .addComponent(spHelpGen, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE))
	            );
	        splitPane.setLeftComponent(paneLeft);

	        paneRight.setBackground(new java.awt.Color(247, 223, 157));

	        btnGen.setFont(new java.awt.Font("Arial Black", 0, 11)); 
	        btnGen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/generate.GIF"))); 
	        btnGen.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
                  btnConvertirActionPerformed(evt);
              }
	        } );

	        resultGen.setColumns(20);
	        resultGen.setRows(5);
	        spResultGen.setViewportView(resultGen);

	        lblGenerate.setFont(new java.awt.Font("Arial Black", 0, 11));
	        lblNbTraces.setFont(new java.awt.Font("Arial Black", 0, 11));  
	        lblGenerate.setText(" Generate !!!");
	        lblNbTraces.setText("Enter the number of traces") ;

	        btnClearGen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/clear.GIF")));
	        btnClearGen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                  btnClearActionPerformed(evt);
              }
	        });

	        btnSaveGen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/sav.GIF"))); 
	        btnSaveGen.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
                  btnSaveActionPerformed(evt);
              }
	        } );
	        
	       
	        paneMenu.setBackground(new java.awt.Color(247, 223, 157));

	        lblParametres.setFont(new java.awt.Font("Arial Black", 0, 11));
	        lblParametres.setText(" Input parameters ");

	        javax.swing.GroupLayout paneMenuLayout = new javax.swing.GroupLayout(paneMenu);
	        paneMenu.setLayout(paneMenuLayout);
	        paneMenuLayout.setHorizontalGroup(
	        		 paneMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                 .addGap(0, 514, Short.MAX_VALUE)
	        );
	        paneMenuLayout.setVerticalGroup(
	        		 paneMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                 .addGap(0, 112, Short.MAX_VALUE)
	        );
	        
	        javax.swing.GroupLayout paneRightLayout = new javax.swing.GroupLayout(paneRight);
	        paneRight.setLayout(paneRightLayout);
	        paneRightLayout.setHorizontalGroup(
	            paneRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(paneRightLayout.createSequentialGroup()
	                .addGroup(paneRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(paneRightLayout.createSequentialGroup()
	                        .addContainerGap()
	                        .addGroup(paneRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
	                            .addGroup(paneRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                                .addComponent(lblNbTraces)
	                                .addGroup(paneRightLayout.createSequentialGroup()
	                                    .addGroup(paneRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
	                                        .addComponent(lblGenerate, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
	                                        .addComponent(tfNbTraces, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
	                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                                    .addComponent(btnGen, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
	                            .addGroup(paneRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                                .addComponent(btnSaveGen, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
	                                .addComponent(btnClearGen, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                        .addComponent(spResultGen))
	                    .addGroup(paneRightLayout.createSequentialGroup()
	                        .addGap(27, 27, 27)
	                        .addGroup(paneRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                            .addComponent(paneMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                            .addComponent(lblParametres, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
	                        .addGap(0, 472, Short.MAX_VALUE)))
	                .addContainerGap())
	        );
	        paneRightLayout.setVerticalGroup(
	            paneRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(paneRightLayout.createSequentialGroup()
	                .addGap(27, 27, 27)
	                .addComponent(lblParametres)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(paneMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addGroup(paneRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(paneRightLayout.createSequentialGroup()
	                        .addComponent(lblNbTraces)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addComponent(tfNbTraces, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                        .addGap(33, 33, 33)
	                        .addGroup(paneRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                            .addComponent(lblGenerate, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
	                            .addComponent(btnGen, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 206, Short.MAX_VALUE)
	                        .addComponent(btnSaveGen, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addComponent(btnClearGen))
	                    .addComponent(spResultGen))
	                .addContainerGap())
	        );

	        splitPane.setRightComponent(paneRight);
	       
	        javax.swing.GroupLayout panGeneratorLayout = new javax.swing.GroupLayout(panGenerator);
	        panGenerator.setLayout(panGeneratorLayout);
	        panGeneratorLayout.setHorizontalGroup(
	            panGeneratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(panGeneratorLayout.createSequentialGroup()
	                .addGroup(panGeneratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(panGeneratorLayout.createSequentialGroup()
	                        .addGap(251, 251, 251)
	                        .addComponent(lblTitreGen, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
	                        .addGap(0, 0, Short.MAX_VALUE))
	                    .addGroup(panGeneratorLayout.createSequentialGroup()
	                        .addContainerGap()
	                        .addComponent(splitPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
	                .addContainerGap())
	        );
	        panGeneratorLayout.setVerticalGroup(
	            panGeneratorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(panGeneratorLayout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(lblTitreGen, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(splitPane))
	        );

	        tabbedPane.addTab("Trace Generator", panGenerator);
	        
	        //Runtime pane
	        paneRuntime.setBackground(new java.awt.Color(255, 255, 204));
	        lblTitreRunTime.setFont(new java.awt.Font("Arial Black", 0, 13));
	        lblTitreRunTime.setForeground(new java.awt.Color(0, 51, 102));
	        lblTitreRunTime.setText("Execution time of various software");
	        lblRepertoireRun.setText("Directory : ");
	        
	        btnRepertoireRun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/search.GIF"))); 
	        btnRepertoireRun.setToolTipText("Search...");
	        
	        paneTools.setBackground(new java.awt.Color(255, 255, 204));
	        lblTools.setText("Tools :");
	        
	        checkJavaMop.setBackground(new java.awt.Color(255, 255, 204));
	        checkJavaMop.setText("Java-MOP");

	        checkBeepBeep.setBackground(new java.awt.Color(255, 255, 204));
	        checkBeepBeep.setText("BeepBeep");

	        checkMaude.setBackground(new java.awt.Color(255, 255, 204));
	        checkMaude.setText("Maude");

	        checkMonopoly.setBackground(new java.awt.Color(255, 255, 204));
	        checkMonopoly.setText("Monopoly");

	        checkMySQL.setBackground(new java.awt.Color(255, 255, 204));
	        checkMySQL.setText("MySQL");

	        checkNuSMV.setBackground(new java.awt.Color(255, 255, 204));
	        checkNuSMV.setText("NuSMV");

	        checkProM.setBackground(new java.awt.Color(255, 255, 204));
	        checkProM.setText("ProM");
	        checkSaxon.setBackground(new java.awt.Color(255, 255, 204));
	        checkSaxon.setText("Saxon");

	        checkSpin.setBackground(new java.awt.Color(255, 255, 204));
	        checkSpin.setText("SPIN");

	        btnGO.setText("GO");

	        btnStop.setText("STOP");
	        
	        javax.swing.GroupLayout paneToolsLayout = new javax.swing.GroupLayout(paneTools);
	        paneTools.setLayout(paneToolsLayout);
	        paneToolsLayout.setHorizontalGroup(
	            paneToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(paneToolsLayout.createSequentialGroup()
	                .addGroup(paneToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(paneToolsLayout.createSequentialGroup()
	                        .addContainerGap()
	                        .addComponent(lblTools))
	                    .addGroup(paneToolsLayout.createSequentialGroup()
	                        .addGap(50, 50, 50)
	                        .addGroup(paneToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                            .addComponent(checkBeepBeep)
	                            .addComponent(checkJavaMop))
	                        .addGap(10, 10, 10)
	                        .addGroup(paneToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                            .addComponent(checkMaude)
	                            .addComponent(checkMonopoly))
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addGroup(paneToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                            .addComponent(checkMySQL)
	                            .addComponent(checkNuSMV))
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addGroup(paneToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                            .addComponent(checkProM)
	                            .addComponent(checkSaxon))
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addComponent(checkSpin)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
	                        .addComponent(btnGO)))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(btnStop)
	                .addGap(14, 14, 14))
	        );
	        paneToolsLayout.setVerticalGroup(
	            paneToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(paneToolsLayout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(paneToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneToolsLayout.createSequentialGroup()
	                        .addComponent(lblTools)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
	                        .addGroup(paneToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                            .addGroup(paneToolsLayout.createSequentialGroup()
	                                .addComponent(checkProM)
	                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                                .addComponent(checkSaxon))
	                            .addGroup(paneToolsLayout.createSequentialGroup()
	                                .addComponent(checkMySQL)
	                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                                .addComponent(checkNuSMV))
	                            .addGroup(paneToolsLayout.createSequentialGroup()
	                                .addComponent(checkMaude)
	                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                                .addComponent(checkMonopoly))
	                            .addGroup(paneToolsLayout.createSequentialGroup()
	                                .addComponent(checkBeepBeep)
	                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                                .addComponent(checkJavaMop))
	                            .addGroup(paneToolsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                                .addComponent(btnGO)
	                                .addComponent(btnStop)))
	                        .addGap(15, 15, 15))
	                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneToolsLayout.createSequentialGroup()
	                        .addGap(0, 0, Short.MAX_VALUE)
	                        .addComponent(checkSpin)
	                        .addGap(29, 29, 29))))
	        );
	        paneTable.setBackground(new java.awt.Color(255, 255, 204));

	        executionTable.setModel(new javax.swing.table.DefaultTableModel(
	            new Object [][] {
	                {null, null, null, null, null, null, null, null, null, null},
	                {null, null, null, null, null, null, null, null, null, null},
	                {null, null, null, null, null, null, null, null, null, null},
	                {null, null, null, null, null, null, null, null, null, null}
	            },
	            new String [] {
	                "Trace", "Maude", "BeepBeep", "Java-MOP", "MonoPoly", "MySQL", "NuSMV", "ProM", "Saxon", "Spin"
	            }
	        ));
	        executionTable.setColumnSelectionAllowed(true);
	        spTable.setViewportView(executionTable);
	        executionTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

	        btnSaveRun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/sav.GIF"))); 

	        javax.swing.GroupLayout paneTableLayout = new javax.swing.GroupLayout(paneTable);
	        paneTable.setLayout(paneTableLayout);
	        paneTableLayout.setHorizontalGroup(
	            paneTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(paneTableLayout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(spTable, javax.swing.GroupLayout.DEFAULT_SIZE, 694, Short.MAX_VALUE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(btnSaveRun, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addContainerGap())
	        );
	        paneTableLayout.setVerticalGroup(
	            paneTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneTableLayout.createSequentialGroup()
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                .addGroup(paneTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addComponent(spTable, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(btnSaveRun, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addGap(335, 335, 335))
	        );

	        btnBuilding.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/build.GIF"))); 

	        javax.swing.GroupLayout paneGraphLayout = new javax.swing.GroupLayout(paneGraph);
	        paneGraph.setLayout(paneGraphLayout);
	        paneGraphLayout.setHorizontalGroup(
	            paneGraphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(paneGraphLayout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(btnBuilding, javax.swing.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
	                .addContainerGap())
	        );
	        paneGraphLayout.setVerticalGroup(
	            paneGraphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(paneGraphLayout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(btnBuilding)
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        );
	        
	        javax.swing.GroupLayout paneRuntimeLayout = new javax.swing.GroupLayout(paneRuntime);
	        paneRuntime.setLayout(paneRuntimeLayout);
	        paneRuntimeLayout.setHorizontalGroup(
	            paneRuntimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(paneRuntimeLayout.createSequentialGroup()
	                .addGroup(paneRuntimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(paneRuntimeLayout.createSequentialGroup()
	                        .addContainerGap()
	                        .addComponent(separatorRun))
	                    .addGroup(paneRuntimeLayout.createSequentialGroup()
	                        .addGroup(paneRuntimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
	                            .addGroup(paneRuntimeLayout.createSequentialGroup()
	                                .addGap(148, 148, 148)
	                                .addComponent(lblTitreRunTime, javax.swing.GroupLayout.PREFERRED_SIZE, 552, javax.swing.GroupLayout.PREFERRED_SIZE))
	                            .addGroup(paneRuntimeLayout.createSequentialGroup()
	                                .addContainerGap()
	                                .addComponent(lblRepertoireRun)
	                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                                .addComponent(tfRepertoireRun, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
	                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                                .addComponent(btnRepertoireRun, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
	                            .addGroup(paneRuntimeLayout.createSequentialGroup()
	                                .addContainerGap()
	                                .addComponent(paneTools, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                            .addGroup(paneRuntimeLayout.createSequentialGroup()
	                                .addContainerGap()
	                                .addComponent(paneTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                            .addGroup(paneRuntimeLayout.createSequentialGroup()
	                                .addContainerGap()
	                                .addComponent(paneGraph, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
	                        .addGap(0, 127, Short.MAX_VALUE)))
	                .addContainerGap())
	        );
	        paneRuntimeLayout.setVerticalGroup(
	            paneRuntimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(paneRuntimeLayout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(lblTitreRunTime, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(22, 22, 22)
	                .addGroup(paneRuntimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
	                    .addGroup(paneRuntimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                        .addComponent(tfRepertoireRun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                        .addComponent(lblRepertoireRun))
	                    .addComponent(btnRepertoireRun, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addGap(9, 9, 9)
	                .addComponent(paneTools, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(separatorRun, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(22, 22, 22)
	                .addComponent(paneTable, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(18, 18, 18)
	                .addComponent(paneGraph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addContainerGap(40, Short.MAX_VALUE))
	        );
	        tabbedPane.addTab("Runtime", paneRuntime);

	        lblTitre.setFont(new java.awt.Font("Arial", 1, 18)); 
	        lblTitre.setForeground(new java.awt.Color(0, 51, 102));
	        lblTitre.setText("  Trace event");

	        menuFile.setText("File");

	        itemQuit.setText("Quit");
	        itemQuit.addActionListener(new java.awt.event.ActionListener() {
  			public void actionPerformed(java.awt.event.ActionEvent evt) {
  				itemQuitActionPerformed(evt);
  				}
  			});
	        menuFile.add(itemQuit);

	        menuBar.add(menuFile);

	        menuHelp.setText("Help");

	        itemAbout.setText("About");
	        itemAbout.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				itemAboutActionPerformed(evt);
				}
	        });
	        menuHelp.add(itemAbout);

	        menuBar.add(menuHelp);

	        setJMenuBar(menuBar);

	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addComponent(tabbedPane, javax.swing.GroupLayout.Alignment.TRAILING)
	            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                .addComponent(lblTitre, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(323, 323, 323))
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addComponent(lblTitre)
	                .addGap(3, 3, 3)
	                .addComponent(tabbedPane))
	        );

	       pack();
	    }
    /**
	 * Guess output format
	 * 
	 * @param evt
	 */
    private void bClickActionPerformed(java.awt.event.ActionEvent evt) {

    	filechoose = new JFileChooser();
    	filechoose.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		status = filechoose.showOpenDialog(this);
		File fileTemp ;

		if (fieldsVisible
				&& (this.textFiel_path.getText().equalsIgnoreCase(""))) {
			this.fieldsVisible = false;
			this.initFields(fieldsVisible);
		}

		if (status == JFileChooser.APPROVE_OPTION) {
			fileTemp = filechoose.getSelectedFile();

			// Define and process command line arguments
			path_file = this.getDirectory(fileTemp.getAbsolutePath().replace("\\", "/"), false);
			myFile = new File(path_file);

			try {
				this.textFiel_path.setText(path_file);
				this.fieldsVisible = true;
				this.initFields(fieldsVisible);

			} catch (Exception ex) {
				System.out.println("problem accessing file : "
						+ myFile.getAbsolutePath());
			}

		}
    }
    /**
	 * Exits the application
	 * 
	 * @param evt
	 */
	private void itemQuitActionPerformed(java.awt.event.ActionEvent evt) {

		System.exit(0);
	}
	
	/**
	 * Guess output format by filename extension
	 * 
	 * @param evt
	 */
    private void comboBoxActionPerformed(java.awt.event.ActionEvent evt) 
    {
			output_format = comboBox.getSelectedItem().toString().toLowerCase();
	}
    /**
	 * window information
	 * 
	 * @param evt
	 */
	private void itemAboutActionPerformed(java.awt.event.ActionEvent evt) {
		JOptionPane.showMessageDialog(this, " First version of the  converter "
				+ " even trace\n" + "(C) 2012 \n", "Information",
				JOptionPane.INFORMATION_MESSAGE, null);
	}

    /**
	 * translate the trace output format
	 * 
	 * @param evt
	 */
	private void btnConvertirActionPerformed(java.awt.event.ActionEvent evt) {
	
		

		if (evt.getSource() == btnConvertir) 
		{
			if(output_format.equalsIgnoreCase("monpoly"))
			{
				btnSaveLTL.setEnabled(true);
			}else
			{
				btnSaveLTL.setEnabled(false);
			}
			
			if((! textFiel_path_LTL.getText().isEmpty()))
			{
				this.translateLTL();
			}
			this.translate();
		
		} else if (evt.getSource() == btnGen)
		{
			this.generateTrace();
		
		}

	}
	/**
	 * build the set of input parameters and their help corresponding 
	 * @param evt
	 */
	@SuppressWarnings("unchecked")
	private void  btnMenuActionPerformed(java.awt.event.ActionEvent  evt)
	{
		listParam = new Vector<String>();
		btnGen.setEnabled(true);
		resultGen.setEditable(true);
		resultGen.setText("");
		btnClearGen.setEnabled(true);
		lblParametres.setVisible(true);
		tfNbTraces.setText("");
		
		
		//Selected menu
		if (evt.getSource() == btnMenu1) {
			this.selectedMenu = "amazone";
		} else if (evt.getSource() == btnMenu2) {
			this.selectedMenu = "bookstore";
		} else if (evt.getSource() == btnMenu3) {
			this.selectedMenu = "cyclegenerator";
		} else if (evt.getSource() == btnMenu4) {
			this.selectedMenu = "llrpgenerator";
		} else if (evt.getSource() == btnMenu5) {
			this.selectedMenu = "petrinet";
		}
		
		// Determine which translator to initialize
		t_gen = initializeGenerator(selectedMenu);

		Options opt = t_gen.getCommandLineOptions();
		Iterator<Option> liste = opt.getOptions().iterator();
		
		//build the input parameter list
		while (liste.hasNext()) {
			
			Option param = liste.next();
			
			if (( param.getOpt() != null)
					&& (!param.getOpt().equalsIgnoreCase("t"))&& (!param.getOpt().equalsIgnoreCase("s"))) 
			{
				listParam.add(param.getOpt() + "/" + param.getDescription());
			}
		}
		
		this.addParamField(listParam);
		this.helpParameters(listParam);
		
	}
	/**
	 * Can translate the input file in the choice of output file
	 */
	private void  translate ()
	{
		
		//Build the file list of directory
		String [] listFile = myFile.list();
		String [] listNameFile = new String [listFile.length] ;
		outTrace = new Vector<String>();
		String ficOutTrace = ""  ;
		
		for(int j =0 ; j< listFile.length ; j++)
		{
			File fic = new File(listFile[j]);
			String strFile = path_file +"/"+fic.getName();
			listNameFile[j] = strFile;			
		}
		
		for (int i = 0; i < listNameFile.length; i++) 
		{

			//String input_format = getExtension(path_file);
			String input_format = getExtension(listNameFile[i]);
			// Determine which trace reader to initialize
			TraceReader reader = initializeReader(input_format);
			if (reader != null) 
			{
				// Instantiate the proper trace reader and checks that the trace
				// exists reader.setEventTagName(event_tag_name);
				
				File in_f = new File(listNameFile[i]);
				if ((!in_f.exists()) || (!in_f.canRead()))
				{
					System.err
							.println("ERROR: Input file not found or Input file is not readable");
					System.exit(1);
				}

				// Determine which translator to initialize
				Translator trans = initializeTranslator(output_format);
				if (trans == null) {
					System.err.println("ERROR: Unrecognized output format");
					System.exit(1);
				}

				// Translate the trace into the output format
				EventTrace trace = reader.parseEventTrace(in_f);
				String out_trace = trans.translateTrace(trace);

				outTrace.add(out_trace);
				ficOutTrace = ficOutTrace.concat(out_trace).concat("\n");
			}
		}
		// display the trace
		if (status == JFileChooser.APPROVE_OPTION) {
		
			try 
			{
				txtArea.setText(ficOutTrace);
				if(!ficOutTrace.isEmpty())
				{
					btnSave.setEnabled(true);
				}
			} catch (Exception ex) {
				System.out.println("problem accessing file with "
						+ this.textFiel_path.getText());
			}
		} else if (status == JFileChooser.CANCEL_OPTION) {
			System.out.println("Opening the file has been canceled !!");
		}
	}
	
	/**
	 * Can translate the input LTL property to the choice of output
	 */
	private void translateLTL() 
	{
		String strLTL = textFiel_path_LTL.getText();
		String str_out;
		// Determine which translator to initialize
		Translator tr = initializeTranslator(output_format);
		if (tr != null) 
		{
			try 
			{
				Operator o = Operator.parseFromString(strLTL);
				str_out = tr.translateFormula(o);
				if (str_out != null) 
				{
					txtAreaLTL.setText(str_out);
				} else 
				{
					txtAreaLTL.setText("M�thode non implement�e !!!");
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Allow to generate random traces
	 */
	private void generateTrace()
	{
		Vector<String> listP =this.buildParameters(listParam);
		outTraceGen = new Vector<String>();
		int taille = listP.size() * 2, k = 0,cp = 1,tf =0 ;
		String [] args = new String [taille] ;
		
		while( k < listP.size())
		{
			args [tf] = listP.get(k);
			args [cp] = listTextFields.get(k).getText()  ;
			cp = cp + 2 ;
			tf = tf + 2;
			k ++ ;
		}
		
		String vChaine = "";
		
		
		
		// Determine which translator to initialize
		t_gen = initializeGenerator(selectedMenu);

		Options options = t_gen.getCommandLineOptions();
		options.addOption("h", "help", false, "Show help");
		CommandLineParser parser = new PosixParser();
		CommandLine c_line = null;
		try {
			// parse the command line arguments
			c_line = parser.parse(options, args);
		} catch (Exception exp) {
			// oops, something went wrong
			System.err.println("ERROR: " + exp.getMessage() + "\n");
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp(t_gen.getAppName() + " [options]", options);
			System.exit(1);
		}
		assert c_line != null;
		if (c_line.hasOption("h")) {
			HelpFormatter hf = new HelpFormatter();
			hf.printHelp(t_gen.getAppName() + " [options]", options);
			System.exit(1);
		}
		int cpt = Integer.parseInt(tfNbTraces.getText());
		
		for (int i = 0; i < cpt; i++) 
		{
			t_gen.initialize(c_line);
			EventTrace trace = t_gen.generate();
			Translator trans = new XmlTranslator();
			String out_trace = trans.translateTrace(trace);
			outTraceGen.add(out_trace) ;

			vChaine = vChaine.concat(out_trace).concat("\n");
		}
		
		
		if( !vChaine.isEmpty())
		{
			resultGen.setText(vChaine);
			System.out.println(vChaine);
			btnSaveGen.setEnabled(true);
		}else
		{
			resultGen.setText("Aucune trace est g�n�r�e !!!");
		}
	}
	
	/**
	 * Build list of parameter names
	 * @param listParam
	 * @return
	 */
	private Vector<String>buildParameters (Vector<String> listParam)
	{
		Vector<String>  tab = new Vector<String>() ;
		String moins = "-";
		String [] temp;
		
		for( int i = 0 ; i < listParam.size() ; i++)
		{
			temp = listParam.get(i).split("/");
			tab.add( moins.concat(temp[0]));
		}
		
		return tab;
	}
	/**
	  * Initialize the Generator, based on the string passed from the
	 * command-line
	 * 
	 * @param output_format
	 *            The menu name
	 * @return An instance of Generator
	 */
	private static TraceGenerator initializeGenerator(String output_format) {
		TraceGenerator t_gen = null;

		if (output_format.compareToIgnoreCase("amazone") == 0) {
			t_gen = new AmazonEcsGenerator();
		} else if (output_format.compareToIgnoreCase("bookstore") == 0) {
			t_gen = new BookstoreGenerator();
		} else if (output_format.compareToIgnoreCase("cyclegenerator") == 0) {
			t_gen = new CycleGenerator();
		} else if (output_format.compareToIgnoreCase("llrpgenerator") == 0) {
			t_gen = new LlrpGenerator();
		} else if (output_format.compareToIgnoreCase("petrinet") == 0) {
			t_gen = new PetriNetGenerator();
		} 
		return t_gen;
	}
	/**
	 * Erase JtxtArea
	 * 
	 * @param evt
	 */
	private void btnClearActionPerformed(java.awt.event.ActionEvent evt) 
	{
		if(evt.getSource() == btnClear)
		{
			txtArea.setText("");
			btnSave.setEnabled(false);
		}else if(evt.getSource()== btnClearLTL)
		{
			txtAreaLTL.setText("");
			btnSaveLTL.setEnabled(false);
		}else if(evt.getSource()== btnClearGen)
		{
			resultGen.setText("");
			btnSaveGen.setEnabled(false);
		}
    }

  
    /**
	 * Save the result in the file
	 * 
	 * @param evt
	 */
	private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {
		
		
		if(output_format.isEmpty())
		{
			output_format = "xml";
		}
		String strExt = "Save File (."+output_format+" )";
		FileFilter filter = new FileNameExtensionFilter(strExt, output_format);
		
		JFileChooser fileSave = new JFileChooser();
		fileSave.setAcceptAllFileFilterUsed(false);
		fileSave.addChoosableFileFilter(filter);
		FileOutputStream fo;
		
		String str = "", type;
		int res = fileSave.showSaveDialog(this);

		if (res == JFileChooser.APPROVE_OPTION) {
			String nameFile = fileSave.getSelectedFile().getPath()+ "."+output_format; 
			File file =  new File(nameFile);

			if ((file.exists()) || (file.canWrite())) {
				JOptionPane.showMessageDialog(this,
						"Output file exist or can't write", "Error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					
					if ((evt.getSource() == btnSave))
					{
						type = "Translator";
						this.saveTrace(file, type);
						txtArea.setText("");
						if(!output_format.equalsIgnoreCase("monpoly"))
						{
							txtAreaLTL.setText("");
						}
					} else if (evt.getSource() == btnSaveLTL) {
						fo = new FileOutputStream(file);
						str = txtAreaLTL.getText();
						fo.write(str.getBytes());
						fo.close();
						txtAreaLTL.setText("");
					}
					else if (evt.getSource() == btnSaveGen)
					{
						type = "Generator";
						fileSave.removeChoosableFileFilter(filter);
						fileSave.setAcceptAllFileFilterUsed(true);
						File fileGen = new File( fileSave.getSelectedFile().getPath()+ "."+output_format);
						this.saveTrace(fileGen, type);
						resultGen.setText("");
					}


					

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} else {
			System.out.println("Save command cancelled by user.");
		}
	}
/**
 * Traces recorded in a directory
 * @param fos
 */
	private void saveTrace(File fos, String type) {
		FileOutputStream fo;
		String str, repertoire = this.getDirectory(fos.getAbsolutePath()
				.replace("\\", "/"), true);
		
		Boolean bMonopoly = (type.equalsIgnoreCase("Translator") &&
							(!output_format.equalsIgnoreCase("monpoly")));
		String strLTL = "\n".concat(txtAreaLTL.getText());
		
		Vector<String> tempTrace = new Vector<String>();
		
		if( type.equalsIgnoreCase("Translator"))
		{
			tempTrace = outTrace ;
		}else
		{
			tempTrace = outTraceGen ;
		}
		if (!tempTrace.isEmpty()) 
		{
			String nameFic = fos.getName();
			File file = new File(repertoire);

			if (file.mkdir()) 
			{
				for (int i = 0; i < tempTrace.size(); i++) 
				{
					str = repertoire.concat(this.buildFile(nameFic, i));

					try {

						fo = new FileOutputStream(new File(str));
						fo.write(tempTrace.get(i).getBytes());
						if(bMonopoly)
						{
							fo.write(strLTL.getBytes());
						}
						fo.close();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Build a file name with the number
	 * @param nameFile
	 * @param rand
	 * @return
	 */
	private String buildFile( String nameFile, int rand)
	{
		String [] listString = nameFile.split("\\.");
		String result = listString[0] + rand +"."+listString[1];
		
		return result;
	}
	/**
	 * Built the path of the directory backup
	 * @param path
	 * @return
	 */
	private String getDirectory(String path , boolean btrace )
	{
		String [] list = path.split("/");
		int rand = randomGenerator.nextInt(100);
		String rep = "" , 
			  repTrace = "/Trace_"+output_format.toUpperCase().concat("_")+rand+"/";
		
		int nbElement , taille = 0 ;
		if(list.length > 0)
		{
			nbElement = list.length ;
			String lastElt = list[nbElement - 1] ;
			
			if( lastElt.split("\\.").length > 1)
			{
				taille = nbElement - 1 ;
			}else {
				taille = nbElement  ;
			}
			
			for(int i = 0 ; i < taille; i++)
			{
				rep = rep.concat(list[i]);
				if(i != (taille-1  ))
				{
					rep = rep.concat("/");
				}
			}
			
			if(btrace)
			{
				rep = rep.concat(repTrace);
			}
		}
		
		
		return rep ;
	}
	/**
	 * Initialize the trace reader, based on the string passed from the
	 * command-line
	 * 
	 * @param input_format
	 *            The reader name
	 * @return An instance of TraceFactory
	 */
	private static TraceReader initializeReader(String input_format) {
		TraceReader tf = null;
		if ((input_format.compareToIgnoreCase("xml") == 0)
				|| (input_format.compareToIgnoreCase("xes") == 0)) {
			tf = new XmlTraceReader();
		} else if (input_format.compareToIgnoreCase("sql") == 0) {
			tf = new SqlTraceReader();
		} else if (input_format.compareToIgnoreCase("csv") == 0) {

			tf = new CsvTraceReader();
		}
		return tf;
	}
	
	/**
	 * Initialize the translator, based on the string passed from the
	 * command-line
	 * 
	 * @param output_format
	 *            The translator name
	 * @return An instance of Translator
	 */
	private static Translator initializeTranslator(String output_format) {
		Translator trans = null;

		if (output_format.compareToIgnoreCase("smv") == 0) {
			trans = new SmvTranslator();
		} else if (output_format.compareToIgnoreCase("sql") == 0) {
			trans = new SqlTranslator();
		} else if (output_format.compareToIgnoreCase("pml") == 0) {
			trans = new PromelaTranslator();
		} else if (output_format.compareToIgnoreCase("json") == 0) {
			trans = new JsonTranslator();
		} else if (output_format.compareToIgnoreCase("xml") == 0) {
			trans = new XmlTranslator();
		} else if (output_format.compareToIgnoreCase("monpoly") == 0) {
			trans = new MonpolyTranslator();
		} else if (output_format.compareToIgnoreCase("xes") == 0) {
			trans = new XesTranslator();
		}else if (output_format.compareToIgnoreCase("mop") == 0) {
			trans = new JavaMopTranslator();
		} else if (output_format.compareToIgnoreCase("maude") == 0) {
			trans = new MaudeTranslator();
		}
		return trans;
	}
	/**
	 * Return the filename extension (rightmost substring after last period)
	 * 
	 * @return The extension
	 */
	private static String getExtension(String filename) {
		int p = filename.lastIndexOf(".");
		if (p == -1)
			return filename;
		return filename.substring(p + 1);
	}
	/**
	 * We allow building  the set of input parameters depending on the choice 
	 * @param listParam
	 */
	private void addParamField(Vector<String> listParam) {
		JLabel lbl;
		JTextField tf;
		 listLabel = new Vector<JLabel>();
		listTextFields = new Vector<JTextField>();
		
		// build list of parameter fields
		if (!listParam.isEmpty()) {
			String lblParam = "lblParam";
			String tfParam = "tfParam", strName;
			String[] tab;

			for (int i = 0; i < listParam.size(); i++) {
				strName = listParam.get(i);
				
				tab = strName.split("/");

				lbl = new JLabel();
				lbl.setName(lblParam.concat(Integer.toString(i)));
				lbl.setText(tab[0]);
				lbl.setToolTipText(tab[1]);
				listLabel.add(lbl);

				tf = new JTextField();
				tf.setName(tfParam.concat(selectedMenu).concat(Integer.toString(i)));
				listTextFields.add(tf);

			}
		}

		paneMenu.removeAll();
		// create a GroupLayout object associate with the panel
		GroupLayout paneMenuLayout = new GroupLayout(paneMenu);
		paneMenu.setLayout(paneMenuLayout);
		paneMenuLayout.setAutoCreateGaps(true);
		paneMenuLayout.setAutoCreateContainerGaps(true);

		// create the Horizontal and Vertical and sequential group
		GroupLayout.ParallelGroup horizontalSeqGrp = paneMenuLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup verticalSeqGrp = paneMenuLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING);
		// create two parallel group for adding the components in the horizontal
		// sequential group
		GroupLayout.SequentialGroup hParallelGroupLbl = paneMenuLayout
				.createSequentialGroup();
		GroupLayout.SequentialGroup hParallelGroupTF = paneMenuLayout
				.createSequentialGroup();

		for (int j = 0; j < listParam.size(); j++) {
			hParallelGroupLbl.addComponent(listLabel.get(j),
					GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE);
			hParallelGroupTF.addComponent(listTextFields.get(j),
					GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE);

		}
		// add two parallel groups sequentially in the horizontal sequential group
		horizontalSeqGrp.addGroup(hParallelGroupLbl);
		horizontalSeqGrp.addGroup(hParallelGroupTF);

		// create one parallel group for adding the components in the vertical sequential group
		GroupLayout.ParallelGroup vparallelGroupLBL = paneMenuLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup vparallelGroupTF = paneMenuLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.SequentialGroup vparallelGroup = paneMenuLayout
				.createSequentialGroup();
		// add the components
		for (int j = 0; j < listParam.size(); j++) 
		{
			vparallelGroupLBL.addComponent(listLabel.get(j),
					GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
					GroupLayout.PREFERRED_SIZE);
			vparallelGroupTF.addComponent(listTextFields.get(j),
					GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
					GroupLayout.PREFERRED_SIZE);
		}

		// add this parallel group in the vertical sequential group
		vparallelGroup.addGroup(vparallelGroupLBL);
		vparallelGroup.addGroup(vparallelGroupTF);
		verticalSeqGrp.addGroup(vparallelGroup);

		// finally set the both sequential group to the grpLayout object
		paneMenuLayout.setHorizontalGroup(horizontalSeqGrp);
		paneMenuLayout.setVerticalGroup(verticalSeqGrp);
		

	}
	/**
	 * Help that give us the meaning of all input parameters
	 * @param listParams
	 */
	private void helpParameters( Vector<String > listParams)
	{
		String strEgal = " = ";
		String strTemps ,strResult ="",str;
		if(! listParams.isEmpty())
		{
			for( int i = 0 ; i < listParams.size() ; i++)
			{
				str = "";
				strTemps = listParams.get(i).replaceAll("/", strEgal).concat("\n");
				String [] tab = strTemps.split(" ");
				
				int taille = (tab.length) / 2 ;
				for(int j = 0 ; j < tab.length ; j++ )
				{
					if(j == taille)
					{
						str = str.concat("\n");
					}
					str = str.concat(tab[j]).concat(" "); 
				}
				
				strResult = strResult.concat(str).concat("\n");
			}
			textAreaHelpGen.setText(strResult);
		}
	}
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
       
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(IHM_TraceEvent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IHM_TraceEvent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IHM_TraceEvent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IHM_TraceEvent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
       

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
            	IHM_TraceEvent traceEvent = new IHM_TraceEvent();
            	traceEvent.setLocationRelativeTo(null);
                traceEvent.setVisible(true);
            }
        });
    }
   
}
