package ui;

import java.util.ArrayList;
import java.util.List;

import klik_main.Klik_JFrame;

//**********************************************************
public class Mini_frame_handler
{
	Klik_JFrame what = null;
	List<Mini_frame> list_of_existing_frames = new ArrayList<Mini_frame>();
	int x = 0;
	int y = 0;
	int bar_height = 10;// stupid default
	
	//**********************************************************
	public Mini_frame_handler(Klik_JFrame  w)
	//**********************************************************
	{
		what = w;
	}
	//**********************************************************
	void compute_father_position()
	//**********************************************************
	{
		x = what.getX();
		y = what.getY();
		bar_height = what.get_bar_height();
	}
	//**********************************************************
	private boolean check(String name)
	//**********************************************************
	{
		for(Mini_frame f : list_of_existing_frames )
		{
			if ( f.get_title().equals(name) ==  true)
			{
				System.out.println("found match !!");
				f.update();
				return true;
			}
		}
		return false;
	}
	//**********************************************************
	public void update_all()
	//**********************************************************
	{
		for(Mini_frame f : list_of_existing_frames )
		{
			f.update();
		}
	}
	//**********************************************************
	public void move()
	//**********************************************************
	{
		compute_father_position();
		int local_y = y+bar_height;
		for(Mini_frame f : list_of_existing_frames )
		{
			f.setLocation(x, local_y);
			local_y += f.getHeight();
		}
	}

	
	//**********************************************************
	private void add(Mini_panel p, String name, boolean no_header)
	//**********************************************************
	{
		Mini_frame f = new Mini_frame(this,name,p,no_header);
		list_of_existing_frames.add(f);
		p.set_frame(f);
		move();
	}
	//**********************************************************
	public void window_closed(Mini_frame f, Mini_panel the_panel)
	//**********************************************************
	{
		list_of_existing_frames.remove(f);
		move();
		
	}
	//**********************************************************
	public void die()
	//**********************************************************
	{
		for(Mini_frame f : list_of_existing_frames )
		{
			f.dispose();
		}
	}
	

	//**********************************************************
	public void show_rename()
	//**********************************************************
	{
		String name = "Rename";
		if (check(name) == true) return;
		Rename_panel p = new Rename_panel(what);
		add(p,name,true);
	}
	//**********************************************************
	public void show_help()
	//**********************************************************
	{
		String name = "Help";
		if (check(name) == true) return;
		Help_mini_panel p = new Help_mini_panel(what);
		add(p,name,true);
	}
	//**********************************************************
	public void show_licenses()
	//**********************************************************
	{
		String name = "Copyright & Licenses";
		if (check(name) == true) return;
		Licenses_mini_panel p = new Licenses_mini_panel(what);
		add(p,name,true);
	}
	//**********************************************************
	public void show_move()
	//**********************************************************
	{
		String name = "Move";
		if (check(name) == true) return;
		Move_panel p = new Move_panel(what);
		add(p,name,false);
	}
	//**********************************************************
	public void show_copy()
	//**********************************************************
	{
		String name = "Copy";
		if (check(name) == true) return;
		Copy_panel p = new Copy_panel(what);
		add(p,name,false);
	}
	//**********************************************************
	@Deprecated
	public void show_preferences()
	//**********************************************************
	{
		String name = "Preferences";
		if (check(name) == true) return;
		Preferences_panel p = new Preferences_panel(what);
		add(p,name,false);
	}
	//**********************************************************
	public void show_exif()
	//**********************************************************
	{
		String name = "EXIF data";
		if (check(name) == true) return;
		Exif_panel p = new Exif_panel(what);
		add(p,name,false);
	}
}
