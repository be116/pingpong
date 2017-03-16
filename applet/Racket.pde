class Racket {
 
  PVector zeroPosition;
  PVector size;
  color col;
  float parabola; //曲線率

  PVector position;
  PVector velocity;
  float rotation;
  
  PVector[] relativePoint, point;

  Racket ( PVector z, PVector s, color c, float p ) {
    zeroPosition = z;
    size = s;
    col = c;
    parabola = p;
    
    position = new PVector( 0, 0 );
    velocity = new PVector( 0, 0 );
    relativePoint = new PVector[2];
    point = new PVector[2];
    for ( int i=0; i < relativePoint.length; i++ ) {
      relativePoint[i] = new PVector( 0, 0 );
      point[i] = new PVector( 0, 0 );
    }

    init();
  }
  
  void init ( ) {
    position.set( zeroPosition );
    rotation = 0;
  }
  
  void calculatePoint (  ) {
    //relativePointの初期化
    relativePoint[0].set( size.x/2, -size.y/2 );
    relativePoint[1].set( -size.x/2, -size.y/2 );
    for ( int i=0; i<point.length; i++ ) {
      //回転
      relativePoint[i].rotate( rotation );
      //pointの計算
      point[i].set( position );
      point[i].add( relativePoint[i] );
    }
  }
  
  float calculateCritical ( float dist ) {
    float d = size.x/2 - dist;
    d /= size.x / 2;
    return d;
  }
  
  void control ( ) {
    float x = mouseX - width / 2;
    float y = parabola * x * x;
    position.set( x, y * 0.4 );
    position.add( zeroPosition );
    
    //微分を求める
    float dx = 1;
    float dy = parabola * ( x + dx ) * ( x + dx ) - y;
    
    rotation = atan2( dy, dx );
    
    calculatePoint();
  }
  
  void draw ( ) {
    //stroke( col );
    noStroke();
    fill( col );
    rectMode(CENTER);
    pushMatrix();
    translate( position.x, position.y );
    rotate( rotation );
    translate( -position.x, -position.y );
    rect( position.x, position.y
    , size.x, size.y );
    popMatrix();
    stroke( color( 255, 0, 0 ) );
    line( point[0].x, point[0].y, point[1].x, point[1].y );
    
  }
  
}
