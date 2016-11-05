package ui;


import java.awt.BorderLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;


//**********************************************************
public class IntelligentFileRenamer extends JFrame implements ActionListener
//**********************************************************
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean debug_flag = true;
	JTextArea the_text_area;
	File GLOBAL_selected_dir;

	//**********************************************************
	public IntelligentFileRenamer()
	//**********************************************************
	{
		//enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		GLOBAL_selected_dir = null;
		setTitle("File manipulation tool box");
		setSize(700,700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		the_text_area = new JTextArea(80,80);
		ScrollPane sp = new ScrollPane (ScrollPane.SCROLLBARS_ALWAYS);
		sp.add (the_text_area);
		add(sp);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane,BoxLayout.Y_AXIS));
		buttonPane.add(Box.createHorizontalStrut(5));
		buttonPane.add(Box.createHorizontalStrut(5));

		JButton jb = new JButton ("Clean names");
		jb.setActionCommand("clean_names");
		jb.addActionListener (this);
		buttonPane.add(jb);

		jb = new JButton ("Rename");
		jb.setActionCommand("rename");
		jb.addActionListener (this);
		buttonPane.add(jb);

		jb = new JButton ("Super Rename");
		jb.setActionCommand("superrename");
		jb.addActionListener (this);
		buttonPane.add(jb);

		
		

		add(buttonPane, BorderLayout.PAGE_END);


	}



	//**********************************************************
	protected void processWindowEvent(WindowEvent e)
	//**********************************************************
	{

		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			if ( debug_flag == true ) System.out.println("WindowEvent.WINDOW_CLOSING");
			setVisible(false);
			System.exit(0);
		}
	}


	//**********************************************************
	@Override
	public void actionPerformed(ActionEvent e)
	//**********************************************************
	{
		if ( debug_flag ) System.out.println(e.getActionCommand());



		if ( e.getActionCommand().equals("clean_names"))
		{
			if ( debug_flag ) System.out.println("clean_names");
			clean_names();
			return;
		}
		if ( e.getActionCommand().equals("rename"))
		{
			if ( debug_flag ) System.out.println("rename");
			rename();
			return;
		}
		if ( e.getActionCommand().equals("superrename"))
		{
			if ( debug_flag ) System.out.println("superrename");
			super_rename();
			return;
		}
		if ( e.getActionCommand().equals("sorttodir"))
		{
			if ( debug_flag ) System.out.println("sorttodir");
			sort_to_dir();
			return;
		}
		if ( e.getActionCommand().equals("delete_empty_dirs"))
		{
			if ( debug_flag ) System.out.println("delete_empty_dirs");
			delete_empty_dirs();
			return;
		}

		/*
		if ( e.getActionCommand().equals("selective erase"))
		{
			if ( debug_flag ) System.out.println("selective erase");
			selective_erase();
			return;
		}
		 */
		if ( e.getActionCommand().equals("duplicate"))
		{
			if ( debug_flag ) System.out.println("duplicate");
			selective_duplicate();
			return;
		}


	}

	//**********************************************************
	private String clean_name(String name)
	//**********************************************************
	{
		// remove leading and trailing spaces
		name = name.trim();
		// replace other spaces with underscore
		name = name.replace(' ','_');
		// replace minus with underscore
		name = name.replace('-','_');
		// replace apostrophe with underscore
		name = name.replace('\'','_');
		// replace arobasse with underscore
		name = name.replace('@','_');
		return name;
	}

	//**********************************************************
	boolean actual_clean_rename(File f)
	//**********************************************************
	{
		String name = f.getName();
		String new_name = clean_name(name);
		if ( new_name.equals(name) == true )
		{
			return true;
		}
		File target = new File(f.getParentFile(),new_name);
		if (target.exists() == true)
		{
			String target_name = target.getName();
			for(int i = 1; i < 100 ; i++)
			{
				if (target_name.lastIndexOf('.') > 0 )
				{
					target_name = target_name.substring(0,target_name.lastIndexOf('.'))
							+ "a" 
							+ target_name.substring(target_name.lastIndexOf('.'));					
				}
				else
				{
					target_name = target_name + "a";					
				}
				
				target = new File(f.getParentFile(),target_name);
				if (target.exists() == false) break;
				else target = null;
			}
		}
		if (target == null)
		{
			the_text_area.append("ERROR: could nor rename:" + name + "\n");
			return false;
		}
		boolean status =  f.renameTo(target);
		if (status == true)
		{
			the_text_area.append("Renamed:" + name +" into:" + target.getName() + "\n");
		}
		return status;
		
	}
	//**********************************************************
	private void clean_names()
	//**********************************************************
	{
		File f = select_target_dir(new File(System.getProperty("user.home")), "Clean all names");
		if ( f == null) return;

		//**********************************************************
		class Operate_on_dir extends Operate_on
		//**********************************************************
		{
			boolean operation(File dir)
			{
				return actual_clean_rename(dir);
			}

		}
		//**********************************************************
		class Operate_on_file extends Operate_on
		//**********************************************************
		{
			boolean operation(File file)
			{
				return actual_clean_rename(file);
			}
		}
		recurse_down_the_file_tree(f, new Operate_on_dir(), new Operate_on_file());
	}

	//**********************************************************
	private abstract class Operate_on
	//**********************************************************
	{
		abstract boolean operation(File f);
	}

	//**********************************************************
	private boolean recurse_down_the_file_tree(File target_dir, Operate_on dir, Operate_on file)
	//**********************************************************
	{	
		System.out.println("******************************");
		System.out.println("target dir: " + target_dir);
		System.out.println("******************************");

		File[] all_files = target_dir.listFiles();

		int i;
		for(i= 0;i< Array.getLength(all_files);i++)
		{
			File tmp = all_files[i];

			if (tmp.isDirectory() == true )
			{
				recurse_down_the_file_tree(tmp,dir,file);
				// better do the stuff afterward !
				if ( dir.operation(tmp) == false) return false;
			}
			else
			{
				if ( file.operation(tmp) == false) return false;
			}
		}
		return true;
	}

	//**********************************************************
	private File select_target_dir(File proposed_selected_dir, String the_operation_description)
	//**********************************************************
	{	
		File selected_dir;
		JFileChooser chooser;
		if ( proposed_selected_dir == null )
		{
			chooser = new JFileChooser(".");
		}
		else
		{
			chooser = new JFileChooser(proposed_selected_dir);
		}
		chooser.setDialogTitle("Select the target directory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			selected_dir = chooser.getSelectedFile();

			System.out.println("the dir is :" + selected_dir);

			String target_name = selected_dir.getName();

			Object[] options = {"Yes, I am sure !", "No, that was a mistake !"};

			int n = JOptionPane.showOptionDialog(this,
					"This will recursively perform the target operation on ALL the files down from directory: " + selected_dir + "\n" 
							+ "The target operation is:" + the_operation_description + "\n"
							+ "Are you sure you want to do that ?:\n",
							"Rename Confirmation Required",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[1]); // default is to cancel

			if ( n ==  JOptionPane.YES_OPTION)
			{
				return selected_dir;
			}

		}		


		return null;

	}



	//**********************************************************
	public void shorten()
	//**********************************************************
	{

		JFileChooser chooser;
		if ( GLOBAL_selected_dir == null )
		{
			chooser = new JFileChooser(".");
		}
		else
		{
			chooser = new JFileChooser(GLOBAL_selected_dir.getParentFile());
		}
		chooser.setDialogTitle("select the target directory (the one where all files will be renamed ...)");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			GLOBAL_selected_dir = chooser.getSelectedFile();

			System.out.println("the dir is :" + GLOBAL_selected_dir);

			String target_name = GLOBAL_selected_dir.getName();

			Object[] options = {"Yes, I am sure !", "No, that was a mistake !"};

			int n = JOptionPane.showOptionDialog(this,
					"This will create a COPY of all the files down from directory: " + GLOBAL_selected_dir + "\n" 
							+ "with a name of the form 8 + 3 (good old dos names) \n"
							+ "such as mus12345.mp3 \n"
							+ "Are you sure you want to do that ?:\n",
							"Rename Confirmation Required",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[1]); // default is to cancel

			if ( n ==  JOptionPane.YES_OPTION)
			{
				shortent_file_names_all_files(GLOBAL_selected_dir);
				System.out.println("Recursive copying (shorter names) done\n");
				the_text_area.append("Recursive copying (shorter names) done\n");

			}

		}		

	}

	//**********************************************************
	public void rename()
	//**********************************************************
	{
		the_text_area.append("select one (any) file in the target directory");


		JFileChooser chooser;
		if ( GLOBAL_selected_dir == null )
		{
			chooser = new JFileChooser(".");
		}
		else
		{
			chooser = new JFileChooser(GLOBAL_selected_dir.getParentFile());
		}
		chooser.setDialogTitle("select the TOP directory (the one that will give the name ...)");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			GLOBAL_selected_dir = chooser.getSelectedFile();

			System.out.println("the dir is :" + GLOBAL_selected_dir);

			String target_name = GLOBAL_selected_dir.getName();

			Object[] options = {"Yes, I am sure !", "No, that was a mistake !"};

			int n = JOptionPane.showOptionDialog(this,
					"This will RENAME all the files down from directory: " + GLOBAL_selected_dir + "\n" 
							+ "Into a name of the form: XXXX_" + target_name + " nnn.yyy\n"
							+ "Are you sure you want to do that ?:\n",
							"Rename Confirmation Required",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[1]); // default is to cancel

			if ( n ==  JOptionPane.YES_OPTION)
			{
				rename_all_files(GLOBAL_selected_dir,target_name, 0);
				System.out.println("Recursive renaming done\n");
				the_text_area.append("Recursive renaming done\n");

			}

		}		

	}


	//**********************************************************
	public void delete_empty_dirs()
	//**********************************************************
	{
		JFileChooser chooser;
		if ( GLOBAL_selected_dir == null )
		{
			chooser = new JFileChooser(".");
		}
		else
		{
			chooser = new JFileChooser(GLOBAL_selected_dir.getParentFile());
		}
		chooser.setDialogTitle("Select the TOP directory (the one where we search ...)");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			GLOBAL_selected_dir = chooser.getSelectedFile();
			recursive_delete_empty_dirs(GLOBAL_selected_dir);
			the_text_area.append("Recursive deletion of empty directories done\n");
		}
	}

	//**********************************************************
	public void sort_to_dir()
	//**********************************************************
	{
		JFileChooser chooser;
		if ( GLOBAL_selected_dir == null )
		{
			chooser = new JFileChooser(".");
		}
		else
		{
			chooser = new JFileChooser(GLOBAL_selected_dir.getParentFile());
		}
		chooser.setDialogTitle("select the TOP directory (the one where we search ...)");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			GLOBAL_selected_dir = chooser.getSelectedFile();

			System.out.println("the dir is :" + GLOBAL_selected_dir);

			// do the check for length N

			System.out.println("analysing...");
			File [] all_files = GLOBAL_selected_dir.listFiles();
			boolean is_dir[] = new boolean[all_files.length];
			boolean is_done[] = new boolean[all_files.length];
			String names[] = new String[all_files.length];
			for ( int i = 0 ; i < all_files.length ; i++)
			{
				names[i] = all_files[i].getName();
				if ( all_files[i].isDirectory() == true )
				{
					is_dir[i] = true;
				}
				else
				{
					is_dir[i] = false;
					is_done[i] = false;
				}
			}
			System.out.println("...done");

			for (int ll = 20 ; ll > 2 ; ll -= 2 )
			{ 		
				// scan that dir for files ...
				//System.out.println("the lentgh is :" + ll);

				for ( int i = 0 ; i < all_files.length ; i++)
				{
					if ( is_dir[i] == true ) continue;

					// make target name

					String target_name = names[i]; 
					//sSystem.out.println("the target name is :" + target_name);
					if ( target_name.length() < ll) continue;
					target_name = target_name.substring(0,ll); 
					//System.out.println("the target name is :" + target_name);

					// check if there is more than 3 NOT DONE files ...
					int count = 0;				
					for ( int j = 0 ; j < all_files.length ; j++)
					{
						if ( is_dir[j] == true ) continue;   
						if ( names[j].startsWith(target_name) )
						{
							if ( is_done[j] == false)
							{
								count++;
							}
						}
					}

					if ( count <= 1 )
					{
						//System.out.println("not enough count = " + count);
						continue;     
					}

					// do it

					File targetdir = new File(all_files[i].getParent(),target_name);
					if ( targetdir.exists() == true)      
					{
						System.out.println("target dir exists");
					}
					else
					{
						targetdir.mkdir();
						System.out.println("created target dir");
					}               
					//current_canvas.clear();
					for ( int j = 0 ; j < all_files.length ; j++)
					{
						if ( is_dir[j] == true ) continue;   
						if ( names[j].startsWith(target_name) )
						{
							if ( all_files[j].exists() == false )
							{
								System.out.println("no such file" + names[j]);
								continue;
							}   
							all_files[j].renameTo(new File(targetdir,names[j]));
							is_done[j] = true; 
							System.out.println("doing: " + all_files[j] + "moved into " + target_name);
						}
					}
				}
				System.out.println("sort done for length=" + ll);
				the_text_area.append("sort done for length=" + ll + "\n");

			}		
		}		

	}     


	//**********************************************************
	public void super_rename()
	//**********************************************************
	{

		JFileChooser chooser;
		if ( GLOBAL_selected_dir == null )
		{
			chooser = new JFileChooser(".");
		}
		else
		{
			chooser = new JFileChooser(GLOBAL_selected_dir.getParentFile());
		}
		chooser.setDialogTitle("select the TOP directory (the one where we search for name-giving directories ...)");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			GLOBAL_selected_dir = chooser.getSelectedFile();

			System.out.println("the dir is :" + GLOBAL_selected_dir);

			// scan that dir for directories ...

			File [] all_files = GLOBAL_selected_dir.listFiles();
			for ( int i = 0 ; i < all_files.length ; i++)
			{
				if ( all_files[i].isDirectory() == false ) continue;





				String target_name = all_files[i].getName();

				Object[] options = {"Do it !", "No, skip that one !"};

				int n = JOptionPane.showOptionDialog(this,
						"This will RENAME all the files down from directory: " + all_files[i] + "\n" 
								+ "Into a name of the form: XXXX_" + target_name + " nnn.yyy\n"
								+ "Are you sure you want to do that ?:\n",
								"Rename Confirmation Required",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								null,
								options,
								options[1]); // default is to cancel

				if ( n ==  JOptionPane.YES_OPTION)
				{
					the_text_area.setText("");
					rename_all_files(all_files[i],target_name, 0);
					System.out.println("Recursive renaming done\n");
					the_text_area.append("Recursive renaming done\n");

				}
			}

		}		

	}

	//**********************************************************
	public void selective_erase()
	//**********************************************************
	{
		JFileChooser chooser = new JFileChooser(".");

		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			File selected_file = chooser.getSelectedFile();

			System.out.println("the file is :" + selected_file);

			File parent = selected_file.getParentFile();

			System.out.println("the parent is :" + parent);

			//String name = parent.getName();

			erase_all_files(parent);
		}		

	}

	//**********************************************************
	private String make_new_name(String old_name, String target_name, int next)
	//**********************************************************
	{
		// first check if the job has not been done already !


		// extract the extension (we dont want to change it yet)

		String extension;
		int index = old_name.lastIndexOf(".");
		if ( index > 0 )
		{
			extension = old_name.substring(index);
		}
		else
		{
			extension = "";
		}

		String new_name = "";

		// extract the leading qualifiers
		// if any

		Vector<String> prefixes = new Vector<String>();

		// these are in the format (????_)
		// 1. ? is always uppercase
		// 2. there may be several of them

		int fromIndex = 0;
		for (;;)
		{
			int index_of_underscore = old_name.indexOf("_",fromIndex);

			if ( index_of_underscore > 0)
			{
				String prefix = old_name.substring(fromIndex,index_of_underscore);
				System.out.println("candidate prefix->" + prefix + "<-");

				String checkor = prefix;
				checkor.toUpperCase();
				if ( prefix.equals(checkor) == true)
				{
					// YES !
					if ( prefix.length() < 5 )
					{
						new_name += prefix + "_";

						fromIndex = index_of_underscore+1;
						continue; // look for another one
					}
				}

			}
			break;
		}

		new_name += target_name + "_" + next + extension;

		return new_name;
	}



	//**********************************************************
	private int shortent_file_names_all_files(File target_dir)
	//**********************************************************
	{
		System.out.println("******************************");
		System.out.println("target dir: " + target_dir);
		System.out.println("******************************");

		File[] all_files = target_dir.listFiles();

		int i;
		for(i= 0;i< Array.getLength(all_files);i++)
		{
			File tmp = all_files[i];


			if (tmp.isDirectory() == true )
			{
				shortent_file_names_all_files(tmp);
				continue;
			}


			File dest = null;	
			try
			{
				dest =  File.createTempFile("mus",".mp3",target_dir);
				tmp.renameTo(dest);
				copy_file(dest,tmp);
				System.out.println("file->" + tmp + "->" + dest);
			}
			catch(Exception e)
			{
				System.out.println("exception->" + e);
			}

		}

		return 0;
	}

	//**********************************************************
	private boolean erase_all_files(File target_dir)
	//**********************************************************
	{
		// returns false if the directory still contains something
		// returns true if the directory is empty

		System.out.println("******************************");
		System.out.println("new dir: " + target_dir);
		System.out.println("******************************");

		File[] all_files = target_dir.listFiles();

		int count_dir = Array.getLength(all_files);
		int count_deleted = 0;

		int i;
		for(i= 0;i< count_dir;i++)
		{
			File tmp = all_files[i];


			if (tmp.isDirectory() == true )
			{
				if ( is_name_included(tmp.getName()) == false)
				{
					if ( true == erase_all_files(tmp))
					{
						System.out.println("deleting dir ->" + tmp.getName());

						tmp.delete();

						count_deleted++;
					}
				}
				continue;
			}


			System.out.println("considering file->" + tmp);


			if ( is_name_included(tmp.getName()) == false)
			{
				System.out.println("deleting file ->" + tmp.getName());

				tmp.delete();

				the_text_area.append(tmp.getName() + "-> erased" + "\n");

				count_deleted++;
			}
		}

		if ( count_dir == count_deleted) return true; // dir is empty !
		else return false;
	}


	//**********************************************************
	private boolean selective_duplicate_files(File target_dir,File destination_dir)
	//**********************************************************
	{
		// returns false if the directory still contains something
		// returns true if the directory is empty

		System.out.println("******************************");
		System.out.println("new dir: " + target_dir);
		System.out.println("******************************");

		File[] all_files = target_dir.listFiles();

		int count_dir = Array.getLength(all_files);
		int count_deleted = 0;

		int i;
		for(i= 0;i< count_dir;i++)
		{
			File tmp = all_files[i];


			if (tmp.isDirectory() == true )
			{
				selective_duplicate_files(tmp, destination_dir);
				continue;
			}


			System.out.println("considering file->" + tmp);


			if ( is_name_included(tmp.getName()) == true)
			{
				System.out.println("copying file ->" + tmp.getName());

				//tmp.renameTo(new File("d:\\tmp\\"+tmp.getName()));

				String target_name = tmp.getName();

				File destination_file;
				for(;;)
				{
					destination_file = new File(destination_dir,target_name);
					if ( destination_file.exists() == false )
					{
						break;
					}
					target_name = target_name.substring(0,target_name.lastIndexOf('.')) + "0" + target_name.substring(target_name.lastIndexOf('.'));
					System.out.println("trying new name->" + target_name);
				}

				try
				{
					copy_file(destination_file,tmp);
				}
				catch (Exception eee)
				{
					the_text_area.append(tmp.getName() + " ==================== failed" + i + "\n");

				}
				the_text_area.append(tmp.getName() + "-> copy + \n");

				count_deleted++;
			}
		}

		if ( count_dir == count_deleted) return true; // dir is empty !
		else return false;
	}

	//**********************************************************
	public void recursive_delete_empty_dirs(File selected_dir)
	//**********************************************************
	{
		//System.out.println("doing dir : " + selected_dir);

		File [] all_files = selected_dir.listFiles();
		int tot = all_files.length;
		if ( tot == 0 )
		{
			selected_dir.delete();
			return;
		}
		for ( int i = 0 ; i < tot ; i++)
		{
			if ( all_files[i].isDirectory() == true )
			{
				//System.out.println("recursing down in : " + all_files[i]);

				recursive_delete_empty_dirs(all_files[i]);
			}
		}
		//System.out.println("...done");
	}



	//**********************************************************
	public static long copy_file(File destfile, File srcfile) throws IOException
	//**********************************************************
	{
		long returned_bytes_copied =0;
		byte[] bytearr = new byte[512];
		int len = 0;
		FileInputStream input = new FileInputStream(srcfile);
		FileOutputStream output = new FileOutputStream(destfile);
		try
		{
			while ((len = input.read(bytearr)) != -1)
			{
				output.write(bytearr, 0, len);
				returned_bytes_copied += len;
			}
		}
		catch (Exception exc)
		{
			System.out.println(exc);
		}
		finally
		{
			input.close();
			output.close();
		}
		return returned_bytes_copied;
	}

	//**********************************************************
	private int rename_all_files(File target_dir, String target_name, int next)
	//**********************************************************
	{
		System.out.println("******************************");
		System.out.println("new dir: " + target_dir);
		System.out.println("******************************");

		int next2;
		File[] all_files = target_dir.listFiles();

		int i;
		next2 = next;
		for(i= 0;i< Array.getLength(all_files);i++)
		{
			File tmp = all_files[i];


			if (tmp.isDirectory() == true )
			{
				next2 = rename_all_files(tmp,target_name,next2);
				continue;
			}


			System.out.println("file->" + tmp);


			String new_name;
			new_name = make_new_name(tmp.getName(), target_name, next2);
			next2 ++;

			System.out.println("new name ->" + new_name);

			File dest =  new File(target_dir,new_name);
			tmp.renameTo(dest);

			the_text_area.append(tmp.getName() + "->" + new_name + "\n");
		}

		return next2;
	}


	//**********************************************************
	private boolean is_name_included(String name)
	//**********************************************************
	{

		//if ( name.indexOf("F_") != -1 ) return true;
		if ( name.indexOf("FF_") != -1 ) return true;
		if ( name.indexOf("FFF_") != -1 ) return true;
		if ( name.indexOf("FFFF") != -1 ) return true;

		//if ( name.indexOf("FC_") != -1 ) return true;
		if ( name.indexOf("FFC_") != -1 ) return true;
		if ( name.indexOf("FFFC_") != -1 ) return true;
		if ( name.indexOf("FFFFC") != -1 ) return true;

		//if ( name.indexOf("P_") != -1 ) return true;
		if ( name.indexOf("PP_") != -1 ) return true;
		if ( name.indexOf("PPP_") != -1 ) return true;
		if ( name.indexOf("PPPP") != -1 ) return true;

		//if ( name.indexOf("U_") != -1 ) return true;
		if ( name.indexOf("UU_") != -1 ) return true;
		if ( name.indexOf("UUU_") != -1 ) return true;
		if ( name.indexOf("UUUU") != -1 ) return true;

		if ( name.indexOf("LBA_") != -1 ) return true;

		//if ( name.indexOf("L_") != -1 ) return true;
		if ( name.indexOf("LL_") != -1 ) return true;
		if ( name.indexOf("LLL_") != -1 ) return true;
		if ( name.indexOf("LLLL") != -1 ) return true;

		//if ( name.indexOf("A_") != -1 ) return true;
		if ( name.indexOf("AA_") != -1 ) return true;
		if ( name.indexOf("AAA_") != -1 ) return true;
		if ( name.indexOf("AAAA") != -1 ) return true;

		//if ( name.indexOf("D_") != -1 ) return true;
		if ( name.indexOf("DD_") != -1 ) return true;
		if ( name.indexOf("DDD_") != -1 ) return true;
		if ( name.indexOf("DDDD") != -1 ) return true;


		return false; //  not good by default
	}



	//**********************************************************
	public void selective_duplicate()
	//**********************************************************
	{

		JFileChooser chooser = new JFileChooser(".");
		chooser.setDialogTitle("select the SOURCE directory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			File selected_file = chooser.getSelectedFile();

			System.out.println("the source is :" + selected_file);

			final File source_dir;
			if ( selected_file.isDirectory() == false)
			{
				return;
				//source_dir = selected_file.getParentFile();
			}
			else
			{
				source_dir = selected_file;
			}

			System.out.println("the source dir is :" + source_dir);
			the_text_area.append("the source dir is :" + source_dir + "\n");


			JFileChooser chooser2 = new JFileChooser(".");
			chooser2.setDialogTitle("select the DESTINATION directory");
			chooser2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			returnVal = chooser2.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				File selected_file2 = chooser2.getSelectedFile();

				System.out.println("the destination is :" + selected_file2);

				final File dest_dir;
				if ( selected_file2.isDirectory() == false)
				{
					return;
					//dest_dir = selected_file2.getParentFile();
				}
				else
				{
					dest_dir = selected_file2;
				}

				System.out.println("the destination dir is :" + dest_dir);
				the_text_area.append("the destination dir is :" + dest_dir + "\n");



				Object[] options = {"Yes, I am sure !", "No, that was a mistake !"};

				int n = JOptionPane.showOptionDialog(this,
						"This will copy selectively all the files down from directory: " + source_dir + "\n" 
								+ "Into the directory: " + dest_dir + "\n"
								+ "(this is normally safe because duplicate names will be automatically renamed)\n"
								+ "Are you sure you want to do that ?:\n",
								"Copy Confirmation Required",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								null,
								options,
								options[1]); // default is to cancel

				if ( n ==  JOptionPane.YES_OPTION)
				{
					Thread t = new Thread()
					{
						public void run()
						{
							selective_duplicate_files(source_dir,dest_dir);
							System.out.println("Selective duplication done\n");
							the_text_area.append("Selective duplication done\n");							
						}
					};
					t.start();

				}


			}		
		}		

	}

	//**********************************************************
	public static void main(String[] args)
	//**********************************************************
	{
		// TODO Auto-generated method stub

		IntelligentFileRenamer f = new IntelligentFileRenamer();
		f.setVisible(true);
	}

}
