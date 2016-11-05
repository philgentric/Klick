package tree_file_browser;

import java.io.File;


public class Main_for_Simple_browsing_frame 
{

	//**********************************************************
	public static void main(String[] args) 
	//**********************************************************
	{
		int width = 6;
		Browsing_with_icons_frame bf = new Browsing_with_icons_frame(null,width);
		bf.setSize(400+width*150,1200);
		bf.setVisible(true);
		bf.validate();

	}
}
