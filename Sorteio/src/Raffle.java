import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

public class Raffle extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static String eventName = new String("EventName");
	static File namesFile = new File("names.txt");
	static{
		Scanner input=null;
		try {
			input = new Scanner(namesFile);
			eventName = input.nextLine();
			input.close();
		} catch (FileNotFoundException e) {
			eventName = "\"names.txt\" not found";
		}
	}
	ArrayList<String> names = new ArrayList<String>();
	JLabel winning[] = new JLabel[5];
	{
		for (int i = 0; i < winning.length; i++) {
			winning[i] = new JLabel(eventName);
		}
	}
	int mediumFontSize = 20;
	int bigFontSize = 40;
	int hugeFontSize = 60;
	String lastWinning [] = new String [winning.length];
	JProgressBar progressBar;
	JButton buttonRaffle = new JButton("Sortear");
	JLabel probability;
	public Raffle(){
		super(eventName);
		for (int i = 0; i < lastWinning.length; i++) {
			lastWinning[i] = eventName;
		}
		buttonRaffle.setFont(buttonRaffle.getFont().deriveFont(Font.BOLD, bigFontSize));

		Scanner input=null;
		try {
			input = new Scanner(namesFile);
			input.nextLine();
			while(input.hasNext()){
				String nome = input.nextLine();
				if(nome.length()!=0)
					names.add(nome);
			}
			input.close();
		} catch (FileNotFoundException e1) {
			buttonRaffle.setEnabled(false);
		}

		JPanel panel = new JPanel(new BorderLayout());
		add(panel);
		JPanel winningPanel = new JPanel(new GridLayout(0, 1, 6, 6));
		for (int i = 0; i < winning.length; i++) {
			winning[i].setForeground(Color.GRAY);
			winningPanel.add(winning[i]);
			winning[i].setFont(winning[i].getFont().deriveFont(Font.BOLD, bigFontSize));
		}
		buttonRaffle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				raffle();
			}
		});

		JPanel namesProgressiveButton = new JPanel(new BorderLayout());
		namesProgressiveButton.add(winningPanel, BorderLayout.CENTER);
		
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setFont(progressBar.getFont().deriveFont(Font.BOLD, mediumFontSize));

        JPanel progressiveButton = new JPanel();
        progressiveButton.add(buttonRaffle);
        progressiveButton.add(progressBar, BorderLayout.AFTER_LAST_LINE);
        probability = new JLabel("Sua chance é de 1/"+names.size());
		probability.setFont(probability.getFont().deriveFont(Font.BOLD, mediumFontSize));
        JPanel progressiveButton2 = new JPanel(new BorderLayout());
		progressiveButton2.add(progressiveButton);
		progressiveButton2.add(probability, BorderLayout.SOUTH);
		namesProgressiveButton.add(progressiveButton2, BorderLayout.SOUTH);
		
		panel.add(namesProgressiveButton, BorderLayout.CENTER);
		ImageIcon image = null;
		JLabel imageLabel = null;
		try {
			image = new ImageIcon(ImageIO.read(new File("eventImage.png")));
			imageLabel = new JLabel(image);
		} catch (IOException e) {
			imageLabel = new JLabel("<html>\"eventImage.png\" <br> not found</html>");
			imageLabel.setFont(imageLabel.getFont().deriveFont(Font.BOLD, mediumFontSize));

		}
		
		panel.add(imageLabel, BorderLayout.WEST);
		
		pack();
		double xSize = getWidth();
		double ySize = getHeight();

		Toolkit tk = Toolkit.getDefaultToolkit();
		Insets scnMax = tk.getScreenInsets(getGraphicsConfiguration());

		double windowRecuce = 0.05;
		
		int xScreenSize = ((int) tk.getScreenSize().getWidth() 
				- scnMax.left - scnMax.right);
		int yScreenSize = ((int) tk.getScreenSize().getHeight() 
				- scnMax.bottom - scnMax.top);

		if (xScreenSize <= xSize) {
			setSize((int) (xScreenSize - xScreenSize * windowRecuce), 
					(int) ySize);
		}
		if ((yScreenSize) <= ySize) {
			xSize = getWidth();
			setSize((int) xSize, (int) 
					(yScreenSize - yScreenSize * windowRecuce));
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	
	public void raffle(){
		Thread run = new Thread(new Runnable() {
			@Override
			public void run() {
				buttonRaffle.setEnabled(false);
		        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		        probability.setText("Sua chance é de 1/"+names.size());
				double progressiveCounter = 0;
				int timesRaffled = 25;
				int blinkTimes = 8;
				double progressiveMaxCounter = timesRaffled*2;
				for (int i = 0; i < winning.length; i++) {
					winning[i].setFont(winning[i].getFont().deriveFont(Font.BOLD, bigFontSize));
					winning[i].setForeground(Color.GRAY);
				}
				ArrayList<String> raffledNames = new ArrayList<String>();
				for (int i = 0; i < winning.length; i++) {
					raffledNames.add(lastWinning[i]);
				}
				int max = names.size();
				for (int i = 0; i < timesRaffled; i++) {
					double r = Math.random();
					raffledNames.add(names.get(((int)(max*r))));
				}
				for (int i = 0; i < raffledNames.size()-winning.length; i++) {
					try {
						Thread.sleep(150+15*i);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (int j = 0; j < winning.length; j++) {
						winning[j].setText(raffledNames.get(i+j));
						lastWinning[j] = raffledNames.get(i+j);
					}
					progressiveCounter++;
					progressBar.setValue((int) ((progressiveCounter/progressiveMaxCounter)*100));
				}
				int blinkTime = 150;
				int lastBlink = 0;
				for (int i = 0; i < timesRaffled; i++) {
					int randBlink = (int) (Math.random()*winning.length);
					lastBlink = randBlink;
					winning[randBlink].setForeground(Color.BLACK);
					try {
						Thread.sleep(blinkTime+15*i);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					winning[randBlink].setForeground(Color.GRAY);
					try {
						Thread.sleep(blinkTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					progressiveCounter++;
					progressBar.setValue((int) ((progressiveCounter/progressiveMaxCounter)*100));
				}
				for (int i = 0; i < names.size(); i++) {
					if (names.get(i).equalsIgnoreCase(winning[lastBlink].getText())) {
						names.remove(i);
					}
				}
				for (int i = 0; i < winning.length; i++) {
					winning[i].setFont(winning[i].getFont().deriveFont(Font.BOLD, bigFontSize));
				}
	            Toolkit.getDefaultToolkit().beep();

				for (int j = 0; j < blinkTimes; j++) {
					winning[lastBlink].setForeground(Color.RED);
					try {
						Thread.sleep(blinkTime+20*j);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					winning[lastBlink].setForeground(Color.BLACK);
					try {
						Thread.sleep(blinkTime+25*j);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				buttonRaffle.setEnabled(true);
		        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			}
		});
		run.start();

	}
	public static void main(String[] args) {
		try {
			String osName = System.getProperty("os.name").toString();
			if (osName != null && !osName.contains("Linux"))
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			else
				UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
    			new Raffle();
            }
        });
	}
}
