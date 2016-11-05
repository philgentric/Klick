package image_decode;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.libjpegturbo.turbojpeg.TJ;
import org.libjpegturbo.turbojpeg.TJDecompressor;


/*
 * given a file, can produce an image but only decodes it, 
 * scaling is not provided, this is why it is abstract
 */

//**********************************************************
public abstract class Image_decoder //implements Image_decoder
//**********************************************************
{
	abstract protected boolean recompute_current_image();
	public abstract Future<Klik_BufferedImage> get_promise();
	public abstract void reset_promise();

	
	public static AtomicBoolean better_skip = new AtomicBoolean(false);
	static boolean TJDecompressor_not_installed_already_reported = false;
	protected Klik_BufferedImage the_current_image = null;
	//TODO private BufferedImage sorry_icon;
	protected String displayed_string = "";

	protected Dimension screen_dimension;
	protected double desired_zoom_factor = -1.0;

	Klik_File image_File_to_be_loaded = null;


	//**********************************************************
	Image_decoder(Klik_File target, Dimension target_dimension_, double zoom_factor_)
	//**********************************************************
	{
		image_File_to_be_loaded = target;
		desired_zoom_factor = zoom_factor_;
		screen_dimension = target_dimension_;
	}


	//**********************************************************
	public static byte[] load_image_from_file(File image_File_to_be_loaded)
	//**********************************************************
	{
		byte[] returned = null;
		// only load data if needed
		FileInputStream fis;
		try
		{
			fis = new FileInputStream(image_File_to_be_loaded);
			int inputSize = fis.available();
			if(inputSize < 1) 
			{
				System.out.println("Input file contains no data");
				fis.close();
				return null;
			}
			returned = new byte[inputSize];					
			fis.read(returned);
			fis.close();
		}
		catch(Exception e)
		{
			System.out.println("Input file problem:"+e);
			return null;			
		}
		return returned;
	}

	// decode using turbo jpeg,
	// and if also_try_to_rescale is true,  also ask turbo jpeg to rescale
	// but it can rescale only to powers of two...
	//**********************************************************
	protected static Klik_BufferedImage decode_using_turbojpeg(
			TJDecompressor tjd, 
			boolean also_try_to_rescale, 
			Dimension target_image_size)
	//**********************************************************
	{
		Dimension target_after_decode_image_size = new Dimension(800, 600); // init dummy
		Klik_BufferedImage returned = new Klik_BufferedImage();
		if ( also_try_to_rescale ==  true)
		{
			if ( compute_rescale_size(tjd, target_after_decode_image_size, target_image_size ) == false)
			{
				System.out.println("compute_rescale_size() failed" );
				return null;
			}
			System.out.println("compute_rescale_size() returns true" );
			// check that the returned size fits what we want
			if ( target_after_decode_image_size.equals(target_image_size) == true)
			{
				returned.rescale_at_draw_time = false;
				System.out.println(".... and the target is reached" );
			}
			else
			{
				returned.rescale_at_draw_time = true;
				System.out.println(".... but the target is NOT reached "
						+target_after_decode_image_size.toString()
						+" versus requested= "
						+target_image_size.toString() );
			}
			try 
			{
				returned.actual_zoom_factor = (double)target_after_decode_image_size.width/(double)tjd.getWidth();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			if ( get_direct_size(tjd, target_after_decode_image_size) == false)
			{
				System.out.println("get_direct_size() failed" );
				return null;				
			}
			returned.rescale_at_draw_time = false;
			returned.actual_zoom_factor = 1.0;
		}
		returned.the_BufferedImage = decode(tjd, target_after_decode_image_size);
		System.out.println(" decode_using_turbojpeg: decompression OK for target image size: " + target_after_decode_image_size.width + "x" + target_after_decode_image_size.height);
		return returned;

	}

	//**********************************************************
	private static boolean get_direct_size(TJDecompressor tjd,
			Dimension target_after_decode_image_size)
			//**********************************************************
	{
		try
		{
			target_after_decode_image_size.width = tjd.getWidth();
			target_after_decode_image_size.height = tjd.getHeight();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("decode_using_turbojpeg EXception getting image size failed "+ e);
			return false;
		}
		return true;
	}
	
	// need to ask turbojpeg what is going to be the size
	// of the decoded image: it may not be the same as what we request

	//**********************************************************
	private static boolean compute_rescale_size(TJDecompressor tjd,
			Dimension output_decoded_image_size, Dimension input_target_screen_size)
			//**********************************************************
	{
		try
		{
			output_decoded_image_size.width = tjd.getScaledWidth(input_target_screen_size.width, input_target_screen_size.height);
			output_decoded_image_size.height = tjd.getScaledHeight(input_target_screen_size.width, input_target_screen_size.height);
		}
		catch(Exception e)
		{
			System.out.println("decode_using_turbojpeg FAST MODE FAILED: turbojpeg cannot scale down for: " + input_target_screen_size.width +"x" + input_target_screen_size.height);
			return false;
		}
		return true;
	}

	//**********************************************************
	protected static BufferedImage decode(TJDecompressor tjd, Dimension target_after_decode_image_size)
	//**********************************************************
	{
		BufferedImage returned = null;
		try
		{
			returned = tjd.decompress(target_after_decode_image_size.width, target_after_decode_image_size.height, BufferedImage.TYPE_INT_RGB, TJ.FLAG_ACCURATEDCT);
		}
		catch(Exception e)
		{
			System.out.println("decode_using_turbojpeg tjd.decompress failed: " + e.toString() );
			return null;
		}
		return returned;
	}

	//**********************************************************
	public String get_displayed_string()
	//**********************************************************
	{
		return displayed_string;
	}

	//**********************************************************
	public Klik_BufferedImage get_current_image()
	//**********************************************************
	{
		return the_current_image;
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
	public double get_zoom()
	//**********************************************************
	{
		if ( the_current_image == null) return -1.0;
		return the_current_image.actual_zoom_factor;
	}




	//**********************************************************
	public void set_current_image(Klik_BufferedImage kbf)
	//**********************************************************
	{
		the_current_image = kbf;
	}

	//**********************************************************
	protected boolean turboJPEG_decode(Dimension real_image_dimension, boolean rescale_with_turbo)
	//**********************************************************
	{
		Dimension desired_image_dimension;
		
	
		byte[] inputBuf = null;
	
		//inputBuf = Abstract_image_factory.load_image_from_file(image_File_to_be_loaded);
		inputBuf = load_image_from_file(image_File_to_be_loaded.file);
		if ( inputBuf == null)
		{
			System.out.println("load_image_from_file FAILED for "+image_File_to_be_loaded.file);
			
			//TODO: this may happen when the file is RENAMED
			//... and the object is not re-synchronized
			
			return false;
		}
		
		real_image_dimension.width = 0;
		real_image_dimension.height = 0;
		TJDecompressor the_turbo_decompressor = null;
		try 
		{
			the_turbo_decompressor = new TJDecompressor(inputBuf);
			real_image_dimension.width = the_turbo_decompressor.getWidth();
			real_image_dimension.height = the_turbo_decompressor.getHeight();
		} 
		catch (NoClassDefFoundError | UnsatisfiedLinkError | Exception e)
		{
			System.out.println("Exception caught "+e);
			//e.printStackTrace();
			
			if ( TJDecompressor_not_installed_already_reported == false)
			{
				String error_message = 
						"...hum... \n"
						+"apparently either we are trying to decode a non-conformant file\n"
						+"("+image_File_to_be_loaded.file.getAbsolutePath()+")\n"
						+"or the Turbo JPEG decompression library is NOT properly installed (?):\n"
						+e
						+"\n nevertheless Klik will work, using the java native\n"
						+"decompression, which may be slower\n"
						+"so if performance is an issue, try re-installing...\n"
						+"(until then, you are going to see this warning every time you restart Klik)";
				JOptionPane.showMessageDialog(null, error_message);
				TJDecompressor_not_installed_already_reported = true;
			}
			the_current_image = new Klik_BufferedImage();
			try 
			{
				ByteArrayInputStream bais = new ByteArrayInputStream(inputBuf);
				the_current_image.the_BufferedImage = ImageIO.read(bais);
				System.out.println("ImageIO read OK ");
			} 
			catch (IOException e1)
			{
				System.out.println("IOException caught "+e1);
				e1.printStackTrace();
				return false;
			}
			return true;
		}
	
		String numbers = (image_File_to_be_loaded.index_in_dir+1)+"/"+ image_File_to_be_loaded.dir_size;
		displayed_string = 
				numbers
				+ "  "
				+real_image_dimension.width
				+"x"
				+real_image_dimension.height
				+image_File_to_be_loaded.file.getAbsolutePath();
		System.out.println("displayed_string : " + displayed_string);
		System.out.println("desired_zoom_factor : " + desired_zoom_factor);
		if ( desired_zoom_factor < 0)
		{
			// adapt image to display
			desired_image_dimension = screen_dimension;
		}
		else
		{
			if ( desired_zoom_factor == 1.0)
			{
				desired_image_dimension = real_image_dimension;
			}
			else
			{
				desired_image_dimension = new Dimension();
				desired_image_dimension.width = (int)(desired_zoom_factor*(double)real_image_dimension.width);
				desired_image_dimension.width = round_even(desired_image_dimension.width);
				desired_image_dimension.height = (int)(desired_zoom_factor*(double)real_image_dimension.height);
				desired_image_dimension.height = round_even(desired_image_dimension.height);
			}
		}
		System.out.println("desired output image size "+desired_image_dimension.toString());
		
		the_current_image = decode_using_turbojpeg(
				the_turbo_decompressor, 
				rescale_with_turbo, 
				desired_image_dimension
				);
	
		if ( the_current_image == null )
		{
			System.out.println("Abstract_image_factory cannot decode and rescale at the same time for:\n" + displayed_string);
	
			// retry but dont ask for rescale = get the NATIVE image
			desired_image_dimension = real_image_dimension;
			the_current_image = decode_using_turbojpeg(the_turbo_decompressor, false, desired_image_dimension);
			if ( the_current_image == null )
			{
				System.out.println("retry ERROR: turbojpeg cannot decode even with rescale off !!!");
				return false;			
			}
			// ok now we have a HUGE image, that we will try to rescale at DISPLAY time
			the_current_image.rescale_at_draw_time = true;
			
		}
		else
		{
			System.out.println("decode OK");
			if ( desired_zoom_factor != -1.0)
			{
				// by a twist of definition, if you did not want to rescale, then... 
				// say you did, so that some else down the pipe does not attempt to do it
				the_current_image.rescale_at_draw_time = false;
			}
			
		}
	
		return true;
	}



}
