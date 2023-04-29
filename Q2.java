//-----------------------------------------------------
//Title: Q2 - CDRC System
//Author: Abdallah Ghordlo and Hussein Abdikarim
//Description: In this question, we had to create a system for a chauffeur-driven rental company.
// This class takes a txt-file as input and reads the file that should contain multiple lines that have specific commands, and the information 
// necessary to call that command, after it. After analyzing the line and storing the information in different variables, the chosen command is called with 
// the given information. The output depends on the given txt-file. 
//-----------------------------------------------------
import java.io.File;
import java.util.Scanner;

public class Q2 {
	public static void main(String[] args) {
		//--------------------------------------------------------
		// Summary: Similar to Q1, by calling multiple methods, "main" first reads the txt-file, analyzes every line in that file, and calls the function that 
		// Is called in the lines. To be able to call the functions, main analyzes the sub string that comes after the commands in each line to
		// extract all the necessary values like the ID, the name, and the number of pieces.
		// Precondition: A txt-file is provided.
		// Postcondition: The functions in the txt-file will be called using the given values.
		//--------------------------------------------------------
		System.out.println("--------------- WELCOME TO CDRC SYSTEM ---------------");
		BST tree = new BST();
		Scanner input = new Scanner(System.in);
		String path = input.nextLine();
		String[] lines = readFile(path);
		
		//This loop iterates once for every line in the txt-file.
		for(int i = 0; i < lines.length; i++) {
			
			//The analyzeLine function is called once for every line.
			String command = analyzeLine(lines[i]);
			
			//The string "command" has the name of the function that was called on that specific line.
			if(command != null) {
				//sub is a substring of the current line. It only takes the part that comes after the command.
				String sub = lines[i].substring(command.length());
				//The following int Array will store the digits for the ID
				int[] ints = new int[sub.length()];
				//The following string will store all the encountered letters in the substring.
				String name = "";
				
				//The following two integers will store the length of the ID and the integer with the satisfaction number (0 or 1).
				int idLength = 0, satisfaction = 0;
				//The following boolean will turn into false when the ID is taken.
				boolean takeID = true;
				
				//In the following loop, sub will be broken down into ID, name, and the satisfaction number, depending on the character.
				for(int j = 0; j < sub.length(); j++) {
					
					if(takeID && sub.charAt(j) >= '0' && sub.charAt(j) <= '9') {
						ints[idLength++] = Character.getNumericValue(sub.charAt(j));
						
						//If there is a space after the current number, then we have taken the ID successfully. takeID will turn to false.
						if(!(j+1 >= sub.length()) && sub.charAt(j+1) == ' ') 
							takeID = false;
					}
					//If ID is taken, then the following number is the satisfaction number.
					else if(!takeID && sub.charAt(j) == '1') {
						satisfaction = 1;
						break;
					}
					//If a character from the alphabet is encountered, then it's part of the name.
					else if((sub.charAt(j) >= 'A' && sub.charAt(j) <= 'Z') || (sub.charAt(j) >= 'a' && sub.charAt(j) <= 'z'))
						name = name+String.valueOf(sub.charAt(j));
				}

				int ID = 0;
				//In the following loop, the integers in "ints" int Array will all be added into a single integer called ID.
				for(int j = 0, k = idLength-1; k >= 0 ; j++, k--)
					ID += ints[k] * ((int) Math.pow(10, j));
				
				//In the following part, depending on which command was called in the current line, the appropriate function is called.
				outer: switch(command) {
				case "Add_Captain": 				
					tree.add(ID, name);
					break;
					
				case "Is_Available":
					tree.rent(ID);
					break;
					
				case "Display_captain":
					tree.printInfo(ID);
					break;
				
				case "Finish":
					tree.finish(ID, satisfaction);
					break;
					
				case "Delete_captain":
					tree.delete(ID);
					break;
					
				case "Display_all_captains":
					System.out.println("----------ALL CAPTAINS----------");
					tree.printAllInfo(tree.getRoot());
					break;
				case "Quit":
					break outer;
				}
			}
		}
		System.out.print("Thank you for using CDRC, Good Bye!");
	}
	
	private static String[] readFile(String fileName) {
		try {
			File file = new File(fileName);
			Scanner scan = new Scanner(file);
			int length = 0;
			while(scan.hasNextLine()) {
				length++;
				scan.nextLine();
			}	
			
			String[] str = new String[length];
			scan = new Scanner(file);
			int i = 0;
			while(scan.hasNextLine()) {
				str[i] = scan.nextLine();
				i++;
			}
			return str;
		}
		catch(Exception e) {
			System.out.println("File not found!");
			return null;
		}
	}
	private static String analyzeLine(String line) {
		String[] commands = {"Add_Captain","Is_Available","Display_captain","Finish","Delete_captain","Display_all_captains","Quit"};
		for(int i = 0; i < commands.length; i++) {
			int temp = line.indexOf(commands[i]);
			if(temp != -1)
				return commands[i];
		}
		return null;
	}	
}

