package klik_main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

//**********************************************************
public class Properties_manager 
//**********************************************************
{
	public boolean debug_flag = false;
	static String properties_file_path = "user.home";	
	private static String properties_file_name = "klik_properties.txt";
	private Properties the_Properties = new Properties();

	//**********************************************************
	public Properties_manager()
	//**********************************************************
	{
	}
	//**********************************************************
	public void store_properties()
	//**********************************************************
	{
		if ( debug_flag  == true ) System.out.println("store_properties()");
		FileOutputStream fos;
		try
		{
			File f = new File(System.getProperty(properties_file_path),properties_file_name);
			if ( f.exists() == false)
			{
				f.createNewFile();
			}
			if ( f.canWrite() == false )
			{
				//TODO: make this a dialog
				System.out.println("ALERT: cannot write properties in:"+f.getAbsolutePath());
				return;
			}

			fos = new FileOutputStream(f);
			the_Properties.store(fos,"no comment");
			fos.close();
			//System.out.println("properties stored in:"+f.getAbsolutePath());
		}
		catch (Exception e)
		{
			System.out.println("store_properties Exception: " + e);
		}
	}
	//**********************************************************
	public void load_properties()
	//**********************************************************
	{
		if ( debug_flag == true ) System.out.println("load_properties()");
		FileInputStream fis;
		try
		{
			File f = new File(System.getProperty(properties_file_path),properties_file_name);
			if ( f.exists()== true)
			{
				if ( f.canRead() == false )
				{
					System.out.println("cannot read properties from:"+f.getAbsolutePath());
					return;
				}
				fis = new FileInputStream(f);
				the_Properties.load(fis);
				System.out.println("properties loaded from:"+f.getAbsolutePath());
				fis.close();
			}
		}
		catch (Exception e)
		{
			System.out.println("load_properties Exception: " + e);
		}
	}
	//**********************************************************
	public String get(String key)
	//**********************************************************
	{
		return (String) the_Properties.get(key);
	}
	//**********************************************************
	public void clear()
	//**********************************************************
	{
		the_Properties.clear();		
	}
	//**********************************************************
	public void put(String key, String value) 
	//**********************************************************
	{
		the_Properties.put(key,value);	
	}
	//**********************************************************
	public void remove(String key, String s) 
	//**********************************************************
	{
		the_Properties.remove(key,s);	
	}

}
