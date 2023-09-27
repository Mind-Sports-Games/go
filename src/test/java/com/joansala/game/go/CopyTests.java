package com.joansala.game.go;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.*;


class CopyTests {

    @DisplayName("copy int test")
    @Test
    public void copyIntTest(){
        GoGame game  = new GoGame();
        GoGame copy  = game.deepCopy();
        assertEquals(game.getCursor(), copy.getCursor());
        copy.setCursor(copy.getCursor() + 1);
        assertNotEquals(game.getCursor(), copy.getCursor());
    }

    @DisplayName("copy Board test")
    @Test
    public void copyBoardTest(){
        GoGame game  = new GoGame(9);
        int[] moves = {41, 40, 49};
        for (int move : moves){
            game.makeMove(move);    
        }
        GoGame copy = game.deepCopy();
        assertEquals(game.toBoard().toDiagram(), copy.toBoard().toDiagram());
        int[] nextMoves = {48, 39, 50};
        for (int move : nextMoves){
            game.makeMove(move);    
        }
        //System.out.println(game.toBoard().toDiagram());
        //System.out.println(copy.toBoard().toDiagram());
        assertNotEquals(game.toBoard().toDiagram(), copy.toBoard().toDiagram());
        assertNotEquals(game.turn(), copy.turn());
        assertNotEquals(game.isLegal(48), copy.isLegal(48));
        assertNotEquals(game.lastMove(), copy.lastMove());
        assertNotEquals(game.score(), copy.score());
        assertNotEquals(game.whiteScore(), copy.whiteScore());
        
    }

}
