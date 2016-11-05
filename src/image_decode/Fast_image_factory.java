package image_decode;

import java.awt.Dimension;
import java.util.concurrent.Future;

import klik_main.Klik_JFrame;


/*
 * an image factory that uses the power-of-2 fast resize capability of turboJPEG 
 */

//**********************************************************
public class Fast_image_factory extends Image_decoder
//**********************************************************
{
	//private boolean adapt_image_to_screen = true;
	
	//**********************************************************
	public Fast_image_factory(Klik_File target,Dimension target_dimension_, double zoom_factor_)
	//**********************************************************
	{
		super(target,/*main_Frame,*/target_dimension_,zoom_factor_);
		recompute_current_image();
	}

	//**********************************************************
	public Rescaling_mode get_Rescaling_mode()
	//**********************************************************
	{
		return Rescaling_mode.FAST;
	}

	//**********************************************************
	public boolean recompute_current_image()
	//**********************************************************
	{
		if (image_File_to_be_loaded == null)
		{
			the_current_image = null;
			displayed_string = "";
			return false;
		}
		Dimension real_image_dimension = new Dimension();
		//Klik_JFrame.better_skip.set(true);
		Scalr_image_factory.better_skip.set(true);
		
		//System.out.println("better_skip =&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&=> true");
		if ( turboJPEG_decode(real_image_dimension, true) == false ) 
		{
			System.out.println("fast image factory, turbo jpeg returns false");
			return false;
		}
		the_current_image.extract_exif_metadata(image_File_to_be_loaded.file);
		the_current_image.quality = "java (fast)";
		return true;
	}

	//**********************************************************
	public boolean is_fast_browse_with_scroll_wheel_enabled()
	//**********************************************************
	{
		return true;
	}

	//**********************************************************
	protected int round_even(int target_image_width)
	//**********************************************************
	{
		int returned = target_image_width;

		int div = returned/2;
		if ( div*2 != returned) returned++;
		return returned;
	}

	//**********************************************************
	public Future<Klik_BufferedImage> get_promise()
	//**********************************************************
	{
		return null;
	}

	//**********************************************************
	public void reset_promise() 
	//**********************************************************
	{
		
	}




}
