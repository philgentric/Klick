package image_decode;

import java.io.File;
import java.util.List;
import java.util.Random;


/*
 * contract : will give you a FILE (actually a little better than that: a Klik_File !-)
 * using public Klik_File get_current_file()
 * if you dont want to always get the same image
 * you have to TELL it to find another one
 * - the next one in DIR order
 * - or skipping X...
 */

//**********************************************************
public class Image_file_source_slave
//**********************************************************
{
	private static final boolean dbg = false;
	private int current_image_index;
	public List<File> eligible_file_list;
	public static Random the_random_generator = new Random();
	
	//**********************************************************
	Image_file_source_slave(List<File> eligible_file_list_, int c)
	//**********************************************************
	{
		eligible_file_list = eligible_file_list_;
		current_image_index = c;
	}

	//**********************************************************
	public Klik_File get_current_Klik_File()
	//**********************************************************
	{		
		System.out.println("Image_file_source_slave.get_current_Klik_File() current_image_index="+current_image_index);
		if ( eligible_file_list.isEmpty() == true) return null;
		check_image_index();
		return new Klik_File(eligible_file_list.get(current_image_index), current_image_index, eligible_file_list.size());
	}

	//**********************************************************
	public File get_current_File()
	//**********************************************************
	{
		System.out.println("Image_file_source_slave.get_current_file() current_image_index="+current_image_index);
		if ( eligible_file_list.isEmpty() == true) return null;
		if ( current_image_index < 0) current_image_index = 0;
		return eligible_file_list.get(current_image_index);
	}



	//**********************************************************
	private boolean check_image_index()
	//**********************************************************
	{
		if ( current_image_index < 0) current_image_index = 0;
		else if ( current_image_index >= eligible_file_list.size())
		{
			current_image_index = eligible_file_list.size()-1;
			return false; // this is the end
		}
		return true;
	}
	//**********************************************************
	void load_image_absolute(int target)
	//**********************************************************
	{
		current_image_index = target;
		check_image_index();		
	}
	//**********************************************************
	boolean load_image_relative(int skip)
	//**********************************************************
	{
		if (skip == Integer.MAX_VALUE)
		{
			current_image_index = eligible_file_list.size()-1;
		}
		else if (skip == Integer.MIN_VALUE)
		{
			current_image_index = 0;
		}
		else
		{
			current_image_index += skip;
		}
		return check_image_index();
	}

	//**********************************************************
	public void load_random_image()
	//**********************************************************
	{
		int r = the_random_generator.nextInt(eligible_file_list.size());
		System.out.println("random: "+r+" out of: "+eligible_file_list.size());
		load_image_absolute(r);
	}
	//**********************************************************
	public boolean load_next_ultim_image(int skip)
	//**********************************************************
	{
		int max_try =  eligible_file_list.size();
		
		for (int skippor = skip; skippor >=0; skippor--)
		{
			System.out.println("ultim skippor =: "+skippor);
			int t = find_next_ultim_image();
			if ( t < 0) return false;
		}
		return true;
	}

	//**********************************************************
	public int find_next_ultim_image()
	//**********************************************************
	{
		int max_try =  eligible_file_list.size();
		for (int i = current_image_index+1;;i++)
		{
			if ( i >= eligible_file_list.size()-1 ) i = 0;
			max_try--;
			if ( max_try < 0)
			{
				// happens in directories that DO NOT contain ultim images
				System.out.println("Image_file_source_slave.find_next_ultim_image():no ultim in dir" );
				return -1;
			}
			File f = eligible_file_list.get(i);
			if (f.getName().contains("ultim") ==  true) 
			{
				System.out.println("Image_file_source_slave.find_next_ultim_image():"+f.getAbsolutePath() );
				current_image_index = i;
				check_image_index();
				return current_image_index;
			}
		}
	}


	//**********************************************************
	@Deprecated
	File get_next_file(boolean stop_at_end)
	//**********************************************************
	{
		if ( eligible_file_list.isEmpty() == true) return null;
		//if ( eligible_file_list.size() == 1) return eligible_file_list.get(0);
		int target;
		if ( current_image_index+1 >= eligible_file_list.size())
		{
			if ( stop_at_end == true) return null;
			target = eligible_file_list.size()-1;
		}
		else
		{
			target = current_image_index+1;
		}
		File f = eligible_file_list.get(target);
		if ( dbg) System.out.println("for index = "+target+" file is="+f.getAbsolutePath() );
		return f;
	}

	
	//**********************************************************
	public boolean isEmpty()
	//**********************************************************
	{
		if (eligible_file_list == null) return true;
		return eligible_file_list.isEmpty();
	}

	//**********************************************************
	public int get_current_file_index_in_dir()
	//**********************************************************
	{
		return current_image_index;
	}

	//**********************************************************
	public int get_dir_size()
	//**********************************************************
	{
		return eligible_file_list.size();
	}



}
