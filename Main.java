package Minesweep;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;
import java.util.Random;
class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int row_n = 0, col_n = 0, flagcount = 0, mine = 0, win_count = 0;
        clearScreen();
        System.out.println(">>>>>> Welcome To Minesweeper <<<<<<\n");
        System.out.println("Select Level : ");
        System.out.println("\t 1. Easy");
        System.out.println("\t 2. Medium");
        System.out.println("\t 3. Hard");
        System.out.println("Enter your level : ");
        int level = sc.nextInt();
        switch (level) {
            case 1 :
                row_n = col_n = 9;
                flagcount = mine =10;
                break;
            case 2 :
                row_n = col_n = 16;
                flagcount = mine =40;
                break;
            case 3 :
                row_n = 16;
                col_n = 30;
                flagcount = mine = 99;
                break;
            default:
                return;
        }
        int[][] result = new int [row_n][col_n];
        String out = "";
        Random random = new Random();
        int count = 0;
        while (count < flagcount) {
            int result_row = random.nextInt(row_n);
            int result_col = random.nextInt(col_n);
            if (result[result_row][result_col] != -2) {
                result[result_row][result_col] = -2;
                if (result_row > 0 && result_col > 0 && result[result_row-1][result_col-1] >= 0) {
                    result[result_row-1][result_col-1] += 1;
                }
                if (result_row > 0 && result[result_row-1][result_col] >= 0) {
                    result[result_row-1][result_col] += 1;
                }
                if (result_row > 0 && result_col < col_n-1 && result[result_row-1][result_col+1] >= 0) {
                    result[result_row-1][result_col+1] += 1;
                }
                if (result_col < col_n-1 && result[result_row][result_col+1] >= 0) {
                    result[result_row][result_col+1] += 1;
                }
                if (result_col > 0 && result[result_row][result_col -1] >= 0) {
                    result[result_row][result_col -1] += 1;
                }
                if (result_row < row_n-1 && result[result_row+1][result_col] >= 0) {
                    result[result_row+1][result_col] += 1;
                }
                if (result_row < row_n-1 && result_col > 0 && result[result_row +1][result_col -1] >= 0) {
                    result[result_row +1][result_col -1] += 1;
                }
                if (result_row < row_n-1 && result_col < col_n-1 && result[result_row +1][result_col +1] >= 0) {
                    result[result_row +1][result_col +1] += 1;
                }
                count++;
            }
        }

        char[][] display = new char[row_n][col_n];
        for (int p_row = 0; p_row < row_n; p_row++) {
            for (int p_col = 0; p_col < col_n; p_col++) {
                display[p_row][p_col] = '-';
            }
        }
        //best Start position
        int[] start_index = best_start(result);
        display[start_index[0]][start_index[1]] = 'X';

        Instant instantStarted = Instant.now();
        boolean loop = true;
        while (loop) {
            clearScreen();
            Instant currentStopped = Instant.now();
            Duration durationBetween = Duration.between(instantStarted, currentStopped);
            System.out.println("------------------- Minesweeper -------------------\n");
            System.out.println("Flag (F) : " + flagcount + "                   " + "Open Count : " + win_count);
            System.out.println("           Time  : " + durationBetween.toSeconds() + " seconds");
            print_matrix(display);
            if (win_count == row_n*col_n - mine) {
                Instant instantStopped = Instant.now();
                Duration durationComplete = Duration.between(instantStarted, instantStopped);
                System.out.println("        Your Time : " + durationComplete.toSeconds());
                System.out.println("\n-------------- CONGRATULATIONS !! --------------\n");
                System.out.println("               YOU WON THE GAME :)               \n\n");
                System.out.println("1.New Game");
                System.out.println("2. Exit ");
                System.out.println("Enter Choice : ");
                int win_choice = sc.nextInt();
                if (win_choice == 1) {
                    clearScreen();
                    main(null);
                } else {
                    System.out.println(" Thank You !");
                    return;
                }
            }
            System.out.println(out);
            out = "";
            System.out.println("Enter row, col :");
            int row = sc.nextInt();
            int col = sc.nextInt();
            sc.nextLine();
            System.out.println("Enter Operation : (Open - o / Flag - f)");
            char operation = sc.nextLine().charAt(0);

            switch(operation) {
                case 'o' :
                    if (display[row][col]=='-'||display[row][col]=='X') {
                        if (result[row][col] == -2) {
                            checkBomb(result, display);
                            return;
                        } else if (result[row][col] == 0) {
                            openEmptySpaces(display, result, row, col);
                            win_count = open_mine(display);
                        } else {
                            display[row][col] = Integer.toString(result[row][col]).charAt(0);
                            win_count++;
                        }
                    } else {
                        out = ">>>>>>>>>>> It's Already Opened / Flagged! <<<<<<<<<<<<<";
                    }
                    break;
                case 'f':
                    out = flag(display,row, col, flagcount);
                    if (out.charAt(out.length()-2) == ' ') {
                        flagcount = Character.getNumericValue(out.charAt(out.length()-1));
                    } else if (out.charAt(out.length()-1) != '<') {
                        flagcount = Character.getNumericValue(out.charAt(out.length()-2)) * 10;
                        flagcount += Character.getNumericValue(out.charAt(out.length()-1));
                    }
                    break;
                default:
                    out = ">>>>>>>>>>>>>> Invalid Operation! <<<<<<<<<<<<<<<<<";
            }
        }
    }

    public static void print_matrix(char[][] display) {
        System.out.print("  | ");
        for (int first_row = 0; first_row < display[0].length; first_row++) {
            if (first_row < 10) {
                System.out.print(first_row + " | ");
            } else {
                System.out.print(first_row + "| ");
            }
        }
        System.out.println();
        for (int row = 0; row < display.length; row++) {
            System.out.print(row+" | ");
            for (int col = 0; col < display[0].length; col++) {
                System.out.print(display[row][col] + " | ");
            }
            System.out.println();
        }
    }

    private static int open_mine(char[][] display_matrix) {
        int count = 0;
        for (int row = 0; row < display_matrix.length; row++) {
            for (int col = 0; col < display_matrix.length; col++) {
                if (display_matrix[row][col] != '-' && display_matrix[row][col] != 'F') {
                    count++;
                }
            }
        }
        return count;
    }

    public static void clearScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
    public static int[] best_start(int[][] result) {
        int[] index = new int[2];
        int result_row = result.length, result_col = result[0].length;
        int max = 0;
        for (int row = 0; row < result_row; row++) {
            for (int col = 0; col < result_col; col++) {
                int count = 0;
                if (result[row][col] == 0) {
                    if (row > 0 && col > 0 && result[row-1][col-1] == 0) {
                        count += 1;
                    }
                    if (row > 0 && result[row-1][col] == 0) {
                        count += 1;
                    }
                    if (row > 0 && col < result_col-1 && result[row-1][col+1] == 0) {
                        count += 1;
                    }
                    if (col < result_col-1 && result[row][col+1] == 0) {
                        count += 1;
                    }
                    if (col > 0 && result[row][col -1] == 0) {
                        count += 1;
                    }
                    if (row < result_row-1 && result[row+1][col] == 0) {
                        count += 1;
                    }
                    if (row < result_row-1 && col > 0 && result[row +1][col -1] == 0) {
                        count += 1;
                    }
                    if (row < result_row-1 && col < result_col-1 && result[row +1][col +1] == 0) {
                        count += 1;
                    }
                    if (count > max) {
                        max = count;
                        index[0] = row;
                        index[1] = col;
                    }
                }
            }
        }
        return index;
    }
    
        public static void checkBomb(int[][] result,char[][] playing) {
            for (int i = 0; i < playing.length; i++) {
                for (int j = 0; j < playing[0].length; j++) {
                    if (result[i][j] == -2 && playing[i][j] != 'F') {
                        playing[i][j] = 'B';
                    }
                }
            }
            clearScreen();
            System.out.println("------------------- Minesweeper -------------------\n");
            print_matrix(playing);
            System.out.println("=============== GAME OVER ===============");
            System.out.println("\n         You lost the Game :(        \n");
            System.out.println("         Better luck next time!         \n\n");
            System.out.println("1.New Game");
            System.out.println("2. Exit ");
            System.out.println("Enter Choice : ");
            Scanner sc = new Scanner(System.in);
            int win_choice = sc.nextInt();
            if (win_choice == 1) {
                clearScreen();
                main(null);
            } else {
                System.out.println(" Thank You !");
                System.exit(0);
            }
        }
        public static void openEmptySpaces(char[][] displaymatrix, int[][] grid, int i, int j) {
            int grid_row = grid.length, grid_col = grid[0].length;
            if (i == -1 || j == -1 || i >= grid_row || j >= grid_col) {
                return;
            }

            if (grid[i][j] != 0) {
                displaymatrix[i][j] = Integer.toString(grid[i][j]).charAt(0);
            }

            if ((displaymatrix[i][j]=='-'||displaymatrix[i][j]=='X') && grid[i][j] == 0) {
                displaymatrix[i][j] = ' ';

                openEmptySpaces(displaymatrix, grid, i, j + 1); // right
                openEmptySpaces(displaymatrix, grid,i + 1, j); // down
                openEmptySpaces(displaymatrix, grid, i, j - 1); // left
                openEmptySpaces(displaymatrix, grid,i - 1, j); // up

                openEmptySpaces(displaymatrix, grid,i + 1, j + 1); // downright
                openEmptySpaces(displaymatrix, grid,i - 1, j + 1); // upright
                openEmptySpaces(displaymatrix, grid,i - 1, j - 1); // upleft
                openEmptySpaces(displaymatrix, grid,i + 1, j - 1); // downleft
            }
        }
        public static String flag(char[][] displayArray, int row, int col, int flagcount) {
            String out = "";
            if (displayArray[row][col] == '-') {
                displayArray[row][col] = 'F';
                flagcount--;
                out = "( " + row + "," + col + " ) is flaged.\n Flag Count : " + flagcount;
            } else if (displayArray[row][col] == 'F') {
                displayArray[row][col] = '-';
                flagcount++;
                out = "( " + row + "," + col + " ) is unflaged.\n Flag Count : " + flagcount;

            } else {
                out = ">>>>>>>>>> It's already opened! <<<<<<<<<<<<";
            }
            return out;
        }
}