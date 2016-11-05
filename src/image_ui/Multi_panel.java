package image_ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import image_decode.Image_file_source;
import image_decode.Klik_File;
import image_decode.Rescaling_mode;

/*
 * this a panel that contains n*p Display_panel(s)
 * in order to maintain appropriate synchronization
 * it relies ona single Image_file_source
 */
//**********************************************************
public class Multi_panel extends JPanel
//**********************************************************
{
	private static final long serialVersionUID = 1L;
	public List< Display_panel> the_Display_panels = new ArrayList<Display_panel>();
	private Image_file_source the_Image_file_source = null;
	private Rescaling_mode the_requested_Rescaling_mode;
	int w;
	//int h;
	
	//**********************************************************
	public Multi_panel(int x, int y)//, Image_file_factory image_file_factory)
	//**********************************************************
	{
		w = x;
		//h = y;
		if ((x > 0) || (y > 0 ))
		{
			setLayout(new GridLayout(x, y));
		}
		for (int i = 0 ; i < x*y ; i++)
		{
			Display_panel dp = new Display_panel(""+i);//, null);//fac_.get_slave(i));
			dp.setPreferredSize(new Dimension(120,80));
			the_Display_panels.add(dp);
			add(dp);
		}
	}

	//**********************************************************
	public File get_image_file_at(int image_index_x, int image_index_y) 
	//**********************************************************
	{
		int index = image_index_x+image_index_y*w;
		Display_panel target =  the_Display_panels.get(index);
		
		return target.get_current_File();
	}


	
	
	//**********************************************************
	public String get_title()
	//**********************************************************
	{
		Display_panel dp = the_Display_panels.get(0);
		return dp.get_displayed_string()+" Mode= "+ dp.get_current_quality();
	}



	//**********************************************************
	public List<String> get_exifs_tags_list() 
	//**********************************************************
	{
		if ( this.the_Display_panels.size() == 1) return the_Display_panels.get(0).get_exifs_tags_list();
		return null;
	}

	//**********************************************************
	public void load_image_relative(int i) 
	//**********************************************************
	{
		if (the_Image_file_source == null ) return;
		the_Image_file_source.load_image_relative(i);
	}


	//**********************************************************
	public boolean load_next_ultim_image() 
	//**********************************************************
	{
		if (the_Image_file_source == null ) return false;
		return the_Image_file_source.load_next_ultim_image();
	}
	//**********************************************************
	public void load_random_image() 
	//**********************************************************
	{
		if (the_Image_file_source == null ) return;
		the_Image_file_source.load_random_image();
		
	}
	//**********************************************************
	public File get_target_dir() 
	//**********************************************************
	{
		if ( the_Image_file_source == null) return null;
		File d = the_Image_file_source.source_dir_File;
		System.out.println("\n\nget_target_dir = "+d);
		return d;
	}
	//**********************************************************
	public Klik_File get_current_file() 
	//**********************************************************
	{
		if (the_Image_file_source == null ) return null;
		return the_Image_file_source.get_current_file();
	}


	//**********************************************************
	public void delete_current_file() 
	//**********************************************************
	{
		if (the_Display_panels.size() > 1)  
		{
			System.out.println("delete not implemented for more than 1 image");
			return;
		}
		if (the_Image_file_source == null ) return;
		the_Image_file_source.delete_current_file();
	}



	//**********************************************************
	public void set_image_file_dir(File local_source_dir_File, File local_File, double zoom) 
	//**********************************************************
	{
		the_Image_file_source = new Image_file_source(local_source_dir_File, local_File, the_Display_panels.size());	
		configure(the_requested_Rescaling_mode,zoom);
	}


	
	//**********************************************************
	public void configure(Rescaling_mode the_requested_Rescaling_mode_,double zoom_) 
	//**********************************************************
	{
		the_requested_Rescaling_mode = the_requested_Rescaling_mode_;
		if ( the_Image_file_source == null)
		{
			System.out.println("BAD! Multi_panel.set_image_getter() the_Image_file_factory == null");
			return;
		}
		int i = 0;
		for ( Display_panel dp : the_Display_panels)
		{
			dp.configure(the_Image_file_source.get_slave(i++),the_requested_Rescaling_mode,zoom_);
		}
	}


	//**********************************************************
	public void rescan_dir() 
	//**********************************************************
	{
		the_Image_file_source.scan_dir(the_Image_file_source.source_dir_File);
	}
	//**********************************************************
	public void scan_dir(File dest) 
	//**********************************************************
	{
		the_Image_file_source.scan_dir(dest);
	}





	//**********************************************************
	public Image_file_source get_image_file_factory() 
	//**********************************************************
	{
		return the_Image_file_source;
	}


	//**********************************************************
	public void scan_dir_next() 
	//**********************************************************
	{
		File next = the_Image_file_source.get_next_file(false);
		scan_dir(next);
		
	}


	//**********************************************************
	public void set_current_file(File f) 
	//**********************************************************
	{
		the_Image_file_source.set_current_file(f);
	}





}
