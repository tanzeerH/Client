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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

import com.networking.clinet.Common.Common;
import com.networking.model.Constraints;
import com.networking.model.User;

public class Client extends JFrame implements ActionListener {

	JLabel lableIP;
	JLabel lblport;
	JLabel labelheader;
	JLabel labelStudentId;
	JTextField txIp;
	JTextField txtport;
	JTextField txtStdntId;
	JTextField txtPath;
	JButton btnBrowse;
	JButton btnSend;
	JButton btnConnect;
	String serverName;
	JTextArea clientText;
	private Socket socket;
	final JFileChooser fc = new JFileChooser();
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;
	DataInputStream fileDataInputStream;
	boolean isIdChecked = false;
	private User user;
	Constraints serverConstraints = new Constraints(Client.this);
	long fileSize = 0, offset = 0;
	int CHUNK_SIZE = 512;
	String fileName = "";
	File[] selectedFiles;
	int portnumber;

	public Client(String name) {
		super("Client");
		serverName = name;
		setUI();
	}

	public Client() {
		setUI();
	}

	private void setUI() {
		lableIP = new JLabel("Enter IP: ");
		labelStudentId = new JLabel("Enter Student Id: ");
		lblport = new JLabel("Enter Port: ");
		labelheader = new JLabel();
		txIp = new JTextField(35);
		txtStdntId = new JTextField(30);
		txtport = new JTextField(30);

		txtPath = new JTextField(35);
		// txtPath.setSize(200,30);
		setLayout(new FlowLayout());
		btnBrowse = new JButton("Browse");
		btnSend = new JButton("Send");
		clientText = new JTextArea(10, 40);

		labelheader.setVisible(false);
		btnSend.setVisible(false);
		btnBrowse.setVisible(false);
		txtPath.setVisible(false);

		btnConnect = new JButton(" Connect ");
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setMultiSelectionEnabled(true);
		btnBrowse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == btnBrowse) {
					int returnVal = fc.showOpenDialog(Client.this);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						selectedFiles = fc.getSelectedFiles();
						String name = "";
						for (int i = 0; i < selectedFiles.length; i++) {
							name = name + selectedFiles[i].getAbsolutePath()
									+ ",";

						}
						txtPath.setText(name.substring(0, name.length() - 1));
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
					// sendFile(txtPath.getText().toString());
					// uploadElement(txtPath.getText().toString()); //element
					// can be file or folders
					displayMessage("Send Button Clicked");
					requestConstraintsToServer();

				}

			}
		});
		btnConnect.addActionListener(this);
		add(lableIP);
		add(txIp);
		add(lblport);
		add(txtport);
		add(labelStudentId);
		add(txtStdntId);
		add(btnConnect);
		add(labelheader);
		add(txtPath);
		add(btnBrowse);
		add(btnSend);
		add(new JScrollPane(clientText));
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
						dataOutputStream
								.writeInt(Common.CONSTANT_CONNECTION_EXIT);
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

			}
		});

	}

	private void requestConstraintsToServer() {
		try {
			dataOutputStream.writeInt(Common.CONSTANT_REQUEST_CONSTRAINTS);
		} catch (IOException e) {

			e.printStackTrace();
		}
		displayMessage("Requesting Constraints to server");
		handleDataFromServer();
	}

	private void getStream() {
		try {
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataInputStream = new DataInputStream(socket.getInputStream());
			displayMessage("got data output streams");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void runClinet() {
		connectToServer();
		getStream();
		checkValidity();
		handleDataFromServer();
	}

	private void connectToServer() {
		displayMessage("Attemting connection");
		try {
			socket = new Socket(InetAddress.getByName(serverName), portnumber);

			displayMessage("connected to "
					+ socket.getInetAddress().getHostName());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void checkValidity() {
		try {
			dataOutputStream.writeInt(Common.CONSTANT_CHECK_VALIDITY);
			dataOutputStream.writeInt(user.getSId());
			displayMessage("Checking Validity");
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private void handleDataFromServer() {

		try {
			System.out.println("waiting for validation");
			int x = dataInputStream.readInt();
			// displayMessage(x + " Received from Server");
			if (x == Common.CONSTANT_ID_OK) {
				showAlertMessage("Connection Complete");
				// file upload UI visibile now
				btnSend.setVisible(true);
				btnBrowse.setVisible(true);
				txtPath.setVisible(true);
				labelheader.setVisible(true);
				labelheader.setText("Student Id: " + user.getSId());

				labelStudentId.setVisible(false);
				lableIP.setVisible(false);
				txtStdntId.setVisible(false);
				txIp.setVisible(false);
				btnConnect.setVisible(false);
				lblport.setVisible(false);
				txtport.setVisible(false);

			} else if (x == Common.CONSTANT_ID_INVALID) {
				showAlertMessage("Invalid Student ID");
			} else if (x == Common.CONSTANT_REQUEST_CONSTRAINTS) {
				displayMessage("Constraints Received");
				serverConstraints.setFileTypes(dataInputStream.readUTF());
				serverConstraints.setSizeMB(dataInputStream.readDouble());
				serverConstraints.setCanUploadFolder(dataInputStream
						.readBoolean());
				serverConstraints.setTotalFileNumber(dataInputStream.readInt());
				serverConstraints.setStudentFileNumber(dataInputStream
						.readInt());
				serverConstraints.setFileNames(dataInputStream.readUTF());
				if ((serverConstraints.getTotalFileNumber() + 1) < (serverConstraints
						.getStudentFileNumber() + selectedFiles.length))
					showAlertMessage("Total File Number Allowed exceded.");
				else {
					for (int i = 0; i < selectedFiles.length; i++) {
						if (serverConstraints
								.checkUplaodValidity(selectedFiles[i])) {
							uploadElement(selectedFiles[i].getAbsolutePath());
						}
					}
				}

			} else if (x == Common.CONSTANT_RESPONSE_FILE_BYTES) {
				sendChunk();
			} else if (x == Common.CONSTANT_FILE_NUM_OVERFLOW) {
				JOptionPane.showMessageDialog(Client.this,
						"Total File number  is exceded");
			} else if (x == Common.CONSTANT_RESPONSE_CONNECTION_REJECTED) {
				JOptionPane.showMessageDialog(Client.this,
						"Connection Request Rejected for ip/id inconsitency");
			}
			// txtPath.setText("data" + x);
			System.out.println("response: " + x);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private void uploadElement(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			uploadDirectory(file);
		} else if (file.isFile()) {
			// sendFile(file.getAbsolutePath());
			sendFileAsChunk(file.getAbsolutePath());
		}
		try {
			dataOutputStream.writeInt(Common.CONSTANT_FOLDER_COMPLETE);
			displayMessage("Writing comete flag to stream");
			JOptionPane.showMessageDialog(Client.this, "File Upload Complete");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void uploadDirectory(File directory) {
		try {
			displayMessage("Uploading Directory....");
			dataOutputStream.writeInt(Common.CONSTANT_FOLDER);
			dataOutputStream.writeUTF(directory.getAbsolutePath());
			dataOutputStream.writeUTF(directory.getName());
			System.out.println("uploading folder");
			File[] files = directory.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					uploadDirectory(files[i]);
				} else {
					sendFileAsChunk(files[i].getAbsolutePath());
				}
			}

			// dataOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendFile(String path) {
		DataInputStream dataInputStream;
		if (path.length() == 0) {

			JOptionPane.showMessageDialog(Client.this,
					"Please insert a file path.");
		} else {

			displayMessage("inside send file function..");
			File myFile = new File(path);
			try {
				dataOutputStream.writeInt(Common.CONSTANT_FILE);
				dataOutputStream.writeUTF(path);
				dataOutputStream.writeUTF(myFile.getName());
				dataOutputStream.writeLong(myFile.length());

				byte[] fileData = new byte[(int) myFile.length()];
				dataInputStream = new DataInputStream(new FileInputStream(
						myFile));
				dataInputStream.readFully(fileData);

				dataInputStream.close();

				dataOutputStream.write(fileData);
				// JOptionPane.showMessageDialog(Client.this,
				// "File Upload Complete");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void sendFileAsChunk(String path) {

		// initializing in start
		offset = 0;
		fileSize = 0;
		if (path.length() == 0) {

			JOptionPane.showMessageDialog(Client.this,
					"Please insert a file path.");
		} else {

			displayMessage("inside sendFileAsChunk function..");
			File myFile = new File(path);

			try {
				fileDataInputStream = new DataInputStream(new FileInputStream(
						myFile));
			} catch (FileNotFoundException e1) {

				e1.printStackTrace();
			}

			try {
				dataOutputStream.writeInt(Common.CONSTANT_FILE);

				dataOutputStream.writeUTF(path);
				dataOutputStream.writeUTF(myFile.getName());
				fileName = myFile.getName();
				fileSize = myFile.length();
				handleDataFromServer();
				/*
				 * byte[] fileData = new byte[(int) myFile.length()];
				 * dataInputStream = new DataInputStream(new
				 * FileInputStream(myFile));
				 * dataInputStream.readFully(fileData);
				 * 
				 * dataInputStream.close();
				 * 
				 * dataOutputStream.write(fileData); //
				 * JOptionPane.showMessageDialog(Client.this, //
				 * "File Upload Complete");
				 */

			} catch (IOException e) {

				e.printStackTrace();
			}

		}

	}

	private void sendChunk() {
		try {

			byte[] filedata = new byte[CHUNK_SIZE];

			displayMessage("offset: " + offset);
			int bytesRead = fileDataInputStream.read(filedata, 0, CHUNK_SIZE);
			if (bytesRead != -1) {
				dataOutputStream.writeInt(Common.CONSTANT_RESPONSE_FILE_BYTES);// sending
																				// //
																				// flag
				displayMessage(fileName + "     " + offset + "  " + bytesRead);
				dataOutputStream.writeUTF(fileName);
				displayMessage("" + offset);
				dataOutputStream.writeLong(offset);
				displayMessage("  " + bytesRead);
				dataOutputStream.writeInt(bytesRead);
				displayMessage(fileName + "     " + offset + "  " + bytesRead);
				dataOutputStream.write(filedata, 0, bytesRead);

				offset = offset + bytesRead;

				handleDataFromServer();
			} else {
				fileDataInputStream.close();
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private void displayMessage(String str) {
		System.out.println(str);
		clientText.append("\n" + str);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnConnect) {
			String ip = txIp.getText().toString();
			String id = txtStdntId.getText().toString();
			String port = txtport.getText().toString();
			if (ip.equals("")) {
				JOptionPane.showMessageDialog(Client.this,
						"Please Enter Ip Address.");
			} else if (id.equals(""))
				JOptionPane.showMessageDialog(Client.this,
						"Please Enter Student Id.");
			else if (port.equals(""))
				JOptionPane.showMessageDialog(Client.this,
						"Please Enter Port Number.");
			else {
				setServerName(ip);
				setport(Integer.valueOf(port));
				user = new User(Integer.valueOf(id), ip);
				runClinet();
			}
		}
	}

	private void setServerName(String name) {
		serverName = name;

	}

	private void setport(int p) {
		portnumber = p;

	}

	private void showAlertMessage(String str) {
		JOptionPane.showMessageDialog(Client.this, str);

	}

}
