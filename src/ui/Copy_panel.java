package ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import klik_main.Klik_JFrame;
import klik_main.Properties_manager;


//**********************************************************
public class Copy_panel extends Mini_panel
//**********************************************************
{

	private static final long serialVersionUID = 1L;

	String target_dir = "";

	protected static final int MAX_DIR_MEMORY = 200;

	//**********************************************************
	Copy_panel(Klik_JFrame context)
	//**********************************************************
	{
		super(context);
		reconfigure();
	}
	
	static String SELECT_ANOTHER_DIR = "SELECT ANOTHER DIR";
	static String last_copy_dir = "last_copy_dir";
	
	
	//**********************************************************
	@Override
	void reconfigure()
	//**********************************************************
	{
		removeAll();
		ListModel<String> listModel = new DefaultListModel<String>();
		((DefaultListModel<String>) listModel).addElement(SELECT_ANOTHER_DIR);
				
		for(String s : get_copy_dirs())
		{
			System.out.println("copy dir:"+s);
			((DefaultListModel<String>) listModel).addElement(s);
		}
		JList<String> list = new JList<String>(listModel);
		JScrollPane pictureScrollPane = new JScrollPane(list);
		add(pictureScrollPane);
		revalidate();
		repaint();
		MouseAdapter mouseListener = new MouseAdapter()
		{
		    public void mouseClicked(MouseEvent e)
		    {
		        if (e.getClickCount() == 2)
		        {
		        	String s = list.getSelectedValue();
					System.out.println("selected item:"+s);
					File target_file_to_be_copied = what_to_configure.get_current_file().file;
					if ( target_file_to_be_copied == null) return;
					File target_dir_to_copy_the_file_to = null;
					if ( s.equals(SELECT_ANOTHER_DIR) == true)
					{
						List<String> l = get_copy_dirs();
						String path = System.getProperty("user.home");
						if ( l.size() > 0)
						{
							path = l.get(l.size()-1);
						}
						JFileChooser chooser = new JFileChooser(path);
						chooser.setDialogType(JFileChooser.SAVE_DIALOG);
						chooser.setDialogTitle("Select the directory to copy to");
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


						int returnVal = chooser.showOpenDialog(null);
						if(returnVal == JFileChooser.APPROVE_OPTION)
						{
							target_dir_to_copy_the_file_to = chooser.getSelectedFile();
						}
						else
						{
							return;
						}
					}
					else
					{
						target_dir_to_copy_the_file_to = new File(s);
						if ( target_dir_to_copy_the_file_to.isDirectory() == false)
						{
							System.out.println("BAD: target_dir_to_copy_the_file_to is not a dir");
							return;
						}						
					}
					File copy = new File(target_dir_to_copy_the_file_to,target_file_to_be_copied.getName());
					actual_copy( target_file_to_be_copied, copy);
					save_copy_dir_to_properties(target_dir_to_copy_the_file_to);
					reconfigure();
		         }
		    }
		};
		list.addMouseListener(mouseListener);
	}
	//**********************************************************
	private List<String> get_copy_dirs()
	//**********************************************************
	{
		List<String> l = new ArrayList<String>();
		for(int i = 0 ; i < MAX_DIR_MEMORY ; i++)
		{
			String key = last_copy_dir+i;
			try
			{
				Properties_manager local = what_to_configure.get_Properties_manager();
				target_dir = (String)local.get(key);
				System.out.println("found copy dir: "+target_dir);
				if ( target_dir == null ) break;
				l.add(target_dir);
			}
			catch (Exception eeee)
			{
				eeee.printStackTrace();
			}
		}
		return l;
	}

	//**********************************************************
	void save_copy_dir_to_properties(File target_dir_to_copy_the_file_to)
	//**********************************************************
	{
		if ( target_dir_to_copy_the_file_to == null) return;
		// store the dir in properties...
		try 
		{
			// find empty slot
			for(int i = 0 ; i < MAX_DIR_MEMORY ; i++)
			{
				String key = last_copy_dir+i;
				Properties_manager local = what_to_configure.get_Properties_manager();
				String s = (String)local.get(key);
				if ( s == null )
				{
					local.put(key,target_dir_to_copy_the_file_to.getCanonicalPath());
					local.store_properties();
					break;
				}
				if (s.equals(target_dir_to_copy_the_file_to.getCanonicalPath()) == true)
				{
					break; // already there dont add it
				}
			}
		}
		catch (Exception eeee)
		{
			eeee.printStackTrace();
		}
		
	}

	//**********************************************************
	private void actual_copy(File target_file_to_be_copied,File copy_)
	//**********************************************************
	{
		Path source = target_file_to_be_copied.toPath();
		Path copy = copy_.toPath();
		CopyOption[] options = new CopyOption[1];
		options[0] = StandardCopyOption.COPY_ATTRIBUTES;
		try {
			Files.copy(source, copy,options);
		} 
		catch( FileAlreadyExistsException e)
		{
			JOptionPane.showMessageDialog(null, "Copy failed: a file with that name already exists !");
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
