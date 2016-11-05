package tree_file_browser;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.coobird.thumbnailator.Thumbnails;

 
/* there is one instance of this per viible node in the tree
 * so by replacing the icon of "label" we can specify a different
 * icon for each file...
 */
//**********************************************************
public class Tree_file_browser_TreeCellRenderer extends DefaultTreeCellRenderer 
//**********************************************************
{
	private static final long serialVersionUID = 1L;
	private FileSystemView fileSystemView;
	private JLabel label;
	
	//**********************************************************
	Tree_file_browser_TreeCellRenderer() 
	//**********************************************************
	{
		label = new JLabel("default text");
		label.setOpaque(true);
		fileSystemView = FileSystemView.getFileSystemView();
	}

	//**********************************************************
	@Override
	public Component getTreeCellRendererComponent(
			JTree tree,
			Object value,
			boolean selected,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus) 
	//**********************************************************
	{

		DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
		File file = (File)node.getUserObject();
		if ( file.isFile())
		{
			//System.out.println("icon file custom");
			if ((file.getName().endsWith(".GIF")) || (file.getName().endsWith(".JPG")) || (file.getName().endsWith(".JPEG")) || (file.getName().endsWith(".jpeg")) || (file.getName().endsWith(".jpg")) || (file.getName().endsWith(".gif")) )
			{
				
				Browser_jlabel_icon_acceptor acceptor = new Browser_jlabel_icon_acceptor(label);
				Clickable_icon ci = new Clickable_icon(
						file, 
						null, 
						true, 
						acceptor, 
						false);	
			}
			else
			{
				final javax.swing.JFileChooser fc = new javax.swing.JFileChooser(); 
				Icon icon = fc.getUI().getFileView(fc).getIcon(file);
				label.setIcon(icon);						
			}
		}
		else
		{
			label.setIcon(fileSystemView.getSystemIcon(file));			
		}
		label.setText(fileSystemView.getSystemDisplayName(file));
		label.setToolTipText(file.getPath());

		if (selected)
		{
			label.setBackground(backgroundSelectionColor);
			label.setForeground(textSelectionColor);
		} 
		else 
		{
			label.setBackground(backgroundNonSelectionColor);
			label.setForeground(textNonSelectionColor);
		}

		return label;
	}
}
