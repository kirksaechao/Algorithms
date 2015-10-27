// Programmers: Genaro Orodio, Broc Oppler, Kirk Saechao, Daniel Silva
// Class: Computer Science 560, San Diego State University

import java.util.*;

public class SolvePuzzle {
    
    public static class Node {
        String UniqueID;   // will store the updated board config and the move from previous board
        String move;
        Node parent; //enables reverse traversal
        
        //constructor
        public Node(String UniqueID, String move, Node previous) {
            this.UniqueID = UniqueID;
            this.move = move;
            parent = previous;
        }
        
        //function to return the parent
        public Node getParent(){
            return parent;
        }
        
        public String getUniqueID(){
            return UniqueID;
        }
        
        public String getMove(){
            return move;
        }
    }
    
    private static HashSet<String> memoizedBoards;  // storage for already-seen boards
    private static int numRows = 4;
    private static int numCols = 5;
    private static int maxNumBlocks = 12;
    private static String[][] board;  //starting board and current board looked at
    
    //create a new Linked List Queue of Node type
    //nodeQueue contains NODES that we must process
    //accessible with add() and remove()
    private static Queue<Node> nodeQueue = new LinkedList<Node>();
    
    //create a new Stack of Strings -> access using push() and pop()
    //winningMoves will store the list of moves leading to winning node
    //PossibleUIDs is used by findAllMoves function. It will store UID's we must check (memoization)
    private static Stack<String> winningMoves = new Stack<String>();
    private static Stack<String> PossibleUIDs = new Stack<String>();
    
