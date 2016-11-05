package image_decode;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.imgscalr.Scalr.Method;
import org.libjpegturbo.turbojpeg.TJDecompressor;

import klik_main.Klik_JFrame;


/*
 * an image factory that uses Scalr as resizer
 */

//**********************************************************
public class Scalr_image_factory extends Image_decoder
//**********************************************************
{

	private static final boolean debug_flag = true;
	protected Method method;
	protected Future<Klik_BufferedImage> promise = null;
	private Rescaling_mode the_Rescaling_mode;
	protected static ExecutorService threadpool = Executors.newFixedThreadPool(10);


	//**********************************************************
	public Scalr_image_factory(Klik_File target, 
			Dimension target_dimension_, 
			double zoom_factor_, 
			Rescaling_mode mode_)
	//**********************************************************
	{
		super(target,target_dimension_, zoom_factor_);
		the_Rescaling_mode = mode_;
		switch (the_Rescaling_mode)
		{
		case SCALR_ULTRA_QUALITY:
			method = Method.ULTRA_QUALITY;
			break;
		case SCALR_QUALITY:
			method = Method.QUALITY;
			break;
		case SCALR_BALANCED:
			method = Method.BALANCED;
			break;
		case SCALR_SPEED:
			method = Method.SPEED;
			break;
		default:
		case SCALR_AUTOMATIC:
			method = Method.AUTOMATIC;
			break;
		
		}
		if ( debug_flag) System.out.println("Scalr_image_factory method=->"+method+"<-");
		
		recompute_current_image();
		
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
		better_skip.set(true);
		if ( debug_flag) System.out.println("recompute_current_image()");

		if ( turboJPEG_decode(real_image_dimension, true) == false ) return false;
				
		if ( the_current_image.rescale_at_draw_time == true)
		{
			if ( promise != null)
			{
				System.out.println("CANCELLING previous rescale thread");
				promise.cancel(true);
			}

			promise = threadpool.submit(
					new Scalr_resize_callable(
							the_current_image.
							the_BufferedImage, 
							screen_dimension.width,
							screen_dimension.height,
							method,
							image_File_to_be_loaded));
			if ( debug_flag) System.out.println("SLOW SCALR RESCALE deferred to separate thread ->"+method+"<-");
		}
		else
		{
			if ( debug_flag) System.out.println("NO SCALR RESCALE ->"+method+"<-");
			//the_current_image.extract_exif_metadata(image_File_to_be_loaded);			
		}
		return true;
	}



	//**********************************************************
	public boolean is_fast_browse_with_scroll_wheel_enabled()
	//**********************************************************
	{
		return false;
	}


	public Future<Klik_BufferedImage> get_promise()
	{
		return promise;
	}


	public void reset_promise()
	{
		promise = null;
	}

	public Rescaling_mode get_Rescaling_mode()
	{
		return the_Rescaling_mode;
	}




}
