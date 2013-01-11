package gfx;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GfxKit implements Serializable {
	private final int partSize = 21;
	int kitId;
	static int myId = 0;
	String kitName;
	
	Point position;
	//ArrayList<GfxPart> partsNeded; //belongs to agent
	ArrayList<GfxPart> partsInsideKit;
	
	
	public int getPartNumber() {
		return partsInsideKit.size();
	}
	
	
	public GfxKit(Point p){
		kitId=myId;
		++myId;
		partsInsideKit=new ArrayList<GfxPart>();
		position = p;
		//partsNeded=partsFrom;
	}
	
	/*
	public ImageIcon getImage(){
		return kitPicture;
	}*/
	
	public boolean addPart(GfxPart part){
		if (partsInsideKit.size()<8) {
			partsInsideKit.add(part);
			return true;
		} else {
			return false;
		}
	}
	
	//only called when the object is sent to the client
	public void paintKit(JPanel frame, Graphics2D d, ImageIcon kitPicture, ImageIcon[] partsImages,ImageIcon badParts ) {
		kitPicture.paintIcon(frame,d, position.x, position.y);
		int xcursor = 0; int ycursor = 0;
		for(GfxPart p : partsInsideKit) {
			p.paintPart(frame, d, position.x + xcursor * partSize,
						position.y + ycursor * partSize, partsImages[p.getid()], badParts);
			if(xcursor == 0)
				++xcursor;
			else {
				++ycursor;
				xcursor = 0;
			}
		}
		//TODO paint parts inside the kit
	}
	
	public void paintKit(JPanel frame, Graphics2D d, int x, int y, ImageIcon kitPicture, ImageIcon[] partsImages,ImageIcon badParts ) {
		kitPicture.paintIcon(frame,d, x, y);
		int xcursor = 0; int ycursor = 0;
		for(GfxPart p : partsInsideKit) {
			p.paintPart(frame, d, x + xcursor * (partSize+2),
						y + ycursor * (partSize+2)+4, partsImages[p.getid()], badParts);
			if(xcursor == 0)
				++xcursor;
			else {
				++ycursor;
				xcursor = 0;
			}
		}
	}
	public boolean deletePart(int slot){
		try
		{
			partsInsideKit.remove(slot);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
}
