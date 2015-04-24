package net.ohoooo.powermeter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.TooManyListenersException;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import javax.swing.JFrame;

public class Main {
	private static JFrame mainFrame;
	private static ChartCanvas cc;
	private static boolean mark = false;
	private static PrintWriter pw;
	private static SerialCom sc;

	public static void main(String[] args) {
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(
					"output.dat", true)));
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		pw.println("Start Logging "
				+ Calendar.getInstance().getTime().toString());

		mainFrame = new JFrame("Power Meter");
		mainFrame.setSize(1024, 850);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		cc = new ChartCanvas();
		mainFrame.getContentPane().add(cc);
		cc.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				mark = true;
			}
		});
		
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				System.out.println("Close");
				pw.close();
				sc.close();
			}
		});

		sc = new SerialCom();

		mainFrame.setVisible(true);

		while (true) {
			cc.repaint();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	static class SerialCom implements SerialPortEventListener {
		static SerialPort port;
		static BufferedReader reader;

		SerialCom() {
			try {
				CommPortIdentifier portId = CommPortIdentifier
						.getPortIdentifier("COM3");
				port = (SerialPort) portId.open("serial", 2000);

				port.setSerialPortParams(115200, SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

				reader = new BufferedReader(new InputStreamReader(
						port.getInputStream()));

				port.addEventListener(this);
				port.notifyOnDataAvailable(true);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		public void close(){
			try {
				port.notifyOnDataAvailable(false);
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			port.close();
		}

		@Override
		public void serialEvent(SerialPortEvent arg0) {
			if (arg0.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
				String buffer = "";
				try {
					while (reader.ready()) {
						buffer = reader.readLine();

						if (buffer.startsWith("T")) {
							String[] values = buffer.split("[TVIP]");
							if (values.length == 5) {
								long t = Long.parseLong(values[1], 16);
								int v = Integer.parseInt(values[2], 16);
								int a = Integer.parseInt(values[3], 16);
								int p = Integer.parseInt(values[4], 16);

								cc.setData(
										(float) ((v * 0.00125) * (a * 0.001)),
										mark);
								pw.println(t + "," + v + "," + a + "," + p
										+ "," + (v * 0.00125) + ","
										+ (a * 0.001) + "," + (p * 0.025) + ","
										+ (v * 0.00125 * a * 0.001) + ","
										+ (mark ? "1" : "0"));
								System.out.println((v * 0.00125) + "V\t"
										+ (a * 0.001) + "A\t" + (p * 0.025) + "W\t"
										+ (v * 0.00125 * a * 0.001) + "W");

								mark = false;
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}
}
