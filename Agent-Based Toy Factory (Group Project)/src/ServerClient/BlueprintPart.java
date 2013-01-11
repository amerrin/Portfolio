package ServerClient;

/*
 * Serves as a "blueprint" for a part.
 * The list of all blueprints that have been created by
 * the managers is stored on the server.
 * Blueprints can be created and edited
 * by the PartsManager.
 */

import java.io.*;
//import java.util.*;

public class BlueprintPart implements Serializable {
	//class data
	private String name;
	private String number;
	private String description;
	private String imageFileName;
	private String imageType;
	
	//Constructor
	public BlueprintPart(String na, String num, String des, String imF, String imT) {
		name = na;
		number = num;
		description = des;
		imageFileName = imF;
		imageType = imT;
	}
	
	/*
	 * Getters and setters start here
	 */
	public String getName() {
		return name;
	}
	
	public void setName(String n) {
		//set as long as string for name is not empty
		if (!n.isEmpty()) {
			name = n;
		}
	}
	
	public String getNumber() {
		return number;
	}
	
	public void setNumber(String n) {
		number = n;
	}
	
	public String getDescription() {
		return description;
	}
	
	//set if string is not empty
	public void setDescription(String d) {
		if (!d.isEmpty()) {
			description = d;
		}
	}
	
	public String getImageName() {
		return imageFileName;
	}
	
	//set imageName if string is not empty
	public void setImageName(String in) {
		if (!in.isEmpty()) {
			imageFileName = in;
		}
	}
	
	public String getImageType() {
		return imageType;
	}
	
	public void setImageType(String i) {
		//set as long as string for name is not empty
		if (!i.isEmpty()) {
			imageType = i;
		}
	}
	
	//compare components
	public boolean equals(BlueprintPart other) {
		return (name.equals(other.name) &&
				number.equals(other.number) &&
				description.equals(other.description) &&
				imageFileName.equals(other.imageFileName) &&
				imageType.equals(other.imageType) );
	}
}
//Created by Michael Bock
