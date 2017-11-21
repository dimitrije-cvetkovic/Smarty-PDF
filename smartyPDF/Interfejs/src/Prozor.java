import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

/*
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.UIManager;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
*/
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import classes.*;
import snippet.DokumentObjekat;

import javax.swing.border.CompoundBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.awt.Window.Type;

public class Prozor
{

	private JFrame frame;
	private JTextField textField;
	private JTextField txtTermin;
	private List<String> listaPDF;
	public  List<String> listaTXT;
	public ArrayList<DokumentObjekat> dob;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try 
				{
					Prozor window = new Prozor();
					window.frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Prozor()
	{
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() 
	{
		listaPDF = new ArrayList<String>();
		listaTXT = new ArrayList<String>();
		dob = new ArrayList<DokumentObjekat>();
		
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBackground(Color.GRAY);
		frame.setSize(1378, 740);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(51, 29, 383, 22);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(51, 62, 234, 204);
		frame.getContentPane().add(scrollPane);
		
		
		
		JList listURL = new JList<String>();
		scrollPane.setViewportView(listURL);
		listURL.setBorder(new LineBorder(new Color(0, 0, 0)));
		DefaultListModel listModelURL = new DefaultListModel();
		listURL.setModel(listModelURL);
		

		
		
		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				String urlZaPretragu;
				
				urlZaPretragu = textField.getText();
				String[] urlZaDownload = null;
				listaPDF = new ArrayList<String>();
				
				try
				{
					org.jsoup.nodes.Document doc = Jsoup.connect(urlZaPretragu).get();
					Pattern pat = Pattern.compile("^[\u0106-\u045f\u0000-\u007e]+\\.pdf$");
					Elements rez = doc.getElementsByAttributeValueMatching("href", pat);

					int i = 0;
					urlZaDownload = new String[rez.size()];
					for(Element el : rez)
						urlZaDownload[i++] = el.absUrl("href");
					
					int j = 0;
					
					for(String urlZaD : urlZaDownload)
					{
						ReadableByteChannel in = null;
						FileOutputStream fos = null;
						FileChannel out = null;
						
						try
						{
							in = Channels.newChannel(new URL(urlZaD.replaceAll(" ", "%20")).openStream());
							String pathForExtraction = urlZaD.substring(urlZaD.lastIndexOf('/')+1);
	
							String path = System.getProperty("user.dir") + "\\pdf\\" + pathForExtraction;
							listaPDF.add(path);
							fos = new FileOutputStream(path);
							out = fos.getChannel();
							out.transferFrom(in, 0, Long.MAX_VALUE);
							//edRez.setText(edRez.getText() + path + "\n");
							listModelURL.addElement(pathForExtraction);
							fos.close(); // mozda ne treba
							out.close();
							in.close();
						}
						catch (Exception ex)
						{
							System.out.println(ex.getStackTrace() + "\n" + urlZaD);
						}
					}
				}
				catch (Exception ex)
				{
					System.out.println(ex.getMessage());
				}
			}
		});
		btnStart.setBounds(455, 28, 89, 23);
		frame.getContentPane().add(btnStart);			
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(51, 386, 234, 284);
		frame.getContentPane().add(scrollPane_3);
		
		JList<String> listTekstualniFajlovi = new JList<String>();
		listTekstualniFajlovi.setBorder(new LineBorder(new Color(0, 0, 0)));
		scrollPane_3.setViewportView(listTekstualniFajlovi);
		DefaultListModel<String> listModelTekstualniFajlovi = new DefaultListModel<String>();
		listTekstualniFajlovi.setModel(listModelTekstualniFajlovi);
		