class BST{
	private Node root;

	public Node getRoot() {
		return root;
	}
	public void setRoot(Node root) {
		this.root = root;
	}
	
	//Given a node, this function returns the node with the highest ID value among the given node's descendants. 
	//If the node doesn't have children, the node itself will be returned.
	public Node max(Node n) {
		//--------------------------------------------------------
		// Summary: This function takes in a node that is considered a root to either a tree or a subtree. The function returns the node 
		// with the biggest key value.
		// Precondition: A node is provided in the parameter.
		// Postcondition: The node containing the biggest key is returned.
		//--------------------------------------------------------
		if(n == null)
			return null;
		Node iter = n;
		while(iter.getRight() != null)
			iter = iter.getRight();
		return iter;
	}
	
	public Node findParent(int ID) {
		//--------------------------------------------------------
		// Summary: This function takes in the ID in form of an int, which it uses to search for parent of the node with the given ID
		// Precondition: An ID is provided in the parameter in the form of an int.
		// Postcondition: The parent of the node containing a key equal to the given ID will be returned. If not found, null is returned.
		//--------------------------------------------------------
		Node iter = root;
		//If the following is true, then we are trying to look for the parent of the root node, which doesn't exist. So, null will be returned.
		if(iter.getKey() == ID)
			return null;
		
		//This loop traverses through the tree until it finds the parent of the node with the given ID.
		while(true) {
			//If the ID of the left child equals the ID given, this node is the parent.
	        if (iter.getLeft() != null && ID == iter.getLeft().getKey())
	        	return iter;
	        ///If the ID of the right child equals the ID given, this node is the parent.
	        else if (iter.getRight() != null && ID == iter.getRight().getKey())
	        	return iter;
			//If ID is smaller, going left.
	        else if (iter.getLeft() != null && ID < iter.getKey())
	        	iter = iter.getLeft();
	        //If ID is bigger, going right.
	        else if (iter.getRight() != null && ID > iter.getKey())
	        	iter = iter.getRight();
	        //If a dead end is encountered, null is returned.
	        else if(iter.getRight() == null && iter.getLeft() == null)
				return null;
		}
	}
	public Node find(int ID) {
		//--------------------------------------------------------
		// Summary: This function takes in the ID in form of an int, which it uses to search for the node that has a key equal to the ID
		// and returns that node.
		// Precondition: An ID is provided in the parameter in the form of an int.
		// Postcondition: The node containing a key equal to the given ID will be returned. If not found, null is returned.
		//--------------------------------------------------------
		Node iter = root;

		//It traverses the tree by comparing the key of the node it's traversing with the given ID. If the ID we are looking for is smaller
		//than the current node's key, it will keep go left. If the ID we are looking for is bigger than the current node's key, it will keep go right.
		//If the desired node is found, it will be returned. However, if a dead end is reached without finding the desired node, null is returned. 
		while(true) {
			//If ID is smaller, going left.
	        if (iter.getLeft() != null && ID < iter.getKey())
	        	iter = iter.getLeft();
	        //If ID is bigger, going right.
	        else if (iter.getRight() != null && ID > iter.getKey())
	        	iter = iter.getRight();
	        //If desired node is found, it's returned.
	        else if (iter.getKey() == ID)
	        	return iter;
	        //If a dead end is encountered, null is returned.
	        else if(iter.getKey() != ID &&(iter.getRight() == null || iter.getLeft() == null))
				return null;
		}
	}
	public void add(int ID, String name) {
		//--------------------------------------------------------
		// Summary: This function takes in an integer for the ID and a String for the name. It creates and adds a product in the
		// appropriate place in the tree.
		// Precondition: An ID and a name are provided.
		// Postcondition: After creating a new Captain instance with the provided information, this function adds the new captain
		// to an appropriate place in the tree.
		//--------------------------------------------------------
		Captain newCap = new Captain(ID, name);
		System.out.println("Add Captain: Add a new captain record in the System");
		
		System.out.println("\n"+newCap.toString());
		System.out.println("----------------------------------------------------------------");
		if(root == null) {
			root = new Node(newCap);}
		
		Node iter = root;
		
		while(true) {
			//If ID is smaller, going left.
	        if (iter.getLeft() != null && ID < iter.getKey())
	        	iter = iter.getLeft();
	        //If ID is bigger, going right.
	        else if (iter.getRight() != null && ID > iter.getKey())
	        	iter = iter.getRight();
	        //If desired node is found, it's returned.
	        else if (iter.getKey() == ID) {
	        	return;
	        }
	        else if(iter.getLeft() == null && ID < iter.getKey()) {
	        	iter.setLeft(new Node(newCap));
	        	return;
	        }
	        //If a dead end is encountered, null is returned.
	        else if(iter.getRight() == null && ID > iter.getKey()) {
	        	iter.setRight(new Node(newCap));
	        	return;
	        }
		}
	}
	public void delete(int ID) {
		//--------------------------------------------------------
		// Summary: This function takes in the ID in form of an int, which it uses to search for the node with the given ID by calling the find() function. 
		// After that, it calls the findParent() function because the parent is needed when deleting nodes in a tree. There are 3 cases: The node we want to
		// delete is a leaf, the node we want to delete has one child, and the node we want to delete has two children. All cases are handled.
		// In the case of two children, the node with the biggest key value in the left subtree replaces the node we want to delete, this is done by calling the
		// max() method and using the node is returns to replace the node we want to delete.
 		// Precondition: An ID is provided in the parameter in the form of an int.
		// Postcondition: The node with the ID provided is deleted from the tree.
		//--------------------------------------------------------
		System.out.print("Delete Captain:");
		Node node = find(ID);
		if(node == null) {
			System.out.println(" Couldn't find any captain with ID number "+ID);
			System.out.println("----------------------------------------------------------------");
			return;
		}
		Node parent = findParent(ID);

		//If the node we want to delete is a root.
		if(parent == null)
			root = null;
		
		//If the node we want to delete is a leaf.
		else if(node.getRight() == null && node.getLeft() == null) {
			//If the node we want to delete is the right child
			if(parent.getRight().getKey() == ID)
				parent.setRight(null);
			//If the node we want to delete is the left child
			else if(parent.getLeft().getKey() == ID)
				parent.setLeft(null);
			node = null;
		}
		
		//If the node we want to delete has two children.
		else if(node.getRight() != null && node.getLeft() != null) {
			//Getting the node with the highest value on the right sub-tree
			Node maxRight = max(node.getRight());
			
			//Changing the connections in the tree. The max value in the right subtree will replace the node we want to delete.
			Node temp = maxRight;
			delete(maxRight.getKey());
			maxRight.setLeft(node.getLeft());
			maxRight.setRight(node.getRight());
			
			//If the node we want to delete is the right child
			if(parent.getRight().getKey() == ID)
				parent.setRight(maxRight);
			//If the node we want to delete is the left child
			else if(parent.getLeft().getKey() == ID)
				parent.setLeft(maxRight);
			
			System.out.println("The captain "+node.getCap().getName()+" left CCR");
			node = maxRight;
			maxRight = null;
		}
		//If the node we want to delete has only a left child.
		else if(node.getRight() == null) {
			Node temp = node.getLeft();
			node.setLeft(null);
			
			//If the node we want to delete is the right child
			if(parent.getRight().getKey() == ID)
				parent.setRight(temp);
			//If the node we want to delete is the left child
			else if(parent.getLeft().getKey() == ID)
				parent.setLeft(temp);
			
			System.out.println("The captain "+node.getCap().getName()+" left CCR");
			node = temp;
			temp = null;
		}
		//If the node we want to delete has only a right child.
		else if(node.getLeft() == null) {
			Node temp = node.getRight();
			node.setRight(null);
			
			//If the node we want to delete is the right child
			if(parent.getRight().getKey() == ID)
				parent.setRight(temp);
			//If the node we want to delete is the left child
			else if(parent.getLeft().getKey() == ID)
				parent.setLeft(temp);
			
			System.out.println("The captain "+node.getCap().getName()+" left CCR");
			node = temp;
			temp = null;
		}
		System.out.println("----------------------------------------------------------------");
	}
	public void printInfo(int ID) {
		//--------------------------------------------------------
		// Summary: This function takes in the ID in form of an int, which it uses to search for the node with the given ID by calling the find() function. 
		// After that, it calls the toString() method to print the information of the captain. If the captain isn't found, a message is printed.
		// Precondition: An ID is provided in the parameter in the form of an int.
		// Postcondition: The information of the captain with the given ID is printed in a specific form.
		//--------------------------------------------------------
		System.out.print("Display Captain: ");
		Node node = find(ID);
		if(node == null) 
			System.out.println("Couldn't find any captain with ID number "+ ID);
		else
			System.out.println("\n" + node.getCap().toString());
		System.out.println("\n----------------------------------------------------------------");
	}
	public void print(int ID) {
		//--------------------------------------------------------
		// Summary: This function takes in the ID in form of an int, which it uses to search for the node with the given ID by calling the find() function. 
		// After that, it calls the toString() method to print the information of the captain. This function is called in the printAllInfo() method.
		// Precondition: An ID is provided in the parameter in the form of an int.
		// Postcondition: The information of the captain with the given ID is printed in a specific form.
		//--------------------------------------------------------
		System.out.println("--CAPTAIN: ");
		Node node = find(ID);
		System.out.println("\n" + node.getCap().toString()+"\n");
	}
	public void printAllInfo(Node root) {
		//--------------------------------------------------------
		// Summary: This function takes in the root of a tree and traverses it. While traversing, it calls the print() function. So, all the nodes 
		// end up being printed out.
		// Precondition: A node is provided in the parameter.
		// Postcondition: The information of the captain with the given ID is printed in a specific form.
		//--------------------------------------------------------
		if(root == null)
			return;
		if(root.getLeft() == null) {
			if(root.getRight() == null) {
				print(root.getCap().getID());
				return;
			}
			print(root.getCap().getID());
			printAllInfo(root.getRight());
			return;
		}
		printAllInfo(root.getLeft());
		print(root.getCap().getID());
		if(root.getRight() != null)
			printAllInfo(root.getRight());
		return;
	}
	public void rent(int ID) {
		//--------------------------------------------------------
		// Summary: This function takes in the ID in form of an int, which it uses to search for the node with the given ID by calling the find() function. 
		// After that, it calls the isAvailable() method to print a message that informs the user whether the captain with the given ID is available.
		// If available, the captain is rented.
		// Precondition: An ID is provided in the parameter in the form of an int.
		// Postcondition: The user is informed whether the renting of the captain with the given ID was successful.
		//--------------------------------------------------------
		System.out.print("IsAvailable: ");
		Node node = find(ID);
		if(node == null)
			System.out.println("Couldn't find any captain with ID number "+ID);
		else if(node.getCap().isAvailable()) {
			System.out.println("Reserve a new Ride with captain "+ID);
			node.getCap().setAvailable(false);
		}
		else
			System.out.println("The captain "+node.getCap().getName()+" is not available. He is on another ride!");
		System.out.println("\n----------------------------------------------------------------");
	}
	public void finish(int ID, int satisfaction) {
		//--------------------------------------------------------
		// Summary: This function takes in the ID in form of an int, and an int of representing whether the customer was satisfied or not.
		// Precondition: An ID and the int representing the customer's satisfaction are provided in the parameter in the form of two integers.
		// Postcondition: A message is printed out informing the user that the ride with captain with the given ID has successfully ended.
		//--------------------------------------------------------
		System.out.print("Finish: ");
		Node node = find(ID);
		if(node == null)
			System.out.println("Couldn't find any captain with ID number "+ID);
		else if(node.getCap().isAvailable())
			System.out.println("The captain "+node.getCap().getName()+" is not in a ride");
		else {
			System.out.println("Finish ride with captain "+ID);
			node.getCap().rate(satisfaction);
			node.getCap().setAvailable(true);
			System.out.println("\n"+node.getCap().toString());
		}	
		System.out.println("----------------------------------------------------------------");
	}
	
