package ui;

import java.awt.GridLayout;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import klik_main.Klik_JFrame;

//**********************************************************
public class Help_mini_panel extends Mini_panel 
//**********************************************************
{
	private static final long serialVersionUID = 1L;
	
	//**********************************************************
	Help_mini_panel(Klik_JFrame context)
	//**********************************************************
	{
		super(context);
		reconfigure();
	}
	//**********************************************************
	@Override
	public void reconfigure()
	//**********************************************************
	{
		
		removeAll();
		setLayout(new GridLayout());
		JTextArea jta = new JTextArea();
		String t = "";
		t += "Klik is a JPEG-only image display and sort program\n";
		t += "Klik has been specifically designed to view directories containing a huge number of images (100k or more)\n";
		t += "For this Klik enables you to select several display modes between very fast and top quality\n";
		t += "The keyboard accelerator keys are:\n";
		t += "\tJump to next image shortcuts:\n";
		t += "\t\tspace-bar:\t\tdisplays the NEXT image in directory order\n";
		t += "\t\tright-arrow:\t\tdisplays the NEXT image in directory order\n";
		t += "\t\tright-arrow+SHIFT:\tJUMPS 10 images in directory order\n";
		t += "\t\tright-arrow+CTRL:\tJUMPS 100 images in directory order\n";
		t += "\t\tright-arrow+SHIFT+CTRL:\tdisplays the LAST image in directory order\n";
		t += "\t\tleft-arrow:\t\tdisplays the PREVIOUS image in directory order\n";
		t += "\t\tleft-arrow+SHIFT:\tJUMPS -10 images in directory order\n";
		t += "\t\tleft-arrow+CTRL:\tJUMPS -100 images in directory order\n";
		t += "\t\tleft-arrow+SHIFT+CTRL:\tdisplays the FIRST image in directory order\n";
		t += "\t\tr:\t\tdisplays the next image chosen at RANDOM\n";
		t += "\t\tu:\t\tdisplays the next image that has the word ->ultim<- in its name, see v for \"vote\" below\n";
		t += "\tMove/Copy:\n";
		t += "\t\tpops up a mini frame where you can choose a destination folder\n";
		t += "\t\tdestination folders will be remembered until you clear history \n";
		t += "\t\twhen a destination folder is listed, clicking on it will MOVE or COPY the current image into that folder\n";
		t += "\tSlideshow:\n";
		t += "\t\tslide show order is the last requested i.e. next in file order or random or ultim\n";
		t += "\t\ts : start/stop the SLIDESHOW\n";
		t += "\t\tz : slow the slideshow by 2  : can get as slow as you want\n";
		t += "\t\tZ : speedup the slideshow by 2  : can get as fast as your platform can go... or hang:-)\n";
		t += "\tZoom:\n";
		t += "\t\tthe window size will automatically track the full image unless a zoom factor has been set as follows:\n";
		t += "\t\t= : pixel-for-pixel zoom (1 image pixel = 1 screen pixel)\n";
		t += "\t\tUp arrow or (+): zoom IN  by 10%\n";
		t += "\t\tDown arrow or (-): zoom OUT by 10%\n";
		t += "\tVote:\n";
		t += "\t\tv : vote for this image = adds _ultim to the file name for later viewing with the u mode\n";
		t += "\tImage Quality versus Speed of display:\n";
		t += "\t\ta : sets the display mode to AUTOMATIC quality\n";
		t += "\t\tf : sets the display mode to FAST\n";
		t += "\t\tF : sets the display mode to VERY FAST = only power of two sizes (very fast browsing)\n";
		t += "\t\tb : sets the display mode to BALANCED quality\n";
		t += "\t\tq : sets the display mode to HIGH quality (slow)\n";
		t += "\t\tQ : sets the display mode to ULTRA HIGH QUALITY (verrrry slow on large images)\n";
		t += "\tCredits: see the Copyrights & License popup for details\n";
		t += "\t\tKlik was written 100% java by Philippe Gentric copyright 2015\n";
		t += "\t\tKlik uses the metadata-extractor 2.6.4 library for EXIF extraction (Apache License V2.0)\n";
		t += "\t\tKlik uses the turbojpeg 1.2.90 library for JPEG decoding\n";
		t += "\t\tKlik uses the imgscalr 4.2 rescaling library (except for the very fast mode and or very large images, where pure java scaling graphcard is used)\n";
		t += "\t\tKlik uses the thumbnailator 0.4.7 library, for thumbnail generation \n";
		
		jta.setText(t);
		add(jta);
		 
		revalidate();
		repaint();
		if ( the_frame != null) the_frame.pack();// do this to make sure the frame size is updated when the text changes!
	
	}
	//**********************************************************
	private void action(Klik_JFrame context, JTextField jtf)
	//**********************************************************
	{
	}
	
}
