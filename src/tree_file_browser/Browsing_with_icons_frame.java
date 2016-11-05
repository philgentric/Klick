package tree_file_browser;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import image_decode.Image_file_source;
import image_decode.Klik_File;
import klik_main.Klik_JFrame;

//**********************************************************
public class Browsing_with_icons_frame extends JFrame
//**********************************************************
{
	private static final long serialVersionUID = -43053915136173635L;
	private static final boolean debug_file = true;
	private static String home = "user.home";	
	private static String default_cache_dir_name = "cache_klik";
	Klik_JFrame target_klik_frame;
	File current_dir;
	JPanel the_icons_panel;
	JScrollPane the_icon_scroll_pane;
	File_tree_browsing_panel file_tree;
	
	int number_of_icons_horizontally;
	
	@Override
	//**********************************************************
	public Dimension getPreferredSize()
	//**********************************************************
	{
		// does not work ???
		return new Dimension(number_of_icons_horizontally*150, 800);
	}
	
	@Override
	//**********************************************************
	public Dimension getMinimumSize()
	//**********************************************************
	{
		// does not work ???
		return new Dimension(number_of_icons_horizontally*150, 800);
	}
	
	//**********************************************************
	public Browsing_with_icons_frame(
			//File current_dir_, 
			Klik_JFrame target_, 
			int number_of_icons_horizontally_)
	//**********************************************************
	{
		number_of_icons_horizontally = number_of_icons_horizontally_;
		target_klik_frame = target_;
		current_dir = null;//current_dir_;

		the_icons_panel =  new JPanel();
		the_icons_panel.setLayout(new GridLayout(0,number_of_icons_horizontally));
		
		the_icon_scroll_pane = new JScrollPane(the_icons_panel);
		the_icon_scroll_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		file_tree = new File_tree_browsing_panel(target_,this);
		JSplitPane the_main_panel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,file_tree,the_icon_scroll_pane);
		
		this.add(the_main_panel);

		load_all_icons();
		
		pack();
		repaint();
	}

	//**********************************************************
	public void load(File dir) 
	//**********************************************************
	{
		if ( dir == null)
		{
			System.out.println("Simple_browsing_frame::load() with dir=null");
		}
		else
		{
			System.out.println("Simple_browsing_frame::load() with dir="+ dir.getAbsolutePath());
		}
		current_dir = dir;
		update_tree(current_dir);
		load_all_icons();
	}

	//**********************************************************
	private void update_tree(File dir) 
	//**********************************************************
	{
		if ( file_tree == null)
		{
			System.out.println("file_tree == null ????");
			return;
		}
		file_tree.expand(current_dir);
	}

	//**********************************************************
	private void load_all_icons() 
	//**********************************************************
	{
		if ( current_dir == null)
		{
			System.out.println("\n\nload_all_icons() GIVE UP, no target");
			return;
		}
		
		System.out.println("\n\nload_all_icons() for dir: "+ current_dir.getAbsolutePath());
		
		File global_icon_dir = get_icon_dir();
		if ( global_icon_dir.exists() == false)
		{
			System.out.println("Browsing_frame: icon dir = "+global_icon_dir.getAbsolutePath()+" does not exist");
			if ( global_icon_dir.mkdir() == false)
			{
				System.out.println("Browsing_frame: icon dir = "+global_icon_dir.getAbsolutePath()+" cannot be created");
			}
		}
		if ( debug_file) System.out.println("Browsing_frame: icon dir = "+global_icon_dir.getAbsolutePath());


		List<Clickable_icon> l = new ArrayList<Clickable_icon>();
		
		the_icons_panel.removeAll();
		Image_file_source ifc = new Image_file_source(current_dir, null,1);
		ifc.scan_dir(current_dir);
		
		//System.out.println("AAAAAAAAAAAAAAAAAAAAAAA for dir="+ current_dir.getAbsolutePath());
		for(;;)
		{
			File f = ifc.get_file();
			if ( f == null)
			{
				if ( debug_file) 
					System.out.println("AAAAAAAAAAAAAAAAAAAAAAA Browsing_with_icons: null file (end of loop?)");
				break;
			}
			//System.out.println("AAAAAAAAAAAAAAAAAAAAAAA f="+f.getAbsolutePath());

			JLabel la = new JLabel();
			la.setPreferredSize(new Dimension(Clickable_icon.large_icon_size,Clickable_icon.large_icon_size));
			Jlabel_icon_acceptor sdf = new Jlabel_icon_acceptor(la);
			boolean multi_threaded = true;
			boolean use_large_icons = true;
			Clickable_icon clim = new Clickable_icon(f,target_klik_frame, multi_threaded, sdf, use_large_icons);
			the_icons_panel.add(la);

			if ( ifc.load_image_relative(1) == false)
			{
				break;
			}

		}
		the_icons_panel.repaint();
		the_icon_scroll_pane.repaint();
		the_icon_scroll_pane.revalidate();

		if ( debug_file) System.out.println("launching browsing frame for "+l.size());
	}
	
	//**********************************************************
	static File get_icon_dir()
	//**********************************************************
	{
		return new File(System.getProperty(home),default_cache_dir_name);
	}
	
	//**********************************************************
	public static void clear_cache()
	//**********************************************************
	{
		File d = get_icon_dir();
		for ( File f : d.listFiles())
		{
			if ( f.getName().endsWith(".png") == true) f.delete();
		}
	}


}
