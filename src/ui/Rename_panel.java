package ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JTextField;

import image_decode.Klik_File;
import klik_main.Klik_JFrame;

//**********************************************************
public class Rename_panel extends Mini_panel 
//**********************************************************
{
	private static final long serialVersionUID = 1L;
	
	//**********************************************************
	Rename_panel(Klik_JFrame context)
	//**********************************************************
	{
		super(context);
		reconfigure();
	}
	//**********************************************************
	@Override
	public void reconfigure()
	//**********************************************************
	{
		Klik_File f = what_to_configure.get_current_file();
		if ( f == null) return;
		
		removeAll();
		setLayout(new GridLayout());
		JTextField jtf = new JTextField(f.file.getName());
		jtf.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				action(what_to_configure, jtf);				
			}
		});
		add(jtf);
		 
		revalidate();
		repaint();
		if ( the_frame != null) the_frame.pack();// do this to make sure the frame size is updated when the text changes!
	
	}
	//**********************************************************
	private void action(Klik_JFrame context, JTextField jtf)
	//**********************************************************
	{
		String new_name = jtf.getText();
		
		Klik_JFrame.rename_current_file(context, new_name);
	}
	
}
