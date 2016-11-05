package ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import image_decode.Rescaling_mode;
import image_ui.Window_mode;
import klik_main.Klik_JFrame;

import javax.swing.JRadioButton;

// preferences

//**********************************************************
@Deprecated
public class Preferences_panel extends Mini_panel 
//**********************************************************
{
	private static final long serialVersionUID = -5993201212791100602L;
	ButtonGroup window_button_group;
	ButtonGroup rescaling_button_group;
	JButton jb_plus;
	JButton jb_minus;
	JButton jb_equal;
	
	//**********************************************************
	public Preferences_panel(Klik_JFrame context)
	//**********************************************************
	{
		super(context);
		//context.preference_panel= this;
		configure();
	}

	//**********************************************************
	public void configure()
	//**********************************************************
	{
		JPanel radioPanel = new JPanel(new GridLayout(0, 1));

		
		JLabel jlabel = new JLabel("Zoom:");
		radioPanel.add(jlabel);

		jb_plus = new JButton("+");
		jb_plus.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				what_to_configure.set_zoom_delta(+1);
			}
		});
		radioPanel.add(jb_plus);

		jb_minus = new JButton("-");
		jb_minus.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				what_to_configure.set_zoom_delta(-1);
			}
		});
		radioPanel.add(jb_minus);

		jb_equal = new JButton("=");
		jb_equal.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				what_to_configure.set_zoom_delta(0);
			}
		});
		radioPanel.add(jb_equal);

		JSeparator separator2 = new JSeparator();
		radioPanel.add(separator2);

		
		String label = "";
		JRadioButton rdb = null;

		// first group: re-scaling mode

		{
			class Rescale_ActionListener implements ActionListener
			{
				@Override
				public void actionPerformed(ActionEvent ev)
				{
					String sss = ev.getActionCommand();
					System.out.println("Rescale_ActionListener event:"+ev);
					for( Rescaling_mode rm : Rescaling_mode.values())
					{
						if ( sss.equals(""+rm) == true)
						{
							System.out.println("sorry rescale in preference not implemented");
							//boolean res = what_to_configure.set_Rescaling_mode(rm);
						}
					}
				}
			}
			ActionListener rescaling_item_listen = new Rescale_ActionListener();

			jlabel = new JLabel("Rescale:");
			radioPanel.add(jlabel);

			rescaling_button_group = new ButtonGroup();
			for(Rescaling_mode rsm : Rescaling_mode.values())
			{
				label = ""+rsm;
				rdb = new JRadioButton(label);
				rescaling_button_group.add(rdb);
				radioPanel.add(rdb);
				rdb.addActionListener(rescaling_item_listen);
				rdb.setEnabled(true);//what_to_configure.is_Rescaling_state_legal(rsm));
				System.out.println("sorry rescale in preference not implemented");

				//if ( rsm.equals(what_to_configure.get_Rescaling_mode()) == true ) rdb.setSelected(true);
			}
		}




		JSeparator separator = new JSeparator();
		radioPanel.add(separator);



		// second group: window mode

		{
			class Window_ActionListener implements ActionListener
			{
				@Override
				public void actionPerformed(ActionEvent ev)
				{
					String sss = ev.getActionCommand();
					System.out.println("Window_ActionListener event:"+ev);
					for( Window_mode wm : Window_mode.values())
					{
						if ( sss.equals(""+wm) == true)
						{
							boolean res = what_to_configure.set_Window_mode(wm);
						}
					}
				}

			}
			ActionListener window_item_listen = new Window_ActionListener();

			jlabel = new JLabel("Window:");
			radioPanel.add(jlabel);

			window_button_group = new ButtonGroup();

			for(Window_mode wm : Window_mode.values())
			{
				label = ""+wm;
				rdb = new JRadioButton(label);
				window_button_group.add(rdb);
				radioPanel.add(rdb);
				rdb.addActionListener(window_item_listen);
				
				//rdb.setEnabled(what_to_configure.is_Window_state_legal(wm));
				if ( wm.equals(what_to_configure.get_Window_mode()) == true ) rdb.setSelected(true);
			}
			

		}


		

		add(radioPanel, BorderLayout.LINE_START);
		//add(picture, BorderLayout.CENTER);
		//setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		//setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
	}

	//**********************************************************
	void reconfigure()
	//**********************************************************
	{
		for( Enumeration<AbstractButton> e = window_button_group.getElements() ; e.hasMoreElements();)
		{
			JRadioButton rdb = (JRadioButton)e.nextElement();
			
			String sss = rdb.getText();
			for( Window_mode wm : Window_mode.values())
			{
				if ( sss.equals(""+wm) == true)
				{
					//rdb.setEnabled(what_to_configure.is_Window_state_legal(wm));
					if ( wm.equals(what_to_configure.get_Window_mode()) == true ) rdb.setSelected(true);					
				}
			}
			
		}
		
		for( Enumeration<AbstractButton> e = rescaling_button_group.getElements() ; e.hasMoreElements();)
		{
			JRadioButton rdb = (JRadioButton)e.nextElement();
			
			String sss = rdb.getText();
			for( Rescaling_mode wm : Rescaling_mode.values())
			{
				if ( sss.equals(""+wm) == true)
				{
					rdb.setEnabled(true);//what_to_configure.is_Rescaling_state_legal(wm));
					System.out.println("sorry rescale in preference not implemented");
					//if ( wm.equals(what_to_configure.get_Rescaling_mode()) == true ) rdb.setSelected(true);					
				}
			}
			
		}
		
		System.out.println("sorry rescale in preference not implemented");

		/*if ( what_to_configure.get_Rescaling_mode()  == Rescaling_mode.FAST)
		{
			jb_plus.setEnabled(false);
			jb_minus.setEnabled(false);
		}
		else
		{
			jb_plus.setEnabled(true);
			jb_minus.setEnabled(true);
		}*/
		
		
		System.out.println("reconfigure(): display");
		what_to_configure.recompute_current_image();
	}
	
}
