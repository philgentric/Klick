package tree_file_browser;

import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

//**********************************************************
public 	class Jlabel_icon_acceptor implements Icon_acceptor
//**********************************************************
{
	JLabel la; 
	//**********************************************************
	public Jlabel_icon_acceptor(JLabel la_)
	//**********************************************************
	{
		la = la_;
	}

	//**********************************************************
	@Override
	public void set_icon(BufferedImage bi)
	//**********************************************************
	{
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
		la.setHorizontalTextPosition(center);
	}

	//**********************************************************
	@Override
	public void setVerticalTextPosition(int bottom)
	//**********************************************************
	{
		la.setVerticalTextPosition(bottom);
	}

	//**********************************************************
	@Override
	public void addMouseListener(MouseAdapter mouseAdapter)
	//**********************************************************
	{
		la.addMouseListener(mouseAdapter);
	}
	
};
