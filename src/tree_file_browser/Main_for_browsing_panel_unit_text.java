package tree_file_browser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.tasks.UnsupportedFormatException;

import javax.imageio.ImageIO;

import java.util.List;
import java.util.ArrayList;

import java.net.URL;


//**********************************************************
class Main_for_browsing_panel_unit_test
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

				File_tree_browsing_panel p = new File_tree_browsing_panel(null,null);
				f.setContentPane(p);

				f.pack();
				f.setLocationByPlatform(true);
				f.setMinimumSize(f.getSize());
				f.setVisible(true);

				//p.show_root();
			}
		});
	}
}

