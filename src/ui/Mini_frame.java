package ui;

import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import klik_main.Klik_JFrame;

//**********************************************************
public class Mini_frame extends  JFrame implements WindowListener //, WindowFocusListener, WindowStateListener
//**********************************************************
{

	private static final long serialVersionUID = 1L;
	Mini_panel the_panel;
	String title = "";
	Mini_frame_handler the_mini_frame_handler;
	
	//**********************************************************
	Mini_frame(Mini_frame_handler mini_frame_handler, String title_, Mini_panel the_panel_, boolean no_header)
	//**********************************************************
	{
		super(title_);
		the_mini_frame_handler = mini_frame_handler;
		title = title_;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//if ( no_header == true) setUndecorated(true);
		the_panel = the_panel_;
		setContentPane(the_panel_);
		setAlwaysOnTop(true);
		pack();
		setVisible(true);
		addWindowListener(this);
		
	}


	//**********************************************************
	@Deprecated
	void set_geometry(Klik_JFrame parent)
	//**********************************************************
	{
		pack();
		setAlwaysOnTop(true);
		setVisible(true);				
		Point p = parent.getLocation();
		p.y+= parent.get_bar_height();
		setLocation(p);
		
	}

	//**********************************************************
	public Object get_title()
	//**********************************************************
	{
		return title;
	}

	//**********************************************************
	public void update()
	//**********************************************************
	{
		the_panel.reconfigure();
		//toFront(); 
		//setVisible(true);
	}


	//**********************************************************
	@Override
	public void windowOpened(WindowEvent e)
	//**********************************************************
	{
		System.out.println("Mini_frame windowOpened");
	}


	//**********************************************************
	@Override
	public void windowClosing(WindowEvent e)
	//**********************************************************
	{
		System.out.println("Mini_frame windowClosing");
	}


	//**********************************************************
	@Override
	public void windowClosed(WindowEvent e)
	//**********************************************************
	{
		System.out.println("Mini_frame windowClosed");
		the_mini_frame_handler.window_closed(this,the_panel);
	}


	//**********************************************************
	@Override
	public void windowIconified(WindowEvent e)
	//**********************************************************
	{
		System.out.println("Mini_frame windowIconified");
	}


	//**********************************************************
	@Override
	public void windowDeiconified(WindowEvent e)
	//**********************************************************
	{
		System.out.println("Mini_frame windowDeiconified");
	}


	//**********************************************************
	@Override
	public void windowActivated(WindowEvent e)
	//**********************************************************
	{
		System.out.println("Mini_frame windowActivated");
	}


	//**********************************************************
	@Override
	public void windowDeactivated(WindowEvent e)
	//**********************************************************
	{
		System.out.println("Mini_frame windowDeactivated");
	}
}
