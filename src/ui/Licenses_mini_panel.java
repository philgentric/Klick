package ui;

import java.awt.GridLayout;
import java.awt.ScrollPane;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import klik_main.Klik_JFrame;
import ui.Mini_panel;

//**********************************************************
public class Licenses_mini_panel extends Mini_panel 
//**********************************************************
{
	private static final long serialVersionUID = 1L;

	//**********************************************************
	Licenses_mini_panel(Klik_JFrame context)
	//**********************************************************
	{
		super(context);
		reconfigure();
	}
	//**********************************************************
	@Override
	public void reconfigure()
	//**********************************************************
	{

		removeAll();
		setLayout(new GridLayout());
		JTextArea jta = new JTextArea();
		String t = "";
		t += "   Klik is Copyright (c) 2015, Philippe Gentric and released under the Apache License V2.0 (http://www.apache.org/licenses/)\n";
		t += "\t\t\n";
		t += "   metadata-extractor (https://github.com/drewnoakes/metadata-extractor) license is Apache License V2.0\n";
		t += "\t\t\n";
		t += "   turbojpeg 1.2.90 library (https://github.com/libjpeg-turbo/libjpeg-turbo) License:\n";
		t += "\t\t\n";
		t += "\t\tThis software is based in part on the work of the Independent JPEG Group.\n";
		t += "\t\t\n";
		t += "\t\tThey require to post the Modified BSD License (because of the TurboJPEG API? I used the java API... anyway):\n";
		t += "\t\tCopyright (c) <year>, <copyright holder>\n";
		t += "\t\tAll rights reserved.\n";
		t += "\t\t\n";
		t += "\t\tRedistribution and use in source and binary forms, with or without\n";
		t += "\t\tmodification, are permitted provided that the following conditions are met:\n";
		t += "\t\t    * Redistributions of source code must retain the above copyright\n";
		t += "\t\t      notice, this list of conditions and the following disclaimer.\n";
		t += "\t\t    * Redistributions in binary form must reproduce the above copyright\n";
		t += "\t\tnotice, this list of conditions and the following disclaimer in the\n";
		t += "\t\tdocumentation and/or other materials provided with the distribution.\n";
		t += "\t\t * Neither the name of the <organization> nor the\n";
		t += "\t\tnames of its contributors may be used to endorse or promote products\n";
		t += "\t\tderived from this software without specific prior written permission.\n";
		t += "\t\t\n";
		t += "\t\tTHIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND\n";
		t += "\t\tANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED\n";
		t += "\t\tWARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE\n";
		t += "\t\tDISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY\n";
		t += "\t\tDIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES\n";
		t += "\t\t(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;\n";
		t += "\t\tLOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND\n";
		t += "\t\tON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n";
		t += "\t\t(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS\n";
		t += "\t\tSOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n";
		t += "\t\t\n";
		t += "   imgscalr (https://github.com/thebuzzmedia/imgscalr) License is Apache License V2.0:\n";
		t += "\t\t\n";
		t += "   thumbnailator 0.4.7 License:\n";
		t += "\t\tThumbnailator - a thumbnail generation library\n";
		t += "\t\t\n";
		t += "\t\tCopyright (c) 2008-2015 Chris Kroells\n";
		t += "\t\t\n";
		t += "\t\tPermission is hereby granted, free of charge, to any person obtaining a copy\n";
		t += "\t\tof this software and associated documentation files (the \"Software\"), to deal\n";
		t += "\t\tin the Software without restriction, including without limitation the rights\n";
		t += "\t\tto use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n";
		t += "\t\tcopies of the Software, and to permit persons to whom the Software is\n";
		t += "\t\tfurnished to do so, subject to the following conditions:\n";
		t += "\t\t\n";
		t += "\t\tThe above copyright notice and this permission notice shall be included in\n";
		t += "\t\tall copies or substantial portions of the Software.\n";
		t += "\t\t\n";
		t += "\t\tTHE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n";
		t += "\t\tIMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n";
		t += "\t\tFITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n";
		t += "\t\tAUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n";
		t += "\t\tLIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n";
		t += "\t\tOUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN\n";
		t += "\t\tTHE SOFTWARE.";
		jta.setText(t);
		
		ScrollPane sp = new ScrollPane();
		sp.add(jta);
		add(sp);
		
		sp.setSize(800, 600);

		revalidate();
		repaint();
		if ( the_frame != null) the_frame.pack();// do this to make sure the frame size is updated when the text changes!

	}
	//**********************************************************
	private void action(Klik_JFrame context, JTextField jtf)
	//**********************************************************
	{
	}

}
