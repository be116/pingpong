import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class PingPong extends PApplet {

MathVec2 mathVec2;
Ball ball;
Racket racket;
Score score;
float gravity = 0.8f;
float ellapsedTime;

public void setup() {
  frameRate( 60 );
  size(400, 500);
  mathVec2 = new MathVec2();
  ball = new Ball( new PVector( width * 0.5f, height + 12 ), 12, color( 255, 0, 0 ) );
  racket = new Racket( new PVector( width * 0.5f, height * 0.8f ), new PVector( 90, 20 ), color( 0 ), -0.002f );
  score = new Score( new PVector( 100, 40 ), new PVector( 160, 50 ) );
}

public void draw() {
  background(255);
  ellapsedTime = 0;
  
  racket.control();
  while ( ellapsedTime < 1.0f ) {
    ellapsedTime += ball.updateByRadius( );
  }
  //\u80cc\u666f
  textSize( 32 );
  fill( 180 );
  textAlign( CENTER );
  text( "CLICK ! ", width*0.5f, height*0.35f, 150, 50 );
  rectMode( CORNER );
  int num = 5;
  float col = 250;
  for ( int i=num; i>0; i-- ) {
    stroke( col / num * i );
    fill( col / num * i );
    rect( 0, height - height * 0.14f / num * i , width, height );
  }
  ball.draw();
  racket.draw();
  score.display();

}

public void mousePressed () {
  ball.jump( -23 );
  score.init();
}
class Ball {
 
  PVector zeroPosition;
  float radius;
  int col;
  float force;

  PVector position;
  PVector velocity;
  
  boolean onGround;
  boolean isRacketting;
  boolean isFalling;
  boolean onLeftWall, onRightWall;
  
  Ball ( PVector p, float r, int c ) {
    zeroPosition = p;
    radius = r;
    col = c;
    
    position = new PVector( 0, 0 );
    velocity = new PVector( 0, 0 );

    init();
  }
  
  public void init ( ) {
    position.set( zeroPosition );
    //velocity.limit( 20 );
    force = 20;
    onGround = false;
    isRacketting = false;
    isFalling = false;
    onLeftWall = onRightWall = false;
  }
  
  public void jump ( float a ) {
    if ( !onGround ) return;
    velocity.y += a;
    init();
  }
  
  public float updateByRadius () {
    float deltaTime = 1.0f;
    if ( velocity.mag() != 0 ) {
      deltaTime = radius / velocity.mag();
      //deltaTime = ( - velocity.mag() + sqrt( pow( velocity.mag(), 2 ) + 2 * gravity * radius ) ) / gravity;
    }
    if ( ellapsedTime + deltaTime > 1.0f ) deltaTime = 1.0f - ellapsedTime;
    return updateByDeltaTime( deltaTime );
  }
  
  // \u5b9f\u969b\u306e\u66f4\u65b0\u6642\u9593\u3092\u8fd4\u3059
  public float updateByDeltaTime ( float deltaTime ) {
    //\u4e00\u56de\u306e\u66f4\u65b0\u304c1F\u3092\u8d85\u3048\u306a\u3044
    if ( ellapsedTime + deltaTime > 1.0f ) deltaTime = 1.0f - ellapsedTime;
    
    if ( velocity.y > 0 ) isFalling = true;
    
    //\u4f4d\u7f6e\u66f4\u65b0
    PVector displacement = new PVector( 0, 0 );
    displacement.set( velocity );
    displacement.mult( deltaTime );
    position.add( displacement );
    
    //\u753b\u9762\u5916\u306e\u5224\u5b9a
    onGround = position.y - radius >= height;
    onLeftWall = position.x - radius < 0;
    onRightWall = position.x + radius > width;
    
    //\u885d\u7a81\u5224\u5b9a   
    PVector s1 = new PVector ( 0, 0 );
    PVector s2 = new PVector ( 0, 0 );
    PVector v1 = new PVector ( 0, 0 );
    PVector v2 = new PVector ( 0, 0 );
    
    s2.set( racket.point[0] );
    v2.set( racket.point[1] );
    v2.sub( racket.point[0] );
    PVector tagNormal = new PVector( 0, 0 );
    tagNormal.set( racket.point[1] ); tagNormal.sub( racket.point[0] ); tagNormal.normalize();
    tagNormal.rotate( HALF_PI ); //\u5224\u5b9a\u5bfe\u8c61\u306e\u6cd5\u7dda\u30d9\u30af\u30c8\u30eb\u3092\u53d6\u5f97
    s1.set( position );
    v1.set( tagNormal ); v1.mult( - radius );

    PVector cross = new PVector( 0, 0 );
    isRacketting = mathVec2.isCrossed ( s1, s2, v1, v2, cross );
    if ( !isFalling ) isRacketting = false; 
    if ( isRacketting ) onGround = false;
    
    //\u885d\u7a81\u5fdc\u7b54
    if ( onGround ) {
      position.set( zeroPosition );
    }
    
    //\u3081\u308a\u3053\u307f\u306e\u89e3\u6d88
    if ( isRacketting ) {
      //\u4fee\u6b63\u91cf\u306e\u8a08\u7b97
      PVector modification = new PVector( 0, 0 );
      float dist = PVector.dist( cross, position );
      modification.set( tagNormal );
      modification.mult( radius - dist );
      
      position.add( modification );
    } else if ( onLeftWall ) {
      position.x = 0 + radius;
    } else if ( onRightWall ) {
      position.x = width - radius;
    }
    
    //\u901f\u5ea6\u306e\u66f4\u65b0
    if ( onGround ) {
      velocity.set( 0, 0 );
    } else if ( isRacketting ) {
      float tagRad = atan2( v2.y, v2.x );
      float velocityRad = atan2( velocity.y, velocity.x );
      float nyusyaRad = tagRad - velocityRad;
      velocity.rotate( 2 * nyusyaRad );
      float dist = PVector.dist( cross, racket.position );
      velocity.setMag( force );
      //velocity.mult( racket.calculateCritical( dist ) );
      score.scorePlus();
    } else if ( onRightWall || onLeftWall ) {
      velocity.x *= -1;
    }else {
      //\u91cd\u529b
      velocity.y += gravity * deltaTime;
    }
    
    return deltaTime;
  }
  
  public void draw ( ) {
    //stroke( col );
    noStroke();
    fill( col );
    ellipseMode(RADIUS);
    ellipse( position.x, position.y, radius, radius );
  }
  
}
class MathVec2 {
  
  MathVec2() {}
  
  public float v2Dot ( PVector v1, PVector v2 ) {
    return v1.x * v2.x + v1.y * v2.y;
  }
  
  public float v2Cross ( PVector v1, PVector v2 ) {
    return v1.x * v2.y - v1.y * v2.x;
  }
  
  public int v2Side ( PVector v1, PVector v2 ) {
    float buf = v2Cross( v1, v2 );
    if ( buf > 0 ) return 1; // \u53f3
    else if ( buf < 0 ) return -1; // \u5de6
    else return 0; //\u7dda\u4e0a
  }
  
  //s1->v1
  //s2->v2
  public boolean isCrossed ( PVector s1, PVector s2, PVector v1, PVector v2, PVector cross ) {
    PVector v = new PVector( s2.x, s2.y );
    v.sub( s1 );
    
    float crs_v1_v2 = v2Cross( v1, v2 );
    if ( crs_v1_v2 == 0.0f ) {
      return false; 
    }
    
    float crs_v_v1 = v2Cross( v, v1 );
    float crs_v_v2 = v2Cross( v, v2 );
    
    float t1 = crs_v_v2 / crs_v1_v2;
    float t2 = crs_v_v1 / crs_v1_v2;
    
    if ( 0 <= t1 && t1 <= 1 && 0 <= t2 && t2 <= 1 ){}
    else  return false;
    
    cross.set( v1 );
    cross.mult( t1 );
    cross.add( s1 );
    
    return true;
  } 
}
class Racket {
 
  PVector zeroPosition;
  PVector size;
  int col;
  float parabola; //\u66f2\u7dda\u7387

  PVector position;
  PVector velocity;
  float rotation;
  
  PVector[] relativePoint, point;

  Racket ( PVector z, PVector s, int c, float p ) {
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
  
  public void init ( ) {
    position.set( zeroPosition );
    rotation = 0;
  }
  
  public void calculatePoint (  ) {
    //relativePoint\u306e\u521d\u671f\u5316
    relativePoint[0].set( size.x/2, -size.y/2 );
    relativePoint[1].set( -size.x/2, -size.y/2 );
    for ( int i=0; i<point.length; i++ ) {
      //\u56de\u8ee2
      relativePoint[i].rotate( rotation );
      //point\u306e\u8a08\u7b97
      point[i].set( position );
      point[i].add( relativePoint[i] );
    }
  }
  
  public float calculateCritical ( float dist ) {
    float d = size.x/2 - dist;
    d /= size.x / 2;
    return d;
  }
  
  public void control ( ) {
    float x = mouseX - width / 2;
    float y = parabola * x * x;
    position.set( x, y * 0.4f );
    position.add( zeroPosition );
    
    //\u5fae\u5206\u3092\u6c42\u3081\u308b
    float dx = 1;
    float dy = parabola * ( x + dx ) * ( x + dx ) - y;
    
    rotation = atan2( dy, dx );
    
    calculatePoint();
  }
  
  public void draw ( ) {
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
class Score {
  
  int score;
  int highscore;
  String scoreStr; 
  PVector position;
  PVector size;
  
  int scoreCol;
  int highscoreCol;

  Score ( PVector p, PVector s ) {
    position = new PVector( 0, 0 );
    size = new PVector( 0, 0 );
    position.set( p );
    size.set( s );
    scoreStr = new String();
    highscore = 0;
    init();
  }
  
  public void init () {
    score = 0;
    scoreCol = color( 10, 10, 10 );
    highscoreCol = color( 255, 0, 0 );
  }
  
  public void scorePlus () {
    score ++;
    if ( score > highscore ) {
      highscore = score;
      scoreCol = highscoreCol;
    }
  }
  
  public void display () {
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
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "PingPong" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
