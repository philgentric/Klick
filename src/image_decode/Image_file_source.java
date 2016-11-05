package image_decode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * abstracts the access to image files in a given directory
 * holds a number of slaves, n = X * Y for multiple simultaneous display
 */

//**********************************************************
public class Image_file_source
//**********************************************************
{
	private static final boolean debug_flag = false;
	public List<File> eligible_file_list = new ArrayList<File>();
	public File source_dir_File = null;
	List<Image_file_source_slave> slaves = new ArrayList<Image_file_source_slave>();
	
	//**********************************************************
	public Image_file_source(File target_dir, File target_file, int num_slaves)
	//**********************************************************
	{
		source_dir_File = target_dir;
		int current_image_index = 0;
		//if ( target_file != null)
		{
			current_image_index = scan_dir(target_file);
			if ( current_image_index < 0 ) current_image_index = 0;
		}
		for ( int i = 0 ; i < num_slaves ; i++)
		{
			slaves.add(new Image_file_source_slave(eligible_file_list, current_image_index));
			current_image_index++;
		}
	}

	/* if target_file is a folder, returns -1
	 * else it returns the dir index of the image contained in target_file
	 */

	//**********************************************************
	public int scan_dir(File target_file)
	//**********************************************************
	{
		int returned_image_index = -1;
		eligible_file_list.clear();
		File all_files_in_dir[] = source_dir_File.listFiles();

		if ( all_files_in_dir == null) return returned_image_index;
		
		//current_image_index = 0;
		int i = 0;
		for(File file : all_files_in_dir)
		{
			if ( file.getName().endsWith(".jpeg") == false)
			{
				if ( file.getName().endsWith(".jpg") == false)
				{
					if ( file.getName().endsWith(".JPEG") == false)
					{
						if ( file.getName().endsWith(".JPG") == false)
						{
							continue;
						}
					}
				}
			}
			eligible_file_list.add(file);
			if ( target_file != null)
			{
				if ( file.getAbsolutePath().equals(target_file.getAbsolutePath()) == true)
				{
					returned_image_index = i;
				}
				
			}
			i++;
		}

		if ( debug_flag == true)
		{
			for ( File f : eligible_file_list)
			{
				System.out.println("--"+f.getAbsolutePath());
			}			
		}
		return returned_image_index;
	}

	//**********************************************************
	public Klik_File get_current_file()
	//**********************************************************
	{
		if ( eligible_file_list.isEmpty() == true)
		{
			System.out.println("Image file source eligible_file_list.isEmpty() == true");
			return null;
		}
		if ( slaves.size() > 1 ) 
		{
			System.out.println("Image file source slaves.size() > 1");
			return null;
		}
		Image_file_source_slave slave =  slaves.get(0);
		if ( slave == null) return null;
		int i = slave.get_current_file_index_in_dir();
		return new Klik_File(eligible_file_list.get(i),i,eligible_file_list.size());
	}



	//**********************************************************
	private int check_image_index(int target)
	//**********************************************************
	{
		if ( target < 0) target = 0;
		else if ( target >= eligible_file_list.size()) target = eligible_file_list.size()-1;
		return target;
	}
	//**********************************************************
	void load_image_absolute(int target)
	//**********************************************************
	{
		int i = 0;
		for ( Image_file_source_slave o : slaves)
		{
			o.load_image_absolute(target+i);
		}
	}
	//**********************************************************
	public boolean load_image_relative(int skip)
	//**********************************************************
	{
		boolean returned = true;
		int i = 0;
		for ( Image_file_source_slave o : slaves)
		{
			if ( o.load_image_relative(skip+i) == false) returned = false;
		}
		return returned;
	}

	
	//**********************************************************
	public void load_random_image()
	//**********************************************************
	{
		for ( Image_file_source_slave o : slaves)
		{
			o.load_random_image();
		}
	}
	
	//**********************************************************
	public boolean load_next_ultim_image()
	//**********************************************************
	{
		int i = 0;
		for ( Image_file_source_slave o : slaves)
		{
			o.load_next_ultim_image(i++);
		}
		return true;
	}


	//**********************************************************
	public File get_next_file(boolean stop_at_end)
	//**********************************************************
	{
		if ( eligible_file_list.isEmpty() == true) return null;
		if ( slaves.size() > 1) return null;
		//if ( eligible_file_list.size() == 1) return eligible_file_list.get(0);
		int target;
		if ( slaves.get(0).get_current_file_index_in_dir()+1 >= eligible_file_list.size())
		{
			if ( stop_at_end == true) return null;
			target = eligible_file_list.size()-1;
		}
		else
		{
			target = slaves.get(0).get_current_file_index_in_dir()+1;
		}
		File f = eligible_file_list.get(target);
		if ( debug_flag) System.out.println("for index = "+target+" file is="+f.getAbsolutePath() );
		return f;
	}

	//**********************************************************
	public File get_file()
	//**********************************************************
	{
		if ( eligible_file_list.isEmpty() == true) return null;
		if ( slaves.size() > 1) return null;
		//if ( eligible_file_list.size() == 1) return eligible_file_list.get(0);
		int target;
		//if ( slaves.get(0).get_current_file_index_in_dir()+1 >= eligible_file_list.size())
		//{
		//	target = 0;
		//}
		//else
		{
			target = slaves.get(0).get_current_file_index_in_dir();
		}
		File f = eligible_file_list.get(target);
		if ( debug_flag) System.out.println("for index = "+target+" file is="+f.getAbsolutePath() );
		return f;
	}
	
	//**********************************************************
	public void delete_current_file()
	//**********************************************************
	{
		File next = get_next_file(false);
		File target = get_current_file().file;
		if (target == null) return;
		System.getProperties();
		
		String home = System.getProperty("user.home");
		File dest_dir = new File(home+File.separator+"klik_deleted_images");
		if ( dest_dir.exists() == false)
		{
			dest_dir.mkdir();
		}
		File dest = new File(dest_dir,target.getName());
		target.renameTo(dest);
		System.out.println("WARNING: file was not deleted but moved to:"+dest_dir.getAbsolutePath());
		scan_dir(next);
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
		if ( slaves.size() > 1) return -1;
		return slaves.get(0).get_current_file_index_in_dir();
	}

	//**********************************************************
	public int get_dir_size()
	//**********************************************************
	{
		return eligible_file_list.size();
	}

	//**********************************************************
	public void set_current_file(File f) 
	//**********************************************************
	{
		scan_dir(f);		
	}


	//**********************************************************
	public Image_file_source_slave get_slave(int i) 
	//**********************************************************
	{
		return slaves.get(i);
	}

}
