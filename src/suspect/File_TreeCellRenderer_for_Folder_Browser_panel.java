package suspect;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import klik_main.Klik_JFrame;
import net.coobird.thumbnailator.Thumbnails;

//**********************************************************
public class File_TreeCellRenderer_for_Folder_Browser_panel extends DefaultTreeCellRenderer 
//**********************************************************
{
	private static final long serialVersionUID = 1L;
	private FileSystemView fileSystemView;
	Klik_JFrame the_klik_jFrame = null;

	//**********************************************************
	File_TreeCellRenderer_for_Folder_Browser_panel(Klik_JFrame the_klik_jFrame_) 
	//**********************************************************
	{
		fileSystemView = FileSystemView.getFileSystemView();
		//the_klik_jFrame = the_klik_jFrame_;
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
        JLabel label = (JLabel) this ;
		System.out.println("File_TreeCellRenderer2.getTreeCellRendererComponent() label:"+label.getText() );
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
		Object ooo = node.getUserObject();
		try 
		{
			File file = (File)ooo;
			process_file_or_dir(file, label);
			System.out.println("getTreeCellRendererComponent() node is a file"+file.getAbsolutePath() );
		}
		catch (ClassCastException e)
		{
			/*
			try 
			{
				Clickable_icon ci = (Clickable_icon)ooo;
				ci.ia = new Jlabel_icon_acceptor(label);
				System.out.println("getTreeCellRendererComponent()  node is a clickable icon" );
			}
			catch (ClassCastException e2)
			{
				System.out.println("fooooooook");
			}*/
		}

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

	//**********************************************************
	JLabel process_file_or_dir(File file, JLabel label)
	//**********************************************************
	{
		if ( file.isFile())
		{
			process_file_only(file, label);
		}
		else
		{
			// is a dir
			label.setIcon(fileSystemView.getSystemIcon(file));			
		}
		label.setText(fileSystemView.getSystemDisplayName(file));
		label.setToolTipText(file.getPath());

		return label;
	}

	//**********************************************************
	static JLabel process_file_only(File file, JLabel label)
	//**********************************************************
	{
		System.out.println("process_file_only "+file.getAbsolutePath()+" label text="+label.getText());
		// any other file
		final javax.swing.JFileChooser fc = new javax.swing.JFileChooser(); 
		Icon icon = fc.getUI().getFileView(fc).getIcon(file);
		label.setIcon(icon);						
		return label;
	}
	
	
}
