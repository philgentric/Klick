package ui;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import klik_main.Klik_JFrame;

//**********************************************************
public class Exif_panel extends Mini_panel
//**********************************************************
{

	private static final long serialVersionUID = 1L;
	//**********************************************************
	Exif_panel(Klik_JFrame context)
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
		removeAll();
		ListModel<String> listModel = new DefaultListModel<String>();
		for(String s : what_to_configure.get_exif_tags_list())
		{
			System.out.println("exif tag:"+s);
			((DefaultListModel<String>) listModel).addElement(s);
		}
		JList<String> list = new JList<String>(listModel);
		JScrollPane pictureScrollPane = new JScrollPane(list);
		add(pictureScrollPane);
		revalidate();
		repaint();
	}

}
