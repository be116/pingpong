class Score {
  
  int score;
  int highscore;
  String scoreStr; 
  PVector position;
  PVector size;
  
  color scoreCol;
  color highscoreCol;

  Score ( PVector p, PVector s ) {
    position = new PVector( 0, 0 );
    size = new PVector( 0, 0 );
    position.set( p );
    size.set( s );
    scoreStr = new String();
    highscore = 0;
    init();
  }
  
  void init () {
    score = 0;
    scoreCol = color( 10, 10, 10 );
    highscoreCol = color( 255, 0, 0 );
  }
  
  void scorePlus () {
    score ++;
    if ( score > highscore ) {
      highscore = score;
      scoreCol = highscoreCol;
    }
  }
  
  void display () {
    textSize( 16 );
    fill( scoreCol );
    textAlign(LEFT);
    text( "SCORE", position.x, position.y, size.x, size.y );
    textAlign(RIGHT);
    text( str( score ), position.x, position.y, size.x, size.y );
    textAlign(LEFT);
    fill( highscoreCol );
    text( "HIGH_SCORE", position.x, position.y + 30, size.x, size.y );
    textAlign(RIGHT);
    text( str( highscore ), position.x, position.y + 30, size.x, size.y );
  }
  
}
