package image_decode;

import image_decode.Image_file_source;
import klik_main.Klik_JFrame;

//**********************************************************
public class Slideshow_thread extends Thread
//**********************************************************
{
	private boolean die;

	private Image_file_source the_Image_file_factory;
	private Klik_JFrame the_frame;

	private long sleep_time;
	//**********************************************************
	public Slideshow_thread(Image_file_source the_Image_file_factory_, Klik_JFrame the_frame_, long sleep_time_)
	//**********************************************************
	{
		the_Image_file_factory = the_Image_file_factory_;
		the_frame = the_frame_;
		sleep_time = sleep_time_;
	}
	
	//**********************************************************
	public void die()
	//**********************************************************
	{
		die = true;
	}
	//**********************************************************
	@Override
	public
	void run()
	//**********************************************************
	{
		for(;;)
		{
			if ( die == true) break;
			the_Image_file_factory.load_random_image();
			the_frame.image_change();
			try {
				
				//while ( the_frame.better_skip.get() == true)
				while ( Scalr_image_factory.better_skip.get() == true)
				{
					sleep(sleep_time);					
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	//**********************************************************
	public void slower()
	//**********************************************************
	{	
		sleep_time =  sleep_time*2;
	}

	//**********************************************************
	public void faster()
	//**********************************************************
	{
		sleep_time =  sleep_time/2;
	}

}
