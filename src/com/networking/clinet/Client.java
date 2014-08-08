package com.networking.clinet;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

public class Client extends JFrame {
	private JTextField txtPath;
	JButton btnBrowse;
	JButton btnSend;
	String serverName;
	private Socket socket;
	final JFileChooser fc = new JFileChooser();
	DataOutputStream dataOutputStream;
	public Client(String name) {
		super("Client");
		serverName = name;
		txtPath = new JTextField(30);
		// txtPath.setSize(200,30);
		setLayout(new FlowLayout());
		btnBrowse = new JButton("Browse");
		btnSend = new JButton("Send");
		btnBrowse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == btnBrowse) {
					int returnVal = fc.showOpenDialog(Client.this);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						txtPath.setText(file.getAbsolutePath());
						// log.append("Opening: " + file.getName() + "." +
						// newline);
					} else {
						// log.append("Open command cancelled by user." +
						// newline);
					}
				}

			}
		});
		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == btnSend) {
					sendFile(txtPath.getText().toString());
				}

			}
		});
		add(txtPath);
		add(btnBrowse);
		add(btnSend);
		setSize(500, 500);
		setVisible(true);
		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				try {
					if (socket != null) {
						socket.close();
						displayMessage("socket closed");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void windowClosed(WindowEvent arg0) {

			}

			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	private void getStream() {
		try {
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			displayMessage("got data output streams");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void runClinet() {
		connectToServer();
		getStream();
	}

	private void connectToServer() {
		displayMessage("Attemting connection");
		try {
			socket = new Socket(InetAddress.getByName(serverName), 12345);

			displayMessage("connected to " + socket.getInetAddress().getHostName());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void sendFile(String path) {
		DataInputStream dataInputStream;
		if (path.length() == 0) {
			
			JOptionPane.showMessageDialog(Client.this,"Please insert a file path.");
		} else {
			displayMessage("inside send file function..");
			File myFile = new File(path);
			try {
				dataOutputStream.writeUTF(myFile.getName());
				dataOutputStream.writeLong(myFile.length());

				byte[] fileData = new byte[(int) myFile.length()];
				dataInputStream = new DataInputStream(new FileInputStream(myFile));
				dataInputStream.readFully(fileData);

				dataInputStream.close();

				dataOutputStream.write(fileData);
				JOptionPane.showMessageDialog(Client.this,"File Upload Complete");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void displayMessage(String str) {
		System.out.println(str);
	}

}
