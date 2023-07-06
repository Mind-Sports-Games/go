package com.joansala.game.go;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.*;
import com.joansala.game.go.GoGame;
import com.joansala.game.go.GoBoard;

class BasicTests {

    @DisplayName("turn test")
    @Test
    public void turnTest(){
        GoGame game  = new GoGame();
        int value = game.turn();
        assertEquals(1, value);
    }

    @DisplayName("gamesize test")
    @Test
    public void gameSizeTest(){
        GoGame game  = new GoGame(13);
        GoBoard board = (GoBoard) game.getBoard();

        int value = board.gameSize();
        assertEquals(13, value);
    }

    @DisplayName("toGoBoard from string test")
    @Test
    public void toGoBoardFromStringTest(){
        GoGame game  = new GoGame(13);
        GoBoard board = (GoBoard) game.getBoard();

        String b1 = "19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19/19 w -";
        GoBoard board2 = board.toBoard(b1);
        int value = board2.gameSize();
        assertEquals(19, value);

        String b2 = "13/13/13/13/13/13/13/13/13/13/13/13/13 w -";
        GoBoard board3 = board.toBoard(b2);
        int value2 = board3.gameSize();
        assertEquals(13, value2);

        String b3 = "9/9/9/9/9/9/9/9/9 w -";
        GoBoard board4 = board.toBoard(b3);
        int value3 = board4.gameSize();
        assertEquals(9, value3);

        String b4 = "9/9/XX3OO2/9/XXXXXXXXX/9/1X1O1XX2/OOOOOOOOO/9 w -";
        GoBoard board5 = board.toBoard(b4);
        int value4 = board5.gameSize();
        assertEquals(9, value4);
    }

    @DisplayName("toGoBoard from string test 2")
    @Test
    public void toGoBoardFromStringTest2(){
        GoGame game  = new GoGame(19);
        GoBoard board = (GoBoard) game.getBoard();
        String b1 = "199/199/199/199/199/199/199/199/199/199/199/7X119/199/199/199/2O196/199/191X7/4X191O2 b -";
        String effectiveB1 = "19/19/19/19/19/19/19/19/19/19/19/7X11/19/19/19/2O16/19/11X7/4X11O2 b -";
        GoBoard board2 = board.toBoard(b1);
        int value = board2.gameSize();
        assertEquals(19, value);
        assertEquals(board2.toDiagram(), effectiveB1);
    }

    @DisplayName("numer of moves in starting 19x19 go game")
    @Test
    public void numberOfMovesTest19(){
        GoGame game  = new GoGame(19);
        game.resetCursor();

        int moveCount = 0;
        int nextMove = game.nextMove();
        while (nextMove != -1){
            moveCount++;
            nextMove = game.nextMove();
        }
        assertEquals(moveCount, 19*19 + 1);
        //assertTrue(game.isForfeit(19*19)); //private method
    }

    @DisplayName("numer of moves in starting 13x13 go game")
    @Test
    public void numberOfMovesTest13(){
        GoGame game  = new GoGame(13);
        game.resetCursor();

        int moveCount = 0;
        int nextMove = game.nextMove();
        while (nextMove != -1){
            moveCount++;
            nextMove = game.nextMove();
        }
        assertEquals(moveCount, 13*13 + 1);
        //assertTrue(game.isForfeit(13*13)); //private method
    }

    @DisplayName("numer of moves in starting 9x9 go game")
    @Test
    public void numberOfMovesTest9(){
        GoGame game  = new GoGame(9);
        game.resetCursor();

        int moveCount = 0;
        int nextMove = game.nextMove();
        while (nextMove != -1){
            moveCount++;
            nextMove = game.nextMove();
        }
        assertEquals(moveCount, 9*9 + 1);
        //assertTrue(game.isForfeit(9*9)); //private method
    }

    @DisplayName("algebra moves in go 9x9 game")
    @Test
    public void algebraMovesTest9(){
        GoGame game  = new GoGame(9);
        GoBoard board = (GoBoard) game.getBoard();
        int value = 35;
        String coord = board.toCoordinates(value);
        int move = board.toMove(coord);

        assertEquals(value, move);
    }

    @DisplayName("algebra moves in go 13x13 game")
    @Test
    public void algebraMovesTest13(){
        GoGame game  = new GoGame(13);
        GoBoard board = (GoBoard) game.getBoard();
        int value = 120;
        String coord = board.toCoordinates(value);
        int move = board.toMove(coord);

        assertEquals(value, move);
    }

    @DisplayName("algebra moves in go 19x19 game")
    @Test
    public void algebraMovesTest19(){
        GoGame game  = new GoGame(19);
        GoBoard board = (GoBoard) game.getBoard();
        int value = 135;
        String coord = board.toCoordinates(value);
        int move = board.toMove(coord);

        assertEquals(value, move);
    }

    @DisplayName("compute score counting areas")
    @Test
    public void computerScoreTest(){
        String fen = "O1O1O1O1O1O1O1O1O1O/OOOOOOOOOOOOOOOOOOO/1XXXXXXXXXXXXXXXXXX/XXXXXXXXXXXXXXXXXXX/XXXXXXXXXXXXXXXXXXX/XXXXXXXXXXXXXXXXXXX/XXXXXXXXXXXXXXXXXXX/XXXXXXXXXXXXXXXXXXX/XXXXXXXXXXXXXXXXXXX/XXXXXXXXXXXXXXXXXXX/XXXXXXXXXXXXXXXXXXX/XXXXXXXXXXXXXXXXXXX/XXXXXXXXXXXXXXXXXXX/X1X1X1X1X1X1X1X1X1X/XXXXXXXXXXXXXXXXXXX/X1X1X1X1X1X1X1X1X1X/XXXXXXXXXXXXXXXXXXX/XXXXXXXXXXXXXXXXXXX/19 b -";
        GoGame game  = new GoGame(19);
        GoBoard board = (GoBoard) game.getBoard();

        int komi = 6;
        GoBoard newBoard = board.toBoard(fen);
        game.setBoard(newBoard);
        game.setKomiScore(komi);
        int whiteScore = game.whiteScore();
        int blackScore = game.blackScore();
        int gameScore = game.score();

        assertEquals(blackScore, 322);
        assertEquals(whiteScore, 44); 
        assertEquals(gameScore, 278);
    }



    
}