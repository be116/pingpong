MathVec2 mathVec2;
Ball ball;
Racket racket;
Score score;
float gravity = 0.8;
float ellapsedTime;

void setup() {
  frameRate( 60 );
  size(400, 500);
  mathVec2 = new MathVec2();
  ball = new Ball( new PVector( width * 0.5, height + 12 ), 12, color( 255, 0, 0 ) );
  racket = new Racket( new PVector( width * 0.5, height * 0.8 ), new PVector( 90, 20 ), color( 0 ), -0.002 );
  score = new Score( new PVector( 100, 40 ), new PVector( 160, 50 ) );
}

void draw() {
  background(255);
  ellapsedTime = 0;
  
  racket.control();
  while ( ellapsedTime < 1.0 ) {
    ellapsedTime += ball.updateByRadius( );
  }
  //背景
  textSize( 32 );
  fill( 180 );
  textAlign( CENTER );
  text( "CLICK ! ", width*0.5, height*0.35, 150, 50 );
  rectMode( CORNER );
  int num = 5;
  float col = 250;
  for ( int i=num; i>0; i-- ) {
    stroke( col / num * i );
    fill( col / num * i );
    rect( 0, height - height * 0.14 / num * i , width, height );
  }
  ball.draw();
  racket.draw();
  score.display();

}

void mousePressed () {
  ball.jump( -23 );
  score.init();
}