	public class Node{
		private Captain cap;
		private int key;
		private Node left, right;

		public Node(Captain captain) {
			this.cap = captain;
			this.key = captain.getID();
		}
		
		public int getKey() {
			return key;
		}

		public void setKey(int key) {
			this.key = key;
		}

		public Captain getCap() {
			return cap;
		}

		public void setCap(Captain cap) {
			this.cap = cap;
		}

		public Node getLeft() {
			return left;
		}

		public void setLeft(Node left) {
			this.left = left;
		}

		public Node getRight() {
			return right;
		}

		public void setRight(Node right) {
			this.right = right;
		}			
	}
}
class Captain{
	private int ID, ratingStar;
	private String name;
	private boolean available;
	
	public Captain(int ID, String name) {
		this.ID = ID;
		this.name = name;
		available = true;
		ratingStar = 0;
	}
	
	public void rate(int rating) {
		if(rating == 0 && ratingStar > 0)
			ratingStar--;
		else if(rating == 1 && ratingStar < 5)
			ratingStar++;
	}
	private String checkAvailable() {
		if(available)
			return "True";
		return "False";
	}
	@Override
	public String toString() {
		return     "		       ID: "+ID+"\r\n"
				+  "		       Name: "+name+"\r\n"
				+  "		       Available: "+checkAvailable()+"\r\n"
				+  "		       Rating star: "+ratingStar;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getRatingStar() {
		return ratingStar;
	}

	public void setRatingStar(int ratingStar) {
		this.ratingStar = ratingStar;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}
	
}
