package tree_file_browser;

import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;


//Icon_acceptor for the tree_file_browser
//**********************************************************
public 	class Browser_jlabel_icon_acceptor implements Icon_acceptor
//**********************************************************
{
	JLabel la; 
	//**********************************************************
	public Browser_jlabel_icon_acceptor(JLabel la_)
	//**********************************************************
	{
		la = la_;
	}

	//**********************************************************
	@Override
	public void set_icon(BufferedImage bi)
	//**********************************************************
	{
		//System.out.println("Browser_jlabel_icon_acceptor.set_icon()");
		la.setIcon(new ImageIcon(bi));
	}

	//**********************************************************
	@Override
	public void set_text(String s)
	//**********************************************************
	{
		la.setText(s);
	}

	//**********************************************************
	@Override
	public void setHorizontalTextPosition(int center) 
	//**********************************************************
	{
		//la.setHorizontalTextPosition(center);
	}

	//**********************************************************
	@Override
	public void setVerticalTextPosition(int bottom)
	//**********************************************************
	{
		//la.setVerticalTextPosition(bottom);
	}

	//**********************************************************
	@Override
	public void addMouseListener(MouseAdapter mouseAdapter)
	//**********************************************************
	{
		if ( mouseAdapter == null) System.out.println("Browser_jlabel_icon_acceptor.addMouseListener() NULL !!!");
		la.addMouseListener(mouseAdapter);
	}

	
};
