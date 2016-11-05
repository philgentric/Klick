package ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import klik_main.Klik_JFrame;

//**********************************************************
public abstract class Mini_panel extends JPanel
//**********************************************************
{

	private static final long serialVersionUID = 1L;
	protected Klik_JFrame what_to_configure;
	protected Mini_frame the_frame = null;
	
	abstract void reconfigure();
	
	//**********************************************************
	Mini_panel(Klik_JFrame context)
	//**********************************************************
	{
		super(new BorderLayout());
		what_to_configure = context;
	}

	//**********************************************************
	void set_frame(Mini_frame the_frame_)
	//**********************************************************
	{
		the_frame = the_frame_;
	}


}
