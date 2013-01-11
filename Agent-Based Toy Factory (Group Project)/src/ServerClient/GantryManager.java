package ServerClient;

/* GantryManager class views lanes, and manages lane non-normatives for V2. */
public class GantryManager extends GfxManager {
	// constants for the GantryManager's offset from the top left corner of the Factory animation
	static final int xOff = 850, yOff = 0; 
	
	/* Pass offsets to GfxManager constructor, and set up non-normative panel. */
	public GantryManager() {
		super(xOff, yOff);
		setTitle("Gantry Manager");
		setSize(200, 750);
		setVisible(true);
		setResizable(false);
	}
	public static void main(String[] args){
		GantryManager gm = new GantryManager();
		
		
	}
}
