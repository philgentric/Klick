package tree_file_browser;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import klik_main.Klik_JFrame;
import net.coobird.thumbnailator.Thumbnails;

//**********************************************************
public class Clickable_icon
//**********************************************************
{
	private static final boolean debug_file = false;
	public static int small_icon_size = 32;
	public static int large_icon_size =128;

	private static ExecutorService executor = Executors.newFixedThreadPool(10); 
	BufferedImage bi;
	File the_file;
	Future<Clickable_icon> the_future = null;
	Icon_acceptor ia;
	ImageIcon default_icon = null;
	private static String home = "user.home";	
	private static String default_cache_dir_name = "cache_klik";

	//**********************************************************
	static File get_icon_dir()
	//**********************************************************
	{
		return new File(System.getProperty(home),default_cache_dir_name);
	}

	//**********************************************************
	public static void clear_cache()
	//**********************************************************
	{
		File d = get_icon_dir();
		for ( File f : d.listFiles())
		{
			if ( f.getName().endsWith(".png") == true) f.delete();
		}
	}

	//**********************************************************
	public Clickable_icon(
			File f_, 
			Klik_JFrame target, 
			boolean multi_threaded_icon_generation, 
			Icon_acceptor ia_,
			boolean icon_big) 
	//**********************************************************
	{
		the_file = f_;
		ia = ia_;

		File icon_dir = get_icon_dir();
		if ( icon_dir.exists() == false)
		{
			System.out.println("Browsing_panel: icon dir = "+icon_dir.getAbsolutePath()+" does not exist");
			if ( icon_dir.mkdir() == false)
			{
				System.out.println("Browsing_panel: icon dir = "+icon_dir.getAbsolutePath()+" cannot be created");
			}
		}
		if ( debug_file) System.out.println("Browsing_panel: icon dir = "+icon_dir.getAbsolutePath());

		/* TODO
		URL url = ClassLoader.getSystemResource("klik_multi_package/resources/QuestionMark.png");

		//System.out.println("URL for default icon="+url);
		if ( url != null)
		{
			Toolkit kit = Toolkit.getDefaultToolkit();
			Image i = kit.createImage(url);
			default_icon = new ImageIcon(i);			
		}
		 */
		String tag = make_html_tag(the_file.getName());
		//la = new JLabel(tag); // the html markup is for getting a MULTI LINE text !!!
		if ( ia != null) ia.set_text(tag);

		int icon_size = small_icon_size;
		if ( icon_big)
		{
			icon_size = large_icon_size;
		}


		String key = the_file.getAbsolutePath();
		//String key = f.getName();
		key = key.replace("\\", "");
		key = key.replace("/", "");
		key = key.replace(":", "");
		key += icon_size;
		key += ".png";

		File icon_file = new File(icon_dir,key);

		bi = get_icon_from_cache(icon_file);
		if ( bi == null)
		{
			if ( multi_threaded_icon_generation == true)
			{
				executor.submit(new Icon_being_loaded(the_file,  icon_file, ia,icon_size));
			}
			else
			{
				try 
				{
					bi = Thumbnails.of(the_file)
							.size(icon_size, icon_size)
							.asBufferedImage();
				} 
				catch( FileNotFoundException  e )
				{					
					System.out.println("ARG !!! cannot find the image file:"+the_file.getAbsolutePath());
					//e.printStackTrace();
					bi = null;
				}	
				catch (IIOException e)
				{
					System.out.println("ARG !!! cannot decode the image file:"+the_file.getAbsolutePath());
					//e.printStackTrace();
					bi = null;
				} catch (IOException e) 
				{
					System.out.println("ARG !!! cannot read the image file:"+the_file.getAbsolutePath());
					//e.printStackTrace();
					bi = null;
				}
				try 
				{
					if ( bi != null) ImageIO.write(bi,"png",icon_file);
				} 
				catch (IOException e)
				{
					System.out.println("ARG !!! cannot write the icon file ! (file system full?)");
					//e.printStackTrace();
				}
			}
		}


		if ( bi != null)
		{
			//ImageIcon image = new ImageIcon(bi); // pass the file location of an image
			if ( ia != null) ia.set_icon(bi);				
			//System.out.println("OK image for file:\n->"+f.getAbsolutePath()+"<-\nusing specific icon");
		}

		if ( ia != null) 
		{
			ia.setHorizontalTextPosition(JLabel.CENTER);
			ia.setVerticalTextPosition(JLabel.BOTTOM);
			ia.addMouseListener(new MouseAdapter()  
			{  
				public void mouseClicked(MouseEvent e)  
				{  
					if ( debug_file) System.out.println("Clickable_icon.mouseClicked()"+ the_file.getAbsolutePath());
					if ( target != null) target.load_image(the_file);
				}

			}); 

		}


	}



	//**********************************************************
	private String make_html_tag(String name) 
	//**********************************************************
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		int len = name.length();
		int partitionSize = 20;;
		for (int i=0; i<len; i+=partitionSize )
		{
			sb.append(name.substring(i, Math.min(len, i + partitionSize)));
			sb.append("<br>");
		}
		sb.append("</html>");
		return sb.toString();
	}



	//**********************************************************
	private static BufferedImage get_icon_from_cache(File f) 
	//**********************************************************
	{
		if ( f.exists() == false) return null;
		BufferedImage bi = null;
		try 
		{
			bi = ImageIO.read(f);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		return bi;
	}


};
