package cz.mff.ui;

import java.io.*;
import java.util.ArrayList;

public class Main {

    private static final int BOARD_SIZE = 9;
    private enum Encoding {
        MINIMAL, EXTENDED;

        private int numberOfDefaultClauses;

        private ArrayList<Integer>[][][] defaultBoardArray;

        static {
            MINIMAL.numberOfDefaultClauses = 8829;
            EXTENDED.numberOfDefaultClauses = 11988;

            MINIMAL.defaultBoardArray = fillInDefaultBoardArray(MINIMAL);
            EXTENDED.defaultBoardArray = fillInDefaultBoardArray(EXTENDED);
        }
        public int getNumberOfDefaultClauses() {
            return numberOfDefaultClauses;
        }

        public ArrayList<Integer>[][][] getDefaultBoardArray() {
            ArrayList<Integer>[][][] deepCopy = new ArrayList[BOARD_SIZE][BOARD_SIZE][BOARD_SIZE];
            for (int x = 0; x < BOARD_SIZE; x++) {
                for (int y = 0; y < BOARD_SIZE; y++) {
                    for (int val = 0; val < BOARD_SIZE; val++) {
                        deepCopy[x][y][val] = new ArrayList<>(this.defaultBoardArray[x][y][val].size());
                        for (int i : this.defaultBoardArray[x][y][val])
                            deepCopy[x][y][val].add(i);
                    }
                }
            }
            return deepCopy;
        }

        private static ArrayList<Integer>[][][] fillInDefaultBoardArray(Encoding encoding) {
            ArrayList<Integer>[][][] result = new ArrayList[BOARD_SIZE][BOARD_SIZE][BOARD_SIZE];
            int clauseCounter = 0;

            // at least one number in each entry
            for (int x = 0; x < BOARD_SIZE; x++) {
                for (int y = 0; y < BOARD_SIZE; y++) {
                    for (int val = 0; val < BOARD_SIZE; val++) {
                        result[x][y][val] = new ArrayList<>();
                        result[x][y][val].add(clauseCounter);
                    }
                    clauseCounter++;
                }
            }

            // Each number appears at most once in each row
            for (int y = 0; y < BOARD_SIZE; y++) {
                for (int val = 0; val < BOARD_SIZE; val++) {
                    for (int xAll = 0; xAll < BOARD_SIZE-1; xAll++) {
                        for (int xRest = xAll+1; xRest < BOARD_SIZE; xRest++) {
                            result[xAll][y][val].add(-clauseCounter);
                            result[xRest][y][val].add(-clauseCounter);
                            clauseCounter++;
                        }
                    }
                }
            }

            // Each number appears at most once in each column
            for (int x = 0; x < BOARD_SIZE; x++) {
                for (int val = 0; val < BOARD_SIZE; val++) {
                    for (int yAll = 0; yAll < BOARD_SIZE-1; yAll++) {
                        for (int yRest = yAll+1; yRest < BOARD_SIZE; yRest++) {
                            result[x][yAll][val].add(-clauseCounter);
                            result[x][yRest][val].add(-clauseCounter);
                            clauseCounter++;
                        }
                    }
                }
            }

            // Each number appears at most once in each 3x3 sub-grid
            for (int val = 0; val < BOARD_SIZE; val++) {
                for (int horSquareInd = 0; horSquareInd < Math.sqrt(BOARD_SIZE); horSquareInd++) {
                    for (int vertSquareInd = 0; vertSquareInd < Math.sqrt(BOARD_SIZE); vertSquareInd++) {
                        for (int x = 0; x < Math.sqrt(BOARD_SIZE); x++) {
                            for (int y = 0; y < Math.sqrt(BOARD_SIZE); y++) {
                                for (int yRest = y+1; yRest < Math.sqrt(BOARD_SIZE); yRest++) {
                                    result[3*horSquareInd+x][3*vertSquareInd+y][val].add(-clauseCounter);
                                    result[3*horSquareInd+x][3*vertSquareInd+yRest][val].add(-clauseCounter);
                                    clauseCounter++;
                                }
                                for (int xRest = x+1; xRest < Math.sqrt(BOARD_SIZE); xRest++) {
                                    for (int yRest = 0; yRest < Math.sqrt(BOARD_SIZE); yRest++) {
                                        result[3*horSquareInd+x][3*vertSquareInd+y][val].add(-clauseCounter);
                                        result[3*horSquareInd+xRest][3*vertSquareInd+yRest][val].add(-clauseCounter);
                                        clauseCounter++;
                                    }
                                }
                            }
                        }
                    }

                }
            }

            if (encoding == Encoding.EXTENDED) {
                // ---- extended representation clauses
                // at most one number in each entry
                for (int x = 0; x < BOARD_SIZE; x++) {
                    for (int y = 0; y < BOARD_SIZE; y++) {
                        for (int val = 0; val < BOARD_SIZE-1; val++) {
                            for (int valRest = val + 1; valRest < BOARD_SIZE; valRest++) {
                                result[x][y][val].add(-clauseCounter);
                                result[x][y][valRest].add(-clauseCounter);
                                clauseCounter++;
                            }
                        }
                    }
                }

                // Each number appears at least once in each row
                for (int y = 0; y < BOARD_SIZE; y++) {
                    for (int val = 0; val < BOARD_SIZE; val++) {
                        for (int x = 0; x < BOARD_SIZE; x++) {
                            result[x][y][val].add(clauseCounter);
                        }
                        clauseCounter++;
                    }
                }

                // Each number appears at least once in each column
                for (int x = 0; x < BOARD_SIZE; x++) {
                    for (int val = 0; val < BOARD_SIZE; val++) {
                        for (int y = 0; y < BOARD_SIZE; y++) {
                            result[x][y][val].add(clauseCounter);
                        }
                        clauseCounter++;
                    }
                }

                // Each number appears at least once in each 3x3 sub-grid
                for (int val = 0; val < BOARD_SIZE; val++) {
                    for (int horSquareInd = 0; horSquareInd < Math.sqrt(BOARD_SIZE); horSquareInd++) {
                        for (int vertSquareInd = 0; vertSquareInd < Math.sqrt(BOARD_SIZE); vertSquareInd++) {
                            for (int x = 0; x < Math.sqrt(BOARD_SIZE); x++) {
                                for (int y = 0; y < Math.sqrt(BOARD_SIZE); y++) {
                                    result[3 * horSquareInd + x][3 * vertSquareInd + y][val].add(clauseCounter);
                                }
                            }
                            clauseCounter++;
                        }

                    }
                }
            }
            return result;
        }

    }

