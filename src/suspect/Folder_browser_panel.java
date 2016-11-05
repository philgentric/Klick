package suspect;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Container;
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

import klik_main.Klik_JFrame;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.tasks.UnsupportedFormatException;

import javax.imageio.ImageIO;

import java.util.List;
import java.util.ArrayList;

import java.net.URL;


//**********************************************************
class Folder_browser_panel extends JPanel
//**********************************************************
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FileSystemView fileSystemView;
	private JTree tree;
	private DefaultTreeModel treeModel;
	Klik_JFrame the_thing;
	File_TreeCellRenderer_for_Folder_Browser_panel the_tre_cell_renderer;
	//**********************************************************
	public Folder_browser_panel(Klik_JFrame the_thing_) 
	//**********************************************************
	{
		the_thing = the_thing_;
		setLayout(new BorderLayout(3,3));
		setBorder(new EmptyBorder(5,5,5,5));

		fileSystemView = FileSystemView.getFileSystemView();

		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		treeModel = new DefaultTreeModel(root);

		TreeSelectionListener treeSelectionListener = new TreeSelectionListener() 
		{
			public void valueChanged(TreeSelectionEvent tse)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
				show_directory_content(node);
				inform_file(node.getUserObject());
			}
		};

		// show the file system roots.
		File[] roots = fileSystemView.getRoots();
		for (File fileSystemRoot : roots) 
		{
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
			root.add(node);
			File[] files = fileSystemView.getFiles(fileSystemRoot, true);
			for (File file : files) 
			{
				//System.out.println("file in root"+file.getAbsolutePath());
				node.add(new DefaultMutableTreeNode(file));
			}
		}

		tree = new JTree(treeModel);
		tree.setRootVisible(false);
		tree.addTreeSelectionListener(treeSelectionListener);
		the_tre_cell_renderer = new File_TreeCellRenderer_for_Folder_Browser_panel(the_thing);
		tree.setCellRenderer(the_tre_cell_renderer);
		tree.expandRow(0);
		JScrollPane treeScroll = new JScrollPane(tree);
		tree.setVisibleRowCount(35);

		Dimension preferredSize = treeScroll.getPreferredSize();
		Dimension widePreferred = new Dimension(300,(int)preferredSize.getHeight());
		treeScroll.setPreferredSize( widePreferred );
		add(treeScroll);

	}

	//**********************************************************
	public void show_root() 
	//**********************************************************
	{
		tree.setSelectionInterval(0,0);
	}


	//**********************************************************
	private void show_directory_content(final DefaultMutableTreeNode node) 
	//**********************************************************
	{
		tree.setEnabled(false);
		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() 
		{
			//**********************************************************
			@Override
			public Void doInBackground() 
			//**********************************************************
			{
				File file = (File) node.getUserObject();
				//if (file.isDirectory()) 
				{
					File[] files = fileSystemView.getFiles(file, true); 
					if (node.isLeaf())
					{
						for (File child : files)
						{
							//if (child.isDirectory()) 
							{
								publish(child);
							}
						}
					}
				}
				return null;
			}

			//**********************************************************
			@Override
			protected void process(List<File> file_list) 
			//**********************************************************
			{
				//System.out.println("process()");
				for (File file : file_list) 
				{
					/*if ((file.getName().endsWith(".GIF")) || (file.getName().endsWith(".JPG")) || (file.getName().endsWith(".JPEG")) || (file.getName().endsWith(".jpeg")) || (file.getName().endsWith(".jpg")) || (file.getName().endsWith(".gif")) )
					{

						Clickable_icon ci = new Clickable_icon(
								file, 
								the_thing, 
								true, 
								null, 
								false);
						System.out.println("Folder_browser_panel.process() attaching a clickableIcon to the node");
						node.add(new DefaultMutableTreeNode(ci));						
					}
					else*/
					{
						System.out.println("Folder_browser_panel.process() attaching a file to the node");
						node.add(new DefaultMutableTreeNode(file));						
					}
				}
			}

			//**********************************************************
			@Override
			protected void done() 
			//**********************************************************
			{
				tree.setEnabled(true);
			}
		};
		worker.execute();
	}

	//**********************************************************
	private void inform_file(Object object) 
	//**********************************************************
	{
		File file = null;
		try
		{
			file = (File)object;
		}
		catch (ClassCastException e)
		{
			/*Clickable_icon ci = null;
			try
			{
				ci = (Clickable_icon)object;
			}
			catch (ClassCastException e2)
			{
				System.out.println("idfdfdeghzdjzehdgzjedhgzejdgzejhgzejdzgjh");
				return;
			}*/
			System.out.println("inform_file() CI");
			return;
		}
		System.out.println("inform_file() 443434: "+file.getAbsolutePath());

		//Icon icon = fileSystemView.getSystemIcon(file);
		JFrame f = (JFrame)getTopLevelAncestor();
		if (f!=null) 
		{
			f.setTitle(fileSystemView.getSystemDisplayName(file) );
		}
		//repaint();
	}
}


