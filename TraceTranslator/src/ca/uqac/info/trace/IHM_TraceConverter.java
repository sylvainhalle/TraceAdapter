/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.uqac.info.trace;

import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import ca.uqac.info.ltl.Operator;
import ca.uqac.info.ltl.Operator.ParseException;
import ca.uqac.info.trace.conversion.JavaMopTranslator;
import ca.uqac.info.trace.conversion.JsonTranslator;
import ca.uqac.info.trace.conversion.MonpolyTranslator;
import ca.uqac.info.trace.conversion.PromelaTranslator;
import ca.uqac.info.trace.conversion.SmvTranslator;
import ca.uqac.info.trace.conversion.SqlTranslator;
import ca.uqac.info.trace.conversion.Translator;
import ca.uqac.info.trace.conversion.XesTranslator;
import ca.uqac.info.trace.conversion.XmlTranslator;

/**
 * @author Samatar
 */
public class IHM_TraceConverter extends javax.swing.JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected File myFile;
	protected JFileChooser filechoose;
	protected int status;
	protected boolean fieldsVisible;
	protected String path_file, output_format = "", input_filename = "";

	/**
	 * 
	 * Creates new form IHM_TraceConverter
	 */
	public IHM_TraceConverter() {
		this.initComponents();
		this.fieldsVisible = false;
		initFields(fieldsVisible);
	}

	private void initFields(boolean bVisible) {
		this.lblTitre2.setVisible(bVisible);
		this.comboBox.setVisible(bVisible);
		this.lblTitre3.setVisible(bVisible);
		btnConvertir.setVisible(bVisible);
		
		btnSave.setEnabled(false);
		btnSaveLTL.setEnabled(false);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initComponents() {
		panelPrincipal = new javax.swing.JPanel();
		LabTitre = new javax.swing.JLabel();
		lblTitre1 = new javax.swing.JLabel();
		bClick = new javax.swing.JButton();
		textFiel_path = new javax.swing.JTextField();
		lblTitre2 = new javax.swing.JLabel();
		comboBox = new javax.swing.JComboBox();
		btnConvertir = new javax.swing.JButton();
		lblTitre3 = new javax.swing.JLabel();
		spTranslate = new javax.swing.JScrollPane();
		txtArea = new javax.swing.JTextArea();
		btnClear_LTL = new javax.swing.JButton();
		lblFileLTL = new javax.swing.JLabel();
		textFiel_path_LTL = new javax.swing.JTextField();
		lblFormatLTL = new javax.swing.JLabel();
		comboBox_LTL = new javax.swing.JComboBox();
		lblStartConv = new javax.swing.JLabel();
		btnConvLTL = new javax.swing.JButton();
		spTranslateLTL = new javax.swing.JScrollPane();
		txtAreaLTL = new javax.swing.JTextArea();
		btnSaveLTL = new javax.swing.JButton();
		btnClear = new javax.swing.JButton();
		btnSave = new javax.swing.JButton();
		lblTitreLTL = new javax.swing.JLabel();
		menuBar = new javax.swing.JMenuBar();
		menu_file = new javax.swing.JMenu();
		menuItem_Quit = new javax.swing.JMenuItem();
		menu_Edit = new javax.swing.JMenu();
		apropos_Item = new javax.swing.JMenuItem();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("ConvEvenTrace");
		setAlwaysOnTop(true);

		panelPrincipal.setBackground(new java.awt.Color(204, 204, 204));
		panelPrincipal.setForeground(new java.awt.Color(204, 204, 255));

		LabTitre.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
		LabTitre.setText("                                Converter traces of events");
		LabTitre.setToolTipText("");

		lblTitre1.setText("   1.  Select File From Your Computer");

		bClick.setText("Browse");
		bClick.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				bClickActionPerformed(evt);
			}
		});

		lblTitre2.setText("2.  Select output format");

		comboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
				"", "XML", "SQL", "SMV", "MONPOLY", "XES", "MOP", "JSON","PML" }));
		comboBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				comboBoxActionPerformed(evt);
			}
		});

		btnConvertir.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/image/load.GIF"))); // NOI18N
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

		btnClear_LTL.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/image/clear.GIF"))); // NOI18N
		btnClear_LTL.setToolTipText("Clear");
		btnClear_LTL.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnClearActionPerformed(evt);
			}
		});

		lblFileLTL.setText("3. Enter LTL prperty");

		lblFormatLTL.setText("4. Select output format");

		comboBox_LTL.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "", "XML", "SQL", "SMV", "MONPOLY", "XES", "MOP", "JSON","PML" }));
		comboBox_LTL.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				comboBoxActionPerformed(evt);
			}
		});

		lblStartConv.setText("Start converting !!");

		btnConvLTL.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/image/load.GIF"))); // NOI18N�
		btnConvLTL.setToolTipText("Translate");
		btnConvLTL.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnConvertirActionPerformed(evt);
			}
		});

		txtAreaLTL.setColumns(20);
		txtAreaLTL.setRows(5);
	
		
		spTranslateLTL.setViewportView(txtAreaLTL);
		spTranslateLTL.setWheelScrollingEnabled(false);

		btnSaveLTL.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/image/save.GIF"))); // NOI18N
		btnSaveLTL.setToolTipText("Save");
		btnSaveLTL.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnSaveActionPerformed(evt);
			}
		});
		
		btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/image/clear.GIF"))); // NOI18N
		btnClear.setToolTipText("Clear");
		btnClear.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnClearActionPerformed(evt);
			}
		});

		btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/image/save.GIF"))); // NOI18N
		btnSave.setToolTipText("Save");
		btnSave.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnSaveActionPerformed(evt);
			}
		});

		lblTitreLTL.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
		lblTitreLTL.setText(" LTL property");

		javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(
				panelPrincipal);
		panelPrincipal.setLayout(panelPrincipalLayout);
		panelPrincipalLayout
				.setHorizontalGroup(panelPrincipalLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								panelPrincipalLayout
										.createSequentialGroup()
										.addGroup(
												panelPrincipalLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																panelPrincipalLayout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addGroup(
																				panelPrincipalLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup(
																								panelPrincipalLayout
																										.createSequentialGroup()
																										.addGroup(
																												panelPrincipalLayout
																														.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.LEADING,
																																false)
																														.addGroup(
																																panelPrincipalLayout
																																		.createSequentialGroup()
																																		.addComponent(
																																				comboBox,
																																				javax.swing.GroupLayout.PREFERRED_SIZE,
																																				91,
																																				javax.swing.GroupLayout.PREFERRED_SIZE)
																																		.addGap(41,
																																				41,
																																				41))
																														.addGroup(
																																javax.swing.GroupLayout.Alignment.TRAILING,
																																panelPrincipalLayout
																																		.createSequentialGroup()
																																		.addComponent(
																																				lblTitre3)
																																		.addPreferredGap(
																																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																																				javax.swing.GroupLayout.DEFAULT_SIZE,
																																				Short.MAX_VALUE)
																																		.addComponent(
																																				btnConvertir,
																																				javax.swing.GroupLayout.PREFERRED_SIZE,
																																				35,
																																				javax.swing.GroupLayout.PREFERRED_SIZE)
																																		.addPreferredGap(
																																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
																										.addComponent(
																												spTranslate))
																						.addGroup(
																								panelPrincipalLayout
																										.createSequentialGroup()
																										.addGroup(
																												panelPrincipalLayout
																														.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.LEADING)
																														.addGroup(
																																panelPrincipalLayout
																																		.createSequentialGroup()
																																		.addGroup(
																																				panelPrincipalLayout
																																						.createParallelGroup(
																																								javax.swing.GroupLayout.Alignment.LEADING)
																																						.addComponent(
																																								comboBox_LTL,
																																								javax.swing.GroupLayout.PREFERRED_SIZE,
																																								81,
																																								javax.swing.GroupLayout.PREFERRED_SIZE)
																																						.addComponent(
																																								lblFormatLTL))
																																		.addGap(31,
																																				31,
																																				31))
																														.addGroup(
																																javax.swing.GroupLayout.Alignment.TRAILING,
																																panelPrincipalLayout
																																		.createSequentialGroup()
																																		.addComponent(
																																				lblStartConv)
																																		.addPreferredGap(
																																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																																		.addComponent(
																																				btnConvLTL,
																																				javax.swing.GroupLayout.PREFERRED_SIZE,
																																				38,
																																				javax.swing.GroupLayout.PREFERRED_SIZE)
																																		.addPreferredGap(
																																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
																										.addComponent(
																												spTranslateLTL,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												474,
																												Short.MAX_VALUE)))
																		.addGroup(
																				panelPrincipalLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup(
																								panelPrincipalLayout
																										.createSequentialGroup()
																										.addGap(51,
																												51,
																												51)
																										.addComponent(
																												btnClear,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												51,
																												javax.swing.GroupLayout.PREFERRED_SIZE))
																						.addGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING,
																								panelPrincipalLayout
																										.createSequentialGroup()
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addGroup(
																												panelPrincipalLayout
																														.createParallelGroup(
																																javax.swing.GroupLayout.Alignment.LEADING)
																														.addGroup(
																																javax.swing.GroupLayout.Alignment.TRAILING,
																																panelPrincipalLayout
																																		.createParallelGroup(
																																				javax.swing.GroupLayout.Alignment.LEADING)
																																		.addComponent(
																																				btnSaveLTL,
																																				javax.swing.GroupLayout.PREFERRED_SIZE,
																																				96,
																																				javax.swing.GroupLayout.PREFERRED_SIZE)
																																		.addComponent(
																																				btnSave,
																																				javax.swing.GroupLayout.PREFERRED_SIZE,
																																				96,
																																				javax.swing.GroupLayout.PREFERRED_SIZE))
																														.addComponent(
																																btnClear_LTL,
																																javax.swing.GroupLayout.Alignment.TRAILING,
																																javax.swing.GroupLayout.PREFERRED_SIZE,
																																51,
																																javax.swing.GroupLayout.PREFERRED_SIZE)))))
														.addGroup(
																panelPrincipalLayout
																		.createSequentialGroup()
																		.addGroup(
																				panelPrincipalLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup(
																								panelPrincipalLayout
																										.createSequentialGroup()
																										.addContainerGap()
																										.addComponent(
																												lblTitre2))
																						.addGroup(
																								panelPrincipalLayout
																										.createSequentialGroup()
																										.addGap(96,
																												96,
																												96)
																										.addComponent(
																												LabTitre,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												315,
																												javax.swing.GroupLayout.PREFERRED_SIZE))
																						.addGroup(
																								panelPrincipalLayout
																										.createSequentialGroup()
																										.addContainerGap()
																										.addComponent(
																												bClick)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent(
																												textFiel_path,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												292,
																												javax.swing.GroupLayout.PREFERRED_SIZE))
																						.addGroup(
																								panelPrincipalLayout
																										.createSequentialGroup()
																										.addContainerGap()
																										.addComponent(
																												lblTitre1,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												322,
																												javax.swing.GroupLayout.PREFERRED_SIZE))
																						.addGroup(
																								panelPrincipalLayout
																										.createSequentialGroup()
																										.addContainerGap()
																										.addComponent(
																												textFiel_path_LTL,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												350,
																												javax.swing.GroupLayout.PREFERRED_SIZE))
																						.addGroup(
																								panelPrincipalLayout
																										.createSequentialGroup()
																										.addContainerGap()
																										.addComponent(
																												lblFileLTL))
																						.addGroup(
																								panelPrincipalLayout
																										.createSequentialGroup()
																										.addGap(257,
																												257,
																												257)
																										.addComponent(
																												lblTitreLTL,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												93,
																												javax.swing.GroupLayout.PREFERRED_SIZE)))
																		.addGap(0,
																				0,
																				Short.MAX_VALUE)))
										.addContainerGap()));
		panelPrincipalLayout
				.setVerticalGroup(panelPrincipalLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								panelPrincipalLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												LabTitre,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												31,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(lblTitre1)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												panelPrincipalLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(bClick)
														.addComponent(
																textFiel_path,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(31, 31, 31)
										.addGroup(
												panelPrincipalLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																panelPrincipalLayout
																		.createSequentialGroup()
																		.addComponent(
																				btnSave,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				24,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				btnClear,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				36,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(
																panelPrincipalLayout
																		.createSequentialGroup()
																		.addComponent(
																				lblTitre2)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				comboBox,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addGap(35,
																				35,
																				35)
																		.addGroup(
																				panelPrincipalLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								lblTitre3)
																						.addComponent(
																								btnConvertir,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								14,
																								javax.swing.GroupLayout.PREFERRED_SIZE)))
														.addComponent(
																spTranslate,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																165,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(69, 69, 69)
										.addComponent(lblTitreLTL)
										.addGap(17, 17, 17)
										.addComponent(lblFileLTL)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												panelPrincipalLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																textFiel_path_LTL,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																34,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(30, 30, 30)
										.addGroup(
												panelPrincipalLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																panelPrincipalLayout
																		.createSequentialGroup()
																		.addComponent(
																				btnSaveLTL,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				24,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				btnClear_LTL,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				36,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(
																panelPrincipalLayout
																		.createSequentialGroup()
																		.addComponent(
																				lblFormatLTL)
																		.addGroup(
																				panelPrincipalLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup(
																								panelPrincipalLayout
																										.createSequentialGroup()
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent(
																												comboBox_LTL,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												javax.swing.GroupLayout.PREFERRED_SIZE)
																										.addGap(26,
																												26,
																												26)
																										.addComponent(
																												lblStartConv))
																						.addGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING,
																								panelPrincipalLayout
																										.createSequentialGroup()
																										.addGap(52,
																												52,
																												52)
																										.addComponent(
																												btnConvLTL,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												14,
																												javax.swing.GroupLayout.PREFERRED_SIZE))))
														.addComponent(
																spTranslateLTL,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																169,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		menu_file.setText("File");

		menuItem_Quit.setText("Quit");
		menuItem_Quit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				menuItem_QuitActionPerformed(evt);
			}
		});
		menu_file.add(menuItem_Quit);

		menuBar.add(menu_file);

		menu_Edit.setText("Help");

		apropos_Item.setText("About");
		apropos_Item.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				apropos_ItemActionPerformed(evt);
			}
		});
		menu_Edit.add(apropos_Item);

		menuBar.add(menu_Edit);

		setJMenuBar(menuBar);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		pack();

	}

	/**
	 * Guess output format
	 * 
	 * @param evt
	 */
	private void bClickActionPerformed(java.awt.event.ActionEvent evt) {

		filechoose = new JFileChooser();
		status = filechoose.showOpenDialog(this);

		if (fieldsVisible
				&& (this.textFiel_path.getText().equalsIgnoreCase(""))) {
			this.fieldsVisible = false;
			this.initFields(fieldsVisible);
		}

		if (status == JFileChooser.APPROVE_OPTION) {
			myFile = filechoose.getSelectedFile();

			// Define and process command line arguments
			path_file = myFile.getAbsolutePath().replace("\\", "/");

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
	private void menuItem_QuitActionPerformed(java.awt.event.ActionEvent evt) {

		System.exit(0);
	}

	/**
	 * translate the trace output format
	 * 
	 * @param evt
	 */
	private void btnConvertirActionPerformed(java.awt.event.ActionEvent evt) {
		String input_format;

		if (evt.getSource() == btnConvertir) {
			input_format = getExtension(path_file);
			
			// Determine which trace reader to initialize
			TraceReader reader = initializeReader(input_format);
			if (reader == null) {
				System.err.println("ERROR: Unrecognized input format");
				System.exit(1);
			}

			// Instantiate the proper trace reader and checks that the trace
			// exists
			// reader.setEventTagName(event_tag_name);
			File in_f = new File(path_file);
			if ((!in_f.exists()) ||(!in_f.canRead())){
				System.err.println("ERROR: Input file not found or Input file is not readable");
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

			// display the trace
			if (status == JFileChooser.APPROVE_OPTION) {
			
				try 
				{
					txtArea.setText(out_trace);
					if(!out_trace.isEmpty())
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
		} else if (evt.getSource() == btnConvLTL) 
		{
			String strLTL = textFiel_path_LTL.getText();
			String str_out;
			// Determine which translator to initialize
			Translator tr = initializeTranslator(output_format);
			if (tr == null) {
				System.err.println("ERROR: Unrecognized output format");
				System.exit(1);
			}
			
			try 
			{
				Operator o = Operator.parseFromString(strLTL);
				str_out = tr.translateFormula(o);
				
				txtAreaLTL.setText(str_out);
				if(!str_out.isEmpty())
				{
					btnSaveLTL.setEnabled(true);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Erase JtxtArea
	 * 
	 * @param evt
	 */
	private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {
		if(evt.getSource() == btnClear)
		{
			txtArea.setText("");
			btnSave.setEnabled(false);
		}else if(evt.getSource()== btnClear_LTL)
		{
			txtAreaLTL.setText("");
			btnSaveLTL.setEnabled(false);
		}
	}

	/**
	 * Save the result in the file
	 * 
	 * @param evt
	 */
	private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {

		JFileChooser fileSave = new JFileChooser();
		FileOutputStream fo;
		String str = "" ;
		int res = fileSave.showSaveDialog(this);

		if (res == JFileChooser.APPROVE_OPTION) {
			File file = fileSave.getSelectedFile();
			
			if ((file.exists()) ||(file.canWrite()) )
			{
				JOptionPane.showMessageDialog(this,
					    "Output file exist or can't write",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
			}else
			{
				try
				{
					fo = new FileOutputStream(file);
					if(evt.getSource() == btnSave )
					{
						str = txtArea.getText();
					}else if (evt.getSource() == btnSaveLTL)
					{
						str = txtAreaLTL.getText();
					}
					
					fo.write(str.getBytes());
					txtArea.setText("");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
		} else {
			System.out.println("Save command cancelled by user.");
		}
	}

	/**
	 * Guess output format by filename extension
	 * 
	 * @param evt
	 */
	private void comboBoxActionPerformed(java.awt.event.ActionEvent evt) {
		if(evt.getSource() == comboBox)
		{
			output_format = comboBox.getSelectedItem().toString().toLowerCase();
		}else if (evt.getSource() == comboBox_LTL)
		{
			output_format = comboBox_LTL.getSelectedItem().toString().toLowerCase();
		}
	}

	/**
	 * window information
	 * 
	 * @param evt
	 */
	private void apropos_ItemActionPerformed(java.awt.event.ActionEvent evt) {
		JOptionPane.showMessageDialog(this, " First version of the  converter "
				+ " even trace\n" + "(C) 2012 \n", "Information",
				JOptionPane.INFORMATION_MESSAGE, null);
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {

		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
					.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(
					IHM_TraceConverter.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(
					IHM_TraceConverter.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(
					IHM_TraceConverter.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(
					IHM_TraceConverter.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		}

		/*
		 * Create and display the form
		 */
		java.awt.EventQueue.invokeLater(new Runnable() {

			IHM_TraceConverter fen = new IHM_TraceConverter();

			public void run() {
				fen.setVisible(true);
				fen.setLocationRelativeTo(null);
			}
		});
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

	// Variables declaration
	private javax.swing.JLabel LabTitre;
	private javax.swing.JMenuItem apropos_Item;
	private javax.swing.JButton bClick;
	private javax.swing.JButton btnClear;
	private javax.swing.JButton btnClear_LTL;
	private javax.swing.JButton btnConvertir;
	private javax.swing.JButton btnSave;
	private javax.swing.JButton btnSaveLTL;
	private javax.swing.JComboBox comboBox;
	private javax.swing.JComboBox comboBox_LTL;
	private javax.swing.JButton btnConvLTL;
	private javax.swing.JLabel lblTitre1;
	private javax.swing.JLabel lblTitre2;
	private javax.swing.JLabel lblTitre3;
	private javax.swing.JLabel lblStartConv;
	private javax.swing.JLabel lblTitreLTL;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JPanel panelPrincipal;
	private javax.swing.JScrollPane spTranslate;
	private javax.swing.JScrollPane spTranslateLTL;
	private javax.swing.JTextArea txtAreaLTL;
	private javax.swing.JLabel lblFormatLTL;
	private javax.swing.JLabel lblFileLTL;
	private javax.swing.JMenuItem menuItem_Quit;
	private javax.swing.JMenu menu_Edit;
	private javax.swing.JMenu menu_file;
	private javax.swing.JTextArea txtArea;
	private javax.swing.JTextField textFiel_path;
	private javax.swing.JTextField textFiel_path_LTL;

}
