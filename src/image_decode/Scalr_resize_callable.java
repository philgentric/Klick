package image_decode;

import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.Callable;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

//**********************************************************
public class Scalr_resize_callable implements Callable<Klik_BufferedImage>
//**********************************************************
{
	private static final boolean debug_flag = false;
	private BufferedImage source;
	private int target_image_width;
	private int target_image_height;
	private Method method;
	private Klik_File image_File_to_be_loaded;

	//**********************************************************
	public Scalr_resize_callable(BufferedImage source_, int w, int h, Method method_, Klik_File image_File_to_be_loaded2)
	//**********************************************************
	{
		//the_customer = the_customer_;
		source = source_;
		target_image_width = w;
		target_image_height = h;
		method = method_;
		image_File_to_be_loaded = image_File_to_be_loaded2;
	}

	//**********************************************************
	@Override
	public Klik_BufferedImage call() throws Exception
	//**********************************************************
	{
		if ( source == null)
		{
			System.out.println("Klik_BufferedImage call() fails =  source is null");
			return null;
		}
		if ( debug_flag) System.out.println("Klik_BufferedImage call()");
		BufferedImage the_generated_image = null;
		Cursor cc = null;
		try
		{
			// AAA cc = the_customer.getCursor();
			// AAA the_customer.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
			if ( debug_flag) System.out.println("--------------------starting call for rescaling-------------"+method);
			// this is the slow call...
			the_generated_image = Scalr_encapsulation.rescale_with_scalr(
					source,
					method,
					target_image_width,
					target_image_height);
			if ( debug_flag) System.out.println("---------------------ending call for rescaling---------------");
		} 
		finally 
		{
			//the_customer.setCursor(Cursor.getDefaultCursor()); this HANGS !!!!
			if ( cc != null) 
			{
				//System.out.println("---------------------restoring cursor---------------");
				// AAA  the_customer.get_ready_to_restore_cursor(cc);
				//System.out.println("---------------------restoring cursor... done---------------");
			}
		}
		
		Klik_BufferedImage returned =  new Klik_BufferedImage();
		returned.rescale_at_draw_time = false;
		returned.the_BufferedImage = the_generated_image;
		returned.quality = "imgscalr:"+method;
		returned.extract_exif_metadata(image_File_to_be_loaded.file);
		if ( debug_flag) System.out.println("Klik_BufferedImage call() end ");
		return returned;
	}

}