    // parses input and creates game board
    public static String getInput() {
        System.out.print("Input the starting configuration: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        //checks if input is maxNumBlocks
        while (input.length() != maxNumBlocks) {
            if (input.length() > maxNumBlocks) {
                System.out.println("Too many blocks!");
                System.out.print("Input the valid configuration, REMOVE " + (input.length()-maxNumBlocks) + " blocks: ");
                input = scanner.nextLine();
            }
            else if (input.length() < maxNumBlocks) {
                System.out.println("Too few blocks!");
                System.out.print("Input valid configuration, NEED " + (maxNumBlocks-input.length()) + " MORE blocks: ");
                input = scanner.nextLine();
            }
        }
        System.out.println();
        return input;
    }
    
    // given a board, constructs a Unique ID -> to be checked in hash table
    public static String makeBoardKey(String[][] puzzle) {
        String[][] puzzleCopy = new String[numRows][numCols];
        for (int i = 0; i < numRows ; i++) {
            for (int j = 0; j < numCols; j ++)
                puzzleCopy[i][j] = puzzle[i][j];
        }
        String boardString = "";
        int col = 0, row = 0;
        while (col < numCols) {
            switch(puzzleCopy[row][col]) {
                case "A":
                    boardString += "A";
                    puzzleCopy[row][col] = "Z";
                    puzzleCopy[row][col+1] = "Z";
                    col += 2;
                    break;
                case "B":
                    boardString += "B";
                    puzzleCopy[row][col] = "Z";
                    puzzleCopy[row+1][col++] = "Z";
                    break;
                case "C":
                    boardString += "C";
                    puzzleCopy[row][col] = "Z";
                    puzzleCopy[row][col+1] = "Z";
                    puzzleCopy[row+1][col] = "Z";
                    puzzleCopy[row+1][col+1] = "Z";
                    col += 2;
                    break;
                case "D":
                    boardString += "D";
                    puzzleCopy[row][col++] = "Z";
                    break;
                case "E":
                    boardString += "E";
                    puzzleCopy[row][col++] = "Z";
                    break;
                case "Z":
                    col++;
                default:
                    break;
            }
            if (col >= numCols) {
                col = 0;
                row++;
            }
            if (row >= numRows)
                break;
        }
        return boardString;
    }
    
    // given a Unique ID String, constructs the corresponding puzzle board
    public static void makeBoard(String input) {
        
        board = new String[numRows][numCols];
        int row = 0, col = 0, charNum = 0;
        boolean checkEmpty = false;
        //making and filling array
        while (charNum < maxNumBlocks) {
            if (col == numCols) { // row filled, go to next row
                col = 0;
                row++;
            }
            if (row == numRows)  // if board filled, leave
                break;
            switch (input.charAt(charNum++)) {
                case 'A':
                    for (int i = 0; i < numRows ; i++) {  // starting at top-most, left-most , look for empty index
                        for (int j = 0; j < numCols; j ++)
                            if (board[i][j] == null) {
                                row = i;
                                col = j;
                                checkEmpty = true;
                                break;
                            }
                        if (checkEmpty) // leave loop if you find an empty index
                            break;
                    }
                    while (board[row][col] != null) {  //checks if index is occupied
                        col++;
                        if (col == numCols) {      //no more room in row, so go down one
                            col = 0;
                            row++;
                        }
                    }
                    board[row][col] = "A";
                    board[row][col+1] = "A";
                    col += 2;
                    break;
                case 'B':
                    for (int i = 0; i < numRows ; i++) {
                        for (int j = 0; j < numCols; j ++)
                            if (board[i][j] == null) {
                                row = i;
                                col = j;
                                checkEmpty = true;
                                break;
                            }
                        if (checkEmpty)
                            break;
                    }
                    while (board[row][col] != null) {
                        col++;
                        if (col == numCols) {
                            col = 0;
                            row++;
                        }
                    }
                    board[row][col] = "B";
                    board[row+1][col++] = "B";
                    break;
                case 'C':
                    for (int i = 0; i < numRows ; i++) {
                        for (int j = 0; j < numCols; j++)
                            if (board[i][j] == null) {
                                row = i;
                                col = j;
                                checkEmpty = true;
                                break;
                            }
                        if (checkEmpty)
                            break;
                    }
                    while (board[row][col] != null) {
                        col++;
                        if (col == numCols) {
                            col = 0;
                            row++;
                        }
                    }
                    board[row][col] = "C";
                    board[row][col+1] = "C";
                    board[row+1][col] = "C";
                    board[row+1][col+1] = "C";
                    col += 2;
                    break;
                case 'D':
                    while (board[row][col] != null) {
                        col++;
                        if (col == numCols) {
                            col = 0;
                            row++;
                        }
                    }
                    board[row][col++] = "D";
                    break;
                case 'E':
                    while (board[row][col] != null) {
                        col++;
                        if (col == numCols) {
                            col = 0;
                            row++;
                        }
                    }
                    board[row][col++] = "E";
                    break;
                default:
                    break;
            }
        }
        
    }
    
    // given a board, scan for all possible moves, push UID of all possible moves onto PossibleUIDs stack
    public static void findAllMoves(String[][] currentBoard, String[][] zBoard) {
        int row = 0, col = 0;
        String[][] tempBoard = currentBoard;
        
        while (zBoard[row][col] == "Z" || zBoard[row][col] == "E") {
            if (row == 3 && col == 4)
                return; // finished checking all
            else if (col != 4)
                col++;
            else {
                col = 0;
                row++;
            }
        }
        /*
         *  BLOCK A TESTS
         */
        if (currentBoard[row][col] == "A") {
            // move up
            if (row > 0) {
                if (currentBoard[row - 1][col] == "E" && currentBoard[row - 1][col + 1] == "E") {
                    tempBoard[row][col] = "E";
                    tempBoard[row][col + 1] = "E";
                    tempBoard[row - 1][col] = "A";
                    tempBoard[row - 1][col + 1] = "A";
                    PossibleUIDs.push(makeBoardKey(tempBoard));
                    tempBoard = currentBoard;
                }
            }
            // move down
            if (row < 3) {
                if (currentBoard[row + 1][col] == "E" && currentBoard[row + 1][col + 1] == "E"){
                    tempBoard[row][col] = "E";
                    tempBoard[row][col + 1] = "E";
                    tempBoard[row - 1][col] = "A";
                    tempBoard[row - 1][col + 1] = "A";
                    PossibleUIDs.push(makeBoardKey(tempBoard));
                    tempBoard = currentBoard;
                }
            }
            // move right
            if (col < 3) {
                if (currentBoard[row][col + 2] == "") {
                    tempBoard[row][col] = "E";
                    tempBoard[row][col + 2] = "A";
                    PossibleUIDs.push(makeBoardKey(tempBoard));
                    tempBoard = currentBoard;
                }
            }
            // move left
            if (col > 0) {
                if (currentBoard[row][col - 1] == "E") {
                    currentBoard[row][col + 1] = "E";
                    currentBoard[row][col - 1] = "A";
                    PossibleUIDs.push(makeBoardKey(tempBoard));
                    tempBoard = currentBoard;
                }
            }
            
            // finished moves for block
            zBoard[row][col] = "Z";
            zBoard[row][col + 1] = "Z";
            findAllMoves(currentBoard, zBoard);
        }
        
        
        /*
         *  BLOCK B TESTS
         */
        else if (currentBoard[row][col] == "B") {
            // move up
            if (row > 0) {
                if (currentBoard[row - 1][col] == "E") {
                    tempBoard[row + 1][col] = "E";
                    tempBoard[row - 1][col] = "B";
                    PossibleUIDs.push(makeBoardKey(tempBoard));
                    tempBoard = currentBoard;
                }
            }
            // move down
            if (row < 2) {
                if (currentBoard[row + 2][col] == "E"){
                    tempBoard[row][col] = "E";
                    tempBoard[row + 2][col] = "B";
                    PossibleUIDs.push(makeBoardKey(tempBoard));
                    tempBoard = currentBoard;
                }
            }
            // move right
            if (col < 4) {
                if (currentBoard[row][col + 2] == "E") {
                    tempBoard[row][col] = "E";
                    tempBoard[row + 1][col] = "E";
                    tempBoard[row][col + 1] = "B";
                    tempBoard[row + 1][col + 1] = "B";
                    PossibleUIDs.push(makeBoardKey(tempBoard));
                    tempBoard = currentBoard;
                }
            }
            // move left
            if (col > 0) {
                if (currentBoard[row][col - 1] == "E") {
                    tempBoard[row][col] = "E";
                    tempBoard[row + 1][col] = "E";
                    tempBoard[row][col - 1] = "B";
                    tempBoard[row + 1][col - 1] = "B";
                    PossibleUIDs.push(makeBoardKey(tempBoard));
                    tempBoard = currentBoard;
                }
            }
            
            // finished moves for block
            zBoard[row][col] = "Z";
            zBoard[row + 1][col] = "Z";
            findAllMoves(currentBoard, zBoard);
            
        }
        
        /*
         *  BLOCK C TESTS
         */
        else if (currentBoard[row][col] == "C") {
            // move up
            if (row > 0) {
                if (currentBoard[row - 1][col] == "E" && currentBoard[row - 1][col + 1] == "E") {
                    tempBoard[row + 1][col] = "E";
                    tempBoard[row + 1][col + 1] = "E";
                    tempBoard[row - 1][col] = "C";
                    tempBoard[row - 1][col + 1] = "c";
                    PossibleUIDs.push(makeBoardKey(tempBoard));
                    tempBoard = currentBoard;
                }
            }
            // move down
            if (row < 2) {
                if (currentBoard[row + 2][col] == "E" && currentBoard[row + 2][col + 1] == "E"){
                    tempBoard[row][col] = "E";
                    tempBoard[row][col + 1] = "E";
                    tempBoard[row + 2][col] = "C";
                    tempBoard[row + 2][col + 1] = "C";
                    PossibleUIDs.push(makeBoardKey(tempBoard));
                    tempBoard = currentBoard;
                }
            }
            // move right
            if (col < 3) {
                if (currentBoard[row][col + 2] == "E" && currentBoard[row - 1][col + 2] == "E") {
                    tempBoard[row][col] = "E";
                    tempBoard[row + 1][col] = "E";
                    tempBoard[row][col + 2] = "C";
                    tempBoard[row + 1][col + 2] = "C";
                    PossibleUIDs.push(makeBoardKey(tempBoard));
                    tempBoard = currentBoard;
                }
            }
            // move left
            if (col > 0) {
                if (currentBoard[row][col - 1] == "E" && currentBoard[row + 1][col - 1] == "E") {
                    tempBoard[row][col + 1] = "E";
                    tempBoard[row + 1][col + 1] = "E";
                    tempBoard[row][col - 1] = "C";
                    tempBoard[row + 1][col - 1] = "C";
                    PossibleUIDs.push(makeBoardKey(tempBoard));
                    tempBoard = currentBoard;
                }
            }
            
            // no moves available for A block
            else {
                zBoard[row][col] = "Z";
                zBoard[row + 1][col] = "Z";
                zBoard[row][col + 1] = "Z";
                zBoard[row + 1][col + 1] = "Z";
                findAllMoves(currentBoard, zBoard);
            }
        }
        
        /* 
         *  BLOCK D TESTS
         */
        else if (currentBoard[row][col] == "D") {
            // move up
            if (row > 0) {
                if (currentBoard[row - 1][col] == "E") {
                    tempBoard[row][col] = "E";
                    tempBoard[row - 1][col] = "D";
                    PossibleUIDs.push(makeBoardKey(tempBoard));
                    tempBoard = currentBoard;
                }
            }
            // move down
            if (row < 3) {
                if (currentBoard[row + 2][col] == "E"){
                    tempBoard[row][col] = "E";
                    tempBoard[row + 1][col] = "D";
                    PossibleUIDs.push(makeBoardKey(tempBoard));
                    tempBoard = currentBoard;
                }
            }
            // move right
            if (col < 4) {
                if (currentBoard[row][col + 2] == "E") {
                    tempBoard[row][col] = "E";
                    tempBoard[row][col + 1] = "D";
                    PossibleUIDs.push(makeBoardKey(tempBoard));
                    tempBoard = currentBoard;
                }
            }
            // move left
            if (col > 0) {
                if (currentBoard[row][col - 1] == "E") {
                    tempBoard[row][col] = "E";
                    tempBoard[row][col - 1] = "D";
                    PossibleUIDs.push(makeBoardKey(tempBoard));
                    tempBoard = currentBoard;
                }
            }
            // finished moves for block
            zBoard[row][col] = "Z";
            findAllMoves(currentBoard, zBoard);
        }
    }
    
    
    /*
     * String[][] tempBoard
     *
     * for (every index in board)
     * 		If a move is found
     * 			tempBoard = currentBoard		-> dont alter current board (so we dont have to revert later)
     * 			make the move in tempBoard
     * 			PossibleUIDs.push ( makeBoardKey (tempBoard) ) -> turn board into UID String -> push onto stack
     * 
     */
    
    
    /*
     *  Employs a breadth first algorithm to find a winning combination
     *  Take in a Node as an argument, calls recursively till winning config found
     *  
     */
    public static void solve(Node ActiveNode) {
        
        String tempUID;
        
        // extract Unique ID from the node and updates board
        makeBoard( ActiveNode.getUniqueID() );
        
        // check board for winning configuration
        if (board[1][3] == "C" && board[2][4] == "C"){
            
            while  (ActiveNode.getParent() != null ){ 			//while there is still a parent
                winningMoves.push( ActiveNode.getMove() );		//push winning move onto stack
                ActiveNode = ActiveNode.getParent();			//Set Active Node to Parent Node
            }
            
            while ( !winningMoves.empty() ){					//while stack is not empty, pop off and print
                System.out.print( winningMoves.pop() );
            }
            
            System.out.println("YOU WIN!");
            return;
        }
        
        //find all possible moves, updates PossibleUIDs(stack) to contain list of all possible moves
        findAllMoves(board, board);
        
        //start checking UIDs for new unseen strings
        while ( !PossibleUIDs.isEmpty() ){
            tempUID = PossibleUIDs.pop();
            
            //if HashSet does not contain the UID
            if ( !(memoizedBoards.contains(tempUID)) ){
                
                //add it to the HashSet
                memoizedBoards.add(tempUID);
                
                //create a new node based on the UID, set move, set parent node
                Node NodeToProcess = new Node(tempUID, move, ActiveNode); 
                nodeQueue.add(NodeToProcess);
                
            }
        }
        
        //dequeue next node
        ActiveNode = nodeQueue.remove();
        
        //recursively call on updated board
        solve(ActiveNode);
        
    }
    
    public static void main (String[] args) {
        
        makeBoard(getInput());	// Create board based on input string
        String key = makeBoardKey(board); // Create key from initial config
        memoizedBoards = new HashSet<String>(); // New Hash Table
        
        // printing the board for debug purposes
        for (int i = 0; i < 4; i++) {
            System.out.print("| ");
            for (int j = 0; j < 5; j++) {
                System.out.print(board[i][j] + " | ");
            }
            System.out.println();
            System.out.println();
        }	
        
        //create root node
        Node startNode = new Node(key,null,null);
        solve(startNode);
    }
    
}