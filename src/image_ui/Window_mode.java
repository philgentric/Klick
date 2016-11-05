package image_ui;

//**********************************************************
public enum Window_mode
//**********************************************************
{
	//true_full_screen, // image size is set to the true screen size (no window top bar, no borders)
	fullscreen, // image size is set to the screen size minus the window borders and top bar
	track, // image size is set to the window size
	//scrolling, // image size and window size are independent IF image size is larger than the display area
	// scrolling commands produce scrolling to explore the image IF image is larger than the display area
}
