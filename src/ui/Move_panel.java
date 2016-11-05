package ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import klik_main.Klik_JFrame;
import klik_main.Properties_manager;


//**********************************************************
public class Move_panel extends Mini_panel
//**********************************************************
{
	boolean debug_flag = true;
	private static final long serialVersionUID = 1L;

	String target_dir = "";

	protected static final int MAX_DIR_MEMORY = 200; // MAGIC

	//**********************************************************
	Move_panel(Klik_JFrame context)
	//**********************************************************
	{
		super(context);
		reconfigure();
	}
	
	static String SELECT_ANOTHER_DIR = "SELECT ANOTHER DIR";
	static String last_move_dir = "last_move_dir";
	//**********************************************************
	@Override
	void reconfigure()
	//**********************************************************
	{
		removeAll();
		ListModel<String> listModel = new DefaultListModel<String>();
		((DefaultListModel<String>) listModel).addElement(SELECT_ANOTHER_DIR);
				
		for(String s : get_move_dirs())
		{
			System.out.println("move dir:"+s);
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
				System.out.println("move panel mouse clicked");
		        if (e.getClickCount() == 2)
		        {
					System.out.println("move panel mouse DOUBLE clicked");
		        	String s = list.getSelectedValue();
					System.out.println("selected item:"+s);
					File target_file_to_be_moved = what_to_configure.get_current_file().file;
					if ( target_file_to_be_moved == null) return;
					File target_dir_to_move_the_file_to = null;
					if ( s.equals(SELECT_ANOTHER_DIR) == true)
					{
						System.out.println("move panel select dir");
						List<String> l = get_move_dirs();
						String path = System.getProperty("user.home");
						if ( l.size() > 0)
						{
							path = l.get(l.size()-1);
						}
						JFileChooser chooser = new JFileChooser(path);
						chooser.setDialogType(JFileChooser.SAVE_DIALOG);
						chooser.setDialogTitle("Select the directory to move to");
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


						int returnVal = chooser.showOpenDialog(null);
						if(returnVal == JFileChooser.APPROVE_OPTION)
						{
							target_dir_to_move_the_file_to = chooser.getSelectedFile();
						}
						else
						{
							return;
						}
					}
					else
					{
						target_dir_to_move_the_file_to = new File(s);
						if ( target_dir_to_move_the_file_to.isDirectory() == false)
						{
							System.out.println("BAD: target_dir_to_move_the_file_to is not a dir");
							return;
						}						
					}
				
					actual_move( target_file_to_be_moved, target_dir_to_move_the_file_to);
					save_move_dir_to_properties(target_dir_to_move_the_file_to);
					reconfigure();
					the_frame.pack(); //dont do this or because of the scrollpane the window cannot be resized?
		         }
		    }
		};
		list.addMouseListener(mouseListener);
	}
	//**********************************************************
	private List<String> get_move_dirs()
	//**********************************************************
	{
		List<String> l = new ArrayList<String>();
		for(int i = 0 ; i < MAX_DIR_MEMORY ; i++)
		{
			String key = last_move_dir+i;
			try
			{
				Properties_manager local = what_to_configure.get_Properties_manager();
				target_dir = (String)local.get(key);
				if ( target_dir == null ) break;
				if ( debug_flag == true) System.out.println("found move dir: "+target_dir);
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
	void save_move_dir_to_properties(File target_dir_to_move_the_file_to)
	//**********************************************************
	{
		if ( target_dir_to_move_the_file_to == null) return;
		// store the dir in properties...
		try 
		{
			// find empty slot
			for(int i = 0 ; i < MAX_DIR_MEMORY ; i++)
			{
				String key = last_move_dir+i;
				Properties_manager local = what_to_configure.get_Properties_manager();
				String s = (String)local.get(key);
				if ( s == null )
				{
					local.put(key,target_dir_to_move_the_file_to.getCanonicalPath());
					local.store_properties();
					break;
				}
				if (s.equals(target_dir_to_move_the_file_to.getCanonicalPath()) == true)
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
	private void actual_move(File target_file_to_be_moved,File target_dir_to_move_the_file_to)
	//**********************************************************
	{
		target_file_to_be_moved.renameTo(new File(target_dir_to_move_the_file_to,target_file_to_be_moved.getName()));
		what_to_configure.scan_dir_next();
		what_to_configure.image_change();
	}

}
