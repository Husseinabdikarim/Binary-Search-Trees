//-----------------------------------------------------
//Title: Q1 - ITS System
//Author: Abdallah Ghordlo and Hussein Abdikarim
//Description: In this question, we had to create an Inventory Tracking System.
// This class takes a txt-file as input and reads the file that should contain multiple lines that have specific commands, and the information 
// necessary to call that command, after it. After analyzing the line and storing the information in different variables, the chosen command is called with 
// the given information. The output depends on the given txt-file. 
//-----------------------------------------------------
import java.io.File;
import java.util.Scanner;

public class Q1 {
	public static void main(String[] args) {
		//--------------------------------------------------------
		// Summary: By calling multiple methods, "main" first reads the txt-file, analyzes every line in that file, and calls the function that 
		// Is called in the lines. To be able to call the functions, main analyzes the sub string that comes after the commands in each line to
		// extract all the necessary values like the ID, the name, and the number of pieces.
		// Precondition: A txt-file is provided.
		// Postcondition: The functions in the txt-file will be called using the given values.
		//--------------------------------------------------------
		System.out.println("--------------- WELCOME TO ITS SYSTEM ---------------");
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
				
				//The following two int Arrays will store the digits for the ID and the pieces
				int[] intsforID = new int[sub.length()];
				int[] intsforPieces = new int[sub.length()];
				//All the characters that are part of the alphabet in the substring will be added to the string "name". 
				String name = "";
				
				int idLength = 0, piecesLength = 0, pieces = 0;
				//The following boolean will turn into false when the ID is taken.
				boolean takeID = true;
				
				//In the following loop, sub will be broken down into ID, name, and pieces depending on the character.
				for(int j = 0; j < sub.length(); j++) {
					//If we still didn't finish taking the ID and we encountered a Number, the number is part of the ID.
					if(takeID && sub.charAt(j) >= '0' && sub.charAt(j) <= '9') {
						intsforID[idLength++] = Character.getNumericValue(sub.charAt(j));
						//If there is a space after the current number, then we have taken the ID successfully. takeID will turn to false.
						if(!(j+1 >= sub.length()) && sub.charAt(j+1) == ' ') 
							takeID = false;
					}
					//If ID is taken, then the following numbers are the number of pieces
					else if(!takeID && sub.charAt(j) >= '0' && sub.charAt(j) <= '9') {
						intsforPieces[piecesLength++] = Character.getNumericValue(sub.charAt(j));
					}
					//If a character from the alphabet is encountered, then it's part of the name.
					else if((sub.charAt(j) >= 'A' && sub.charAt(j) <= 'Z') || (sub.charAt(j) >= 'a' && sub.charAt(j) <= 'z'))
						name = name+String.valueOf(sub.charAt(j));
				}

				int ID = 0;
				//In the following loop, the integers in intsforID int Array will all be added into a single integer called ID. 
				for(int j = 0, k = idLength-1; k >= 0 ; j++, k--)
					ID += intsforID[k] * ((int) Math.pow(10, j));
				
				//In the following loop, the integers in intsforPieces int Array will all be added into a single integer pieces. 
				for(int j = 0, k = piecesLength-1; k >= 0 ; j++, k--)
					pieces += intsforPieces[k] * ((int) Math.pow(10, j));

				//In the following part, depending on which command was called in the current line, the appropriate function is called.
				outer: switch(command) {
				case "Add_product": 				
					tree.createProd(ID, name, pieces);
					break;
					
				case "Is_Available":
					tree.isAvailable(ID);
					break;
					
				case "Quit":
					break outer;
				}
			}
		}
		System.out.print("\nThank you for using ITS, Good Bye!");
	}
	
	private static String[] readFile(String fileName) {
		//--------------------------------------------------------
		// Summary: This function takes in the file name, reads it, and returns a String Array containing the lines of the txt-file.
		// Precondition: A file name is provided in the parameter.
		// Postcondition: A String Array containing the lines will be returned.
		//--------------------------------------------------------
		try {
			File file = new File(fileName);
			Scanner scan = new Scanner(file);
			int length = 0;
			//First we get the number of lines in the txt-file.
			while(scan.hasNextLine()) {
				length++;
				scan.nextLine();
			}	
			
			String[] str = new String[length];
			scan = new Scanner(file);
			int i = 0;
			//Then the lines are added into a String array, which is later returned.
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
		//--------------------------------------------------------
		// Summary: This function takes in a String, analyzes it, and returns the command that was called on the given line as a String.
		// Precondition: A String is provided in the parameter.
		// Postcondition: A String containing the called command will be returned.
		//--------------------------------------------------------
		
		//The following String array consists of the available methods
		String[] commands = {"Add_product","Is_Available","Quit"};
		
		//The following loop checks which command was called by checking whether any of the available functions are found in the line.
		for(int i = 0; i < commands.length; i++) {
			int temp = line.indexOf(commands[i]);
			if(temp != -1)
				return commands[i];
		}
		//If the line doesn't consist of any of the available functions, return null.
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
	        else if(iter.getRight() == null && iter.getLeft() == null)
				return null;
		}
	}
	
	public void createProd(int ID, String name, int pieces) {
		//--------------------------------------------------------
		// Summary: This function takes in two integers for the ID and the number of pieces, and one String for the name. It creates and 
		// adds a product in the appropriate place in the tree.
		// Precondition: An ID, a name, and the number of pieces are provided.
		// Postcondition: After creating a new product with the provided information, this function adds the new product to an appropriate place in the tree.
		//--------------------------------------------------------
		Product newProd = new Product(ID, name, pieces);
		System.out.println("Create Product:\n");
		
		System.out.println(newProd.toString());
		if(root == null) 
			root = new Node(newProd);
		
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
	        	iter.setLeft(new Node(newProd));
	        	return;
	        }
	        //If a dead end is encountered, null is returned.
	        else if(iter.getRight() == null && ID > iter.getKey()) {
	        	iter.setRight(new Node(newProd));
	        	return;
	        }
		}
	}
	public void isAvailable(int ID) {
		//--------------------------------------------------------
		// Summary: This function takes in the ID in form of an int, which it uses to call for the find() function, and returns the number of pieces left.
		// If the product isn't found has 0 pieces, a message will be printed out.
		// Precondition: An ID is provided in the parameter in the form of an int.
		// Postcondition: The function doesn't return anything but it does print out the number of pieces left.
		//--------------------------------------------------------
		Node node = find(ID);
		if(node == null || node.getProd().getPieces() == 0) {
			System.out.println("The product is out of stock!!!\n");
		}
		else if(node.getProd().getPieces() > -1) {
			System.out.println("There are "+node.getProd().getPieces()+" products\n");
		}
		return;
	}
	
	public class Node{
		private Product prod;
		private int key;
		private Node left, right;

		public Node(Product prod) {
			this.prod = prod;
			this.key = prod.getID();
		}
		
		public int getKey() {
			return key;
		}

		public void setKey(int key) {
			this.key = key;
		}
		
		public Product getProd() {
			return prod;
		}

		public void setProd(Product prod) {
			this.prod = prod;
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
class Product{
	private int ID, pieces;
	private String name;
	
	public Product(int ID, String name, int pieces) {
		this.ID = ID;
		this.name = name;
		this.pieces = pieces;
	}
	
	public int getPieces() {
		return pieces;
	}

	public void setPieces(int piece) {
		this.pieces = pieces;
	}

	@Override
	public String toString() {
		return     "		       ID: "+ID+"\r\n"
				+  "		       Name: "+name+"\r\n"
				+  "		       Piece: "+pieces;
	}

	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
}