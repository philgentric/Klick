package image_decode;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.imgscalr.Scalr;
import org.libjpegturbo.turbojpeg.TJ;
import org.libjpegturbo.turbojpeg.TJDecompressor;

//**********************************************************
public class Scalr_encapsulation 
//**********************************************************
{

	private static final boolean debug_flag = false;

	//**********************************************************
	// big plus of scalr is it can rescale to ANY size, with high quality
	// but on large images, it is going ot be very slow
	static BufferedImage rescale_with_scalr(
			BufferedImage input_image, Scalr.Method scalr_method, 
			int target_width, int target_height)
	//**********************************************************
	{
		if (input_image == null) return null;
		BufferedImage returned = null;

		double target_aspect_ratio = (double)target_height/(double)target_width;

		int local_input_image_width =  input_image.getWidth();
		int local_input_image_height = input_image.getHeight();


		double image_aspect_ratio = (double)local_input_image_height/(double)local_input_image_width;

		// is the image too wide to fit the aspect ratio?

		int local_width;
		int local_height;
		if ( image_aspect_ratio > target_aspect_ratio)
		{
			// the image is portrait
			local_height = target_height;
			local_width = (int)((double)target_height/(double)local_input_image_height * (double)local_input_image_width);
			local_width = 2*(local_width/2);
		}
		else
		{
			local_width = target_width;
			local_height = (int)((double)target_width/(double)local_input_image_width * (double)local_input_image_height);
			local_height = 2*(local_height/2);
		}
		int target_size;
		if ( local_width > local_height) target_size = local_width;
		else target_size = local_height;

		System.out.println("rescale_with_scalr method="+scalr_method+" resize target:"+target_size+" RESIZE ratio is: 1/"+ (double)local_input_image_width/(double)target_width);

		try
		{
			returned = Scalr.resize(input_image, scalr_method, target_size);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println(" AAAAAAARRRRRRRRRRGGGGGGGGG Exception (2)  in Scalr.resize " + e );
			return null;
		}
		catch (OutOfMemoryError e)
		{
			System.out.println("OUT OF MEMORY (2)  in ImageIO:read:" + e );
			return null;
		}
		if ( debug_flag) System.out.println("rescale_with_scalr "+scalr_method+" resized OK !");
		return returned;
	}

	//**********************************************************
	public static void main(String[] args)
	//**********************************************************
	{
		JFileChooser fc = new JFileChooser();
	    int returnVal = fc.showOpenDialog(null);
	    
	    if(returnVal == JFileChooser.APPROVE_OPTION)
	    {
	    	File image_File_to_be_loaded = fc.getSelectedFile();
			byte[] raw_data = Fast_image_factory.load_image_from_file(image_File_to_be_loaded);
			try 
			{
				TJDecompressor the_turbo_decompressor = new TJDecompressor(raw_data);
				int real_image_width = the_turbo_decompressor.getWidth();
				int real_image_height = the_turbo_decompressor.getHeight();
				
				
				BufferedImage current_image = the_turbo_decompressor.decompress(
						real_image_width, real_image_height, BufferedImage.TYPE_INT_RGB, TJ.FLAG_ACCURATEDCT);
				
				JFrame f = new JFrame()
				{
					private static final long serialVersionUID = 1L;	
					public void paint(Graphics g)
					{
						g.drawImage(current_image, 0, 0, null);
					}
				};
				f.setVisible(true);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

	    }
		

	}
	
	
}
