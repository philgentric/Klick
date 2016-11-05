package tree_file_browser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.coobird.thumbnailator.Thumbnails;

//**********************************************************
public class Icon_being_loaded implements Callable<Clickable_icon>
//**********************************************************
{
	BufferedImage bi;
	File f;
	Future<Clickable_icon> the_future = null;
	//JLabel the_target_jlabel;
	Icon_acceptor the_target_jlabel;
	//Klik_JFrame target;
	File icon_file;
	int target_size;
	
	//**********************************************************
	Icon_being_loaded(File f_, File icon_file_, Icon_acceptor la_, int size)
	//**********************************************************
	{
		f = f_;
		icon_file = icon_file_;
		//target = target_;
		the_target_jlabel = la_;
		target_size = size;
	}

	//**********************************************************
	@Override
	public Clickable_icon call() throws Exception 
	//**********************************************************
	{
		//System.out.println("launching icon creation in separate thread for f="+f.getName());
		//Clickable_icon returned = new Clickable_icon( f, icon_file, target, false, the_target_jlabel,true);
		Clickable_icon returned = new Clickable_icon( f, null, false, the_target_jlabel,true);
		BufferedImage bi = null;

		try 
		{
			bi = Thumbnails.of(f)
					.size(target_size,target_size)
					.asBufferedImage();
		} 
		catch( FileNotFoundException e)
		{			
			System.out.println("ARG !!! cannot find the image !!"+f.getAbsolutePath());
			//e.printStackTrace();
		}	
		catch (IIOException e)
		{
			System.out.println("ARG !!! cannot decode the image !!"+f.getAbsolutePath());
			//e.printStackTrace();
		}
		catch (IOException e)
		{
			System.out.println("IOException: cannot thumbnail image: "+f.getAbsolutePath());
			//e.printStackTrace();
		}
		try 
		{
			ImageIO.write(bi,"png",icon_file);
		} 
		catch (IOException e)
		{
			System.out.println("ARG !!! cannot write the icon file !!"+f.getAbsolutePath());
			//e.printStackTrace();
		}

		returned.bi = bi;
		returned.the_file = f;
		//ImageIcon image = new ImageIcon(bi); // pass the file location of an image
		returned.ia.set_icon(bi);				
		//returned.la.validate();
		//p.validate();
		return returned;
	}

}
