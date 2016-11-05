package image_decode;

import java.io.File;

//**********************************************************
public class Klik_File 
//**********************************************************
{
	int index_in_dir;
	public File file;
	public int dir_size;
	//**********************************************************
	public Klik_File(File file_, int index, int dir_size_) 
	//**********************************************************
	{
		index_in_dir = index;
		file = file_;
		dir_size = dir_size_;
	}
}
