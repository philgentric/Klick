package suspect;

import java.awt.Image;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.imageio.ImageIO;

import java.util.ArrayList;

import java.net.URL;


//**********************************************************
class Main_for_folder_browser_unit_test 
//**********************************************************
{
	//**********************************************************
	public static void main(String[] args) 
	//**********************************************************
	{
		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run() {
				try 
				{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} 
				catch(Exception e) 
				{
				}
				JFrame f = new JFrame("file browser unit test");
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				Main_for_folder_browser_unit_test fb = new Main_for_folder_browser_unit_test();
				f.setContentPane(new Folder_browser_panel(null));

				try {
					URL urlBig = fb.getClass().getResource("fb-icon-32x32.png");
					URL urlSmall = fb.getClass().getResource("fb-icon-16x16.png");
					ArrayList<Image> images = new ArrayList<Image>();
					images.add( ImageIO.read(urlBig) );
					images.add( ImageIO.read(urlSmall) );
					f.setIconImages(images);
				} catch(Exception weTried) {}

				f.pack();
				f.setLocationByPlatform(true);
				f.setMinimumSize(f.getSize());
				f.setVisible(true);
			}
		});
	}
}

