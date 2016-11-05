package image_decode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

//**********************************************************
public class Klik_BufferedImage
//**********************************************************
{
	public BufferedImage the_BufferedImage = null;
	public boolean rescale_at_draw_time = false; 
	public double actual_zoom_factor = -1.0; // pixel to pixel ratio between original and THIS
	public boolean is_rotated_90 = false;
	public boolean is_rotated_180 = false;
	public boolean is_rotated_270 = false;
	public String quality ="unknown quality";
	public List <String> exifs_tags_list = null;
	public String date = "";
	
	//**********************************************************
	public List<String> extract_exif_metadata(File image_to_be_loaded)
	//**********************************************************
	{
		exifs_tags_list = new ArrayList<String>();

		if (image_to_be_loaded == null)
		{
			System.out.println("FATAL image_to_be_loaded == null");
			return null;
		}
		if (image_to_be_loaded.exists() == false)
		{
			System.out.println("FATAL image_to_be_loaded.exists() == false");
			return null;
		}
		is_rotated_90 = false;
		is_rotated_180 = false;
		is_rotated_270 = false;
		try
		{
			Metadata metadata = ImageMetadataReader.readMetadata(image_to_be_loaded);
			for (Directory directory : metadata.getDirectories())
			{
				for (Tag tag : directory.getTags())
				{
					//System.out.println(tag);
					exifs_tags_list.add(tag.toString());
					if ( tag.toString().contains("Orientation") == true)
					{
						if ( tag.toString().contains("Thumbnail") == false)
						{
							// Orientation - Right side, top (Rotate 90 CW)
							if (tag.toString().contains("90") == true)
							{
								if (tag.toString().contains("CW") == true)
								{
									// have to rotate +90
									is_rotated_90 = true;
								}

							}
							else if (tag.toString().contains("180") == true)
							{
								is_rotated_180 = true;

							}
							else if (tag.toString().contains("270") == true)
							{
								is_rotated_270 = true;

							}
						}				        		
						//System.out.println(tag.getTagName());
						//System.out.println(tag.getTagTypeHex());
					}
				}
			}
		}
		catch (ImageProcessingException | IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for ( String s : exifs_tags_list)
		{
			if ( s.contains("Date/Time") == true )
			{
				date = s.substring(s.indexOf("Date/Time"));
				break;
			}
		}
		System.out.println("extract_exif_metadata() end date= "+date);
		return exifs_tags_list;
	}
	
}
