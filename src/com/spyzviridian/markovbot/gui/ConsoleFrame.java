package com.spyzviridian.markovbot.gui;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.border.TitledBorder;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

import com.spyzviridian.markovbot.config.Config;

import javax.swing.UIManager;

import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import java.awt.Font;

import javax.swing.JLabel;

import java.awt.SystemColor;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.image.BufferedImage;

import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;

import java.awt.Component;

import javax.swing.Box;

public class ConsoleFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6060784720469248516L;
	private JPanel contentPane;
	private JTextPane consoleText;
	private JScrollPane consolePanelScroll;
	private JLabel stateLabel;
	private JPanel noWrapPanel;
	private JPanel propertyPanelBorder;
	private JButton testTweetButton;
	private JPanel buttonsPanel;
	private JPanel bevelBorderButtonsPanel;
	private JTextField textField;
	private JButton blockUserButton;
	private JButton unblockUserButton;
	private JButton followUserButton;
	private JButton unfollowUserButton;
	private JButton tweetButton;
	private JPanel profilePanel;
	private JPanel etchedBorderProfilePanel;
	private JPanel profilePicPanel;
	private JPanel infoPanel;
	private JLabel botNameLabel;
	private JLabel lblTweets;
	private JLabel lblFollowing;
	private JLabel lblFollowers;
	private Component horizontalStrut;
	/**
	 * Create the frame.
	 */
	public ConsoleFrame() {
		// Título
		setTitle("Twitter Bot v0.5b");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.menu);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{378, 0, 0};
		gbl_contentPane.rowHeights = new int[]{535, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JPanel consolePanelBorder = new JPanel();
		consolePanelBorder.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Console", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_consolePanelBorder = new GridBagConstraints();
		gbc_consolePanelBorder.fill = GridBagConstraints.BOTH;
		gbc_consolePanelBorder.insets = new Insets(0, 0, 5, 5);
		gbc_consolePanelBorder.gridx = 0;
		gbc_consolePanelBorder.gridy = 0;
		contentPane.add(consolePanelBorder, gbc_consolePanelBorder);
		consolePanelBorder.setLayout(new BorderLayout());
		
		noWrapPanel = new JPanel(new BorderLayout());
		//consolePanelBorder.add(noWrapPanel);
		noWrapPanel.setLayout(new BorderLayout());
		
		consoleText = new JTextPane();
		consoleText.setAutoscrolls(false);
		//noWrapPanel.add(consoleText);
		consoleText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		consoleText.setEditable(false);
		consoleText.setBackground(new Color(36, 44, 41));
		
		consolePanelScroll = new JScrollPane(noWrapPanel);
		consolePanelScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		consolePanelBorder.add(consolePanelScroll);
		consolePanelScroll.setViewportView(consoleText);
		
		propertyPanelBorder = new JPanel();
		propertyPanelBorder.setBorder(new TitledBorder(null, "Twitter Bot", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_propertyPanelBorder = new GridBagConstraints();
		gbc_propertyPanelBorder.fill = GridBagConstraints.BOTH;
		gbc_propertyPanelBorder.insets = new Insets(0, 0, 5, 0);
		gbc_propertyPanelBorder.gridx = 1;
		gbc_propertyPanelBorder.gridy = 0;
		contentPane.add(propertyPanelBorder, gbc_propertyPanelBorder);
		GridBagLayout gbl_propertyPanelBorder = new GridBagLayout();
		gbl_propertyPanelBorder.columnWidths = new int[]{0, 0};
		gbl_propertyPanelBorder.rowHeights = new int[]{535, 0};
		gbl_propertyPanelBorder.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_propertyPanelBorder.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		propertyPanelBorder.setLayout(gbl_propertyPanelBorder);
		
		//consolePanelScroll.add(consoleText);
		
		JPanel propertyPanel = new JPanel();
		GridBagConstraints gbc_propertyPanel = new GridBagConstraints();
		gbc_propertyPanel.fill = GridBagConstraints.BOTH;
		gbc_propertyPanel.gridx = 0;
		gbc_propertyPanel.gridy = 0;
		propertyPanelBorder.add(propertyPanel, gbc_propertyPanel);
		GridBagLayout gbl_propertyPanel = new GridBagLayout();
		gbl_propertyPanel.columnWidths = new int[]{0, 0};
		gbl_propertyPanel.rowHeights = new int[]{0, 0, 0};
		gbl_propertyPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_propertyPanel.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		propertyPanel.setLayout(gbl_propertyPanel);
		
		etchedBorderProfilePanel = new JPanel();
		etchedBorderProfilePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		GridBagConstraints gbc_etchedBorderProfilePanel = new GridBagConstraints();
		gbc_etchedBorderProfilePanel.fill = GridBagConstraints.BOTH;
		gbc_etchedBorderProfilePanel.insets = new Insets(0, 0, 5, 0);
		gbc_etchedBorderProfilePanel.gridx = 0;
		gbc_etchedBorderProfilePanel.gridy = 0;
		propertyPanel.add(etchedBorderProfilePanel, gbc_etchedBorderProfilePanel);
		GridBagLayout gbl_etchedBorderProfilePanel = new GridBagLayout();
		gbl_etchedBorderProfilePanel.columnWidths = new int[]{0, 0};
		gbl_etchedBorderProfilePanel.rowHeights = new int[]{0, 0};
		gbl_etchedBorderProfilePanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_etchedBorderProfilePanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		etchedBorderProfilePanel.setLayout(gbl_etchedBorderProfilePanel);
		
		profilePanel = new JPanel();
		GridBagConstraints gbc_profilePanel = new GridBagConstraints();
		gbc_profilePanel.fill = GridBagConstraints.BOTH;
		gbc_profilePanel.gridx = 0;
		gbc_profilePanel.gridy = 0;
		etchedBorderProfilePanel.add(profilePanel, gbc_profilePanel);
		GridBagLayout gbl_profilePanel = new GridBagLayout();
		gbl_profilePanel.columnWidths = new int[]{100, 0};
		gbl_profilePanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_profilePanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_profilePanel.rowWeights = new double[]{1.0, 0.0, 1.0, Double.MIN_VALUE};
		profilePanel.setLayout(gbl_profilePanel);
		
		profilePicPanel = new JPanel();
		profilePicPanel.setBackground(new Color(0, 0, 0));
		GridBagConstraints gbc_profilePicPanel = new GridBagConstraints();
		gbc_profilePicPanel.insets = new Insets(0, 0, 5, 0);
		gbc_profilePicPanel.fill = GridBagConstraints.BOTH;
		gbc_profilePicPanel.gridx = 0;
		gbc_profilePicPanel.gridy = 0;
		profilePanel.add(profilePicPanel, gbc_profilePicPanel);
		profilePicPanel.setLayout(new BorderLayout(0, 0));
		
		botNameLabel = new JLabel("@bot");
		botNameLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_botNameLabel = new GridBagConstraints();
		gbc_botNameLabel.insets = new Insets(0, 0, 5, 0);
		gbc_botNameLabel.gridx = 0;
		gbc_botNameLabel.gridy = 1;
		profilePanel.add(botNameLabel, gbc_botNameLabel);
		
		infoPanel = new JPanel();
		infoPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_infoPanel = new GridBagConstraints();
		gbc_infoPanel.fill = GridBagConstraints.BOTH;
		gbc_infoPanel.gridx = 0;
		gbc_infoPanel.gridy = 2;
		profilePanel.add(infoPanel, gbc_infoPanel);
		GridBagLayout gbl_infoPanel = new GridBagLayout();
		gbl_infoPanel.columnWidths = new int[]{0, 0};
		gbl_infoPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_infoPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_infoPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		infoPanel.setLayout(gbl_infoPanel);
		
		lblTweets = new JLabel("Tweets: ...");
		lblTweets.setHorizontalAlignment(SwingConstants.LEFT);
		lblTweets.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_lblTweets = new GridBagConstraints();
		gbc_lblTweets.insets = new Insets(0, 5, 5, 0);
		gbc_lblTweets.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblTweets.gridx = 0;
		gbc_lblTweets.gridy = 0;
		infoPanel.add(lblTweets, gbc_lblTweets);
		
		lblFollowing = new JLabel("Following: ...");
		lblFollowing.setHorizontalAlignment(SwingConstants.LEFT);
		lblFollowing.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_lblFollowing = new GridBagConstraints();
		gbc_lblFollowing.insets = new Insets(0, 5, 5, 0);
		gbc_lblFollowing.anchor = GridBagConstraints.WEST;
		gbc_lblFollowing.gridx = 0;
		gbc_lblFollowing.gridy = 1;
		infoPanel.add(lblFollowing, gbc_lblFollowing);
		
		lblFollowers = new JLabel("Followers: ...");
		lblFollowers.setHorizontalAlignment(SwingConstants.LEFT);
		lblFollowers.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_lblFollowers = new GridBagConstraints();
		gbc_lblFollowers.insets = new Insets(0, 5, 5, 0);
		gbc_lblFollowers.anchor = GridBagConstraints.WEST;
		gbc_lblFollowers.gridx = 0;
		gbc_lblFollowers.gridy = 2;
		infoPanel.add(lblFollowers, gbc_lblFollowers);
		
		horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.gridx = 0;
		gbc_horizontalStrut.gridy = 3;
		infoPanel.add(horizontalStrut, gbc_horizontalStrut);
		
		bevelBorderButtonsPanel = new JPanel();
		bevelBorderButtonsPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_bevelBorderButtonsPanel = new GridBagConstraints();
		gbc_bevelBorderButtonsPanel.fill = GridBagConstraints.BOTH;
		gbc_bevelBorderButtonsPanel.gridx = 0;
		gbc_bevelBorderButtonsPanel.gridy = 1;
		propertyPanel.add(bevelBorderButtonsPanel, gbc_bevelBorderButtonsPanel);
		GridBagLayout gbl_bevelBorderButtonsPanel = new GridBagLayout();
		gbl_bevelBorderButtonsPanel.columnWidths = new int[]{0, 0};
		gbl_bevelBorderButtonsPanel.rowHeights = new int[]{0, 0, 0};
		gbl_bevelBorderButtonsPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_bevelBorderButtonsPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		bevelBorderButtonsPanel.setLayout(gbl_bevelBorderButtonsPanel);
		
		buttonsPanel = new JPanel();
		GridBagConstraints gbc_buttonsPanel = new GridBagConstraints();
		gbc_buttonsPanel.insets = new Insets(0, 0, 5, 0);
		gbc_buttonsPanel.fill = GridBagConstraints.BOTH;
		gbc_buttonsPanel.gridx = 0;
		gbc_buttonsPanel.gridy = 0;
		bevelBorderButtonsPanel.add(buttonsPanel, gbc_buttonsPanel);
		GridBagLayout gbl_buttonsPanel = new GridBagLayout();
		gbl_buttonsPanel.columnWidths = new int[]{0, 0, 0};
		gbl_buttonsPanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_buttonsPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_buttonsPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		buttonsPanel.setLayout(gbl_buttonsPanel);
		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.gridwidth = 2;
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 0;
		buttonsPanel.add(textField, gbc_textField);
		textField.setColumns(10);
		
		blockUserButton = new JButton("Block user");
		if(Config.getInstance().getProperty(Config.Property.OFFLINE_MODE).equalsIgnoreCase("true")) {
			blockUserButton.setEnabled(false);
		}
		blockUserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!isInputEmpty()){
					GUIController.getInstance().blockUser(getInput());
					clearInput();
				}
			}
		});
		GridBagConstraints gbc_blockUserButton = new GridBagConstraints();
		gbc_blockUserButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_blockUserButton.insets = new Insets(0, 0, 5, 5);
		gbc_blockUserButton.gridx = 0;
		gbc_blockUserButton.gridy = 1;
		buttonsPanel.add(blockUserButton, gbc_blockUserButton);
		
		unblockUserButton = new JButton("Unblock user");
		if(Config.getInstance().getProperty(Config.Property.OFFLINE_MODE).equalsIgnoreCase("true")) {
			unblockUserButton.setEnabled(false);
		}
		unblockUserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!isInputEmpty()){
					GUIController.getInstance().unblockUser(getInput());
					clearInput();
				}
			}
		});
		GridBagConstraints gbc_unblockUserButton = new GridBagConstraints();
		gbc_unblockUserButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_unblockUserButton.insets = new Insets(0, 0, 5, 0);
		gbc_unblockUserButton.gridx = 1;
		gbc_unblockUserButton.gridy = 1;
		buttonsPanel.add(unblockUserButton, gbc_unblockUserButton);
		
		followUserButton = new JButton("Follow user");
		if(Config.getInstance().getProperty(Config.Property.OFFLINE_MODE).equalsIgnoreCase("true")) {
			followUserButton.setEnabled(false);
		}
		followUserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!isInputEmpty()){
					GUIController.getInstance().followUser(getInput());
					clearInput();
				}
			}
		});
		GridBagConstraints gbc_followUserButton = new GridBagConstraints();
		gbc_followUserButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_followUserButton.insets = new Insets(0, 0, 0, 5);
		gbc_followUserButton.gridx = 0;
		gbc_followUserButton.gridy = 2;
		buttonsPanel.add(followUserButton, gbc_followUserButton);
		
		unfollowUserButton = new JButton("Unfollow user");
		if(Config.getInstance().getProperty(Config.Property.OFFLINE_MODE).equalsIgnoreCase("true")) {
			unfollowUserButton.setEnabled(false);
		}
		unfollowUserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!isInputEmpty()){
					GUIController.getInstance().unfollowUser(getInput());
					clearInput();
				}
			}
		});
		GridBagConstraints gbc_unfollowUserButton = new GridBagConstraints();
		gbc_unfollowUserButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_unfollowUserButton.gridx = 1;
		gbc_unfollowUserButton.gridy = 2;
		buttonsPanel.add(unfollowUserButton, gbc_unfollowUserButton);
		
		tweetButton = new JButton("Tweet it");
		if(Config.getInstance().getProperty(Config.Property.OFFLINE_MODE).equalsIgnoreCase("true")) {
			tweetButton.setEnabled(false);
		}
		tweetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!isInputEmpty()){
					GUIController.getInstance().tweet(getInput());
					clearInput();
				}
			}
		});
		GridBagConstraints gbc_tweetButton = new GridBagConstraints();
		gbc_tweetButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_tweetButton.gridx = 0;
		gbc_tweetButton.gridy = 1;
		bevelBorderButtonsPanel.add(tweetButton, gbc_tweetButton);
		
		JPanel statePanel = new JPanel();
		statePanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagConstraints gbc_statePanel = new GridBagConstraints();
		gbc_statePanel.insets = new Insets(0, 5, 0, 5);
		gbc_statePanel.fill = GridBagConstraints.BOTH;
		gbc_statePanel.gridx = 0;
		gbc_statePanel.gridy = 1;
		contentPane.add(statePanel, gbc_statePanel);
		statePanel.setLayout(new BorderLayout(8, 0));
		
		stateLabel = new JLabel("-");
		stateLabel.setForeground(new Color(0, 51, 0));
		stateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		stateLabel.setFont(new Font("Calibri", Font.BOLD, 14));
		statePanel.add(stateLabel);
		
		testTweetButton = new JButton("Test tweet");
		GridBagConstraints gbc_testTweetButton = new GridBagConstraints();
		gbc_testTweetButton.gridx = 1;
		gbc_testTweetButton.gridy = 1;
		contentPane.add(testTweetButton, gbc_testTweetButton);
		testTweetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GUIController.getInstance().testTweet();
			}
		});
		
		consolePanelScroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				e.getAdjustable().setValue(e.getAdjustable().getMaximum());
			}
		});
		
	}
	
	public void updateLabel(String str){
		stateLabel.setText(str);
	}
	
	public Style addStyle(String name){
		return consoleText.addStyle(name, null);
	}
	
	public StyledDocument getStyledDocument(){
		return consoleText.getStyledDocument();
	}
	
	public void setBotName(String str){
		botNameLabel.setText(str);
	}
	
	public void updateImage(Image img){
		BufferedImage bi = toBufferedImage(img);
		float proportions = (float)bi.getWidth()/(float)bi.getHeight();
		JLabel picLabel = new JLabel(new ImageIcon(bi.getScaledInstance(profilePicPanel.getWidth(), (int) (profilePicPanel.getWidth()/proportions), Image.SCALE_FAST)));
		picLabel.setBounds(0, 0, profilePicPanel.getWidth(), profilePicPanel.getHeight());
		profilePicPanel.add(picLabel);
		profilePicPanel.setPreferredSize(profilePicPanel.getPreferredSize());
		//profilePicPanel.setBounds(picLabel.getBounds());
		
		//profilePicPanel.update(profilePicPanel.getGraphics());
	}
	
	public void updateInfo(int tweets, int following, int followers){
		lblTweets.setText("Tweets: "+tweets);
		lblFollowing.setText("Following: "+following);
		lblFollowers.setText("Followers: "+followers);
	}
	
	private boolean isInputEmpty(){
		return textField.getText().length() <= 0;
	}
	
	private String getInput(){
		return textField.getText();
	}
	
	private void clearInput(){
		textField.setText("");
	}
	
	private BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
	

}
