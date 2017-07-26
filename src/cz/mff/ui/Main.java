package cz.mff.ui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

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
            return defaultBoardArray;
        }

        private static ArrayList<Integer>[][][] fillInDefaultBoardArray(Encoding encoding) {
            ArrayList<Integer>[][][] result = new ArrayList[9][9][9];
            int clauseCounter = 0;

            // at least one number in each entry
            for (int x = 0; x < 9; x++) {
                for (int y = 0; y < 9; y++) {
                    for (int val = 0; val < 9; val++) {
                        result[x][y][val] = new ArrayList<>();
                        result[x][y][val].add(clauseCounter);
                    }
                    clauseCounter++;
                }
            }

            // Each number appears at most once in each row
            for (int y = 0; y < 9; y++) {
                for (int val = 0; val < 9; val++) {
                    for (int xAll = 0; xAll < 8; xAll++) {
                        for (int xRest = xAll+1; xRest < 9; xRest++) {
                            result[xAll][y][val].add(-clauseCounter);
                            result[xRest][y][val].add(-clauseCounter);
                            clauseCounter++;
                        }
                    }
                }
            }

            // Each number appears at most once in each column
            for (int x = 0; x < 9; x++) {
                for (int val = 0; val < 9; val++) {
                    for (int yAll = 0; yAll < 8; yAll++) {
                        for (int yRest = yAll+1; yRest < 9; yRest++) {
                            result[x][yAll][val].add(-clauseCounter);
                            result[x][yRest][val].add(-clauseCounter);
                            clauseCounter++;
                        }
                    }
                }
            }

            // Each number appears at most once in each 3x3 sub-grid
            for (int val = 0; val < 9; val++) {
                for (int horSquareInd = 0; horSquareInd < 3; horSquareInd++) {
                    for (int vertSquareInd = 0; vertSquareInd < 3; vertSquareInd++) {
                        for (int x = 0; x < 3; x++) {
                            for (int y = 0; y < 3; y++) {
                                for (int yRest = y+1; yRest < 3; yRest++) {
                                    result[3*horSquareInd+x][3*vertSquareInd+y][val].add(-clauseCounter);
                                    result[3*horSquareInd+x][3*vertSquareInd+yRest][val].add(-clauseCounter);
                                    clauseCounter++;
                                }
                                for (int xRest = x+1; xRest < 3; xRest++) {
                                    for (int yRest = 0; yRest < 3; yRest++) {
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
                for (int x = 0; x < 9; x++) {
                    for (int y = 0; y < 9; y++) {
                        for (int val = 0; val < 8; val++) {
                            for (int valRest = val + 1; valRest < 9; valRest++) {
                                result[x][y][val].add(-clauseCounter);
                                result[x][y][valRest].add(-clauseCounter);
                                clauseCounter++;
                            }
                        }
                    }
                }

                // Each number appears at least once in each row
                for (int y = 0; y < 9; y++) {
                    for (int val = 0; val < 9; val++) {
                        for (int x = 0; x < 9; x++) {
                            result[x][y][val].add(clauseCounter);
                        }
                        clauseCounter++;
                    }
                }

                // Each number appears at least once in each column
                for (int x = 0; x < 9; x++) {
                    for (int val = 0; val < 9; val++) {
                        for (int y = 0; y < 9; y++) {
                            result[x][y][val].add(clauseCounter);
                        }
                        clauseCounter++;
                    }
                }

                // Each number appears at least once in each 3x3 sub-grid
                for (int val = 0; val < 9; val++) {
                    for (int horSquareInd = 0; horSquareInd < 3; horSquareInd++) {
                        for (int vertSquareInd = 0; vertSquareInd < 3; vertSquareInd++) {
                            for (int x = 0; x < 3; x++) {
                                for (int y = 0; y < 3; y++) {
                                    result[3 * horSquareInd + x][3 * vertSquareInd + y][val].add(clauseCounter);
                                }
                            }
                            clauseCounter++;
                        }

                    }
                }
            }

            /*try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(1);*/

            return result;
        }

    }

    private static int[][] parseBoard(String line) {

        int[][] board = new int[9][9];

        for(int i = 0; i < board.length; i++) {

            for(int j = 0; j < board.length; j++) {
                int cur = parseChar(line.charAt(9 * i + j));
                board[i][j] = cur;
                /*if (cur > 0)
                    board[i][j] = cur;
                else
                    throw new IllegalArgumentException(String.format("Char %c in input", line.charAt(9 * i + j)));
                */
            } }

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

    public static void main(String[] args) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader("sudoku-inputs.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                int[][] board = parseBoard(line);

                printBoard(board);

                // create formula from board
                // create 3D array vars -> occurence in clauses

                ArrayList<Integer>[][][] varsInClauses = addFixedNumbersToBoardArray(board, Encoding.EXTENDED);

                // go thru 3D array and create 2D array clausses -> vars


                // transform 2D array to dimacs format

               // output to file
            }
        }
    }

    private static ArrayList<Integer>[][][] addFixedNumbersToBoardArray(int[][] board, Encoding encoding) {
        int clauseCounter = encoding.getNumberOfDefaultClauses();
        ArrayList<Integer>[][][] result = encoding.getDefaultBoardArray().clone();

        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                if (board[x][y] > 0) {
                    result[x][y][board[x][y]].add(clauseCounter);
                    clauseCounter++;
                }
            }

        }

        return result;
    }

    /***
     * Create a default (empty board) 3D array noting, which variable is used in which clause of the CNF
     *
     * @return 3D array: res[x][y][val] = N indicates, that VAR_x,y,val is used in clause N (-N for negation)
     */
    private static ArrayList<Integer>[][][] fillInDefaultBoardArray(Encoding encoding) {
        ArrayList<Integer>[][][] result = new ArrayList[9][9][9];
        int clauseCounter = 0;

        // at least one number in each entry
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                for (int val = 0; val < 9; val++) {
                    result[x][y][val] = new ArrayList<>();
                    result[x][y][val].add(clauseCounter);
                }
                clauseCounter++;
            }
        }

        // Each number appears at most once in each row
        for (int y = 0; y < 9; y++) {
            for (int val = 0; val < 9; val++) {
                for (int xAll = 0; xAll < 8; xAll++) {
                    for (int xRest = xAll+1; xRest < 9; xRest++) {
                        result[xAll][y][val].add(-clauseCounter);
                        result[xRest][y][val].add(-clauseCounter);
                        clauseCounter++;
                    }
                }
            }
        }

        // Each number appears at most once in each column
        for (int x = 0; x < 9; x++) {
            for (int val = 0; val < 9; val++) {
                for (int yAll = 0; yAll < 8; yAll++) {
                    for (int yRest = yAll+1; yRest < 9; yRest++) {
                        result[x][yAll][val].add(-clauseCounter);
                        result[x][yRest][val].add(-clauseCounter);
                        clauseCounter++;
                    }
                }
            }
        }

        // Each number appears at most once in each 3x3 sub-grid
        for (int val = 0; val < 9; val++) {
            for (int horSquareInd = 0; horSquareInd < 3; horSquareInd++) {
                for (int vertSquareInd = 0; vertSquareInd < 3; vertSquareInd++) {
                    for (int x = 0; x < 3; x++) {
                        for (int y = 0; y < 3; y++) {
                            for (int yRest = y+1; yRest < 3; yRest++) {
                                result[3*horSquareInd+x][3*vertSquareInd+y][val].add(-clauseCounter);
                                result[3*horSquareInd+x][3*vertSquareInd+yRest][val].add(-clauseCounter);
                                clauseCounter++;
                            }
                            for (int xRest = x+1; xRest < 3; xRest++) {
                                for (int yRest = 0; yRest < 3; yRest++) {
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
            for (int x = 0; x < 9; x++) {
                for (int y = 0; y < 9; y++) {
                    for (int val = 0; val < 8; val++) {
                        for (int valRest = val + 1; valRest < 9; valRest++) {
                            result[x][y][val].add(-clauseCounter);
                            result[x][y][valRest].add(-clauseCounter);
                            clauseCounter++;
                        }
                    }
                }
            }

            // Each number appears at least once in each row
            for (int y = 0; y < 9; y++) {
                for (int val = 0; val < 9; val++) {
                    for (int x = 0; x < 9; x++) {
                        result[x][y][val].add(clauseCounter);
                    }
                    clauseCounter++;
                }
            }

            // Each number appears at least once in each column
            for (int x = 0; x < 9; x++) {
                for (int val = 0; val < 9; val++) {
                    for (int y = 0; y < 9; y++) {
                        result[x][y][val].add(clauseCounter);
                    }
                    clauseCounter++;
                }
            }

            // Each number appears at least once in each 3x3 sub-grid
            for (int val = 0; val < 9; val++) {
                for (int horSquareInd = 0; horSquareInd < 3; horSquareInd++) {
                    for (int vertSquareInd = 0; vertSquareInd < 3; vertSquareInd++) {
                        for (int x = 0; x < 3; x++) {
                            for (int y = 0; y < 3; y++) {
                                result[3 * horSquareInd + x][3 * vertSquareInd + y][val].add(clauseCounter);
                            }
                        }
                        clauseCounter++;
                    }

                }
            }
        }

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(1);

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
}
