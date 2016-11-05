package klik_main;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import image_decode.Klik_File;
import image_decode.Rescaling_mode;
import image_decode.Scalr_image_factory;
import image_decode.Slideshow_thread;
import image_ui.Multi_panel;
import image_ui.Window_mode;
import tree_file_browser.Browsing_with_icons_frame;
import ui.Mini_frame_handler;


//**********************************************************
public class Klik_JFrame extends JFrame implements ActionListener, KeyListener, MouseListener, MouseWheelListener, WindowListener
//**********************************************************
{
	private boolean debug_flag = false;
	private boolean mouse_debug = false; // console print for all mouse events

	Multi_panel the_multi_panel = null;
	private Mini_frame_handler the_Mini_frame_handler = null;
	private Window_mode the_Window_mode = Window_mode.track; //true_full_screen;
	private static String menu_text_for_dir = "View images in directory: ";

	private static final long serialVersionUID = 1L;

	private PopupMenu popupmenu;
	private Menu menu;
	private static int MAX_DIR_MEMORY = 100;
	int x;
	int y;

	private Properties_manager the_Properties_manager = new Properties_manager();
	private Rescaling_mode the_requested_Rescaling_mode = Rescaling_mode.SCALR_AUTOMATIC;
	private Cursor cursor_to_restore = null;

	Slideshow_thread the_slide_show_thread = null;
	//public static AtomicBoolean better_skip = new AtomicBoolean(false);
	private int slide_show_sleep_time = 300; // ms

	private boolean better_rescan_dir = false; // rescan the current dir
	private double zoom =-1;// no zoom
	private boolean is_main; // if true closing the window kills the WHOLE app

	//**********************************************************
	void start(int X_, int Y_, boolean is_main_, boolean full_screen)
	//**********************************************************
	{
		x = X_;
		y = Y_;
		is_main = is_main_;
		the_Mini_frame_handler = new Mini_frame_handler(this);

		// make sure mini frames follow the big one
		addComponentListener( new ComponentListener()
		{
			public void componentResized( ComponentEvent e )
			{
			}
			public void componentMoved( ComponentEvent e )
			{
				the_Mini_frame_handler.move();
			}
			public void componentShown( ComponentEvent e )
			{

			}
			public void componentHidden( ComponentEvent e )
			{

			}
		} );

		setTitle("right click to get the menu");
		setLayout(new BorderLayout());

		init_display_panel(x,y);			

		if ( is_main) setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// closing this one kills all others too
		the_Properties_manager .load_properties();
		if ( full_screen)
		{
			setLocation(0,0);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			setSize(screenSize);
		}
		else
		{
			setSize(800, 600);
			setLocation(300, 100);			
		}
		determine_desired_image_size();		

		popupmenu = new PopupMenu("PopUp Menu");
		menu = new Menu("Menu");
		create_menu();

		add(popupmenu);

		MenuBar mbar=new MenuBar();
		mbar.add(menu);
		setMenuBar(mbar);


		addMouseListener(this);	
		addMouseMotionListener(the_multi_panel.the_Display_panels.get(0)); // TODO MAGIC only single image has scroll 		
		addMouseWheelListener(this);	
		setFocusable(true);
		addKeyListener(this);

		// manage window resize:

		//**********************************************************
		class MyAdapter extends ComponentAdapter
		//**********************************************************
		{
			Klik_JFrame main_frame;
			//**********************************************************
			public MyAdapter(Klik_JFrame main_frame_)
			//**********************************************************
			{
				main_frame = main_frame_;
			}
			

			//**********************************************************
			@Override
			public void componentResized(ComponentEvent e)
			//**********************************************************
			{
				//if ( the_Image_file_factory == null) return;
				configure();
				System.out.println("window resize!");
				repaint();
			}
		}
		addComponentListener(new MyAdapter(this));

	}




	//**********************************************************
	private void init_display_panel(int x_, int y_) 
	//**********************************************************
	{
		getContentPane().removeAll();
		x = x_;
		y = y_;
		the_multi_panel = new Multi_panel(x, y);
		getContentPane().add(the_multi_panel, BorderLayout.CENTER);
		configure();
		validate();
		repaint();
		image_change();

	}




	//**********************************************************
	private void create_menu()
	//**********************************************************
	{
		if ( debug_flag == true ) System.out.println("create_menu()");

		menu.removeAll();
		popupmenu.removeAll();

		MenuItem mi;

		mi = create_help_menu_item();
		popupmenu.add(mi);
		mi = create_help_menu_item();
		menu.add(mi);

		mi = create_change_display_grid_item(1);
		popupmenu.add(mi);
		mi = create_change_display_grid_item(1);
		menu.add(mi);

		mi = create_change_display_grid_item(2);
		popupmenu.add(mi);
		mi = create_change_display_grid_item(2);
		menu.add(mi);

		mi = create_change_display_grid_item(3);
		popupmenu.add(mi);
		mi = create_change_display_grid_item(3);
		menu.add(mi);

		mi = create_change_display_grid_item(4);
		popupmenu.add(mi);
		mi = create_change_display_grid_item(4);
		menu.add(mi);

		mi = create_browse_item(1);
		popupmenu.add(mi);
		mi = create_browse_item(1);
		menu.add(mi);
		mi = create_browse_item(2);
		popupmenu.add(mi);
		mi = create_browse_item(2);
		menu.add(mi);
		mi = create_browse_item(8);
		popupmenu.add(mi);
		mi = create_browse_item(8);
		menu.add(mi);


		mi = create_rename_menu_item();
		popupmenu.add(mi);
		mi = create_rename_menu_item();
		menu.add(mi);

		mi = create_move_menu_item();
		menu.add(mi);
		mi = create_move_menu_item();
		popupmenu.add(mi);

		mi = create_copy_menu_item();
		menu.add(mi);
		mi = create_copy_menu_item();
		popupmenu.add(mi);

		mi = create_delete_menu_item();
		menu.add(mi);
		mi = create_delete_menu_item();
		popupmenu.add(mi);

		mi = create_edit_menu_item();
		menu.add(mi);
		mi = create_edit_menu_item();
		popupmenu.add(mi);

		mi = create_edit_gimp_menu_item();
		menu.add(mi);
		mi = create_edit_gimp_menu_item();
		popupmenu.add(mi);

		mi = create_view_menu_item();
		menu.add(mi);
		mi = create_view_menu_item();
		popupmenu.add(mi);

		for(int i = 0 ; i < MAX_DIR_MEMORY ; i++)
		{
			String key = "last_dir"+i;
			String s = the_Properties_manager.get(key);
			if ( s == null ) break;

			if ((new File(s)).exists() == false)			
			{
				the_Properties_manager.remove(key, s);
				continue;
			}
			String tag = menu_text_for_dir+s;
			MenuItem mi2 = create_view_dir_menu_item(tag);
			menu.add(mi2);
			mi2 = create_view_dir_menu_item(tag);
			popupmenu.add(mi2);
		}

		mi = create_clear_menu_item();
		menu.add(mi);
		mi = create_clear_menu_item();
		popupmenu.add(mi);

		mi = create_clear_cache_menu_item();
		menu.add(mi);
		mi = create_clear_cache_menu_item();
		popupmenu.add(mi);


		/*
		mi = create_preferences_menu_item();
		menu.add(mi);
		mi = create_preferences_menu_item();
		popupmenu.add(mi);
		*/
		
		mi = create_exif_menu_item();
		menu.add(mi);
		mi = create_exif_menu_item();
		popupmenu.add(mi);

		mi = create_license_menu_item();
		menu.add(mi);
		mi = create_license_menu_item();
		popupmenu.add(mi);


		

	}


	//**********************************************************
	private MenuItem create_browse_item(int width)
	//**********************************************************
	{
		MenuItem mi;
		mi = new MenuItem("Browse width= "+width+" icons");
		Klik_JFrame target_klik_frame = this;
		mi.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				//File target_dir = the_multi_panel.get_target_dir();
				//if ( target_dir == null) return;
				File target_dir = null;
				
				if ( the_multi_panel != null)
				{
					target_dir = the_multi_panel.get_target_dir();
				}
				
				Browsing_with_icons_frame bf = new Browsing_with_icons_frame(target_klik_frame,width);
				bf.setSize(400+width*150,1200);
				bf.setVisible(true);
				bf.validate();

				bf.load(target_dir);

			}
		});
		return mi;
	}

	//**********************************************************
	private MenuItem create_change_display_grid_item(int x_)
	//**********************************************************
	{
		MenuItem mi;
		mi = new MenuItem("Display grid size = "+x_+"x"+x_);
		Klik_JFrame target = this;
		mi.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				init_display_panel(x_,x_);
			}
		});
		return mi;
	}



	//**********************************************************
	private MenuItem create_exif_menu_item() 
	//**********************************************************
	{
		MenuItem mi;
		mi = new MenuItem("EXIF data");
		mi.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				the_Mini_frame_handler.show_exif();												
			}
		});
		return mi;
	}


	//**********************************************************
	@Deprecated
	private MenuItem create_preferences_menu_item() 
	//**********************************************************
	{
		MenuItem mi;
		mi = new MenuItem("Preferences");
		mi.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				the_Mini_frame_handler.show_preferences();												
			}
		});
		return mi;
	}


	//**********************************************************
	private MenuItem create_clear_menu_item() 
	//**********************************************************
	{
		MenuItem mi;
		mi = new MenuItem("Clear browsing history in this menu");
		mi.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				the_Properties_manager.clear();
				create_menu();
				the_Properties_manager.store_properties();
			}
		});
		return mi;
	}


	private MenuItem create_clear_cache_menu_item() 
	{
		MenuItem mi;
		mi = new MenuItem("Clear icon cache (warning: rebuilding the icon cache can take a very long time...)");
		mi.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				Browsing_with_icons_frame.clear_cache();
			}
		});
		return mi;
	}


	//**********************************************************
	private MenuItem create_view_dir_menu_item(String tag) 
	//**********************************************************
	{
		MenuItem mi2 = new MenuItem(tag);
		mi2.addActionListener(this);
		mi2.setActionCommand(tag);
		return mi2;
	}


	//**********************************************************
	private MenuItem create_view_menu_item() 
	//**********************************************************
	{
		MenuItem mi;
		mi = new MenuItem("Choose folder to load images from");
		mi.addActionListener(this);
		mi.setActionCommand("View images in directory...");
		return mi;
	}


	//**********************************************************
	private MenuItem create_edit_gimp_menu_item()
	//**********************************************************
	{
		MenuItem mi;
		mi = new MenuItem("Edit with Gimp (works only for macos)");
		mi.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try 
				{
					invoke_gimp();
					better_rescan_dir = true;
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		return mi;
	}

	//**********************************************************
	private MenuItem create_edit_menu_item()
	//**********************************************************
	{
		MenuItem mi;
		mi = new MenuItem("Edit (with default system app)");
		mi.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try 
				{
					invoke_editor();
					better_rescan_dir = true;
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		return mi;
	}


	//**********************************************************
	private MenuItem create_delete_menu_item() 
	//**********************************************************
	{
		MenuItem mi;
		mi = new MenuItem("Delete");
		mi.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int dialogButton = JOptionPane.showConfirmDialog (null, "Please confirm you want to delet this image (cannot be undone)","Are you sure?",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
				if( dialogButton == JOptionPane.YES_OPTION)
				{
					the_multi_panel.delete_current_file();
					System.out.println("delete!!");
					image_change();
				}
			}
		});
		return mi;
	}


	//**********************************************************
	private MenuItem create_copy_menu_item() 
	//**********************************************************
	{
		MenuItem mi;
		mi = new MenuItem("Copy");
		mi.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				the_Mini_frame_handler.show_copy();								
			}
		});
		return mi;
	}


	//**********************************************************
	private MenuItem create_move_menu_item() 
	//**********************************************************
	{
		MenuItem mi;
		mi = new MenuItem("Move");
		mi.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				the_Mini_frame_handler.show_move();				
			}
		});
		return mi;
	}


	//**********************************************************
	private MenuItem create_rename_menu_item() 
	//**********************************************************
	{
		MenuItem mi;
		mi = new MenuItem("Rename");
		mi.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				the_Mini_frame_handler.show_rename();
			}
		});
		return mi;
	}

	//**********************************************************
	private MenuItem create_help_menu_item() 
	//**********************************************************
	{
		MenuItem mi;
		mi = new MenuItem("Help");
		mi.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				the_Mini_frame_handler.show_help();
			}
		});
		return mi;
	}

	//**********************************************************
	private MenuItem create_license_menu_item() 
	//**********************************************************
	{
		MenuItem mi;
		mi = new MenuItem("Licenses");
		mi.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				the_Mini_frame_handler.show_licenses();
			}
		});
		return mi;
	}




	//**********************************************************
	public static void rename_current_file(Klik_JFrame context, String new_name) 
	//**********************************************************
	{
		File target = context.get_current_file().file;
		File dest = new File(target.getParent(),new_name);
		try
		{
			System.out.println("Renaming->"+target.getCanonicalPath()+"<-into->"+dest+"<-");
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		target.renameTo(dest);
		context.scan_dir(dest);
		context.image_change();
	}

	//**********************************************************
	public boolean set_Window_mode(Window_mode wm)
	//**********************************************************
	{
		the_Window_mode = wm;
		determine_desired_image_size();
		repaint();
		return true;
	}



	//**********************************************************
	public Window_mode get_Window_mode()
	//**********************************************************
	{
		return the_Window_mode;
	}





	//**********************************************************
	public int get_bar_height()
	//**********************************************************
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment ();
		GraphicsDevice gd = ge.getDefaultScreenDevice ();
		GraphicsConfiguration gc = gd.getDefaultConfiguration ();
		Insets insets_screen = Toolkit.getDefaultToolkit().getScreenInsets(gc);
		return insets_screen.top; // insets_screen.bottom	+
	}
	//**********************************************************
	private void determine_desired_image_size()
	//**********************************************************
	{

		switch (the_Window_mode)
		{
		//case true_full_screen:
		/* this code gives true full screen but how to get a menu ???

	    	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

	    	if (gd.isFullScreenSupported() == true)
	    	{
	    		try
	    		{
	    			gd.setFullScreenWindow(this);
	    		}
	    		catch(Exception e)
	    		{
		    		System.out.println("Full screen failed!");

	    		}
	    	}
	    	else
	    	{
	    		System.out.println("Full screen not supported");
	    	}
		 */
		case fullscreen:
			setLocation(0,0);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			setSize(screenSize);
			System.out.println("screen size getScreenSize: "+screenSize.width+"x"+screenSize.height);

			break;
		case track:
		default:
			break;
		}
	}

	// not used
	//**********************************************************
	void close ()
	//**********************************************************
	{
		setVisible (false);
		
		dispose();
		if ( is_main)
		{
			System.out.println( "\nEXIT");
			System.exit(0);
		}
	}

	//**********************************************************
	@Override
	public void actionPerformed(ActionEvent e)
	//**********************************************************
	{
		String cmd = e.getActionCommand();
		if ( cmd.startsWith(menu_text_for_dir) == true)
		{
			String dir_name = cmd.substring(menu_text_for_dir.length());
			System.out.println( "menu_text_for_dir "+dir_name );
			try
			{
				System.out.println("menu event:" + cmd);
				the_multi_panel.set_image_file_dir(new File(dir_name),null,zoom);
				image_change();
			} 
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
			return;
		}



		if( cmd.equals( "View images in directory..." ) )
		{
			System.out.println( "View images in directory..." );
			String start_dir = ".";
			for(int i = 0 ; i < MAX_DIR_MEMORY ; i++)
			{
				String key = "last_dir"+i;
				String s = (String) the_Properties_manager.get(key);
				if ( s == null )
				{
					break;
				}
				if ( (new File(s)).exists() == true)
				{
					start_dir = s;					
				}
				else
				{
					// we need to remove this dir, for some reason, it is now invalid
					the_Properties_manager.remove(key,s);
				}
			}

			JFileChooser chooser = new JFileChooser(start_dir);
			chooser.setDialogTitle("Select the directory or file to display");
			chooser.setFileHidingEnabled(true);
			//chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			int returnVal = chooser.showOpenDialog(null);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				File local_File = chooser.getSelectedFile();
				File local_source_dir_File = null;
				if ( local_File.isFile() == true)
				{
					// we want to parent dir too
					local_source_dir_File = local_File.getParentFile();
				}
				else
				{
					local_source_dir_File = local_File;
					local_File = null;
				}
				try {


					// find empty slot but at the same time check if this dir was already stored
					if ( check_already_stored(local_source_dir_File) == false)
					{
						// find empty slot
						for(int i = 0 ; i < MAX_DIR_MEMORY ; i++)
						{
							String key = "last_dir"+i;
							String s = (String) the_Properties_manager.get(key);
							if ( s == null )
							{
								the_Properties_manager.put(key,local_source_dir_File.getCanonicalPath());
								create_menu();
								the_Properties_manager.store_properties();
								break;
							}
						}
						System.out.println( local_source_dir_File.getCanonicalPath() );
					}
					//invoke_feh(source_dir_File);
					the_multi_panel.set_image_file_dir(local_source_dir_File,local_File,zoom);
					//the_Image_file_factory =  new Image_file_factory(local_source_dir_File, local_File,x*y);
					//set_image_factory(the_Image_file_factory);
					image_change();

				} catch (Exception eeee) {
					eeee.printStackTrace();
				}

			}

		}		
	}




	private boolean check_already_stored(File local_source_dir_File) throws IOException 
	{
		for(int i = 0 ; i < MAX_DIR_MEMORY ; i++)
		{
			String key = "last_dir"+i;
			String s = (String) the_Properties_manager.get(key);
			if ( s == null )
			{
				continue;
			}
			if (s.equals(local_source_dir_File.getCanonicalPath()) == true)
			{
				return true; // already there
			}
		}
		return false;
	}



	/*
	private void set_image_factory(Image_file_factory the_Image_file_factory2) {
		if ( use_multi)
		{
			the_multi_panel.set_Image_file_factory(the_Image_file_factory2);
		}
		else
		{
			//the_Display_panel.set_Image_file_factory(the_Image_file_factory2);					
		}
	}
	 */

	//**********************************************************
	public void image_change()
	//**********************************************************
	{
		zoom = -1.0; // reset zoom
		configure();
		repaint();		
		the_Mini_frame_handler.update_all();
	}








	//**********************************************************
	@Deprecated
	private void invoke_feh(File source_dir_File) throws IOException, InterruptedException
	//**********************************************************
	{
		// can add -Y to hide cursor
		Process p = Runtime.getRuntime().exec("/opt/local/bin/feh -F -x " + source_dir_File.getCanonicalPath());
		p.waitFor();
		System.out.println("feh ended");

		BufferedReader reader = 
				new BufferedReader(new InputStreamReader(p.getInputStream()));

		String line = "";			
		while ((line = reader.readLine())!= null)
		{
			System.out.println(line);
		}

		/*BufferedWriter writer = 
				new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
		 */

	}

	//**********************************************************
	private void invoke_editor() throws IOException, InterruptedException
	//**********************************************************
	{
		if (the_multi_panel.get_current_file() == null) return;
		Desktop desktop = Desktop.getDesktop();
        desktop.edit (the_multi_panel.get_current_file().file);
	}
	
	//**********************************************************
	private void invoke_gimp() throws IOException, InterruptedException
	//**********************************************************
	{
		if (the_multi_panel.get_current_file() == null) return;
		String target = the_multi_panel.get_current_file().file.getCanonicalPath();
		//Process p = Runtime.getRuntime().exec("/Applications/Gimp.app/Contents/MacOS/gimp-2.8 " + "'"+target+"'");
		List<String> command = new ArrayList<String>();
		String gimp_path =  "/Applications/Gimp.app/Contents/MacOS/gimp-2.8";
		command.add(gimp_path);
		String[] pieces = target.split("\\s+");

		//    /bin/bash -c "/Applications/Gimp.app/Contents/MacOS/gimp-2.8 /Users/philippegentric/Desktop/dossiersanstitre/vagues\ 20141129_112739.jpg"

		String[] pieces2 = new String[3];
		pieces2[0] = "/bin/bash";
		pieces2[1] = "-c";
		pieces2[2] = gimp_path;
		pieces2[2] += " ";
		for(int i = 0; i < pieces.length-1; i++)
		{
			pieces2[2] += pieces[i]+"\\ ";
		}
		pieces2[2] += pieces[pieces.length-1];
		for( String s: pieces2) System.out.println(s);
		Runtime.getRuntime().exec(pieces2);
	}


	//**********************************************************
	@Override
	public void mouseClicked(MouseEvent e)
	//**********************************************************
	{
		if ( debug_flag ) System.out.println("mouseClicked"+e);
		if ( debug_flag ) System.out.println(" mouse x=" + e.getX()+ " mouse y=" + e.getY());

		if (e.getClickCount() == 2)
		{
			System.out.println("double clicked");
			pop_up_another_full_frame(e);
		}
		
		if ( e.isMetaDown() == true )
		{
			// right click
			popupmenu.show(e.getComponent(), e.getX(), e.getY());
			return;
		}
		//the_Display_panel.end_of_drag();

	}


	//**********************************************************
	@Override
	public void mousePressed(MouseEvent e)
	//**********************************************************
	{
		//if ( debug_flag )
		System.out.println("mousePressed"+e);
		//the_Display_panel.end_of_drag();
	}
	
	//**********************************************************
	void pop_up_another_full_frame(MouseEvent e)
	//**********************************************************
	{
		if (( x == 1) && ( y == 1)) return; // ignore
		
		// let us compute where the user clicked
		int image_index_x = -1;
		{
			int w = getWidth();
			int width_of_individual_images = w/x;
			int xx = e.getX();
			for ( int i = 0; i < x ;i++)
			{
				if ((xx > (i)*width_of_individual_images) && (xx < (i+1)*width_of_individual_images))
				{
					image_index_x = i;
				}
			}
		}
		int image_index_y = -1;
		{
			int h = getHeight();
			int height_of_individual_images = h/y;
			int yy = e.getY();
			for ( int i = 0; i < y ;i++)
			{
				if ((yy > (i)*height_of_individual_images) && (yy < (i+1)*height_of_individual_images))
				{
					image_index_y = i;
				}
			}
		}
		File target_file = the_multi_panel.get_image_file_at(image_index_x,image_index_y);
		if ( target_file == null) return; // cancel!
		
		Klik_JFrame hehe = new Klik_JFrame();
		hehe.start(1,1, false, true);
		hehe.load_image(target_file);
		hehe.setLocation(0,0);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		hehe.setSize(screenSize);
		hehe.setVisible(true);
	}





	//**********************************************************
	@Override
	public void mouseReleased(MouseEvent e)
	//**********************************************************
	{
		//if ( debug_flag ) System.out.println("mouseReleased"+e);
		//the_Display_panel.end_of_drag();

	}


	//**********************************************************
	@Override
	public void mouseEntered(MouseEvent e)
	//**********************************************************
	{
		//if ( debug_flag ) System.out.println("mouseEntered"+e);

	}


	//**********************************************************
	@Override
	public void mouseExited(MouseEvent e)
	//**********************************************************
	{
		//if ( debug_flag ) System.out.println("mouseExisted"+e);

	}

	//**********************************************************
	@Override
	public void keyTyped(KeyEvent e)
	//**********************************************************
	{
		System.out.println("keyTyped: "+e);
	}


	//**********************************************************
	@Override
	public void keyPressed(KeyEvent e)
	//**********************************************************
	{
		if (Scalr_image_factory.better_skip.get() == true)
		{
			System.out.println("\n\n\n\n\n\nSKIPPING KEY EVENT\n\n\n\n\n");
			return;
		}
		if ( better_rescan_dir == true)
		{
			the_multi_panel.rescan_dir();
			better_rescan_dir = false;
		}
		//the_Image_factory.cancel_current_slow_rescale();

		System.out.println("\n\n\n\n\n\n\n\nkeyPressed: "+e);

		if ( e.getKeyCode() == 32 ) // space bar
		{
			System.out.println("keypressed 32 space bar");
			plus_1();
			image_change();
			return;
		}
		if ( e.getKeyCode() == 37 )// left arrow
		{
			String keyname = KeyEvent.getKeyText(e.getKeyCode());
			if (( e.isShiftDown() == true) && ( e.isControlDown() == true))
			{
				System.out.println("keypressed "+e.getKeyCode()+" " + keyname + " + CTRL + SHIFT");
				back_all();
			}
			if ( e.isControlDown() == true)
			{
				System.out.println("keypressed "+e.getKeyCode()+" " + keyname + " + CTRL");
				back_100();				
			}
			else if ( e.isShiftDown() == true)
			{
				System.out.println("keypressed "+e.getKeyCode()+" " + keyname + " + SHIFT");
				back_10();
			}
			else
			{
				System.out.println("keypressed "+e.getKeyCode()+" " + keyname + "");
				back_1();
			}
			image_change();
			return;
		}
		if (e.getKeyCode() == 38 ) // up arrow
		{
			String keyname = KeyEvent.getKeyText(e.getKeyCode());
			if (( e.isShiftDown() == true) && ( e.isControlDown() == true))
			{
				System.out.println("keypressed "+e.getKeyCode()+" " + keyname + " + CTRL + SHIFT");
			}
			if ( e.isControlDown() == true)
			{
				System.out.println("keypressed "+e.getKeyCode()+" " + keyname + " + CTRL");
			}
			else if ( e.isShiftDown() == true)
			{
				System.out.println("keypressed "+e.getKeyCode()+" " + keyname + " + SHIFT");
			}
			else
			{
				System.out.println("keypressed "+e.getKeyCode()+" " + keyname + "");
				set_zoom_delta(1);
			}
			return;
		}
		if ( e.getKeyCode() == 39 ) // right arrow 
		{
			String keyname = KeyEvent.getKeyText(e.getKeyCode());
			if (( e.isShiftDown() == true) && ( e.isControlDown() == true))
			{
				System.out.println("keypressed "+e.getKeyCode()+" " + keyname + " + CTRL + SHIFT");
				plus_all();
			}
			if ( e.isControlDown() == true)
			{
				System.out.println("keypressed "+e.getKeyCode()+" " + keyname + " + CTRL");
				plus_100();				
			}
			else if ( e.isShiftDown() == true)
			{
				System.out.println("keypressed "+e.getKeyCode()+" " + keyname + " + SHIFT");
				plus_10();
			}
			else
			{
				System.out.println("keypressed "+e.getKeyCode()+" " + keyname + "");
				plus_1();
			}
			image_change();
			return;
		}
		if ( e.getKeyCode() == 40 ) // down arrow
		{
			String keyname = KeyEvent.getKeyText(e.getKeyCode());
			if (( e.isShiftDown() == true) && ( e.isControlDown() == true))
			{
				System.out.println("keypressed "+e.getKeyCode()+" " + keyname + " + CTRL + SHIFT");
			}
			if ( e.isControlDown() == true)
			{
				System.out.println("keypressed "+e.getKeyCode()+" " + keyname + " + CTRL");
			}
			else if ( e.isShiftDown() == true)
			{
				System.out.println("keypressed "+e.getKeyCode()+" " + keyname + " + SHIFT");
			}
			else
			{
				System.out.println("keypressed "+e.getKeyCode()+" " + keyname + "");
				set_zoom_delta(-1);
			}
			return;
		}
		if ( e.getKeyCode() == 47 ) // equal = or +, with shift
		{
			if (e.isShiftDown() == false) // equal
			{
				System.out.println("keypressed 47 equal(=)");
				set_zoom_delta(0);				
			}
			else
			{
				System.out.println("keypressed 47 equal(=) + shift is plus (+)");
				set_zoom_delta(1);
			}
			return;
		}
		if ( e.getKeyCode() == 61 ) // minus -
		{
			System.out.println("keypressed 61 minus(-)");
			set_zoom_delta(-1);
			return;
		}


		/*
		 * image quality
		 */
		if ( e.getKeyCode() == 65 ) // a
		{
			System.out.println("keypressed 65 a");
			the_requested_Rescaling_mode = Rescaling_mode.SCALR_AUTOMATIC;
			//if ( the_Image_getter == null) return;
			//get_proper_image_getter(the_Image_getter.get_zoom(),the_requested_Rescaling_mode);
			configure();

			repaint();		
			return;
		}
		if ( e.getKeyCode() == 70 ) // f
		{
			int modifiersEx = e.getModifiersEx();
			String modString = "extended modifiers = " + modifiersEx;
			System.out.println("keypressed 70 f"+ modString);

			//the_Display_panel.set_message("key pressef = f");
			if ( modifiersEx == 0)
			{
				the_requested_Rescaling_mode = Rescaling_mode.SCALR_SPEED;				
			}
			else
			{
				the_requested_Rescaling_mode = Rescaling_mode.FAST;				
			}
			configure();
			repaint();		
			return;
		}
		if ( e.getKeyCode() == 66 ) // b
		{
			System.out.println("keypressed 66 b");
			//the_Image_getter.reset_promise();
			//the_Display_panel.set_message("key pressed 66");
			the_requested_Rescaling_mode = Rescaling_mode.SCALR_BALANCED;
			configure();
			repaint();		
			return;
		}

		if ( e.getKeyCode() == 81 ) // q
		{
			int modifiersEx = e.getModifiersEx();
			String modString = "extended modifiers = " + modifiersEx;
			System.out.println("keypressed 81 q"+ modString);

			//the_Image_getter.reset_promise();
			//the_Display_panel.set_message("key pressed 81");
			if ( modifiersEx == 0)
			{
				the_requested_Rescaling_mode = Rescaling_mode.SCALR_QUALITY;				
			}
			else
			{
				the_requested_Rescaling_mode = Rescaling_mode.SCALR_ULTRA_QUALITY;				
			}
			configure();
			repaint();		
			return;
		}

		/*
		 * fast browse 
		 */

		// RANDOM
		if ( e.getKeyCode() == 82 ) // r 
		{
			System.out.println("keypressed 82 r");
			the_multi_panel.load_random_image();
			image_change();
			return;
		}
		// ULTIM
		if ( e.getKeyCode() == 85 ) // u
		{
			System.out.println("keypressed 85 u");
			if ( the_multi_panel.load_next_ultim_image() == true)
			{
				image_change();
			}
			return;
		}

		// slide show start stop
		if ( e.getKeyCode() == 83 ) // s 
		{
			System.out.println("keypressed 83 s");
			the_requested_Rescaling_mode = Rescaling_mode.SCALR_AUTOMATIC;
			if ( the_slide_show_thread != null)
			{
				the_slide_show_thread.die();
				the_slide_show_thread = null;
			}
			else
			{
				the_slide_show_thread = new Slideshow_thread(the_multi_panel.get_image_file_factory(), this, slide_show_sleep_time);
				the_slide_show_thread.start();				
			}
			return;
		}
		if ( e.getKeyCode() == 90 ) // z
		{
			if ( the_slide_show_thread == null) return;
			int modifiersEx = e.getModifiersEx();
			String modString = "extended modifiers = " + modifiersEx;
			System.out.println("keypressed 90 z"+ modString);

			if ( modifiersEx == 0)
			{
				the_slide_show_thread.slower();			
			}
			else
			{
				the_slide_show_thread.faster();			
			}
			return;
		}

		// VOTE
		if ( e.getKeyCode() == 86 ) // v
		{
			System.out.println("keypressed 86 v");
			String new_name = get_current_file().file.getName();
			if (new_name.contains("_ultim") == true)
			{
				JOptionPane.showMessageDialog(null,"This image has already been up-voted","Not done",JOptionPane.WARNING_MESSAGE);

				// sorry cannot vote more than once
				return;
			}
			String extension;
			int index = new_name.lastIndexOf(".");
			if ( index > 0 )
			{
				extension = new_name.substring(index);
			}
			else
			{
				extension = "";
			}
			new_name =  new_name.substring(0,index);
			new_name = new_name + "_ultim"+ extension;
			rename_current_file(this, new_name);
			return;
		}
	}




	private void configure() 
	{
		the_multi_panel.configure(the_requested_Rescaling_mode,zoom );			
	}




	private void plus_1() 
	{
		the_multi_panel.load_image_relative(1*x*y);
	}
	private void plus_10() 
	{
		the_multi_panel.load_image_relative(10*x*y);
	}
	private void plus_100() 
	{
		the_multi_panel.load_image_relative(100*x*y);
	}
	private void plus_all() 
	{
		the_multi_panel.load_image_relative(Integer.MAX_VALUE);
	}

	private void back_1() 
	{
		the_multi_panel.load_image_relative(-1*x*y);
	}
	private void back_10() 
	{
		the_multi_panel.load_image_relative(-10*x*y);
	}
	private void back_100() 
	{
		the_multi_panel.load_image_relative(-100*x*y);
	}
	private void back_all() 
	{
		the_multi_panel.load_image_relative(Integer.MIN_VALUE);
	}








	//**********************************************************
	@Override
	public void keyReleased(KeyEvent e)
	//**********************************************************
	{
		System.out.println("keyReleased: "+e);
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			System.out.println("ESCAPE");
			dispose();
		}
	}





	//**********************************************************
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	//**********************************************************
	{
		if ( mouse_debug == true) System.out.println("mouseWheelMoved: "+e);
		if ( mouse_debug == true) System.out.println("wheel roration: "+e.getWheelRotation());
		if (Scalr_image_factory.better_skip.get() == true)
		{
			System.out.println("\n\n\n\n\n\nSKIPPING MOUSE WHEEL EVENT\n\n\n\n\n");
			return;
		}

		if ( better_rescan_dir == true)
		{
			the_multi_panel.rescan_dir();
			better_rescan_dir = false;
		}

		//if ( is_fast_browse_with_scroll_wheel_enabled() == true)
		{
			int wheel_rotation = e.getWheelRotation();
			boolean do_them_all = true;
			if ( do_them_all)
			{
				System.out.println("wheel_rotation="+wheel_rotation);
				//for ( int i = 0; i < wheel_rotation; i++)
				{
					if ( wheel_rotation > 0 ) plus_1();
					else if ( wheel_rotation < 0 ) back_1();
					image_change();		
				}
			}
			else
			{
				the_multi_panel.load_image_relative(wheel_rotation);	
				image_change();
			}
			//the_Display_panel.set_message("mouse wheel");
		}
	}



	//**********************************************************
	public void recompute_current_image()
	//**********************************************************
	{
		//get_proper_image_getter(-1.0, the_Image_getter.get_Rescaling_mode());
		//the_Display_panel.set_image_getter(the_requested_Rescaling_mode,the_Image_file_factory);
		configure();
		repaint();
	}

	//**********************************************************
	public void set_zoom_delta(int i)
	//**********************************************************
	{
		System.out.println("set_zoom_delta() ZOOM request="+i);
		if ( i == 0)
		{
			zoom = 1.0; // native !!
			System.out.println("old ZOOM="+zoom);
		}
		else if ( i < 0)
		{
			if ( zoom < 0 ) zoom = 1.0;
			System.out.println("old ZOOM="+zoom);
			zoom *= 0.9;
		}
		else if ( i > 0)
		{
			if ( zoom < 0 ) zoom = 1.0;
			zoom *= 1.1;
		}
		System.out.println("new ZOOM="+zoom);
		configure();
		repaint();
	}




	//**********************************************************
	public Properties_manager get_Properties_manager()
	//**********************************************************
	{
		return the_Properties_manager;
	}


	//**********************************************************
	public Klik_File get_current_file()
	//**********************************************************
	{
		return the_multi_panel.get_current_file();
	}


	//**********************************************************
	public void scan_dir(File dest)
	//**********************************************************
	{
		the_multi_panel.scan_dir(dest);
	}
	//**********************************************************
	public void scan_dir_next()
	//**********************************************************
	{
		the_multi_panel.scan_dir_next();
	}


	@Override
	public void windowOpened(WindowEvent e) {
	}


	@Override
	public void windowClosing(WindowEvent e)
	{
		the_Mini_frame_handler.die();
	}


	@Override
	public void windowClosed(WindowEvent e) {
	}


	@Override
	public void windowIconified(WindowEvent e) {
	}


	@Override
	public void windowDeiconified(WindowEvent e) {
	}


	@Override
	public void windowActivated(WindowEvent e) {
	}


	@Override
	public void windowDeactivated(WindowEvent e) {
	}


	//**********************************************************
	public void paint(Graphics g)
	//**********************************************************
	{
		super.paint(g);//Components(g);
		Scalr_image_factory.better_skip.set(false);
		System.out.println("better_skip has been set to false===================================================");
		this.setTitle(get_title());			

		if ( cursor_to_restore != null)
		{
			setCursor(cursor_to_restore);
		}

	}




	private String get_title() 
	{
		return the_multi_panel.get_title();
	}




	//**********************************************************
	public void get_ready_to_restore_cursor(Cursor cc)
	//**********************************************************
	{
		cursor_to_restore  = cc;
	}




	//**********************************************************
	public List<String> get_exif_tags_list()
	//**********************************************************
	{
		//return the_Display_panel.get_exifs_tags_list();
		return the_multi_panel.get_exifs_tags_list();
	}




	//**********************************************************
	public void load_image(File f) 
	//**********************************************************
	{
		System.out.println("Klik_JFrame.load_image()");
		the_multi_panel.set_image_file_dir(f.getParentFile(), f, zoom);
				
		image_change();
		setVisible(true);
	}


}
