package tree_file_browser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Component;
import java.awt.Image;
import java.awt.Rectangle;
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
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import klik_main.Klik_JFrame;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.tasks.UnsupportedFormatException;

import javax.imageio.ImageIO;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.net.URL;

/**
 * a panel that contains a tree to display
 * the file system directory structure 
 * corresponding to the icons/files being browsed
 */
//**********************************************************
class File_tree_browsing_panel extends JPanel
//**********************************************************
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FileSystemView fileSystemView;
	private JTree the_jtree;
	private DefaultTreeModel treeModel;
	Klik_JFrame target_app_for_a_selected_image;
	Browsing_with_icons_frame target_app_for_a_selected_dir;

	//**********************************************************
	public File_tree_browsing_panel(
			Klik_JFrame image_selected_target_, 
			Browsing_with_icons_frame dir_selected_target_) 
	//**********************************************************
	{
		target_app_for_a_selected_image = image_selected_target_;
		target_app_for_a_selected_dir = dir_selected_target_;
		setLayout(new BorderLayout(3,3));
		setBorder(new EmptyBorder(5,5,5,5));

		fileSystemView = FileSystemView.getFileSystemView();

		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		treeModel = new DefaultTreeModel(root);

		TreeSelectionListener treeSelectionListener = new TreeSelectionListener() 
		{
			public void valueChanged(TreeSelectionEvent tse)
			{
				TreePath tp = tse.getPath();
				System.out.println("TreeSelectionListener::valueChanged() treePath="+tp.toString());
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
				System.out.println("node="+node.toString());
				show_directory_content(node);
				inform_file((File)node.getUserObject());

				/*
				if ( treePath_to_be_expanded != null)
				{
					System.out.println("TreeSelectionListener::valueChanged() CALLING(2) expand\n on treePath_to_be_expanded="
							+treePath_to_be_expanded.toString());

					the_jtree.expandPath(treePath_to_be_expanded);
				}
				*/
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

		the_jtree = new JTree(treeModel);
		the_jtree.setRootVisible(false);
		the_jtree.addTreeSelectionListener(treeSelectionListener);
		the_jtree.setCellRenderer(new Tree_file_browser_TreeCellRenderer());
		the_jtree.expandRow(0);
		JScrollPane treeScroll = new JScrollPane(the_jtree);
		the_jtree.setVisibleRowCount(35);

		Dimension preferredSize = treeScroll.getPreferredSize();
		Dimension widePreferred = new Dimension(300,(int)preferredSize.getHeight());
		treeScroll.setPreferredSize( widePreferred );
		add(treeScroll);

		show_root();
	}

	//**********************************************************
	public void show_root() 
	//**********************************************************
	{
		the_jtree.setSelectionInterval(0,0);
	}


	//**********************************************************
	private DefaultMutableTreeNode show_directory_content(final DefaultMutableTreeNode node)//, File optional_expansion_target) 
	//**********************************************************
	{
		the_jtree.setEnabled(false);
		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() 
		{
			//**********************************************************
			@Override
			public Void doInBackground() 
			//**********************************************************
			{
				File file = (File) node.getUserObject();
				File[] files = fileSystemView.getFiles(file, true); 
				if (node.isLeaf())
				{
					for (File child : files)
					{
						publish(child);
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
				for (File child : file_list) 
				{
					node.add(new DefaultMutableTreeNode(child));
				}
			}

			//**********************************************************
			@Override
			protected void done() 
			//**********************************************************
			{
				the_jtree.setEnabled(true);
			}
		};
		worker.execute();
		return null;
	}

	//**********************************************************
	private DefaultMutableTreeNode show_directory_simple_content(final DefaultMutableTreeNode node)
	//**********************************************************
	{
		the_jtree.setEnabled(false);
		File file = (File) node.getUserObject();
		File[] files = fileSystemView.getFiles(file, true); 
		if (node.isLeaf())
		{
			for (File child : files)
			{
				node.add(new DefaultMutableTreeNode(child));
			}
		}
		the_jtree.expandPath(new TreePath(node.getPath()));
		the_jtree.setEnabled(true);
		return null;
	}


	//**********************************************************
	private void inform_file(File file) 
	//**********************************************************
	{
		System.out.println("\n\ninform_file() tree selected item is = "+file.getAbsolutePath());

		JFrame f = (JFrame)getTopLevelAncestor();
		if (f!=null) 
		{
			f.setTitle(fileSystemView.getSystemDisplayName(file) );
		}
		if ( file.isFile())
		{

			if ((file.getName().endsWith(".JPG")) || (file.getName().endsWith(".JPEG")) || (file.getName().endsWith(".jpeg")) || (file.getName().endsWith(".jpg")) )
			{
				System.out.println("...inform_file() tree selected item jpeg, requesting display");
				target_app_for_a_selected_image.load_image(file);
				return;
			}
			else
			{
				System.out.println("...inform_file() tree selected item is not jpeg, requesting system default open");
				// use system default bahavior
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(file);
					return;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}
		if ( file.isDirectory())
		{
			System.out.println("...inform_file() tree selected item is dir, requesting DISPLAY");
			target_app_for_a_selected_dir.load(file);
		}
		repaint();
	}

	/*
	 * expand the tree until the target dir is displayed
	 */

	//TreePath treePath_to_be_expanded = null;

	//**********************************************************
	public void expand(File dir) 
	//**********************************************************
	{
/*
		if (treePath_to_be_expanded!= null) 
		{
			System.out.println("CALLING(1) expand on: treePath_to_be_expanded="+treePath_to_be_expanded.toString());
			the_jtree.expandPath(treePath_to_be_expanded);
		}
*/

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
		// node is /

		// list the Files from leaf up to root
		ArrayList<File> l = new ArrayList<>();
		File f =  dir;
		for(;;)
		{
			if ( f == null) break; 
			l.add(f);
			f = f.getParentFile();
		}
		// reverse the list, create and link the nodes
		//DefaultMutableTreeNode the_fucking_node = null;
		DefaultMutableTreeNode papa = root;
		for ( int i = l.size()-1 ; i >=0 ; i--)
		{
			File fff = l.get(i);


			DefaultMutableTreeNode tn = find_son(papa, fff);
			if ( tn == null)
			{
				System.out.println("son NODE not found, calling show_directory_content on "+papa);
				show_directory_simple_content(papa);
				tn = find_son(papa, fff);			
				if ( tn == null)
				{
					System.out.println("son NODE not found, AGAIN ???? shit");
				}
				else
				{
					System.out.println("son NODE  found"+tn.toString());
				}
			}

			papa = tn;
			//the_fucking_node = tn; // ready when we exit that loop
			//System.out.println(l.get(i).toString());
		}
		//TreePath 
		//treePath_to_be_expanded = new TreePath(the_fucking_node.getPath());
		//System.out.println("NEW treePath_to_be_expanded="+treePath_to_be_expanded.toString());

		//the_jtree.expandPath(treePath_to_be_expanded);

	}

	// maybe the path was ALREADY
	// explored and the son exists?
	//**********************************************************
	private DefaultMutableTreeNode find_son(DefaultMutableTreeNode papa, File fff) 
	//**********************************************************
	{
		// check if in the subnodes of papa
		// there is not already what we are looking for
		Enumeration<DefaultMutableTreeNode> sons = papa.children();
		for ( ;;)
		{
			if ( sons.hasMoreElements() == false) break;
			DefaultMutableTreeNode son = sons.nextElement();
			File checked = (File) son.getUserObject();
			if ( checked.getAbsolutePath().equals(fff.getAbsolutePath()))
			{
				System.out.println("found son NODE"+son.toString());
				return son;
			}
		}
		return null; // not found
	}


}

