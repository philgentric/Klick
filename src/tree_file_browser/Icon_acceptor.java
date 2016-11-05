package tree_file_browser;

import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;

public interface Icon_acceptor 
{
	void set_icon(BufferedImage bi);
	void set_text(String s);
	void setHorizontalTextPosition(int center);
	void setVerticalTextPosition(int bottom);
	void addMouseListener(MouseAdapter mouseAdapter);
}
