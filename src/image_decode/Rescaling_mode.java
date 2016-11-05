package image_decode;

//**********************************************************
public enum Rescaling_mode
//**********************************************************
{
	FAST, // only power of twos sizes are considered: very fast but can be frustrating 
	//i.e. when image is displayed close to 1/2 screen 
	// this is the mode providing fast BROWSING mode, 
	// where scrolling commands (mouse or 2 fingers track pad)
	// produce very fast browsing through the current image folder

	SCALR_ULTRA_QUALITY, // will match the window size, AND top quality, but VERY slow
	SCALR_QUALITY,
	SCALR_BALANCED, 
	SCALR_SPEED, 
	SCALR_AUTOMATIC, // will match the window size, or screen size in full screen mode, slow
	
}
