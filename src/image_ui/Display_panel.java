package image_ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

import javax.swing.JPanel;

import image_decode.Fast_image_factory;
import image_decode.Image_decoder;
import image_decode.Image_file_source_slave;
import image_decode.Klik_BufferedImage;
import image_decode.Rescaling_mode;
import image_decode.Scalr_image_factory;


/* this panel can display images using:
 * 1) a file source to get an image file
 * 2) an Image_decoder to decode that file
 */
//**********************************************************
public class Display_panel extends JPanel  implements MouseMotionListener
//**********************************************************
{
	private String message = null;
	private boolean simple_drawing_method = false;
	public static boolean debug_flag = true;
	private static final long serialVersionUID = 1L;
	
	private Image_decoder the_Image_decoder = null;
	private Image_file_source_slave the_File_factory_slave;

	private int scroll_x = 0;
	private int scroll_y = 0;
	
	//**********************************************************
	public Display_panel(String string)
	//**********************************************************
	{
		message = string;
	}

	

	//**********************************************************
	void configure(Image_file_source_slave fac, Rescaling_mode the_Rescaling_mode, double zoom_) 
	//**********************************************************
	{
		scroll_x = 0;
		scroll_y = 0;
		//zoom = zoom_;
		System.out.println("ENTERING set_image_getter() for "+message);
		//the_File_factory = fac;
		the_File_factory_slave = fac;
		//if ( the_File_factory == null)
		if ( the_File_factory_slave == null)
		{
			System.out.println("set_image_getter() fails: the_File_factory == null");
			return;
		}
		switch( the_Rescaling_mode )
		{
		case FAST:
			System.out.println("FAST image factory");
			the_Image_decoder = new Fast_image_factory(
					the_File_factory_slave.get_current_Klik_File(), 
					//klik_JFrame, 
					new Dimension(getWidth(), getHeight()),
					zoom_);
			return;
		case SCALR_AUTOMATIC:
		case SCALR_ULTRA_QUALITY:
		case SCALR_QUALITY:
		case SCALR_BALANCED:
		case SCALR_SPEED:
			break;
		default:	
			the_Rescaling_mode = Rescaling_mode.SCALR_SPEED;
			break;
	
		}
		System.out.println("image getter is the SCALR one in mode="+the_Rescaling_mode);
		the_Image_decoder = new Scalr_image_factory(
				//the_File_factory.get_current_file(), 
				the_File_factory_slave.get_current_Klik_File(), 
				new Dimension(getWidth(), getHeight()),
				zoom_,
				the_Rescaling_mode);
	}

/*
	//**********************************************************
	public void tell_display_about_the_move(int scroll_x_, int scroll_y_)
	//**********************************************************
	{
		scroll_x = scroll_x_;
		scroll_y = scroll_y_;
	}
*/
	//**********************************************************
	public void tell_display_about_the_move_increment(int scroll_x_, int scroll_y_)
	//**********************************************************
	{
		scroll_x += scroll_x_;
		scroll_y += scroll_y_;
	}

	
	private boolean mouse_debug = true;
	private boolean valid_drag = false;
	private int drag_x = 0;
	private int drag_y = 0;
	
	//**********************************************************
	@Override
	public void mouseDragged(MouseEvent e) 
	//**********************************************************
	{
		if ( mouse_debug == true) System.out.println("mouseDragged: "+e);
		if ( valid_drag == true)
		{
			int scroll_x_increment = e.getX()-drag_x;
			int scroll_y_increment = e.getY()-drag_y;
			tell_display_about_the_move_increment(scroll_x_increment,scroll_y_increment);
			repaint();
			if ( mouse_debug == true) System.out.println("mouseDragged / repaint ");
		}
		drag_x = e.getX();
		drag_y = e.getY();		
		valid_drag = true;
		
	}


	//**********************************************************
	@Override
	public void mouseMoved(MouseEvent e)
	//**********************************************************
	{
		valid_drag = false;		
	}


	
	
	//**********************************************************
	public void set_message(String string)
	//**********************************************************
	{
		if ( message != null) message += " "+string;
		else message = string;
	}



	public File get_current_File() 
	{
		if ( the_File_factory_slave == null) return null;
		return the_File_factory_slave.get_current_File();
	}


	

	public String get_current_quality() 
	{
		if ( the_Image_decoder == null) return "???";
		if ( the_Image_decoder.get_current_image() == null) return "???";
		return the_Image_decoder.get_current_image().quality;
	}


	public String get_displayed_string() 
	{
		if ( the_Image_decoder == null) return "???";
		return the_Image_decoder.get_displayed_string();
	}


	public List<String> get_exifs_tags_list() 
	{
		return the_Image_decoder.get_current_image().exifs_tags_list;
	}


	public void set_Image_file_factory_outlet(Image_file_source_slave get_slave) 
	{
		the_File_factory_slave = get_slave;
	}








	



	//**********************************************************
	private void draw_image_with_affineTransform(Graphics2D g2) 
	//**********************************************************
	{
		g2.setBackground(Color.BLACK);
		g2.clearRect(0, 0, getWidth(), getHeight());

		System.out.println("entering Display_panel::draw_image_with_affineTransform()");
		
		Klik_BufferedImage kbf = get_kbf();
		if ( kbf == null) return;
		int w = kbf.the_BufferedImage.getWidth();
		int h = kbf.the_BufferedImage.getHeight();
		AffineTransform trans = new AffineTransform();
		// AffineTransform API is misleading
		// the operations are NOT performed when one calls "scale" or "rotate"
		// what these calls do is they setup the transform matrix
		// which will be executed in the final paint call
		// ... thus the weird things below

		if ( debug_flag == true)
		{
			//set_message(kbf.quality);
			System.out.println("message should be= "+message);
		}
		int W = w;
		int H = h;
		double s = 1.0;
		if ( kbf.rescale_at_draw_time == true)
		{
			// if the decoder could not resize, we do it here
			{
				double sx = (double)getWidth()/(double)w;
				double sy = (double)getHeight()/(double)h;
				if ( sx < sy) s = sx;
				else s = sy;
			}
			System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&     affine transform rescale: "+s);
			trans.scale(s, s);
			// it causes a change in image size
			W = (int)((double)w*s);
			H = (int)((double)h*s);
			set_message(" scaling using AffineTransform");
		}

		int target_x = 0;
		int target_y = 0;
		int display_area_width = (int)getWidth();
		int display_area_height = (int)getHeight();
		if (( kbf.is_rotated_90) || (kbf.is_rotated_270))
		{
			target_x = (display_area_width-H)/2;
			target_y = (display_area_height-W)/2;												
		}
		else
		{
			target_x = (display_area_width-W)/2;
			target_y = (display_area_height-H)/2;												
		}

		int x = target_x+(int)((double)scroll_x/s);
		int y = target_y+(int)((double)scroll_y/s);
		if ( kbf.is_rotated_90 )
		{
			if ( debug_flag == true) System.out.println("Doing the painting, rotated 90");
			trans.rotate( Math.toRadians(90) );
			trans.translate(y,-x-h);	
		}
		else if ( kbf.is_rotated_180 )
		{
			if ( debug_flag == true) System.out.println("Doing the painting, rotated 180");
			// also works to do this trans BEFORE rotate
			//trans.translate(x+w,y+h);	
			trans.rotate( Math.toRadians(180) );
			trans.translate(-x-w,-y-h);	
		}
		else if ( kbf.is_rotated_270)
		{
			if ( debug_flag == true) System.out.println("Doing the painting, rotated 270");
			trans.rotate( Math.toRadians(270) );
			trans.translate(-y-w,x);	
		}
		else
		{
			if ( debug_flag == true) System.out.println("Doing the painting, rotated 0");
			trans.translate(x,y);	
		}

		Image local = kbf.the_BufferedImage;
		g2.drawImage(local, trans, this);
	}
	
	//**********************************************************
	private void draw_image_using_translate(Graphics g) 
	//**********************************************************
	{
		((Graphics2D) g).setBackground(Color.BLACK);
		g.clearRect(0, 0, getWidth(), getHeight());

		System.out.println("entering Display_panel"+message+"::draw_image_using_translate()");
		Klik_BufferedImage kbf = get_kbf();
		if ( kbf == null)
		{
			System.out.println("Display_panel"+message+"::draw_image_using_translate kbf == null");
			return; 			
		}
		BufferedImage bi = kbf.the_BufferedImage;
		if ( bi == null)
		{
			System.out.println("Display_panel"+message+"::draw_image_using_translate bi == null");
			return; 			
		}
		int w = bi.getWidth();
		int h = bi.getHeight();

		if ( debug_flag == true)
		{
			//set_message(kbf.quality);
			System.out.println("message should be= "+message);
		}
		if ( kbf.rescale_at_draw_time == true)
		{
			set_message(" ARG !! would need to rescale at draw time but cannot (simple method)");
		}

        double angle = 0;
		if ( kbf.is_rotated_90 )
		{
			if ( debug_flag == true) System.out.println("Doing the painting, rotated 90");
			angle = Math.toRadians(90);
		}
		else if ( kbf.is_rotated_180 )
		{
			if ( debug_flag == true) System.out.println("Doing the painting, rotated 180");
			angle = Math.toRadians(180);
		}
		else if ( kbf.is_rotated_270)
		{
			if ( debug_flag == true) System.out.println("Doing the painting, rotated 270");
			angle = Math.toRadians(270);
		}

		int target_x = 0;
		int target_y = 0;
		int display_area_width = (int)getWidth();
		int display_area_height = (int)getHeight();
		if (( kbf.is_rotated_90) || (kbf.is_rotated_270))
		{
			target_x = (display_area_width-h)/2;
			target_y = (display_area_height-w)/2;												
		}
		else
		{
			target_x = (display_area_width-w)/2;
			target_y = (display_area_height-h)/2;												
		}
		
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(w/2, h/2);
		g2d.rotate(angle);
        g2d.translate(-w/2, -h/2);
        g2d.drawImage(kbf.the_BufferedImage, target_x+scroll_x, target_y+scroll_y, null);

	}


	//**********************************************************
	private Klik_BufferedImage get_kbf() 
	//**********************************************************
	{
		
		Klik_BufferedImage kbf = null;
		if ( the_Image_decoder == null)
		{
			System.out.println("Display_panel"+message+"::draw_image_using_translate the_Image_decoder == null");
			return null; 
		}
		Future<Klik_BufferedImage> local_promise = the_Image_decoder.get_promise();
		if ( local_promise != null)
		{
			try 
			{
				// blocks until the future is done
				System.out.println("in Display_panel::draw_image_method1(), going to block on promise");
				kbf = local_promise.get();
				if (kbf != null)
				{
					the_Image_decoder.set_current_image(kbf);
					the_Image_decoder.reset_promise();
					if (kbf.the_BufferedImage != null)
					{
						System.out.println("going to use future image"+kbf.the_BufferedImage.toString());
					}
					else
					{
						System.out.println("FATAL: future image is null???"+kbf.toString());
						return null;
					}
				}
			} 
			//catch (CancellationException |InterruptedException | ExecutionException e  ) 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			kbf = the_Image_decoder.get_current_image();			
			if ( kbf == null)
			{
				System.out.println("Display_panel::draw_image_method1() kbf == null");
				return null; 
			}
		}
		return kbf;
	}

	//**********************************************************
	public void paintComponent(Graphics g)
	//**********************************************************
	{
		System.out.println("entering Display_panel::paintComponent()");

		if ( simple_drawing_method  == true)
		{
			draw_image_using_translate(g);
			
			if ( message != null)
			{
				g.setColor(Color.WHITE);
				g.drawString(message, 5, 10);	
				message = null;
			}
			g.finalize();
		}
		else
		{
			Graphics2D g2 = (Graphics2D) g;

			draw_image_with_affineTransform(g2);
			
			if ( message != null)
			{
				g2.setColor(Color.WHITE);
				g2.drawString(message, 5, 10);	
				message = null;
			}
			g2.finalize();
			
			
		}

		

	}







}
