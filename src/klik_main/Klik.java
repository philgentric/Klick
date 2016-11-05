package klik_main;

//**********************************************************
public class Klik
//**********************************************************
{
	//**********************************************************
	public static void main(String[] args)
	//**********************************************************
	{
	    javax.swing.SwingUtilities.invokeLater(new Runnable() 
	    {
	        public void run() 
	        {
	    		Klik_JFrame the_thing = new Klik_JFrame();
	    		the_thing.start(1,1,true, false);
	    		the_thing.setVisible(true);
	    		
	        }
	    });
	}
}