    private static ArrayList<Integer>[] getVarsToClausesMap(ArrayList<Integer>[][][] varsInClauses, int numOfClauses) {
        ArrayList<Integer>[] result = new ArrayList[numOfClauses];
        for (int i = 0; i < numOfClauses; i++) {
            result[i] = new ArrayList<>();
        }

        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                for (int val = 0; val < BOARD_SIZE; val++) {
                    int varNumber = getVarNumber(x,y,val);
                    for (int clause : varsInClauses[x][y][val]) {
                        try {
                            if (clause >= 0)
                                result[clause].add(varNumber);
                            else
                                result[-clause].add(-varNumber);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Transform variable identifier S_x,y,val to variable number used in dimacs format. Dimensions x, y and val are
     * the size of the board, we treat the number as if it was in base BOARD_SIZE and calculate it's 10-base equivalent + 1
     * @param x horizontal var identifier
     * @param y vertical var identifier
     * @param val value var identifier
     * @return variables' number in 10-base
     */
    private static int getVarNumber(int x, int y, int val) {
        int result = 1;
        result += val;
        result += y*BOARD_SIZE;
        result += x*BOARD_SIZE*BOARD_SIZE;
        return result;
    }

    private static int getFixedPoints(int[][] board) {
        int result = 0;
        for (int[] line : board) {
            for (int num : line) {
                if (num > 0)
                    result++;
            }
        }
        return result;
    }

    private static ArrayList<Integer>[][][] addFixedNumbersToBoardArray(int[][] board, Encoding encoding) {
        int clauseCounter = encoding.getNumberOfDefaultClauses();
        ArrayList<Integer>[][][] result = encoding.getDefaultBoardArray();

        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                if (board[x][y] > 0) {
                    result[x][y][board[x][y]-1].add(clauseCounter);
                    clauseCounter++;
                }
            }
        }
        return result;
    }

    private static void printBoard(int[][] board) {
        for (int[] boardLine : board) {
            for (int curnum : boardLine)
                System.out.print(curnum);
            System.out.println();
        }
        System.out.println("------------");
    }

    private static int[][] parseBoard(String line) {
        int[][] board = new int[BOARD_SIZE][BOARD_SIZE];

        for(int i = 0; i < board.length; i++) {

            for(int j = 0; j < board.length; j++) {
                int cur = parseChar(line.charAt(9 * i + j));
                board[i][j] = cur;
            }
        }
        return board;
    }

    private static int parseChar(char c)
    {
    switch(c) {
        case '1': return 1;
        case '2': return 2;
        case '3': return 3;
        case '4': return 4;
        case '5': return 5;
        case '6': return 6;
        case '7': return 7;
        case '8': return 8;
        case '9': return 9;
        }

    return 0;
    }

    public static void main(String[] args) {
        final String OUTPUT_DIR = "outputs";
        try (BufferedReader br = new BufferedReader(new FileReader("sudoku-inputs.txt"))) {
            String line;
            int boardCounter = 0;
            while ((line = br.readLine()) != null) {
                if (boardCounter < 2) {
                    boardCounter++;
                    continue;
                }
                int[][] board = parseBoard(line);

                //printBoard(board);

                // create formula from board
                for (Encoding encoding : Encoding.values()) {
                    // create 3D array vars -> occurence in clauses
                    ArrayList<Integer>[][][] varsInClauses = addFixedNumbersToBoardArray(board, encoding);

                    int numOfClauses = encoding.getNumberOfDefaultClauses() + getFixedPoints(board);
                    // go thru 3D array and create 2D array clausses -> vars
                    ArrayList<Integer>[] clausesToVars = getVarsToClausesMap(varsInClauses, numOfClauses);

                    // transform 2D array to dimacs format
                    StringBuilder outputStrBuilder = new StringBuilder();
                    outputStrBuilder.append(String.format("p cnf %d %d\n",
                            getVarNumber(BOARD_SIZE, BOARD_SIZE, BOARD_SIZE),numOfClauses));
                    for (ArrayList<Integer> clause : clausesToVars) {
                        for (int var : clause) {
                            outputStrBuilder.append(var);
                            outputStrBuilder.append(' ');
                        }
                        outputStrBuilder.append(0);
                        outputStrBuilder.append('\n');
                    }

                    // output to file
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(
                            String.format("%s/%d-%s.cnf", OUTPUT_DIR, boardCounter, encoding.toString())))) {
                        bw.write(outputStrBuilder.toString());
                    } catch (IOException outputException) {
                        System.err.println("Exception caught during output");
                        outputException.printStackTrace();
                    }

                }
                boardCounter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