		JButton btnKreirajText = new JButton("Kreiraj tekstualne fajlove");
		btnKreirajText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("select folder");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				
				if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) 
				{
				
					int sizeListe = listaPDF.size();
					try
					{
					    for(int i=0; i<sizeListe; i++)
					    {
					    	BufferedWriter ifs;
	
								String pathIzListe = listaPDF.get(i);
						    	String pathZaUpis = pathIzListe.substring(pathIzListe.lastIndexOf('\\')+1);
						    	pathZaUpis = pathZaUpis.substring(0,pathZaUpis.lastIndexOf('.'));
						    	listaTXT.add(pathZaUpis+".txt");//mozda treba .txt
						    	listModelTekstualniFajlovi.addElement(pathZaUpis+".txt");
						    	//listaTXT.set(i,pathZaUpis+".txt");
						    	Tika tik = new Tika();
						       	tik.setMaxStringLength(5000000);
						       	Metadata metadata = new Metadata();
						       	//ovde samo odakle se cita ce biti gde je smesten dir 
						    	TikaInputStream reader = TikaInputStream.get(new File(pathIzListe), metadata);
						    	String contents = tik.parseToString(reader, metadata);
								ifs = new BufferedWriter(new FileWriter(chooser.getSelectedFile().getAbsolutePath()+"\\"+pathZaUpis+".txt"));
								ifs.write(contents);			      
								ifs.close();
	
					    }
					} 
					catch (IOException | TikaException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btnKreirajText.setBounds(312, 613, 232, 23);
		frame.getContentPane().add(btnKreirajText);
		
		JButton btnKreirajJSON = new JButton("Kreiraj JSON fajlove");
		btnKreirajJSON.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("select folder");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				
				if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) 
				{
					int sizeListe = listaPDF.size();
					try
					{
					    for(int i=0; i<sizeListe; i++)
					    {
								String pathIzListe = listaPDF.get(i);
						    	String pathZaUpis = pathIzListe.substring(pathIzListe.lastIndexOf('\\')+1);
						    	pathZaUpis = pathZaUpis.substring(0,pathZaUpis.lastIndexOf('.'));
						    	
						    	Tika tik = new Tika();
						       	tik.setMaxStringLength(5000000);
						       	Metadata metadata = new Metadata();
						       	//ovde samo odakle se cita ce biti gde je smesten dir 
						    	TikaInputStream reader = TikaInputStream.get(new File(pathIzListe), metadata);
						    	String contents = tik.parseToString(reader, metadata);
						    	
						    	dob.add(new DokumentObjekat());
						    	dob.get(i).setSadrzaj(contents);
						    	String[] metaNames = metadata.names();
						    	if(metaNames!=null)
						    	{
							    	for(int j=0; j<metadata.size(); j++)
							    	{
							    		dob.get(i).setMetadataNames(metaNames[j], j);
							    		dob.get(i).setMetadataValues(metadata.get(metaNames[j]), j);
							    	}
						    	}
						    	dob.get(i).setNaziv(pathIzListe);
						    	Map<String,Object> dokumentObjekatDataMap = new HashMap<String,Object>();
						    	dokumentObjekatDataMap.put("sadrzaj", dob.get(i).getSadrzaj());
						    	for(int j=0; j<metaNames.length; j++)
						    	{
						    		dokumentObjekatDataMap.put(dob.get(i).getMetadataNames(j), dob.get(i).getMetadataValues(j));
						    	}
						    	dokumentObjekatDataMap.put("naziv", dob.get(i).getNaziv());
						    	ObjectMapper mapper = new ObjectMapper();
						    	mapper.writeValue(new File(chooser.getSelectedFile().getAbsolutePath()+"\\"+pathZaUpis+".json"), dokumentObjekatDataMap);	
					    }
					} 
					catch (IOException | TikaException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btnKreirajJSON.setBounds(312, 647, 232, 23);
		frame.getContentPane().add(btnKreirajJSON);
		
		//TERMINI
		//TERMINI
		
		txtTermin = new JTextField();
		txtTermin.setBounds(312, 62, 122, 20);
		frame.getContentPane().add(txtTermin);
		txtTermin.setColumns(10);
		DefaultListModel listModelTermin = new DefaultListModel();
		
		JButton btnDodaj = new JButton("Dodaj");
		btnDodaj.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				listModelTermin.addElement(txtTermin.getText());
				txtTermin.setText("");
			}
		});
		btnDodaj.setBounds(455, 61, 89, 23);
		frame.getContentPane().add(btnDodaj);
				
		JButton btnSnimi = new JButton("Snimi listu termina");
		btnSnimi.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("select folder");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
				if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) 
				{
					try
					{
						BufferedWriter ss =new BufferedWriter(new FileWriter(chooser.getSelectedFile().getAbsolutePath()+".txt"));
						for(int i=0; i<listModelTermin.size(); i++)
						{
							ss.write(listModelTermin.get(i).toString());
							ss.newLine();
						}
						ss.close();
					} 
					catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btnSnimi.setBounds(312, 409, 230, 23);
		frame.getContentPane().add(btnSnimi);
		
		JButton btnUcitaj = new JButton("Ucitaj listu termina");
		btnUcitaj.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("select file");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
				
				if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) 
				{
					try
					{
						BufferedReader ss =new BufferedReader(new FileReader(chooser.getSelectedFile().getAbsolutePath()));
						String line;
						while((line=ss.readLine())!=null)
						{
							listModelTermin.addElement(line);
						}
						ss.close();
					} 
					catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btnUcitaj.setBounds(312, 443, 232, 23);
		frame.getContentPane().add(btnUcitaj);
		
		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(865, 93, 452, 577);
		frame.getContentPane().add(scrollPane_4);
		
		JList listTekst = new JList();
		listTekst.setBorder(new LineBorder(new Color(0, 0, 0)));
		scrollPane_4.setViewportView(listTekst);
		DefaultListModel listModelTekst = new DefaultListModel();
		listTekst.setModel(listModelTekst);
		
		JLabel lblNaslovTXTFajla = new JLabel("");
		lblNaslovTXTFajla.setHorizontalAlignment(SwingConstants.LEFT);
		lblNaslovTXTFajla.setForeground(Color.LIGHT_GRAY);
		lblNaslovTXTFajla.setBackground(Color.CYAN);
		lblNaslovTXTFajla.setBounds(892, 59, 425, 23);
		frame.getContentPane().add(lblNaslovTXTFajla);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(583, 93, 233, 577);
		frame.getContentPane().add(scrollPane_2);
		
		JList listUredjena = new JList();
		scrollPane_2.setViewportView(listUredjena);
		listUredjena.setBorder(new LineBorder(new Color(0, 0, 0)));
		DefaultListModel listModelUredjena = new DefaultListModel();
		listUredjena.setModel(listModelUredjena);
		listUredjena.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent arg0) 
			{
				// TODO Auto-generated method stub
				int index = listUredjena.getSelectedIndex();
				String fajl = listModelUredjena.get(index).toString();
				lblNaslovTXTFajla.setText(fajl);
				if(!listModelTekst.isEmpty())
				{
					listModelTekst.removeAllElements();
				}
				try
				{
					
					BufferedReader ss =new BufferedReader(new FileReader(System.getProperty("user.dir") + "\\txt\\" +fajl));
					String line;
					while((line=ss.readLine())!=null)
					{
						listModelTekst.addElement(line);
					}
					ss.close();
					/*if(panel.getComponentCount()>0)
					{
						panel.removeAll();
						JLabel lblMeta = new JLabel("Meta podaci");
						panel.add(lblMeta);
						for(int i=0; i<dob.get(i).getMetadataNamesCount(); i++)
						{
							JLabel lblMetaName = new JLabel(dob.get(i).getMetadataNames(i));
							panel.add(lblMetaName);
							lblMetaName.setHorizontalAlignment(SwingConstants.LEFT);
							JLabel lblMetaValue = new JLabel(dob.get(i).getMetadataValues(i));
							panel.add(lblMetaValue);
							lblMetaName.setHorizontalAlignment(SwingConstants.PREVIOUS);
						}
					}*/
				} 
				catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		
		JButton brnGenerisi = new JButton("Generisi uredjenje");
		brnGenerisi.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				//List<String> termini= (ArrayList<String>)listModelTermin.elements(); 
				List<String> termini = new ArrayList<String>();
				for(int j=0; j<listModelTermin.size();j++)
				{
					termini.add(j, listModelTermin.get(j).toString());
				}
				Runner r = new Runner();
				List<Result> rez = r.Sortiraj(listaTXT, termini);
				for(int i=0; i<rez.size(); i++)
				{
					listModelUredjena.add(i, rez.get(i).name);
				}
			}
		});
		brnGenerisi.setBounds(583, 61, 233, 23);
		frame.getContentPane().add(brnGenerisi);
		
		JButton btnObrisiListu = new JButton("Obrisi listu termina");
		btnObrisiListu.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				listModelTermin.removeAllElements();
			}
		});
		btnObrisiListu.setBounds(312, 477, 231, 23);
		frame.getContentPane().add(btnObrisiListu);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(312, 93, 230, 295);
		frame.getContentPane().add(scrollPane_1);
		
		JList listTermin = new JList();
		scrollPane_1.setViewportView(listTermin);
		listTermin.setModel(listModelTermin);
		listTermin.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		JButton btnSnimiListuPDF = new JButton("Snimi listu putanja pdf dokumenata");
		btnSnimiListuPDF.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("select folder");
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setAcceptAllFileFilterUsed(false);
				
				if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) 
				{
					try
					{
						BufferedWriter ss =new BufferedWriter(new FileWriter(chooser.getSelectedFile().getAbsolutePath()+".txt"));
						for(int i=0; i<listModelURL.size(); i++)
						{
							ss.write(listModelURL.get(i).toString());
							ss.newLine();
						}
						ss.close();
					} 
					catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btnSnimiListuPDF.setBounds(51, 277, 234, 23);
		frame.getContentPane().add(btnSnimiListuPDF);
		
		JButton btnUcitajListuPDF = new JButton("Ucitaj listu putanja pdf dokumenata");
		btnUcitajListuPDF.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("select file");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
				
				if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) 
				{
					try
					{
						BufferedReader ss =new BufferedReader(new FileReader(chooser.getSelectedFile().getAbsolutePath()));
						String line;
						while((line=ss.readLine())!=null)
						{
							listModelURL.addElement(line);
							String path = System.getProperty("user.dir") + "\\pdf\\" + line;
							listaPDF.add(path);
						}
						ss.close();
					} 
					catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btnUcitajListuPDF.setBounds(51, 311, 234, 23);
		frame.getContentPane().add(btnUcitajListuPDF);
		
		JButton btnObrisiListuPDF = new JButton("Obrisi listu putanja pdf dokumenata");
		btnObrisiListuPDF.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				listModelURL.removeAllElements();
				listaPDF.removeAll(listaPDF);
			}
		});
		btnObrisiListuPDF.setBounds(51, 345, 234, 23);
		frame.getContentPane().add(btnObrisiListuPDF);
		
		
		
		
		
		JButton btnObrisiListuTXT = new JButton("Obrisi listu tekstualnih fajlova");
		btnObrisiListuTXT.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				listModelTekstualniFajlovi.removeAllElements();
				listaTXT.removeAll(listaTXT);
			}
		});
		btnObrisiListuTXT.setBounds(312, 579, 232, 23);
		frame.getContentPane().add(btnObrisiListuTXT);
		
		JButton btnUcitajListuTXT = new JButton("Ucitaj listu tekstualnih fajlova");
		btnUcitajListuTXT.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("select file");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
				
				if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) 
				{
					try
					{
						BufferedReader ss =new BufferedReader(new FileReader(chooser.getSelectedFile().getAbsolutePath()));
						String line;
						while((line=ss.readLine())!=null)
						{
							listModelTekstualniFajlovi.addElement(line);
							listaTXT.add(line);
						}
						ss.close();
					} 
					catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btnUcitajListuTXT.setBounds(312, 545, 232, 23);
		frame.getContentPane().add(btnUcitajListuTXT);
		
		JButton btnSnimiListuTXT = new JButton("Snimi listu tekstualnih fajlova");
		btnSnimiListuTXT.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("select folder");
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setAcceptAllFileFilterUsed(false);
				
				if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) 
				{
					try
					{
						BufferedWriter ss =new BufferedWriter(new FileWriter(chooser.getSelectedFile().getAbsolutePath()+".txt"));
						for(int i=0; i<listModelTekstualniFajlovi.size(); i++)
						{
							ss.write(listModelTekstualniFajlovi.get(i).toString());
							ss.newLine();
						}
						ss.close();
					} 
					catch (IOException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btnSnimiListuTXT.setBounds(312, 511, 232, 23);
		frame.getContentPane().add(btnSnimiListuTXT);
		
		JButton btnObrisiUredjenje = new JButton("Obrisi generisano uredjenje");
		btnObrisiUredjenje.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				listModelUredjena.removeAllElements();
			}
		});
		btnObrisiUredjenje.setBounds(583, 29, 233, 23);
		frame.getContentPane().add(btnObrisiUredjenje);
		
		JButton btnNewButton = new JButton("Obrisi polje za citanje");
		btnNewButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				listModelTekst.removeAllElements();
				lblNaslovTXTFajla.setText("");
			}
		});
		btnNewButton.setBounds(992, 29, 180, 23);
		frame.getContentPane().add(btnNewButton);
		

	}
}
	
