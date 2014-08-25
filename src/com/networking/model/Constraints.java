package com.networking.model;

import java.awt.Component;
import java.io.File;
import java.lang.reflect.Field;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Constraints {

	Component parent;

	public Constraints(Component parComponent) {
		parent = parComponent;
	}

	// private String directory="";
	private String filetypes = "";
	private String fileNames = "";
	// private String students;
	// private String path;
	private double sizeMB = 2;
	// private int fileNum=10;
	private boolean CanUploadFolder = false;

	private boolean FileTypeInFolderOK = true;

	private int totalfileNumber, studentfileNumber;

	public void setFileTypes(String files) {
		this.filetypes = files;
	}

	public void setFileNames(String files) {
		this.fileNames = files;
	}

	public void setSizeMB(double size) {
		this.sizeMB = size;
	}

	public void setTotalFileNumber(int num) {
		this.totalfileNumber = num;
	}

	public void setStudentFileNumber(int num) {
		this.studentfileNumber = num;
	}

	public void setCanUploadFolder(boolean canFolder) {
		this.CanUploadFolder = canFolder;
	}

	public String getFileTypes() {
		return this.filetypes;
	}

	public String getFileNames() {
		return this.fileNames;
	}

	public double getSizeMB() {
		return this.sizeMB;
	}

	public int getTotalFileNumber() {
		return this.totalfileNumber;
	}

	public int getStudentFileNumber() {
		return this.studentfileNumber;
	}

	public boolean getCanUploadFolder() {
		return this.CanUploadFolder;
	}

	public boolean checkUplaodValidity(File file) {
		boolean result = checkFolderValidity(file);
		if (!result) {
			JOptionPane.showMessageDialog(parent, "Folder Uploading Not Permitted");
			return false;
		}
		// ordering of functions is very important here;
		result = checkFileSizeValidity(file);
		if (!result) {
			JOptionPane.showMessageDialog(parent, "File Size Too BIG");
			return false;
		}
		if (!FileTypeInFolderOK && !filetypes.equals("") && file.isDirectory()) {
			JOptionPane.showMessageDialog(parent, "File Type MisMatch in Folder");
			return false;
		}
		FileTypeInFolderOK = true;
		if (!filetypes.equals("") && file.isFile()) {
			result = checkFileTypeValidity(file.getName());
			if (!result) {
				JOptionPane.showMessageDialog(parent, "File Type MisMatch");
				return false;
			}
		}
		if (!fileNames.equals("") && file.isFile()) {
			result = checkFileNameValidity(file.getName());
			if (!result) {
				JOptionPane.showMessageDialog(parent, "File Name MisMatch");
				return false;
			}
		}
		return true;
	}

	public boolean checkFileTypeValidity(String fileName) {

		String ext = getFileExtension(fileName);
		String[] types = filetypes.split(",");
		for (int i = 0; i < types.length; i++) {
			if (ext.equals(types[i]))
				return true;
		}
		return false;
	}

	public boolean checkFileNameValidity(String fileName) {
		String[] names = fileNames.split(",");
		for (int i = 0; i < names.length; i++) {
			if (fileName.equals(names[i]))
				return true;
		}

		return false;
	}

	public boolean checkFileSizeValidity(File file) {
		double size = 0;
		if (file.isDirectory())
			size = folderSize(file,0);
		else
			size = file.length();
		size = file.length();
		System.out.println("" + size);
		//JOptionPane.showMessageDialog(parent, ""+size);
		if (size > (sizeMB*1024*1024)) {
			return false;
		} else
			return true;

	}

	public boolean checkFolderValidity(File file) {

		if (file.isDirectory() && !CanUploadFolder) {
			return false;
		} else
			return true;
	}

	private String getFileExtension(String fileName) {
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
			return fileName.substring(fileName.lastIndexOf("."));
		else
			return "";
	}

	public long folderSize(File directory,int length) {
		//long length = 0;
		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				boolean result = checkFileTypeValidity(file.getName());
				if (!result)
					FileTypeInFolderOK = false;
				length += file.length();
			} else
				length += folderSize(file,length);
		}
		return length;
	}

}
