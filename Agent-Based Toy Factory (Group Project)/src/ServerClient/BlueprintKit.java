package ServerClient;

/*
 * Serves as a "blueprint" for a kit.
 * The list of all blueprints that have been created by
 * the managers is stored on the server.
 * Blueprints can be created and edited
 * by the KitManager.
 */

import java.io.*;
import java.util.*;

public class BlueprintKit implements Serializable  {
	//class data
	private String name;
	private ArrayList<BlueprintPart> parts;
	
	//Constructor
	public BlueprintKit(String n, ArrayList<BlueprintPart> p) throws Exception {
		name = n;
		//check that ArrayList is not null
		if (p==null) {
			throw new Exception("ArrayList of BlueprintParts is null!");
		}
		//check that array list has between 4 and 8 parts
		else if (p.size()<4 || p.size()>8) {
			throw new Exception("ArrayList of BlueprintParts has less than 4 or more than 8 parts!");
		} else {
			parts = p;
		}
	}
	
	/*
	 * getters and setters below
	 */
	public String getName() {
		return name;
	}
	
	public void setName(String n) {
		//check if string is blank
		if (!n.equals("")) {
			name = n;
		}
	}
	
	public ArrayList<BlueprintPart> getParts() {
		return parts;
	}
	
	public void setParts(ArrayList<BlueprintPart> p) throws Exception {
		//check that ArrayList is not null
		if (p==null) {
			throw new Exception("ArrayList of BlueprintParts is null!");
		}
		//check that array list has between 4 and 8 parts
		else if (p.size()<4 || p.size()>8) {
			throw new Exception("ArrayList of BlueprintParts has less than 4 or more than 8 parts!");
		}
		else {
			parts = p;
		}
	}
	
	public void addBlueprintPartToBlueprintKit(BlueprintPart newPart) {
		if (parts.size() < 8) {
			parts.add(newPart);
		}
	}
	
	//compare components
	public boolean equals(BlueprintKit other) {
		// if the names are not equal or if the number of parts is different
		if ( (!name.equals(other.name)) || (parts.size() != other.parts.size()) )
			return false;
		
		// if any of the parts are not the same, then the kits are not equal
		for (int i = 0; i < parts.size(); ++i) {
			if (!parts.get(i).equals(other.parts.get(i)))
				return false;
		}
		return true;
	}
}

// Created by Michael Bock
